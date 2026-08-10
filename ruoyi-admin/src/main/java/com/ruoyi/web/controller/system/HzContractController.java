package com.ruoyi.web.controller.system;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.constant.HttpStatus;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.PageDomain;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.core.page.TableSupport;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.system.domain.HzBill;
import com.ruoyi.system.domain.HzContract;
import com.ruoyi.system.domain.HzCheckoutApply;
import com.ruoyi.system.domain.HzDocument;
import com.ruoyi.system.mapper.HzBillMapper;
import com.ruoyi.system.mapper.HzCheckoutApplyMapper;
import com.ruoyi.system.service.EsignService;
import com.ruoyi.system.service.IHzContractService;
import com.ruoyi.system.service.IHzDocumentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletResponse;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 合同管理Controller
 *
 * @author ruoyi
 */
@RestController("adminContractController")
@RequestMapping("/system/contract")
public class HzContractController extends BaseController
{
    @Autowired
    private IHzContractService contractService;

    @Autowired
    private HzBillMapper billMapper;

    @Autowired
    private HzCheckoutApplyMapper checkoutApplyMapper;

    @Autowired
    private IHzDocumentService documentService;

    @Autowired
    private EsignService esignService;

    /**
     * 查询合同列表
     */
    @PreAuthorize("@ss.hasPermi('gangzhu:contract:list')")
    @GetMapping("/list")
    public TableDataInfo list(HzContract contract)
    {
        PageDomain pageDomain = TableSupport.buildPageRequest();
        Integer pageNum = pageDomain.getPageNum();
        Integer pageSize = pageDomain.getPageSize();

        // 使用默认值：第1页，每页10条
        if (pageNum == null || pageNum <= 0) {
            pageNum = 1;
        }
        if (pageSize == null || pageSize <= 0) {
            pageSize = 10;
        }

        IPage<HzContract> page = contractService.selectContractPage(contract, pageNum, pageSize);

        // 回填退租日期（仅已完成退租的合同有值），供列表展示
        backfillCheckoutInfo(page.getRecords());

        // 手动构建分页响应
        TableDataInfo rspData = new TableDataInfo();
        rspData.setCode(HttpStatus.SUCCESS);
        rspData.setMsg("查询成功");
        rspData.setRows(page.getRecords());
        rspData.setTotal(page.getTotal());
        return rspData;
    }

    /**
     * 导出合同列表
     */
    @PreAuthorize("@ss.hasPermi('gangzhu:contract:export')")
    @Log(title = "合同管理", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, HzContract contract,
                       @RequestParam(required = false) Long[] contractIds)
    {
        List<HzContract> list;
        if (contractIds != null && contractIds.length > 0) {
            // 勾选导出：仅导出指定 ids 的合同
            list = contractService.selectContractListByIds(contractIds);
        } else {
            // 全量导出：按搜索条件查
            list = contractService.selectContractList(contract);
        }
        // 回填退租信息（仅已完成退租 apply_status=5；未退租的合同留空）
        backfillCheckoutInfo(list);
        ExcelUtil<HzContract> util = new ExcelUtil<HzContract>(HzContract.class);
        util.exportExcel(response, list, "合同数据");
    }

    /**
     * 为导出的合同列表回填退租日期、应退押金、应退租金、应退总额
     * 数据来源：退租申请表 hz_checkout_apply，仅取已完成退租（apply_status=5）的记录，
     * 同一合同存在多条时取最新一条（applyId 最大）；退租日期优先取实际退租日期，为空回退计划退租日期。
     */
    private void backfillCheckoutInfo(List<HzContract> list)
    {
        if (list == null || list.isEmpty()) {
            return;
        }
        List<Long> contractIds = list.stream()
                .map(HzContract::getContractId)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
        if (contractIds.isEmpty()) {
            return;
        }
        List<HzCheckoutApply> applies = checkoutApplyMapper.selectList(
                new LambdaQueryWrapper<HzCheckoutApply>()
                        .in(HzCheckoutApply::getContractId, contractIds)
                        .eq(HzCheckoutApply::getApplyStatus, "5")
                        .eq(HzCheckoutApply::getDelFlag, "0")
                        .orderByAsc(HzCheckoutApply::getApplyId));
        if (applies.isEmpty()) {
            return;
        }
        // 升序遍历，覆盖式放入 map，最终保留同一合同下 applyId 最大（最新）的一条
        Map<Long, HzCheckoutApply> applyMap = new HashMap<>();
        for (HzCheckoutApply a : applies) {
            applyMap.put(a.getContractId(), a);
        }
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        for (HzContract c : list) {
            HzCheckoutApply a = applyMap.get(c.getContractId());
            if (a == null) {
                continue;
            }
            Date checkoutDate = a.getActualCheckoutDate() != null ? a.getActualCheckoutDate() : a.getPlanCheckoutDate();
            c.setCheckoutDate(checkoutDate != null ? sdf.format(checkoutDate) : null);
            c.setCheckoutDepositRefund(a.getDepositRefund());
            c.setCheckoutRentRefund(a.getRentRefund());
            c.setCheckoutRefundAmount(a.getRefundAmount());
        }
    }

