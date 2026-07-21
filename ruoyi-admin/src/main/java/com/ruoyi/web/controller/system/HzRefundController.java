package com.ruoyi.web.controller.system;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.common.utils.PageUtils;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.system.domain.HzBill;
import com.ruoyi.system.domain.HzCheckoutApply;
import com.ruoyi.system.domain.HzCheckoutRecord;
import com.ruoyi.system.domain.HzRefundApplyVO;
import com.ruoyi.system.mapper.HzBillMapper;
import com.ruoyi.system.mapper.HzCheckoutApplyMapper;
import com.ruoyi.system.mapper.HzCheckoutRecordMapper;
import com.ruoyi.system.service.IHzRefundService;
import com.ruoyi.system.service.WechatPayService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 退款��理Controller
 *
 * @author ruoyi
 */
@RestController("adminRefundController")
@RequestMapping("/gangzhu/refund")
public class HzRefundController extends BaseController {

    @Autowired
    private IHzRefundService refundService;

    @Autowired
    private HzCheckoutRecordMapper checkoutRecordMapper;

    @Autowired
    private HzCheckoutApplyMapper checkoutApplyMapper;

    @Autowired
    private HzBillMapper billMapper;

    @Autowired
    private WechatPayService wechatPayService;

    /**
     * 查询退款申请列表
     */
    @PreAuthorize("@ss.hasPermi('gangzhu:refund:list')")
    @GetMapping("/list")
    public TableDataInfo list(HzRefundApplyVO query) {
        Page<HzCheckoutApply> page = PageUtils.getPage();
        return refundService.selectRefundList(page,
                query.getRefundNo(),
                query.getContractNo(),
                query.getRefundStatus(),
                query.getProjectId(),
                query.getRefundType(),
                query.getTenantName()
        );
    }

    /**
     * 获取退款申请详细信息
     */
    @PreAuthorize("@ss.hasPermi('gangzhu:refund:query')")
    @GetMapping(value = "/{refundId}")
    public AjaxResult getInfo(@PathVariable("refundId") Long refundId) {
        return success(refundService.selectRefundById(refundId));
    }

    /**
     * 审核退款申请
     */
    @PreAuthorize("@ss.hasPermi('gangzhu:refund:audit')")
    @Log(title = "退款管理", businessType = BusinessType.UPDATE)
    @PostMapping("/audit")
    public AjaxResult audit(@RequestBody HzRefundApplyVO vo) {
        return toAjax(refundService.auditRefund(
                vo.getRefundId(),
                vo.getRefundStatus(),
                vo.getApproveOpinion()
        ));
    }

    /**
     * 删除退款申请
     */
    @PreAuthorize("@ss.hasPermi('gangzhu:refund:remove')")
    @Log(title = "退款管理", businessType = BusinessType.DELETE)
    @DeleteMapping("/{refundId}")
    public AjaxResult remove(@PathVariable Long refundId) {
        return toAjax(refundService.deleteRefundById(refundId));
    }

