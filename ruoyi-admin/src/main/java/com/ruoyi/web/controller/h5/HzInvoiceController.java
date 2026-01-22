package com.ruoyi.web.controller.h5;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.system.domain.HzBill;
import com.ruoyi.system.domain.HzContract;
import com.ruoyi.system.domain.HzHouse;
import com.ruoyi.system.domain.HzInvoice;
import com.ruoyi.system.domain.HzInvoiceApply;
import com.ruoyi.system.domain.HzProject;
import com.ruoyi.system.domain.HzUser;
import com.ruoyi.system.mapper.HzBillMapper;
import com.ruoyi.system.mapper.HzContractMapper;
import com.ruoyi.system.mapper.HzHouseMapper;
import com.ruoyi.system.mapper.HzInvoiceApplyMapper;
import com.ruoyi.system.mapper.HzProjectMapper;
import com.ruoyi.system.service.IHzBillService;
import com.ruoyi.system.service.IHzInvoiceApplyService;
import com.ruoyi.system.service.IHzInvoiceService;
import com.ruoyi.system.service.IHzUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * H5端开票管理Controller
 *
 * @author ruoyi
 */
@RestController
@RequestMapping("/h5/app/invoice")
public class HzInvoiceController extends BaseController
{
    @Autowired
    private IHzInvoiceApplyService invoiceApplyService;

    @Autowired
    private IHzInvoiceService invoiceService;

    @Autowired
    private IHzUserService userService;

    @Autowired
    private IHzBillService billService;

    @Autowired
    private HzContractMapper contractMapper;

    @Autowired
    private HzHouseMapper houseMapper;

    @Autowired
    private HzProjectMapper projectMapper;

    @Autowired
    private HzBillMapper billMapper;

    @Autowired
    private HzInvoiceApplyMapper invoiceApplyMapper;

    /**
     * 获取我的发票申请列表
     */
    @GetMapping("/myList")
    public AjaxResult myList(@RequestParam Long userId)
    {
        HzUser user = userService.selectHzUserById(userId);
        if (user == null)
        {
            return error("用户不存在");
        }

        // tenantId 直接等于 userId
        List<HzInvoiceApply> list = invoiceApplyService.selectInvoiceApplyListByTenantId(userId);
        return success(list);
    }

    /**
     * 获取入住信息（通过账单反查房源/项目信息）
     */
    @GetMapping("/checkinInfo")
    public AjaxResult getCheckinInfo(@RequestParam Long userId)
    {
        // 直接查询用户的可开票账单（已支付且未开票）
        LambdaQueryWrapper<HzBill> billWrapper = new LambdaQueryWrapper<>();
        billWrapper.eq(HzBill::getTenantId, userId)
                .eq(HzBill::getBillStatus, "1") // 1=已支付
                .and(w -> w.eq(HzBill::getInvoiceStatus, "0").or().isNull(HzBill::getInvoiceStatus)) // 0=未开票 或 null
                .eq(HzBill::getDelFlag, "0")
                .orderByDesc(HzBill::getBillPeriod)
                .last("LIMIT 1");
        HzBill bill = billMapper.selectOne(billWrapper);

        if (bill == null)
        {
            // 没有可开票账单，返回空数据但不报错
            Map<String, Object> result = new HashMap<>();
            result.put("community", "");
            result.put("room", "");
            result.put("deposit", "0元");
            result.put("hasBill", false);
            return success(result);
        }

        // 通过账单的合同ID获取房源和项目信息
        HzContract contract = contractMapper.selectById(bill.getContractId());
        if (contract == null)
        {
            return error("未找到关联的合同信息");
        }

        HzHouse house = houseMapper.selectById(contract.getHouseId());
        if (house == null)
        {
            return error("未找到房源信息");
        }

        HzProject project = projectMapper.selectById(house.getProjectId());
        if (project == null)
        {
            return error("未找到项目信息");
        }

        Map<String, Object> result = new HashMap<>();
        result.put("community", project.getProjectName());
        result.put("room", house.getHouseNo());
        result.put("deposit", house.getDeposit() != null ? house.getDeposit().toString() + "元" : "0元");
        result.put("houseId", house.getHouseId());
        result.put("projectId", project.getProjectId());
        result.put("contractId", contract.getContractId());
        result.put("hasBill", true);

        return success(result);
    }

    /**
     * 获取可开票账单列表（已支付且未开票）
     */
    @GetMapping("/availableBills")
    public AjaxResult getAvailableBills(@RequestParam Long userId)
    {
        // 直接查询用户的可开票账单，不依赖合同状态
        LambdaQueryWrapper<HzBill> billWrapper = new LambdaQueryWrapper<>();
        billWrapper.eq(HzBill::getTenantId, userId)
                .eq(HzBill::getBillStatus, "1") // 1=已支付
                .and(w -> w.eq(HzBill::getInvoiceStatus, "0").or().isNull(HzBill::getInvoiceStatus)) // 0=未开票 或 null
                .eq(HzBill::getDelFlag, "0")
                .orderByDesc(HzBill::getBillPeriod);
        List<HzBill> bills = billMapper.selectList(billWrapper);

        return success(bills);
    }

