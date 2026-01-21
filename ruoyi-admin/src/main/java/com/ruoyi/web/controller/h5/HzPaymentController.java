package com.ruoyi.web.controller.h5;

import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.system.domain.HzBill;
import com.ruoyi.system.domain.HzPayment;
import com.ruoyi.system.domain.HzTenant;
import com.ruoyi.system.domain.HzTransaction;
import com.ruoyi.system.service.IHzBillService;
import com.ruoyi.system.service.IHzPaymentService;
import com.ruoyi.system.service.IHzTenantService;
import com.ruoyi.system.service.IHzTransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

/**
 * H5端支付管理Controller
 *
 * @author ruoyi
 */
@RestController
@RequestMapping("/h5/payment")
public class HzPaymentController extends BaseController {

    @Autowired
    private IHzPaymentService paymentService;

    @Autowired
    private IHzBillService billService;

    @Autowired
    private IHzTenantService tenantService;

    @Autowired
    private IHzTransactionService transactionService;

    /**
     * 创建支付订单
     */
    @PostMapping("/create")
    public AjaxResult createPayment(@RequestBody HzPayment payment) {
        // TODO: 从登录态获取userId
        Long userId = 1L;
        HzTenant tenant = tenantService.selectTenantByUserId(userId);
        if (tenant == null) {
            return error("请先完善租户信息");
        }

        // 校验账单是否存在
        HzBill bill = billService.selectBillById(payment.getBillId());
        if (bill == null) {
            return error("账单不存在");
        }

        // 校验账单是否属于当前租户
        if (!bill.getTenantId().equals(tenant.getTenantId())) {
            return error("无权操作此账单");
        }

        // 校验账单状态
        if ("1".equals(bill.getBillStatus())) {
            return error("账单已支付");
        }
        if ("4".equals(bill.getBillStatus())) {
            return error("账单已关闭");
        }

        // 校验支付金额
        if (payment.getPaymentAmount() == null || payment.getPaymentAmount().compareTo(BigDecimal.ZERO) <= 0) {
            return error("支付金额必须大于0");
        }
        if (payment.getPaymentAmount().compareTo(bill.getUnpaidAmount()) > 0) {
            return error("支付金额不能大于未支付金额");
        }

        // 创建支付记录
        int result = paymentService.insertPayment(payment);
        if (result > 0) {
            // 创建交易流水
            HzTransaction transaction = new HzTransaction();
            transaction.setBillId(bill.getBillId());
            transaction.setPaymentId(payment.getPaymentId());
            transaction.setTenantId(tenant.getTenantId());
            transaction.setTransactionType("1"); // 支付
            transaction.setTransactionAmount(payment.getPaymentAmount());
            transaction.setPayChannel(payment.getPaymentMethod());
            transactionService.insertTransaction(transaction);

            return success(payment);
        }
        return error("创建支付订单失败");
    }

    /**
     * 支付回调处理 (模拟)
     * TODO: 实际应该对接港区支付接口
     */
    @PostMapping("/callback")
    public AjaxResult paymentCallback(@RequestBody HzPayment payment) {
        // 更新支付记录状态
        HzPayment existPayment = paymentService.selectPaymentById(payment.getPaymentId());
        if (existPayment == null) {
            return error("支付记录不存在");
        }

        existPayment.setPaymentStatus(payment.getPaymentStatus());
        existPayment.setTransactionNo(payment.getTransactionNo());
        paymentService.updatePayment(existPayment);

        // 如果支付成功,更新账单状态
        if ("2".equals(payment.getPaymentStatus())) {
            HzBill bill = billService.selectBillById(existPayment.getBillId());
            if (bill != null) {
                // 更新账单已支付金额和未支付金额
                BigDecimal paidAmount = bill.getPaidAmount().add(existPayment.getPaymentAmount());
                BigDecimal unpaidAmount = bill.getBillAmount().subtract(paidAmount);

                bill.setPaidAmount(paidAmount);
                bill.setUnpaidAmount(unpaidAmount);

                // 判断账单是否全部支付
                if (unpaidAmount.compareTo(BigDecimal.ZERO) == 0) {
                    bill.setBillStatus("1"); // 已支付
                } else if (paidAmount.compareTo(BigDecimal.ZERO) > 0) {
                    bill.setBillStatus("2"); // 部分支付
                }

                billService.updateBill(bill);
            }

            // 更新交易流水状态
            List<HzTransaction> transactions = transactionService.selectTransactionListByBillId(existPayment.getBillId());
            for (HzTransaction transaction : transactions) {
                if (transaction.getPaymentId().equals(existPayment.getPaymentId())) {
                    transaction.setTransactionStatus("1"); // 成功
                    transaction.setThirdPartyNo(payment.getTransactionNo());
                    transactionService.updateTransaction(transaction);
                    break;
                }
            }
        }

        return success();
    }

    /**
     * 查询账单的支付记录列表
     */
    @GetMapping("/list/{billId}")
    public AjaxResult list(@PathVariable("billId") Long billId) {
        List<HzPayment> list = paymentService.selectPaymentListByBillId(billId);
        return success(list);
    }

    /**
     * 获取支付记录详情
     */
    @GetMapping("/{paymentId}")
    public AjaxResult getInfo(@PathVariable("paymentId") Long paymentId) {
        HzPayment payment = paymentService.selectPaymentById(paymentId);
        return success(payment);
    }
}
