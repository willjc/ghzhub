package com.ruoyi.web.controller.system;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.system.domain.HzBill;
import com.ruoyi.system.domain.HzBillVO;
import com.ruoyi.system.mapper.HzBillMapper;
import com.ruoyi.system.service.IHzBillService;
import com.ruoyi.system.service.IHzHouseOrderService;
import com.ruoyi.system.service.WechatPayService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletResponse;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 账单管理Controller
 *
 * @author ruoyi
 */
@RestController("adminBillController")
@RequestMapping("/system/bill")
public class HzBillController extends BaseController
{
    @Autowired
    private IHzBillService billService;

    @Autowired
    private HzBillMapper billMapper;

    @Autowired
    private WechatPayService wechatPayService;

    @Autowired
    private IHzHouseOrderService houseOrderService;

    /**
     * 查询账单列表
     */
    @PreAuthorize("@ss.hasPermi('gangzhu:bill:list')")
    @GetMapping("/list")
    public TableDataInfo list(HzBill bill)
    {
        IPage<HzBillVO> page = billService.selectBillVOPage(bill);
        TableDataInfo rspData = new TableDataInfo();
        rspData.setRows(page.getRecords());
        rspData.setTotal(page.getTotal());
        return rspData;
    }

    /**
     * 导出账单列表
     */
    @PreAuthorize("@ss.hasPermi('gangzhu:bill:export')")
    @Log(title = "账单管理", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, HzBill bill)
    {
        List<HzBill> list = billService.selectBillList(bill);
        ExcelUtil<HzBill> util = new ExcelUtil<HzBill>(HzBill.class);
        util.exportExcel(response, list, "账单数据");
    }

    /**
     * 获取账单详细信息
     */
    @PreAuthorize("@ss.hasPermi('gangzhu:bill:query')")
    @GetMapping(value = "/{billId}")
    public AjaxResult getInfo(@PathVariable("billId") Long billId)
    {
        return success(billService.selectBillVOById(billId));
    }

    /**
     * 新增账单
     */
    @PreAuthorize("@ss.hasPermi('gangzhu:bill:add')")
    @Log(title = "账单管理", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody HzBill bill)
    {
        return toAjax(billService.insertBill(bill));
    }

    /**
     * 修改账单
     */
    @PreAuthorize("@ss.hasPermi('gangzhu:bill:edit')")
    @Log(title = "账单管理", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody HzBill bill)
    {
        return toAjax(billService.updateBill(bill));
    }

    /**
     * 删除账单
     */
    @PreAuthorize("@ss.hasPermi('gangzhu:bill:remove')")
    @Log(title = "账单管理", businessType = BusinessType.DELETE)
    @DeleteMapping("/{billIds}")
    public AjaxResult remove(@PathVariable Long[] billIds)
    {
        int count = 0;
        for (Long billId : billIds)
        {
            count += billService.deleteBillById(billId);
        }
        return toAjax(count);
    }

    /**
     * 手工同步微信支付结果（补单）。
     * <p>使用场景：微信支付后台查到该账单已支付、但本地仍为未支付（回调丢失、定时任务未及时补回）。</p>
     * <p>查单优先级：last_out_trade_no（实际下到微信、可能带 -R- 后缀） &gt; bill_no（老账单兼容）。</p>
     */
    @PreAuthorize("@ss.hasPermi('gangzhu:bill:edit')")
    @Log(title = "账单管理-手工补单", businessType = BusinessType.UPDATE)
    @PostMapping("/manualSyncWechatPay/{billId}")
    public AjaxResult manualSyncWechatPay(@PathVariable("billId") Long billId)
    {
        HzBill bill = billMapper.selectById(billId);
        if (bill == null || "2".equals(bill.getDelFlag())) {
            return error("账单不存在");
        }
        if ("1".equals(bill.getBillStatus())) {
            Map<String, Object> data = new HashMap<>();
            data.put("paid", true);
            data.put("billStatus", "1");
            data.put("message", "账单已为已支付状态，无需补单");
            return success(data);
        }

        // 选择上报微信的 out_trade_no
        String lookupOutTradeNo;
        if (bill.getLastOutTradeNo() != null && !bill.getLastOutTradeNo().isEmpty()) {
            lookupOutTradeNo = bill.getLastOutTradeNo();
        } else if (bill.getBillNo() != null && !bill.getBillNo().isEmpty()) {
            lookupOutTradeNo = bill.getBillNo();
        } else {
            return error("账单无 bill_no 且无 last_out_trade_no，无法查微信");
        }

        try {
            Map<String, Object> wxResult = wechatPayService.queryByOutTradeNo(lookupOutTradeNo);
            String tradeState = (String) wxResult.get("trade_state");
            String transactionId = (String) wxResult.get("transaction_id");

            Map<String, Object> data = new HashMap<>();
            data.put("queriedOutTradeNo", lookupOutTradeNo);
            data.put("tradeState", tradeState);
            data.put("transactionId", transactionId);

            if (!"SUCCESS".equals(tradeState)) {
                data.put("paid", false);
                data.put("message", "微信侧亦未支付，不补单。trade_state=" + tradeState);
                return success(data);
            }

            // 微信已支付 → 本地补单
            bill.setBillStatus("1");
            bill.setPaidAmount(bill.getBillAmount());
            bill.setUnpaidAmount(BigDecimal.ZERO);
            bill.setPayTime(DateUtils.getTime());
            bill.setPayMethod("wechat");
            bill.setTransactionNo(transactionId);
            billMapper.updateById(bill);

            // 押金账单 → 推进订单状态
            if ("1".equals(bill.getBillType()) && bill.getOrderNo() != null && !bill.getOrderNo().isEmpty()) {
                try {
                    houseOrderService.onDepositPaid(bill.getOrderNo());
                } catch (Exception e) {
                    logger.warn("手工补单-onDepositPaid 异常，不影响主流程 billId={}: {}", bill.getBillId(), e.getMessage());
                }
            }
            // 押金/首期租金 → 推进合同到履行中
            if ("1".equals(bill.getBillType()) || "2".equals(bill.getBillType())) {
                try {
                    houseOrderService.tryAdvanceContractToFulfilling(bill.getContractId());
                } catch (Exception e) {
                    logger.warn("手工补单-tryAdvanceContractToFulfilling 异常，不影响主流程 billId={}: {}", bill.getBillId(), e.getMessage());
                }
            }

            data.put("paid", true);
            data.put("billStatus", "1");
            data.put("message", "补单成功");
            logger.info("[ManualSyncWechatPay] 补单成功 billId={}, billNo={}, lastOutTradeNo={}, transactionId={}",
                    bill.getBillId(), bill.getBillNo(), bill.getLastOutTradeNo(), transactionId);
            return success(data);
        } catch (Exception e) {
            logger.error("[ManualSyncWechatPay] 查微信失败 billId={}, lookupOutTradeNo={}", billId, lookupOutTradeNo, e);
            return error("查微信失败：" + e.getMessage());
        }
    }
}