    /**
     * 提交开票申请
     */
    @PostMapping("/apply")
    public AjaxResult submitApply(@RequestBody Map<String, Object> params)
    {
        Long userId = Long.valueOf(params.get("userId").toString());
        Long billId = Long.valueOf(params.get("billId").toString());
        String headType = (String) params.get("headType"); // personal 或 company
        String name = (String) params.get("name"); // 个人名称或企业名称
        String phone = (String) params.get("phone");
        String email = (String) params.get("email");
        String taxNo = (String) params.get("taxNo"); // 税号（企业可选）

        // 检查该账单是否已申请开票（通过bill_ids字段查询）
        LambdaQueryWrapper<HzInvoiceApply> checkWrapper = new LambdaQueryWrapper<>();
        checkWrapper.eq(HzInvoiceApply::getTenantId, userId)
                .like(HzInvoiceApply::getBillIds, String.valueOf(billId))
                .eq(HzInvoiceApply::getDelFlag, "0");
        HzInvoiceApply existApply = invoiceApplyMapper.selectOne(checkWrapper);
        if (existApply != null)
        {
            return error("该账单已申请开票");
        }

        // 获取账单信息
        HzBill bill = billService.selectBillById(billId);
        if (bill == null)
        {
            return error("账单不存在");
        }

        // 校验账单是否属于当前用户（tenantId = userId）
        if (!bill.getTenantId().equals(userId))
        {
            return error("无权操作此账单");
        }

        // 获取用户信息用于填充租户名称
        HzUser user = userService.selectHzUserById(userId);

        // 创建开票申请，tenantId 直接使用 userId
        HzInvoiceApply invoiceApply = new HzInvoiceApply();
        invoiceApply.setTenantId(userId);
        invoiceApply.setTenantName(user != null ? user.getRealName() : name);
        invoiceApply.setApplyNo(invoiceApplyService.generateApplyNo());
        invoiceApply.setBillIds(String.valueOf(billId)); // 存储为字符串
        invoiceApply.setInvoiceType("1"); // 1=增值税普通发票
        invoiceApply.setInvoiceTitle(name);
        invoiceApply.setTaxNo(taxNo);
        invoiceApply.setInvoiceAmount(bill.getBillAmount());
        invoiceApply.setInvoiceContent("房屋租赁费");
        invoiceApply.setEmail(email);
        invoiceApply.setReceiverName(name);
        invoiceApply.setReceiverPhone(phone);
        invoiceApply.setReceiverAddress(email);
        invoiceApply.setApplyTime(DateUtils.getTime());
        invoiceApply.setApplyStatus("1"); // 1=开票中
        invoiceApply.setDelFlag("0");

        int result = invoiceApplyService.insertInvoiceApply(invoiceApply);
        if (result > 0)
        {
            // 更新账单的开票状态为"开票中"
            bill.setInvoiceStatus("1");
            billService.updateBill(bill);

            return success(invoiceApply);
        }
        return error("提交失败");
    }

    /**
     * 获取发票详情
     */
    @GetMapping("/{applyId}")
    public AjaxResult getInfo(@PathVariable("applyId") Long applyId, @RequestParam(required = false) Long userId)
    {
        HzInvoiceApply invoiceApply = invoiceApplyService.selectInvoiceApplyById(applyId);
        if (invoiceApply == null)
        {
            return error("开票申请不存在");
        }

        // 如果提供了userId，校验申请是否属于当前用户（tenantId = userId）
        if (userId != null && !invoiceApply.getTenantId().equals(userId))
        {
            return error("无权查看此申请");
        }

        Map<String, Object> result = new HashMap<>();
        result.put("applyId", invoiceApply.getApplyId());
        result.put("applyNo", invoiceApply.getApplyNo());
        result.put("invoiceType", invoiceApply.getInvoiceType());
        result.put("invoiceTitle", invoiceApply.getInvoiceTitle());
        result.put("taxNo", invoiceApply.getTaxNo());
        result.put("invoiceAmount", invoiceApply.getInvoiceAmount());
        result.put("invoiceContent", invoiceApply.getInvoiceContent());
        result.put("receiverName", invoiceApply.getReceiverName());
        result.put("receiverPhone", invoiceApply.getReceiverPhone());
        result.put("receiverEmail", invoiceApply.getEmail() != null ? invoiceApply.getEmail() : invoiceApply.getReceiverAddress());
        result.put("applyStatus", invoiceApply.getApplyStatus());
        result.put("createTime", invoiceApply.getCreateTime());

        // 获取关联账单信息（从bill_ids中获取第一个账单ID）
        if (invoiceApply.getBillIds() != null && !invoiceApply.getBillIds().isEmpty())
        {
            String[] billIdArray = invoiceApply.getBillIds().split(",");
            if (billIdArray.length > 0)
            {
                try
                {
                    Long firstBillId = Long.valueOf(billIdArray[0].trim());
                    HzBill bill = billService.selectBillById(firstBillId);
                    if (bill != null)
                    {
                        result.put("bill", bill);
                    }
                }
                catch (NumberFormatException e)
                {
                    // 忽略解析错误
                }
            }
        }

        // 如果已开票，通过apply_id反向查询发票信息
        if ("2".equals(invoiceApply.getApplyStatus()))
        {
            HzInvoice invoice = invoiceService.selectInvoiceByApplyId(applyId);
            if (invoice != null)
            {
                result.put("invoice", invoice);
            }
        }

        return success(result);
    }
}
