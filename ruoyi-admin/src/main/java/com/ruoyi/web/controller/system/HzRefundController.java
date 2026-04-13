package com.ruoyi.web.controller.system;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.enums.BusinessType;
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
        startPage();
        List<HzRefundApplyVO> list = refundService.selectRefundList(
                query.getRefundNo(),
                query.getContractNo(),
                query.getRefundStatus()
        );
        return getDataTable(list);
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
     * 微信原路退款
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
        if (apply.getRefundAmount() == null || apply.getRefundAmount().compareTo(BigDecimal.ZERO) <= 0) {
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

        // 3. 查押金账单（bill_type='1' 押金，pay_method='wechat'，bill_status='1' 已支付）
        LambdaQueryWrapper<HzBill> billQuery = new LambdaQueryWrapper<>();
        billQuery.eq(HzBill::getContractId, apply.getContractId())
                 .eq(HzBill::getBillType, "1")
                 .eq(HzBill::getPayMethod, "wechat")
                 .eq(HzBill::getBillStatus, "1")
                 .eq(HzBill::getDelFlag, "0")
                 .last("LIMIT 1");
        HzBill depositBill = billMapper.selectOne(billQuery);
        if (depositBill == null) {
            return error("未找到微信支付的押金账单，该押金可能未通过微信支付，无法原路退款");
        }
        if (depositBill.getTransactionNo() == null || depositBill.getTransactionNo().isEmpty()) {
            return error("押金账单缺少微信交易号，无法发起退款");
        }

        // 4. 计算金额（元转分）
        int refundFen = apply.getRefundAmount().multiply(new BigDecimal("100")).intValue();
        int totalFen = depositBill.getBillAmount().multiply(new BigDecimal("100")).intValue();
        String outRefundNo = "REFUND" + System.currentTimeMillis() + refundId;

        // 5. 调用微信退款 API
        Map<String, Object> refundResult;
        try {
            refundResult = wechatPayService.wechatRefund(
                    depositBill.getTransactionNo(), outRefundNo,
                    refundFen, totalFen, "退租退款");
        } catch (Exception e) {
            logger.error("微信退款失败，refundId={}", refundId, e);
            return error("微信退款失败：" + e.getMessage());
        }

        // 6. 更新退租记录为已退还
        LambdaUpdateWrapper<HzCheckoutRecord> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(HzCheckoutRecord::getApplyId, refundId)
                     .set(HzCheckoutRecord::getRefundStatus, "1")
                     .set(HzCheckoutRecord::getRefundTime, new Date())
                     .set(HzCheckoutRecord::getPaymentMethod, "3")   // 3=微信
                     .set(HzCheckoutRecord::getPaymentRemark,
                             "微信原路退款，退款单号：" + outRefundNo)
                     .set(HzCheckoutRecord::getUpdateBy, SecurityUtils.getUsername())
                     .set(HzCheckoutRecord::getUpdateTime, new Date());
        checkoutRecordMapper.update(null, updateWrapper);

        return AjaxResult.success("微信退款申请成功，预计2分钟内到账", refundResult);
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