    /**
     * 获取合同详细信息
     */
    @PreAuthorize("@ss.hasPermi('gangzhu:contract:query')")
    @GetMapping(value = "/{contractId}")
    public AjaxResult getInfo(@PathVariable("contractId") Long contractId)
    {
        HzContract contract = contractService.selectContractById(contractId);
        // 回填退租日期，供详情弹窗展示
        if (contract != null) {
            backfillCheckoutInfo(java.util.Collections.singletonList(contract));
        }
        return success(contract);
    }

    /**
     * 新增合同
     */
    @PreAuthorize("@ss.hasPermi('gangzhu:contract:add')")
    @Log(title = "合同管理", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody HzContract contract)
    {
        return toAjax(contractService.insertContract(contract));
    }

    /**
     * 修改合同
     */
    @PreAuthorize("@ss.hasPermi('gangzhu:contract:edit')")
    @Log(title = "合同管理", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody HzContract contract)
    {
        return toAjax(contractService.updateContract(contract));
    }

    /**
     * 删除合同
     */
    @PreAuthorize("@ss.hasPermi('gangzhu:contract:remove')")
    @Log(title = "合同管理", businessType = BusinessType.DELETE)
    @DeleteMapping("/{contractIds}")
    public AjaxResult remove(@PathVariable Long[] contractIds)
    {
        for (Long contractId : contractIds) {
            contractService.deleteContractById(contractId);
        }
        return success();
    }

    /**
     * 审核合同
     */
    @PreAuthorize("@ss.hasPermi('gangzhu:contract:edit')")
    @Log(title = "合同审核", businessType = BusinessType.UPDATE)
    @PostMapping("/approve")
    public AjaxResult approve(@RequestBody HzContract contract)
    {
        // 验证合同ID
        if (contract.getContractId() == null) {
            return error("合同ID不能为空");
        }

        // 获取原合同
        HzContract existContract = contractService.selectContractById(contract.getContractId());
        if (existContract == null) {
            return error("合同不存在");
        }

        // 只能审核草稿状态的合同
        if (!"0".equals(existContract.getContractStatus())) {
            return error("只能审核草稿状态的合同");
        }

        // 更新合同状态为已签署,保存合同附件
        existContract.setContractStatus("2");
        if (contract.getContractFile() != null && !contract.getContractFile().isEmpty()) {
            existContract.setContractFile(contract.getContractFile());
        }
        if (contract.getRemark() != null && !contract.getRemark().isEmpty()) {
            existContract.setRemark(contract.getRemark());
        }

        return toAjax(contractService.updateContract(existContract));
    }

    // ==================== 合同详情扩展接口 ====================

    /**
     * 查询 e签宝 合同模板所有控件定义（管理端调试用，用于核对传参是否完整）
     */
    @GetMapping("/esign-template")
    @PreAuthorize("@ss.hasPermi('gangzhu:contract:query')")
    public AjaxResult getEsignTemplateComponents() {
        try {
            return success(esignService.queryTemplateDetail());
        } catch (Exception e) {
            return error("查询模板失败：" + e.getMessage());
        }
    }

