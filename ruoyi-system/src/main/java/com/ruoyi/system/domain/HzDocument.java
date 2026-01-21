package com.ruoyi.system.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.ruoyi.common.core.domain.BaseEntity;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * 资料文档对象 hz_document
 *
 * @author ruoyi
 */
@TableName("hz_document")
public class HzDocument extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /** 文档ID */
    @TableId(type = IdType.AUTO)
    private Long documentId;

    /** 租户ID */
    @TableField("tenant_id")
    private Long tenantId;

    /** 文档类型(1:身份证 2:学历证明 3:工作证明 4:收入证明 5:人才证书 6:其他) */
    @TableField("document_type")
    private String documentType;

    /** 文档名称 */
    @TableField("document_name")
    private String documentName;

    /** 文件路径 */
    @TableField("file_path")
    private String filePath;

    /** 文件大小(字节) */
    @TableField("file_size")
    private Long fileSize;

    /** 文件格式 */
    @TableField("file_format")
    private String fileFormat;

    /** 审核状态(0:待审核 1:已通过 2:已拒绝) */
    @TableField("audit_status")
    private String auditStatus;

    /** 审核人 */
    @TableField("auditor")
    private String auditor;

    /** 审核时间 */
    @TableField("audit_time")
    private String auditTime;

    /** 审核意见 */
    @TableField("audit_opinion")
    private String auditOpinion;

    /** 删除标志(0:存在 2:删除) */
    @TableField("del_flag")
    private String delFlag;

    public void setDocumentId(Long documentId) {
        this.documentId = documentId;
    }

    public Long getDocumentId() {
        return documentId;
    }

    public void setTenantId(Long tenantId) {
        this.tenantId = tenantId;
    }

    public Long getTenantId() {
        return tenantId;
    }

    public void setDocumentType(String documentType) {
        this.documentType = documentType;
    }

    public String getDocumentType() {
        return documentType;
    }

    public void setDocumentName(String documentName) {
        this.documentName = documentName;
    }

    public String getDocumentName() {
        return documentName;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFileSize(Long fileSize) {
        this.fileSize = fileSize;
    }

    public Long getFileSize() {
        return fileSize;
    }

    public void setFileFormat(String fileFormat) {
        this.fileFormat = fileFormat;
    }

    public String getFileFormat() {
        return fileFormat;
    }

    public void setAuditStatus(String auditStatus) {
        this.auditStatus = auditStatus;
    }

    public String getAuditStatus() {
        return auditStatus;
    }

    public void setAuditor(String auditor) {
        this.auditor = auditor;
    }

    public String getAuditor() {
        return auditor;
    }

    public void setAuditTime(String auditTime) {
        this.auditTime = auditTime;
    }

    public String getAuditTime() {
        return auditTime;
    }

    public void setAuditOpinion(String auditOpinion) {
        this.auditOpinion = auditOpinion;
    }

    public String getAuditOpinion() {
        return auditOpinion;
    }

    public void setDelFlag(String delFlag) {
        this.delFlag = delFlag;
    }

    public String getDelFlag() {
        return delFlag;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
            .append("documentId", getDocumentId())
            .append("tenantId", getTenantId())
            .append("documentType", getDocumentType())
            .append("documentName", getDocumentName())
            .append("filePath", getFilePath())
            .append("fileSize", getFileSize())
            .append("fileFormat", getFileFormat())
            .append("auditStatus", getAuditStatus())
            .append("auditor", getAuditor())
            .append("auditTime", getAuditTime())
            .append("auditOpinion", getAuditOpinion())
            .append("delFlag", getDelFlag())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("updateBy", getUpdateBy())
            .append("updateTime", getUpdateTime())
            .append("remark", getRemark())
            .toString();
    }
}
