package com.ruoyi.web.controller.h5;

import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.system.domain.HzDocument;
import com.ruoyi.system.domain.HzTenant;
import com.ruoyi.system.service.IHzDocumentService;
import com.ruoyi.system.service.IHzTenantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * H5端资料文档Controller
 *
 * @author ruoyi
 */
@RestController
@RequestMapping("/h5/document")
public class HzDocumentController extends BaseController {

    @Autowired
    private IHzDocumentService documentService;

    @Autowired
    private IHzTenantService tenantService;

    /**
     * 查询当前用户的资料列表
     */
    @GetMapping("/list")
    public AjaxResult list() {
        // TODO: 从登录态获取userId
        Long userId = 1L;
        HzTenant tenant = tenantService.selectTenantByUserId(userId);
        if (tenant == null) {
            return error("请先完善租户信息");
        }

        List<HzDocument> list = documentService.selectDocumentListByTenantId(tenant.getTenantId());
        return success(list);
    }

    /**
     * 获取资料详情
     */
    @GetMapping("/{documentId}")
    public AjaxResult getInfo(@PathVariable("documentId") Long documentId) {
        HzDocument document = documentService.selectDocumentById(documentId);
        return success(document);
    }

    /**
     * 根据类型查询资料列表
     */
    @GetMapping("/type/{documentType}")
    public AjaxResult listByType(@PathVariable("documentType") String documentType) {
        // TODO: 从登录态获取userId
        Long userId = 1L;
        HzTenant tenant = tenantService.selectTenantByUserId(userId);
        if (tenant == null) {
            return error("请先完善租户信息");
        }

        List<HzDocument> list = documentService.selectDocumentListByTenantIdAndType(
                tenant.getTenantId(), documentType);
        return success(list);
    }

    /**
     * 上传资料
     */
    @PostMapping("/upload")
    public AjaxResult upload(@RequestBody HzDocument document) {
        // TODO: 从登录态获取userId
        Long userId = 1L;
        HzTenant tenant = tenantService.selectTenantByUserId(userId);
        if (tenant == null) {
            return error("请先完善租户信息");
        }

        // 设置租户ID
        document.setTenantId(tenant.getTenantId());

        int result = documentService.insertDocument(document);
        return result > 0 ? success() : error("上传失败");
    }

    /**
     * 删除资料
     */
    @DeleteMapping("/{documentId}")
    public AjaxResult delete(@PathVariable("documentId") Long documentId) {
        // TODO: 从登录态获取userId
        Long userId = 1L;
        HzTenant tenant = tenantService.selectTenantByUserId(userId);
        if (tenant == null) {
            return error("请先完善租户信息");
        }

        // 校验资料是否存在
        HzDocument existDocument = documentService.selectDocumentById(documentId);
        if (existDocument == null) {
            return error("资料不存在");
        }

        // 校验是否属于当前租户
        if (!existDocument.getTenantId().equals(tenant.getTenantId())) {
            return error("无权操作此资料");
        }

        // 只有待审核状态可以删除
        if (!"0".equals(existDocument.getAuditStatus())) {
            return error("当前状态不允许删除");
        }

        int result = documentService.deleteDocumentById(documentId);
        return result > 0 ? success() : error("删除失败");
    }
}
