package com.ruoyi.web.controller.system;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.system.domain.HzHouseStatusAudit;
import com.ruoyi.system.service.IHzHouseStatusAuditService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 房源状态变更审批Controller
 *
 * @author ruoyi
 */
@RestController
@RequestMapping("/system/house/statusAudit")
public class HzHouseStatusAuditController extends BaseController {

    @Autowired
    private IHzHouseStatusAuditService auditService;

    /**
     * 物业提交状态变更申请（单个房源）
     */
    @PreAuthorize("@ss.hasPermi('gangzhu:house:statusChange')")
    @PostMapping("/submit")
    public AjaxResult submit(@RequestParam Long houseId, @RequestParam String targetStatus) {
        return toAjax(auditService.submitStatusChange(houseId, targetStatus));
    }

    /**
     * 物业批量提交状态变更申请
     */
    @PreAuthorize("@ss.hasPermi('gangzhu:house:statusChange')")
    @PostMapping("/batchSubmit")
    public AjaxResult batchSubmit(@RequestParam List<Long> houseIds, @RequestParam String targetStatus) {
        int count = auditService.batchSubmitStatusChange(houseIds, targetStatus);
        return AjaxResult.success("已提交" + count + "条审批申请");
    }

    /**
     * 管理方审批状态变更
     */
    @PreAuthorize("@ss.hasPermi('gangzhu:house:statusApprove')")
    @PostMapping("/approve")
    public AjaxResult approve(@RequestParam Long auditId, @RequestParam String approveStatus,
                              @RequestParam(required = false) String opinion) {
        return toAjax(auditService.approveStatusChange(auditId, approveStatus, opinion));
    }

    /**
     * 分页查询审批列表
     */
    @PreAuthorize("@ss.hasPermi('gangzhu:house:statusAudit')")
    @GetMapping("/list")
    public AjaxResult list(HzHouseStatusAudit audit,
                           @RequestParam(defaultValue = "1") int pageNum,
                           @RequestParam(defaultValue = "10") int pageSize) {
        IPage<HzHouseStatusAudit> page = auditService.selectAuditPage(audit, pageNum, pageSize);
        return AjaxResult.success(page);
    }

    /**
     * 查询审批详情
     */
    @PreAuthorize("@ss.hasPermi('gangzhu:house:statusAudit')")
    @GetMapping("/{auditId}")
    public AjaxResult getInfo(@PathVariable Long auditId) {
        return AjaxResult.success(auditService.selectAuditById(auditId));
    }
}
