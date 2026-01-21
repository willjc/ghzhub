package com.ruoyi.system.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.ruoyi.common.core.domain.BaseEntity;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * 承诺书对象 hz_commitment
 *
 * @author ruoyi
 */
@TableName("hz_commitment")
public class HzCommitment extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /** 承诺书ID */
    @TableId(type = IdType.AUTO)
    private Long commitmentId;

    /** 租户ID */
    @TableField("tenant_id")
    private Long tenantId;

    /** 项目ID */
    @TableField("project_id")
    private Long projectId;

    /** 承诺书类型(1:资格申请承诺 2:入住承诺 3:其他) */
    @TableField("commitment_type")
    private String commitmentType;

    /** 承诺内容 */
    @TableField("commitment_content")
    private String commitmentContent;

    /** 签署时间 */
    @TableField("sign_time")
    private String signTime;

    /** 签名图片 */
    @TableField("signature_image")
    private String signatureImage;

    /** IP地址 */
    @TableField("ip_address")
    private String ipAddress;

    /** 设备信息 */
    @TableField("device_info")
    private String deviceInfo;

    /** 状态(0:有效 1:失效) */
    @TableField("status")
    private String status;

    /** 删除标志(0:存在 2:删除) */
    @TableField("del_flag")
    private String delFlag;

    public void setCommitmentId(Long commitmentId) {
        this.commitmentId = commitmentId;
    }

    public Long getCommitmentId() {
        return commitmentId;
    }

    public void setTenantId(Long tenantId) {
        this.tenantId = tenantId;
    }

    public Long getTenantId() {
        return tenantId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    public Long getProjectId() {
        return projectId;
    }

    public void setCommitmentType(String commitmentType) {
        this.commitmentType = commitmentType;
    }

    public String getCommitmentType() {
        return commitmentType;
    }

    public void setCommitmentContent(String commitmentContent) {
        this.commitmentContent = commitmentContent;
    }

    public String getCommitmentContent() {
        return commitmentContent;
    }

    public void setSignTime(String signTime) {
        this.signTime = signTime;
    }

    public String getSignTime() {
        return signTime;
    }

    public void setSignatureImage(String signatureImage) {
        this.signatureImage = signatureImage;
    }

    public String getSignatureImage() {
        return signatureImage;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setDeviceInfo(String deviceInfo) {
        this.deviceInfo = deviceInfo;
    }

    public String getDeviceInfo() {
        return deviceInfo;
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
            .append("commitmentId", getCommitmentId())
            .append("tenantId", getTenantId())
            .append("projectId", getProjectId())
            .append("commitmentType", getCommitmentType())
            .append("commitmentContent", getCommitmentContent())
            .append("signTime", getSignTime())
            .append("signatureImage", getSignatureImage())
            .append("ipAddress", getIpAddress())
            .append("deviceInfo", getDeviceInfo())
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