    /**
     * 微信原路退款（押金 + 已付租金 双笔退款）
     * <p>
     * 业务说明：
     * - hz_checkout_apply.refund_amount：总应退金额（管理员核算）
     * - hz_checkout_apply.deposit_refund：其中应退押金部分（管理员可调整）
     * - 已付租金应退 = refund_amount - deposit_refund
     * <p>
     * 拆分策略：
     * - 押金部分走【押金账单】对应的 transaction_no 原路退（不超过押金已付金额）
     * - 租金部分走【首期已付租金账单】对应的 transaction_no 原路退（不超过该笔账单已付金额）
     * - 两笔互相独立调用，任一成功即更新 record 为已退还，失败明细写入 paymentRemark
     * <p>
     * refundId 实际对应 hz_checkout_apply.apply_id
     */
    @PreAuthorize("@ss.hasPermi('gangzhu:refund:payment')")
    @Log(title = "微信退款", businessType = BusinessType.UPDATE)
    @PostMapping("/wechat/{refundId}")
    public AjaxResult wechatRefund(@PathVariable Long refundId) {
        // 1. 查退租申请
        HzCheckoutApply apply = checkoutApplyMapper.selectById(refundId);
        if (apply == null) {
            return error("退款记录不存在");
        }
        BigDecimal totalRefund = apply.getRefundAmount();
        if (totalRefund == null || totalRefund.compareTo(BigDecimal.ZERO) <= 0) {
            return error("退款金额无效");
        }

        // 2. 防重：检查退租记录是否已退还
        LambdaQueryWrapper<HzCheckoutRecord> recordQuery = new LambdaQueryWrapper<>();
        recordQuery.eq(HzCheckoutRecord::getApplyId, refundId).last("LIMIT 1");
        HzCheckoutRecord record = checkoutRecordMapper.selectOne(recordQuery);
        if (record == null) {
            return error("退租确认记录不存在，用户尚未完成退租确认");
        }
        if ("1".equals(record.getRefundStatus())) {
            return error("该退款已处理，请勿重复操作");
        }

        // 3. 拆分金额：押金部分 + 租金部分
        // 兜底：当 apply.depositRefund 未保存（NULL 或 0，覆盖老数据 / 自动退款流程）时，从押金账单已付金额推断
        BigDecimal depositRefund;
        if (apply.getDepositRefund() == null
                || apply.getDepositRefund().compareTo(BigDecimal.ZERO) <= 0) {
            BigDecimal depositPaidGuess = BigDecimal.ZERO;
            LambdaQueryWrapper<HzBill> guessQuery = new LambdaQueryWrapper<>();
            guessQuery.eq(HzBill::getContractId, apply.getContractId())
                      .eq(HzBill::getBillType, "1")
                      .eq(HzBill::getPayMethod, "wechat")
                      .eq(HzBill::getBillStatus, "1")
                      .in(HzBill::getDelFlag, "0", "2") // 退款针对已失效/退租合同的历史账单，兼容软删(del_flag=2)
                      .last("LIMIT 1");
            HzBill guessBill = billMapper.selectOne(guessQuery);
            if (guessBill != null) {
                depositPaidGuess = guessBill.getPaidAmount() != null ? guessBill.getPaidAmount()
                        : (guessBill.getBillAmount() != null ? guessBill.getBillAmount() : BigDecimal.ZERO);
            }
            // 推断的押金部分不超过应退总额
            depositRefund = depositPaidGuess.compareTo(totalRefund) > 0 ? totalRefund : depositPaidGuess;
            logger.warn("退款单 {} 未保存 deposit_refund，自动按押金账单已付推断为 {}", refundId, depositRefund);
        } else {
            depositRefund = apply.getDepositRefund();
        }
        if (depositRefund.compareTo(BigDecimal.ZERO) < 0) {
            return error("应退押金不能为负数");
        }
        if (depositRefund.compareTo(totalRefund) > 0) {
            return error("应退押金不能超过应退总额");
        }
        BigDecimal rentRefund = totalRefund.subtract(depositRefund);

        // 4. 查押金账单（bill_type='1' 押金，wechat 已支付）
        HzBill depositBill = null;
        if (depositRefund.compareTo(BigDecimal.ZERO) > 0) {
            LambdaQueryWrapper<HzBill> depositQuery = new LambdaQueryWrapper<>();
            depositQuery.eq(HzBill::getContractId, apply.getContractId())
                        .eq(HzBill::getBillType, "1")
                        .eq(HzBill::getPayMethod, "wechat")
                        .eq(HzBill::getBillStatus, "1")
                        .in(HzBill::getDelFlag, "0", "2") // 退款针对已失效/退租合同的历史账单，兼容软删(del_flag=2)
                        .last("LIMIT 1");
            depositBill = billMapper.selectOne(depositQuery);
            if (depositBill == null) {
                return error("未找到微信支付的押金账单，该押金可能未通过微信支付，无法原路退款");
            }
            if (depositBill.getTransactionNo() == null || depositBill.getTransactionNo().isEmpty()) {
                return error("押金账单缺少微信交易号，无法发起退款");
            }
            BigDecimal depositPaid = depositBill.getPaidAmount() != null ? depositBill.getPaidAmount()
                    : (depositBill.getBillAmount() != null ? depositBill.getBillAmount() : BigDecimal.ZERO);
            if (depositRefund.compareTo(depositPaid) > 0) {
                return error("应退押金(" + depositRefund + ")超过押金已付金额(" + depositPaid + ")，请调整");
            }
        }

        // 5. 查已付租金账单（bill_type='2' 租金，wechat 已支付）—— 取第一笔有 transaction_no 的
        HzBill rentBill = null;
        if (rentRefund.compareTo(BigDecimal.ZERO) > 0) {
            LambdaQueryWrapper<HzBill> rentQuery = new LambdaQueryWrapper<>();
            rentQuery.eq(HzBill::getContractId, apply.getContractId())
                     .eq(HzBill::getBillType, "2")
                     .eq(HzBill::getPayMethod, "wechat")
                     .eq(HzBill::getBillStatus, "1")
                     .in(HzBill::getDelFlag, "0", "2") // 退款针对已失效/退租合同的历史账单，兼容软删(del_flag=2)
                     .isNotNull(HzBill::getTransactionNo)
                     .orderByAsc(HzBill::getPayTime);
            List<HzBill> rentBills = billMapper.selectList(rentQuery);
            // 选一个已付金额 >= rentRefund 的账单作为退款载体
            for (HzBill b : rentBills) {
                if (b.getTransactionNo() == null || b.getTransactionNo().isEmpty()) continue;
                BigDecimal paid = b.getPaidAmount() != null ? b.getPaidAmount()
                        : (b.getBillAmount() != null ? b.getBillAmount() : BigDecimal.ZERO);
                if (paid.compareTo(rentRefund) >= 0) {
                    rentBill = b;
                    break;
                }
            }
            if (rentBill == null) {
                return error("已付租金账单中没有单笔金额足以支撑 ¥" + rentRefund + " 的退款，请调整应退押金或线下处理");
            }
        }

        // 6. 调用微信退款 API（事务外，不可回滚）
        long ts = System.currentTimeMillis();
        boolean depositOk = false;
        boolean rentOk = false;
        StringBuilder remark = new StringBuilder();

        if (depositBill != null) {
            String outRefundDeposit = "REFUND_DEP" + ts + refundId;
            int depositFen = depositRefund.multiply(new BigDecimal("100")).intValue();
            int depositTotalFen = depositBill.getBillAmount().multiply(new BigDecimal("100")).intValue();
            try {
                Map<String, Object> r1 = wechatPayService.wechatRefund(
                        depositBill.getTransactionNo(), outRefundDeposit,
                        depositFen, depositTotalFen, "退租退款-押金");
                depositOk = true;
                remark.append("押金已申请退款 ¥").append(depositRefund)
                      .append(" 单号:").append(outRefundDeposit).append("; ");
                logger.info("退款管理-押金退款成功 refundId={} resp={}", refundId, r1);
            } catch (Exception e) {
                if (e.getMessage() != null && e.getMessage().contains("订单已全额退款")) {
                    // 微信侧已全额退款，视为幂等成功
                    depositOk = true;
                    remark.append("押金已退款(微信侧已全额退款，幂等放行) ¥").append(depositRefund).append("; ");
                    logger.warn("退款管理-押金退款幂等成功(订单已全额退款) refundId={}", refundId);
                } else {
                    remark.append("押金退款失败:").append(e.getMessage()).append("; ");
                    logger.error("退款管理-押金微信退款失败 refundId={}", refundId, e);
                }
            }
        }

        if (rentBill != null) {
            String outRefundRent = "REFUND_RENT" + ts + refundId;
            int rentFen = rentRefund.multiply(new BigDecimal("100")).intValue();
            int rentTotalFen = rentBill.getBillAmount().multiply(new BigDecimal("100")).intValue();
            try {
                Map<String, Object> r2 = wechatPayService.wechatRefund(
                        rentBill.getTransactionNo(), outRefundRent,
                        rentFen, rentTotalFen, "退租退款-已付租金");
                rentOk = true;
                remark.append("已付租金退款 ¥").append(rentRefund)
                      .append(" 单号:").append(outRefundRent).append("; ");
                logger.info("退款管理-租金退款成功 refundId={} resp={}", refundId, r2);
            } catch (Exception e) {
                if (e.getMessage() != null && e.getMessage().contains("订单已全额退款")) {
                    // 微信侧已全额退款，视为幂等成功
                    rentOk = true;
                    remark.append("已付租金已退款(微信侧已全额退款，幂等放行) ¥").append(rentRefund).append("; ");
                    logger.warn("退款管理-租金退款幂等成功(订单已全额退款) refundId={}", refundId);
                } else {
                    remark.append("租金退款失败:").append(e.getMessage()).append("; ");
                    logger.error("退款管理-租金微信退款失败 refundId={}", refundId, e);
                }
            }
        }

        // 7. 判定整体状态并更新退租记录
        boolean depositRequired = depositBill != null;
        boolean rentRequired = rentBill != null;
        boolean allSuccess = (!depositRequired || depositOk) && (!rentRequired || rentOk);
        boolean anySuccess = depositOk || rentOk;

        if (!anySuccess) {
            // 全部失败：保留 refund_status=0，允许重试
            return error("微信退款失败 | " + remark);
        }

        // 任一成功就标记已退还（防止重复退已成功的笔），失败明细写 remark 由管理员人工补救
        String finalRemark = (allSuccess ? "微信原路退款成功 | " : "微信退款部分成功，请人工核对剩余金额 | ") + remark;
        LambdaUpdateWrapper<HzCheckoutRecord> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(HzCheckoutRecord::getApplyId, refundId)
                     .set(HzCheckoutRecord::getRefundStatus, "1")
                     .set(HzCheckoutRecord::getRefundTime, new Date())
                     .set(HzCheckoutRecord::getPaymentMethod, "3")   // 3=微信
                     .set(HzCheckoutRecord::getPaymentRemark, finalRemark)
                     .set(HzCheckoutRecord::getUpdateBy, SecurityUtils.getUsername())
                     .set(HzCheckoutRecord::getUpdateTime, new Date());
        checkoutRecordMapper.update(null, updateWrapper);

        if (allSuccess) {
            return AjaxResult.success("微信退款申请成功，预计2分钟内到账", finalRemark);
        } else {
            return AjaxResult.warn("退款部分成功：" + finalRemark);
        }
    }

    /**
     * 提交付款信息
     */
    @PreAuthorize("@ss.hasPermi('gangzhu:refund:payment')")
    @Log(title = "提交付款信息", businessType = BusinessType.UPDATE)
    @PostMapping("/payment")
    public AjaxResult submitPayment(@RequestBody HzRefundApplyVO vo) {
        // 通过退租申请ID查找退租记录
        LambdaUpdateWrapper<HzCheckoutRecord> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(HzCheckoutRecord::getApplyId, vo.getRefundId())
               .set(HzCheckoutRecord::getPaymentMethod, vo.getPaymentMethod())
               .set(HzCheckoutRecord::getPaymentVoucher, vo.getPaymentVoucher())
               .set(HzCheckoutRecord::getPaymentRemark, vo.getPaymentRemark())
               .set(HzCheckoutRecord::getRefundStatus, "1")  // 已退还
               .set(HzCheckoutRecord::getRefundTime, new Date())
               .set(HzCheckoutRecord::getUpdateBy, SecurityUtils.getUsername())
               .set(HzCheckoutRecord::getUpdateTime, new Date());

        int rows = checkoutRecordMapper.update(null, wrapper);
        return toAjax(rows);
    }
}