    /**
     * 合同详情 - 账单与缴费记录（管理端专用）
     */
    @GetMapping("/{contractId}/bills")
    @PreAuthorize("@ss.hasPermi('gangzhu:contract:query')")
    public AjaxResult getContractBills(@PathVariable Long contractId) {
        HzContract contract = contractService.selectContractById(contractId);
        if (contract == null) return error("合同不存在");

        List<HzBill> bills = billMapper.selectList(
            new LambdaQueryWrapper<HzBill>()
                .eq(HzBill::getContractId, contractId)
                .eq(HzBill::getDelFlag, "0")
                .orderByAsc(HzBill::getBillType)
                .orderByAsc(HzBill::getBillDate)
        );

        Map<String, Object> result = new HashMap<>();
        result.put("contractId",  contractId);
        result.put("tenantName",  contract.getTenantName());
        result.put("tenantPhone", contract.getTenantPhone());
        result.put("tenantIdCard",contract.getTenantIdCard());
        result.put("bills", bills.stream().map(b -> {
            Map<String, Object> item = new HashMap<>();
            item.put("billId",        b.getBillId());
            item.put("billNo",        b.getBillNo());
            item.put("billType",      b.getBillType());
            item.put("billTypeText",  billTypeText(b.getBillType()));
            item.put("billAmount",    b.getBillAmount());
            item.put("paidAmount",    b.getPaidAmount());
            item.put("unpaidAmount",  b.getUnpaidAmount());
            item.put("billStatus",    b.getBillStatus());
            item.put("billStatusText",billStatusText(b.getBillStatus()));
            item.put("payTime",       b.getPayTime());
            item.put("payMethod",     b.getPayMethod());
            item.put("transactionNo", b.getTransactionNo());
            item.put("billDate",      b.getBillDate());
            item.put("dueDate",       b.getDueDate());
            item.put("billPeriod",    b.getBillPeriod());
            item.put("billSeq",       b.getBillSeq());
            item.put("periodStartDate", b.getPeriodStartDate());
            item.put("periodEndDate",   b.getPeriodEndDate());
            return item;
        }).collect(Collectors.toList()));
        return success(result);
    }

    /**
     * 合同详情 - 用户上传资料列表（管理端专用）
     */
    @GetMapping("/{contractId}/documents")
    @PreAuthorize("@ss.hasPermi('gangzhu:contract:query')")
    public AjaxResult getContractDocuments(@PathVariable Long contractId) {
        HzContract contract = contractService.selectContractById(contractId);
        if (contract == null) return error("合同不存在");
        List<HzDocument> docs = documentService.selectDocumentListByContractId(contractId);
        return success(docs);
    }

    private String billTypeText(String type) {
        if (type == null) return "";
        switch (type) {
            case "1": return "押金";
            case "2": return "租金";
            case "3": return "水费";
            case "4": return "电费";
            default:  return "其他";
        }
    }

    private String billStatusText(String status) {
        if (status == null) return "";
        switch (status) {
            case "0": return "待支付";
            case "1": return "已支付";
            case "2": return "部分支付";
            case "3": return "已逾期";
            case "4": return "已关闭";
            default:  return "未知";
        }
    }

    /**
     * 获取合同电子 PDF 查看链接（管理端专用，实时刷新 e签宝 临时链接）
     */
    @GetMapping("/{contractId}/pdf-url")
    @PreAuthorize("@ss.hasPermi('gangzhu:contract:query')")
    public AjaxResult getContractPdfUrl(@PathVariable Long contractId) {
        HzContract contract = contractService.selectContractById(contractId);
        if (contract == null) return error("合同不存在");

        String flowId = contract.getEsignFlowId();
        if (flowId != null && !flowId.isEmpty()) {
            try {
                String freshUrl = esignService.getSignedPdfUrl(flowId);
                return AjaxResult.success("操作成功", (Object) freshUrl);
            } catch (Exception e) {
                logger.error("获取合同PDF链接失败，contractId={}, flowId={}", contractId, flowId, e);
                return error("获取 PDF 链接失败：" + e.getMessage());
            }
        }

        // 无 flowId：尝试 contractContent 是否本身为 URL（旧数据）
        String content = contract.getContractContent();
        if (content != null && content.startsWith("http")) {
            return AjaxResult.success("操作成功", (Object) content);
        }

        return error("该合同暂无电子 PDF");
    }
}
