package com.ruoyi.gangzhu.policy.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.constant.HttpStatus;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.common.utils.PageUtils;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.gangzhu.policy.domain.HzPolicy;
import com.ruoyi.gangzhu.policy.service.IHzPolicyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * 政策文件Controller
 *
 * @author ruoyi
 */
@RestController
@RequestMapping("/gangzhu/policy")
public class HzPolicyController extends BaseController {

    @Autowired
    private IHzPolicyService policyService;

    /**
     * 查询政策文件列表
     */
    @PreAuthorize("@ss.hasPermi('gangzhu:policy:list')")
    @GetMapping("/list")
    public TableDataInfo list(HzPolicy policy) {
        Page<HzPolicy> page = PageUtils.getPage();
        IPage<HzPolicy> pageResult = policyService.selectPolicyPage(page, policy);
        TableDataInfo data = new TableDataInfo();
        data.setCode(HttpStatus.SUCCESS);
        data.setRows(pageResult.getRecords());
        data.setTotal(pageResult.getTotal());
        data.setMsg("查询成功");
        return data;
    }

    /**
     * 导出政策文件列表
     */
    @PreAuthorize("@ss.hasPermi('gangzhu:policy:export')")
    @Log(title = "政策文件", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, HzPolicy policy) {
        List<HzPolicy> list = policyService.selectPolicyList(policy);
        ExcelUtil<HzPolicy> util = new ExcelUtil<HzPolicy>(HzPolicy.class);
        util.exportExcel(response, list, "政策文件数据");
    }

    /**
     * 获取政策文件详细信息
     */
    @PreAuthorize("@ss.hasPermi('gangzhu:policy:query')")
    @GetMapping(value = "/{policyId}")
    public AjaxResult getInfo(@PathVariable("policyId") Long policyId) {
        return success(policyService.getById(policyId));
    }

    /**
     * 新增政策文件
     */
    @PreAuthorize("@ss.hasPermi('gangzhu:policy:add')")
    @Log(title = "政策文件", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody HzPolicy policy) {
        return toAjax(policyService.insertPolicy(policy));
    }

    /**
     * 修改政策文件
     */
    @PreAuthorize("@ss.hasPermi('gangzhu:policy:edit')")
    @Log(title = "政策文件", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody HzPolicy policy) {
        return toAjax(policyService.updatePolicy(policy));
    }

    /**
     * 删除政策文件
     */
    @PreAuthorize("@ss.hasPermi('gangzhu:policy:remove')")
    @Log(title = "政策文件", businessType = BusinessType.DELETE)
    @DeleteMapping("/{policyIds}")
    public AjaxResult remove(@PathVariable Long[] policyIds) {
        return toAjax(policyService.deletePolicyByIds(policyIds));
    }

    /**
     * 增加浏览次数
     */
    @PostMapping("/view/{policyId}")
    public AjaxResult increaseViewCount(@PathVariable Long policyId) {
        return toAjax(policyService.increaseViewCount(policyId));
    }
}
