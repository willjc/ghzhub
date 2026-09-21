package com.ruoyi.web.controller.system;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.PageUtils;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.common.utils.poi.ExcelUtil;
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

import jakarta.servlet.http.HttpServletResponse;
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

    private static final int MAX_EXPORT_ROWS = 10000;

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
        return selectRefunds(PageUtils.getPage(), query);
    }

    /**
     * 导出退款列表
     */
    @PreAuthorize("@ss.hasPermi('gangzhu:refund:export')")
    @Log(title = "退款管理", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, HzRefundApplyVO query) {
        TableDataInfo data = selectRefunds(new Page<>(1, MAX_EXPORT_ROWS), query);
        if (data.getTotal() > MAX_EXPORT_ROWS) {
            throw new ServiceException("导出数据超过10000条，请增加筛选条件后重试");
        }
        List<HzRefundApplyVO> list = data.getRows().stream()
                .map(HzRefundApplyVO.class::cast)
                .toList();
        ExcelUtil<HzRefundApplyVO> util = new ExcelUtil<>(HzRefundApplyVO.class);
        util.exportExcel(response, list, "退款管理数据");
    }

    private TableDataInfo selectRefunds(Page<HzCheckoutApply> page, HzRefundApplyVO query) {
        return refundService.selectRefundList(page,
                query.getRefundNo(),
                query.getContractNo(),
                query.getRefundStatus(),
                query.getApproveStatus(),
                query.getProjectId(),
                query.getRefundType(),
                query.getTenantName(),
                query.getBeginApplyTime(),
                query.getEndApplyTime(),
                query.getBeginApproveTime(),
                query.getEndApproveTime()
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
     * - 两笔互相独立记录状态；部分成功时只允许重试失败款项
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

        // 2. 防重：整体完成后禁止重复操作；部分完成时仅重试失败款项
        HzCheckoutRecord record = checkoutRecordMapper.selectByApplyId(refundId);
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
            // 用原生 SQL 绕过全局逻辑删除：失效/退租合同的账单已被软删(del_flag=2)
            HzBill guessBill = billMapper.selectWechatDepositBillForRefund(apply.getContractId());
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
        boolean depositRequired = depositRefund.compareTo(BigDecimal.ZERO) > 0;
        boolean rentRequired = rentRefund.compareTo(BigDecimal.ZERO) > 0;
        boolean depositOk = !depositRequired || "1".equals(record.getDepositRefundStatus());
        boolean rentOk = !rentRequired || "1".equals(record.getRentRefundStatus());

        // 4. 查押金账单（bill_type='1' 押金，wechat 已支付）
        HzBill depositBill = null;
        if (depositRequired && !depositOk) {
            // 用原生 SQL 绕过全局逻辑删除：失效/退租合同的账单已被软删(del_flag=2)
            depositBill = billMapper.selectWechatDepositBillForRefund(apply.getContractId());
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
        if (rentRequired && !rentOk) {
            // 用原生 SQL 绕过全局逻辑删除：失效/退租合同的账单已被软删(del_flag=2)
            List<HzBill> rentBills = billMapper.selectWechatRentBillsForRefund(apply.getContractId());
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
        StringBuilder remark = new StringBuilder();
        if ("2".equals(record.getRefundStatus()) && record.getPaymentRemark() != null) {
            remark.append("上次:").append(truncate(record.getPaymentRemark(), 180)).append(" | 本次:");
        }
        if (depositRequired && depositOk) {
            remark.append("押金此前已退款，本次跳过; ");
        }
        if (rentRequired && rentOk) {
            remark.append("租金此前已退款，本次跳过; ");
        }

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
                    remark.append("押金退款失败:").append(truncate(e.getMessage(), 150)).append("; ");
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
                    remark.append("租金退款失败:").append(truncate(e.getMessage(), 150)).append("; ");
                    logger.error("退款管理-租金微信退款失败 refundId={}", refundId, e);
                }
            }
        }

        // 7. 按押金、租金各自结果保存进度；只有全部成功才标记整体已退还
        String refundStatus = HzCheckoutRecord.resolveRefundStatus(
                depositRequired, depositOk, rentRequired, rentOk);
        boolean allSuccess = "1".equals(refundStatus);
        boolean anySuccess = "2".equals(refundStatus);
        String finalRemark = (allSuccess ? "微信原路退款成功 | "
                : anySuccess ? "微信退款部分成功，可重试失败款项 | " : "微信退款失败 | ") + remark;
        // 兜底截断，避免超出 payment_remark(varchar 500) 长度导致写库失败
        finalRemark = truncate(finalRemark, 480);
        Date now = new Date();
        LambdaUpdateWrapper<HzCheckoutRecord> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(HzCheckoutRecord::getApplyId, refundId)
                     .set(HzCheckoutRecord::getRefundStatus, refundStatus)
                     .set(HzCheckoutRecord::getDepositRefundStatus, depositOk ? "1" : "0")
                     .set(HzCheckoutRecord::getRentRefundStatus, rentOk ? "1" : "0")
                     .set(HzCheckoutRecord::getRefundTime, allSuccess ? now : null)
                     .set(HzCheckoutRecord::getPaymentMethod, "3")   // 3=微信
                     .set(HzCheckoutRecord::getPaymentRemark, finalRemark)
                     .set(HzCheckoutRecord::getUpdateBy, SecurityUtils.getUsername())
                     .set(HzCheckoutRecord::getUpdateTime, now);
        checkoutRecordMapper.update(null, updateWrapper);

        if (allSuccess) {
            return AjaxResult.success("微信退款申请成功，预计2分钟内到账");
        }
        if (anySuccess) {
            return AjaxResult.success("退款部分成功，可在补足商户余额后重试失败款项",
                    Map.of("refundStatus", "2"));
        }
        return error("微信退款失败，请稍后重试；如多次失败请联系管理员在微信商户平台核对");
    }

    /**
     * 截断字符串，避免超出数据库字段长度。
     *
     * @param s      原始字符串
     * @param maxLen 最大保留长度
     * @return 截断后的字符串（null 转为空串）
     */
    private String truncate(String s, int maxLen) {
        if (s == null) {
            return "";
        }
        return s.length() > maxLen ? s.substring(0, maxLen) : s;
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
               .set(HzCheckoutRecord::getDepositRefundStatus, "1")
               .set(HzCheckoutRecord::getRentRefundStatus, "1")
               .set(HzCheckoutRecord::getRefundTime, new Date())
               .set(HzCheckoutRecord::getUpdateBy, SecurityUtils.getUsername())
               .set(HzCheckoutRecord::getUpdateTime, new Date());

        int rows = checkoutRecordMapper.update(null, wrapper);
        return toAjax(rows);
    }
}
