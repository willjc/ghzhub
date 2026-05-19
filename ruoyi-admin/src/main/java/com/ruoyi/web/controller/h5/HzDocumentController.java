package com.ruoyi.web.controller.h5;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.system.domain.HzContract;
import com.ruoyi.system.domain.HzDocument;
import com.ruoyi.system.domain.HzTenant;
import com.ruoyi.system.mapper.HzContractMapper;
import com.ruoyi.system.service.IHzDocumentService;
import com.ruoyi.system.service.IHzHouseOrderService;
import com.ruoyi.system.service.IHzTenantService;
import com.ruoyi.system.service.IHzUserMessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.ruoyi.common.config.RuoYiConfig;
import com.ruoyi.common.utils.file.FileUploadUtils;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    @Autowired
    private IHzHouseOrderService orderService;

    @Autowired
    private IHzUserMessageService messageService;

    @Autowired
    private HzContractMapper contractMapper;

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
     * 上传资料（接受 multipart/form-data）
     * @param file         图片/文件
     * @param documentType 资料类型：1身份证 2学历证明 3工作证明 4收入证明 5人才证书
     * @param tenantId     H5用户ID（由前端从 localStorage 传入）
     */
    @PostMapping("/upload")
    public AjaxResult upload(
            MultipartFile file,
            @RequestParam(required = false) String documentType,
            @RequestParam(required = false) Long tenantId,
            @RequestParam(required = false) Long contractId) {
        if (file == null || file.isEmpty()) {
            return error("请选择要上传的文件");
        }
        if (tenantId == null) {
            return error("用户未登录");
        }
        try {
            // 保存文件，返回相对路径 /profile/upload/...
            String filePath = FileUploadUtils.upload(RuoYiConfig.getUploadPath(), file);

            HzDocument document = new HzDocument();
            document.setTenantId(tenantId);
            document.setDocumentType(documentType);
            document.setDocumentName(file.getOriginalFilename());
            document.setFilePath(filePath);
            document.setFileSize(file.getSize());
            // 业务规则：上传即默认通过，1 个月内由管理员异步复核与抽查
            document.setAuditStatus("1");
            document.setAuditOpinion("系统默认通过，1 个月内复核");
            document.setAuditTime(new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new java.util.Date()));
            document.setDelFlag("0");
            document.setCreateTime(new java.util.Date());
            if (contractId != null) {
                document.setContractId(contractId);
            }

            int result = documentService.insertDocument(document);
            if (result > 0) {
                // 发送资料上传成功消息
                try {
                    String docTypeLabel = getDocTypeLabel(documentType);
                    messageService.sendMessage(tenantId, "system", "资料上传成功",
                            "您的" + docTypeLabel + "已上传成功并默认通过，请于合同签订后 72 小时内办理入住手续");
                } catch (Exception msgEx) {
                    logger.warn("发送资料提交消息失败: {}", msgEx.getMessage());
                }

                // 默认通过即视为审核通过，触发订单完成检查（两份资料齐则订单转 3）
                try {
                    orderService.onDocumentsApproved(tenantId);
                } catch (Exception orderEx) {
                    logger.warn("触发订单资料齐套检查失败: {}", orderEx.getMessage());
                }

                Map<String, Object> data = new HashMap<>();
                data.put("documentId", document.getDocumentId());
                data.put("filePath", filePath);
                return success(data);
            }
            return error("保存失败");
        } catch (Exception e) {
            logger.error("上传资料失败", e);
            return error("上传失败：" + e.getMessage());
        }
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

    /**
     * 查询指定用户的资料提交状态（账单页用于判断是否允许缴租）
     * @param userId H5用户ID（hz_user.user_id）
     * @return submitted: 是否已提交资料；count: 已提交数量；approved: 是否已有审核通过的资料
     */
    @GetMapping("/status/{userId}")
    public AjaxResult getDocumentStatus(@PathVariable Long userId) {
        Map<String, Object> result = new HashMap<>();

        // 短路判断：若该用户有处于履行中/已到期/已解约状态的合同，则资料视为已完成
        // 适用于老数据迁移用户（已实际入住，无需再补传资料）
        long activeContractCount = contractMapper.selectCount(
            new LambdaQueryWrapper<HzContract>()
                .eq(HzContract::getTenantId, userId)
                .in(HzContract::getContractStatus, "3", "4", "5")
                .eq(HzContract::getDelFlag, "0")
        );
        if (activeContractCount > 0) {
            result.put("submitted", true);
            result.put("count", 1);
            result.put("approved", true);
            return success(result);
        }

        // hz_document.tenant_id 直接存储 hz_user.user_id，不经过 hz_tenant
        List<HzDocument> docs = documentService.selectDocumentListByTenantId(userId);
        int count = docs != null ? docs.size() : 0;
        boolean approved = docs != null && docs.stream().anyMatch(d -> "1".equals(d.getAuditStatus()));
        result.put("submitted", count > 0);
        result.put("count", count);
        result.put("approved", approved);
        return success(result);
    }

    /**
     * 审核资料（管理端调用）
     * 请求体：{ "documentId": 1, "auditStatus": "1", "auditOpinion": "审核通过" }
     * auditStatus: 1=通过, 2=拒绝
     */
    @PutMapping("/audit")
    public AjaxResult audit(@RequestBody HzDocument document) {
        if (document.getDocumentId() == null || document.getAuditStatus() == null) {
            return error("参数不完整");
        }

        HzDocument existDocument = documentService.selectDocumentById(document.getDocumentId());
        if (existDocument == null) {
            return error("资料不存在");
        }

        int result = documentService.updateDocument(document);
        if (result > 0 && "1".equals(document.getAuditStatus())) {
            // 发送资料审核通过消息
            try {
                messageService.sendMessage(existDocument.getTenantId(), "system", "资料审核通过",
                        "您提交的资料已审核通过");
            } catch (Exception msgEx) {
                logger.warn("发送资料审核消息失败: {}", msgEx.getMessage());
            }

            // 触发检查：如果该租户的工作证明和学历证明均已通过，则完成预订单
            if (existDocument.getTenantId() != null) {
                orderService.onDocumentsApproved(existDocument.getTenantId());
            }
        } else if (result > 0 && "2".equals(document.getAuditStatus())) {
            // 发送资料驳回消息（直接用驳回原因作为消息内容）
            try {
                String reason = document.getAuditOpinion() != null ? document.getAuditOpinion() : "资料审核未通过";
                messageService.sendMessage(existDocument.getTenantId(), "system", "资料审核未通过", reason);
            } catch (Exception msgEx) {
                logger.warn("发送资料驳回消息失败: {}", msgEx.getMessage());
            }
        }
        return result > 0 ? success() : error("审核失败");
    }

    /**
     * 重新上传被驳回的资料（覆盖原记录）
     * <p>仅当原记录 audit_status=2（已驳回）时允许；
     * 上传成功后会重置 audit_status=0（待审核），清空 audit_opinion / audit_time / auditor。</p>
     *
     * @param file       新文件（仅图片）
     * @param documentId 被驳回资料 ID
     */
    @PostMapping("/reupload")
    public AjaxResult reupload(MultipartFile file,
                               @RequestParam("documentId") Long documentId) {
        if (file == null || file.isEmpty()) {
            return error("请选择要上传的文件");
        }
        HzDocument exist = documentService.selectDocumentById(documentId);
        if (exist == null) {
            return error("资料不存在");
        }
        if (!"2".equals(exist.getAuditStatus())) {
            return error("仅被驳回的资料可以重新上传");
        }
        try {
            String filePath = FileUploadUtils.upload(RuoYiConfig.getUploadPath(), file);
            HzDocument update = new HzDocument();
            update.setDocumentId(documentId);
            update.setFilePath(filePath);
            update.setFileSize(file.getSize());
            update.setDocumentName(file.getOriginalFilename());
            update.setAuditStatus("0");
            update.setAuditOpinion("");
            update.setAuditTime(null);
            update.setAuditor(null);
            update.setUpdateTime(new java.util.Date());
            int rows = documentService.updateDocument(update);
            if (rows > 0) {
                try {
                    String docTypeLabel = getDocTypeLabel(exist.getDocumentType());
                    messageService.sendMessage(exist.getTenantId(), "system", "资料重新提交成功",
                            "您的" + docTypeLabel + "已重新提交，请等待审核");
                } catch (Exception ignore) { }
                Map<String, Object> data = new HashMap<>();
                data.put("documentId", documentId);
                data.put("filePath", filePath);
                return success(data);
            }
            return error("更新失败");
        } catch (Exception e) {
            logger.error("重新上传资料失败", e);
            return error("上传失败：" + e.getMessage());
        }
    }

    /**
     * 标记资料违规（管理端抽查专用）
     * <p>用于「上传即默认通过」机制下的事后抽查。仅做数据留痕：
     * 把 audit_status 置为 2，audit_opinion 写入 "[违规] " + reason，便于后续查询统计；
     * 不发 H5 消息（违规处置由运营线下追缴+计入诚信档案），不动订单/合同状态。</p>
     *
     * 请求体：{ "documentId": 1, "violationReason": "..." }
     */
    @PutMapping("/violation")
    public AjaxResult markViolation(@RequestBody Map<String, Object> body) {
        Object idObj = body.get("documentId");
        String reason = body.get("violationReason") == null ? "" : String.valueOf(body.get("violationReason")).trim();
        if (idObj == null) {
            return error("参数不完整");
        }
        if (reason.isEmpty()) {
            return error("请填写违规原因");
        }
        Long documentId;
        try {
            documentId = Long.valueOf(String.valueOf(idObj));
        } catch (Exception e) {
            return error("documentId 非法");
        }
        HzDocument exist = documentService.selectDocumentById(documentId);
        if (exist == null) {
            return error("资料不存在");
        }
        HzDocument update = new HzDocument();
        update.setDocumentId(documentId);
        update.setAuditStatus("2");
        update.setAuditOpinion("[违规] " + reason);
        update.setAuditTime(new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new java.util.Date()));
        int rows = documentService.updateDocument(update);
        return rows > 0 ? success() : error("标记失败");
    }

    /**
     * 资料类型编码转中文标签
     */
    private String getDocTypeLabel(String documentType) {
        if (documentType == null) return "资料";
        switch (documentType) {
            case "1": return "身份证";
            case "2": return "学历证明";
            case "3": return "工作证明";
            case "4": return "收入证明";
            case "5": return "人才证书";
            default: return "资料";
        }
    }
}
