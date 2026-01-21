package com.ruoyi.system.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.ruoyi.common.core.domain.BaseEntity;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * 承诺书模板对象 hz_commitment_template
 *
 * @author ruoyi
 */
@TableName("hz_commitment_template")
public class HzCommitmentTemplate extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /** 模板ID */
    @TableId(type = IdType.AUTO)
    private Long templateId;

    /** 模板名称 */
    @TableField("template_name")
    private String templateName;

    /** 模板编码 */
    @TableField("template_code")
    private String templateCode;

    /** 承诺书类型(1:人才公寓 2:保租房 3:市场租赁) */
    @TableField("commitment_type")
    private String commitmentType;

    /** 模板内容(HTML) */
    @TableField("template_content")
    private String templateContent;

    /** 版本号 */
    @TableField("version")
    private String version;

    /** 是否默认(0:否 1:是) */
    @TableField("is_default")
    private String isDefault;

    /** 状态(0:正常 1:停用) */
    @TableField("status")
    private String status;

    /** 删除标志(0:存在 2:删除) */
    @TableField("del_flag")
    private String delFlag;

    public void setTemplateId(Long templateId) {
        this.templateId = templateId;
    }

    public Long getTemplateId() {
        return templateId;
    }

    public void setTemplateName(String templateName) {
        this.templateName = templateName;
    }

    public String getTemplateName() {
        return templateName;
    }

    public void setTemplateCode(String templateCode) {
        this.templateCode = templateCode;
    }

    public String getTemplateCode() {
        return templateCode;
    }

    public void setCommitmentType(String commitmentType) {
        this.commitmentType = commitmentType;
    }

    public String getCommitmentType() {
        return commitmentType;
    }

    public void setTemplateContent(String templateContent) {
        this.templateContent = templateContent;
    }

    public String getTemplateContent() {
        return templateContent;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getVersion() {
        return version;
    }

    public void setIsDefault(String isDefault) {
        this.isDefault = isDefault;
    }

    public String getIsDefault() {
        return isDefault;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
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
            .append("templateId", getTemplateId())
            .append("templateName", getTemplateName())
            .append("templateCode", getTemplateCode())
            .append("commitmentType", getCommitmentType())
            .append("templateContent", getTemplateContent())
            .append("version", getVersion())
            .append("isDefault", getIsDefault())
            .append("status", getStatus())
            .append("delFlag", getDelFlag())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("updateBy", getUpdateBy())
            .append("updateTime", getUpdateTime())
            .append("remark", getRemark())
            .toString();
    }
}
