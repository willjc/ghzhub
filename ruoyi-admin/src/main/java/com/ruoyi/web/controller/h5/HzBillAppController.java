package com.ruoyi.web.controller.h5;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.system.domain.HzBill;
import com.ruoyi.system.domain.HzBillVO;
import com.ruoyi.system.mapper.HzBillMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * H5用户端 - 账单API
 */
@RestController
@RequestMapping("/h5/app/bill")
public class HzBillAppController extends BaseController {

    @Autowired
    private HzBillMapper billMapper;

    /**
     * 根据合同ID获取押金账单
     */
    @GetMapping("/deposit/{contractId}")
    public AjaxResult getDepositBill(@PathVariable Long contractId) {
        // 查询押金账单（bill_type='1'）
        LambdaQueryWrapper<HzBill> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(HzBill::getContractId, contractId)
               .eq(HzBill::getBillType, "1")  // 1=押金
               .eq(HzBill::getDelFlag, "0")
               .last("LIMIT 1");

        HzBill depositBill = billMapper.selectOne(wrapper);

        if (depositBill == null) {
            return error("未找到押金账单");
        }

        // 组装返回数据
        Map<String, Object> result = new HashMap<>();
        result.put("billId", depositBill.getBillId());
        result.put("billNo", depositBill.getBillNo());
        result.put("contractId", depositBill.getContractId());
        result.put("billType", depositBill.getBillType());
        result.put("period", "押金");
        result.put("status", getStatusText(depositBill.getBillStatus()));
        result.put("amount", depositBill.getBillAmount());
        result.put("dateRange", depositBill.getBillPeriod());
        result.put("billStatus", depositBill.getBillStatus());  // 0=待支付, 1=已支付

        return success(result);
    }

    /**
     * 支付账单
     */
    @PostMapping("/pay")
    public AjaxResult payBill(@RequestBody Map<String, Object> params) {
        try {
            if (params.get("billId") == null || params.get("payAmount") == null) {
                return error("参数不完整：billId 和 payAmount 不能为空");
            }
            Long billId = Long.parseLong(params.get("billId").toString());
            BigDecimal payAmount = new BigDecimal(params.get("payAmount").toString());

            // 查询账单
            HzBill bill = billMapper.selectById(billId);
            if (bill == null) {
                return error("账单不存在");
            }

            // 检查账单状态
            if ("1".equals(bill.getBillStatus())) {
                return error("账单已支付");
            }

            // 检查支付金额
            if (payAmount.compareTo(bill.getUnpaidAmount()) != 0) {
                return error("支付金额不正确");
            }

            // 模拟生成交易流水号
            String transactionNo = "TXN" + DateUtils.dateTimeNow(DateUtils.YYYYMMDDHHMMSS);

            // 更新账单状态
            bill.setBillStatus("1");  // 1=已支付
            bill.setPaidAmount(bill.getPaidAmount().add(payAmount));
            bill.setUnpaidAmount(bill.getUnpaidAmount().subtract(payAmount));
            bill.setPayTime(DateUtils.getTime());
            bill.setPayMethod("online");  // 在线支付
            bill.setTransactionNo(transactionNo);

            int result = billMapper.updateById(bill);

            if (result > 0) {
                Map<String, Object> data = new HashMap<>();
                data.put("billId", billId);
                data.put("transactionNo", transactionNo);
                data.put("payTime", bill.getPayTime());
                data.put("payAmount", payAmount);

                // 返回成功结果
                AjaxResult ajax = success(data);
                ajax.put("msg", "支付成功");
                return ajax;
            } else {
                return error("支付失败");
            }

        } catch (Exception e) {
            logger.error("支付账单失败", e);
            return error("支付失败：" + e.getMessage());
        }
    }

    /**
     * 根据合同ID获取所有账单（支持分页）
     */
    @GetMapping("/list/{contractId}")
    public AjaxResult getBillList(@PathVariable Long contractId,
                                   @RequestParam(required = false) String billType,
                                   @RequestParam(required = false) String billStatus) {
        LambdaQueryWrapper<HzBill> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(HzBill::getContractId, contractId)
               .eq(billType != null, HzBill::getBillType, billType)
               .eq(billStatus != null, HzBill::getBillStatus, billStatus)
               .eq(HzBill::getDelFlag, "0")
               .orderByAsc(HzBill::getBillDate);

        List<HzBill> bills = billMapper.selectList(wrapper);

        return success(bills);
    }

