package com.ruoyi.web.controller.system;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.system.domain.HzQualificationAppeal;
import com.ruoyi.system.domain.HzQualificationAppealVO;
import com.ruoyi.system.service.IHzQualificationAppealService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * 资格申述Controller（管理端）
 *
 * @author ruoyi
 */
@RestController
@RequestMapping("/system/qualificationAppeal")
public class HzQualificationAppealController extends BaseController {

    @Autowired
    private IHzQualificationAppealService appealService;

    /**
     * 查询资格申述列表（管理端，包含用户信息）
     */
    @PreAuthorize("@ss.hasPermi('gangzhu:qualification:list')")
    @GetMapping("/list")
    public TableDataInfo list(HzQualificationAppeal appeal,
                               @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
                               @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize) {
        IPage<HzQualificationAppealVO> page = appealService.selectAppealVOPage(
            appeal,
            pageNum,
            pageSize
        );

        TableDataInfo dataTable = new TableDataInfo();
        dataTable.setRows(page.getRecords());
        dataTable.setTotal(page.getTotal());
        return dataTable;
    }

    /**
     * 获取资格申述详细信息
     */
    @PreAuthorize("@ss.hasPermi('gangzhu:qualification:query')")
    @GetMapping("/{appealId}")
    public AjaxResult getInfo(@PathVariable Long appealId) {
        return success(appealService.selectAppealVOById(appealId));
    }

    /**
     * 审核资格申述
     */
    @PreAuthorize("@ss.hasPermi('gangzhu:qualification:edit')")
    @Log(title = "资格申述审核", businessType = BusinessType.UPDATE)
    @PutMapping("/handle")
    public AjaxResult handle(@RequestBody HzQualificationAppeal appeal) {
        // 从appeal中获取申诉原因作为新学历（前端会传入）
        String newEducation = appeal.getAppealReason(); // 临时使用appealReason传递新学历

        int result = appealService.handleAppeal(
            appeal.getAppealId(),
            appeal.getHandleResult(),
            appeal.getHandleOpinion(),
            newEducation
        );

        return toAjax(result);
    }

    /**
     * 删除资格申述
     */
    @PreAuthorize("@ss.hasPermi('gangzhu:qualification:remove')")
    @Log(title = "资格申述", businessType = BusinessType.DELETE)
    @DeleteMapping("/{appealIds}")
    public AjaxResult remove(@PathVariable Long[] appealIds) {
        for (Long appealId : appealIds) {
            appealService.deleteAppealById(appealId);
        }
        return success();
    }
}
