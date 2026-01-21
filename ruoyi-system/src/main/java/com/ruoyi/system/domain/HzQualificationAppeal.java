package com.ruoyi.system.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.ruoyi.common.core.domain.BaseEntity;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * 资格申诉对象 hz_qualification_appeal
 *
 * @author ruoyi
 */
@TableName("hz_qualification_appeal")
public class HzQualificationAppeal extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /** 申诉ID */
    @TableId(type = IdType.AUTO)
    private Long appealId;

    /** 资格ID */
    @TableField("qualification_id")
    private Long qualificationId;

    /** 租户ID */
    @TableField("tenant_id")
    private Long tenantId;

    /** 申诉原因 */
    @TableField("appeal_reason")
    private String appealReason;

    /** 申诉材料 */
    @TableField("appeal_attachments")
    private String appealAttachments;

    /** 申诉时间 */
    @TableField("appeal_time")
    private String appealTime;

    /** 处理结果(0:待处理 1:通过 2:不通过) */
    @TableField("handle_result")
    private String handleResult;

    /** 处理意见 */
    @TableField("handle_opinion")
    private String handleOpinion;

    /** 处理人 */
    @TableField("handle_by")
    private String handleBy;

    /** 处理时间 */
    @TableField("handle_time")
    private String handleTime;

    /** 状态 */
    @TableField("status")
    private String status;

    /** 删除标志(0:存在 2:删除) */
    @TableField("del_flag")
    private String delFlag;

    /** 用户ID（前端传递，不存储到数据库） */
    @TableField(exist = false)
    private Long userId;

    public void setAppealId(Long appealId) {
        this.appealId = appealId;
    }

    public Long getAppealId() {
        return appealId;
    }

    public void setQualificationId(Long qualificationId) {
        this.qualificationId = qualificationId;
    }

    public Long getQualificationId() {
        return qualificationId;
    }

    public void setTenantId(Long tenantId) {
        this.tenantId = tenantId;
    }

    public Long getTenantId() {
        return tenantId;
    }

    public void setAppealReason(String appealReason) {
        this.appealReason = appealReason;
    }

    public String getAppealReason() {
        return appealReason;
    }

    public void setAppealAttachments(String appealAttachments) {
        this.appealAttachments = appealAttachments;
    }

    public String getAppealAttachments() {
        return appealAttachments;
    }

    public void setAppealTime(String appealTime) {
        this.appealTime = appealTime;
    }

    public String getAppealTime() {
        return appealTime;
    }

    public void setHandleResult(String handleResult) {
        this.handleResult = handleResult;
    }

    public String getHandleResult() {
        return handleResult;
    }

    public void setHandleOpinion(String handleOpinion) {
        this.handleOpinion = handleOpinion;
    }

    public String getHandleOpinion() {
        return handleOpinion;
    }

    public void setHandleBy(String handleBy) {
        this.handleBy = handleBy;
    }

    public String getHandleBy() {
        return handleBy;
    }

    public void setHandleTime(String handleTime) {
        this.handleTime = handleTime;
    }

    public String getHandleTime() {
        return handleTime;
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

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getUserId() {
        return userId;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
            .append("appealId", getAppealId())
            .append("qualificationId", getQualificationId())
            .append("tenantId", getTenantId())
            .append("appealReason", getAppealReason())
            .append("appealAttachments", getAppealAttachments())
            .append("appealTime", getAppealTime())
            .append("handleResult", getHandleResult())
            .append("handleOpinion", getHandleOpinion())
            .append("handleBy", getHandleBy())
            .append("handleTime", getHandleTime())
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