    /**
     * 根据用户ID获取账单列表
     * @param userId 用户ID（tenant_id字段存储的就是user_id）
     * @param billType 账单类型（可选）：1=押金, 2=租金, 3=水费, 4=电费, 5=燃气费, 6=物业费, 7=其他
     * @param billStatus 账单状态（可选）：0=待支付, 1=已支付, 2=部分支付, 3=已逾期, 4=已关闭
     */
    @GetMapping("/user/{userId}")
    public AjaxResult getBillListByUserId(@PathVariable Long userId,
                                          @RequestParam(required = false) String billType,
                                          @RequestParam(required = false) String billStatus) {
        // 使用关联查询，获取完整的账单信息（包含项目名称、楼栋、单元等）
        List<HzBillVO> bills = billMapper.selectBillVOByUserId(userId, billType, billStatus);

        return success(bills);
    }

    /**
     * 批量支付账单
     * @param params 支付参数：{billIds: [1,2,3], payAmount: 100.00}
     */
    @PostMapping("/pay/batch")
    public AjaxResult payBillBatch(@RequestBody Map<String, Object> params) {
        try {
            @SuppressWarnings("unchecked")
            List<Long> billIds = (List<Long>) params.get("billIds");
            BigDecimal payAmount = new BigDecimal(params.get("payAmount").toString());

            if (billIds == null || billIds.isEmpty()) {
                return error("请选择要支付的账单");
            }

            // 查询账单
            LambdaQueryWrapper<HzBill> wrapper = new LambdaQueryWrapper<>();
            wrapper.in(HzBill::getBillId, billIds)
                   .eq(HzBill::getDelFlag, "0");

            List<HzBill> bills = billMapper.selectList(wrapper);

            if (bills.isEmpty()) {
                return error("账单不存在");
            }

            // 验证所有账单的状态和金额
            BigDecimal totalUnpaid = BigDecimal.ZERO;
            for (HzBill bill : bills) {
                if ("1".equals(bill.getBillStatus())) {
                    return error("存在已支付的账单");
                }
                totalUnpaid = totalUnpaid.add(bill.getUnpaidAmount());
            }

            // 检查支付金额
            if (payAmount.compareTo(totalUnpaid) != 0) {
                return error("支付金额不正确，应付：" + totalUnpaid);
            }

            // 模拟生成交易流水号
            String transactionNo = "TXN" + DateUtils.dateTimeNow(DateUtils.YYYYMMDDHHMMSS);
            String payTime = DateUtils.getTime();

            // 更新所有账单状态
            int count = 0;
            for (HzBill bill : bills) {
                bill.setBillStatus("1");  // 1=已支付
                bill.setPaidAmount(bill.getPaidAmount().add(bill.getUnpaidAmount()));
                bill.setUnpaidAmount(BigDecimal.ZERO);
                bill.setPayTime(payTime);
                bill.setPayMethod("online");
                bill.setTransactionNo(transactionNo);

                count += billMapper.updateById(bill);
            }

            if (count > 0) {
                Map<String, Object> data = new HashMap<>();
                data.put("billIds", billIds);
                data.put("transactionNo", transactionNo);
                data.put("payTime", payTime);
                data.put("payAmount", payAmount);
                data.put("billCount", billIds.size());

                AjaxResult ajax = success(data);
                ajax.put("msg", "支付成功");
                return ajax;
            } else {
                return error("支付失败");
            }

        } catch (Exception e) {
            logger.error("批量支付账单失败", e);
            return error("支付失败：" + e.getMessage());
        }
    }

    /**
     * 将账单状态代码转换为文本
     */
    private String getStatusText(String billStatus) {
        switch (billStatus) {
            case "0": return "未支付";
            case "1": return "已支付";
            case "2": return "部分支付";
            case "3": return "已逾期";
            case "4": return "已关闭";
            default: return "未知状态";
        }
    }
}
