package com.ruoyi.web.controller.system;

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
import com.ruoyi.system.domain.HzContract;
import com.ruoyi.system.service.IHzContractService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletResponse;
import java.util.List;

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
    public void export(HttpServletResponse response, HzContract contract)
    {
        List<HzContract> list = contractService.selectContractList(contract);
        ExcelUtil<HzContract> util = new ExcelUtil<HzContract>(HzContract.class);
        util.exportExcel(response, list, "合同数据");
    }

    /**
     * 获取合同详细信息
     */
    @PreAuthorize("@ss.hasPermi('gangzhu:contract:query')")
    @GetMapping(value = "/{contractId}")
    public AjaxResult getInfo(@PathVariable("contractId") Long contractId)
    {
        return success(contractService.selectContractById(contractId));
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
}
