package com.ruoyi.system.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.ruoyi.common.core.domain.BaseEntity;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * 黑名单对象 hz_blacklist
 *
 * @author ruoyi
 */
@TableName("hz_blacklist")
public class HzBlacklist extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /** 黑名单ID */
    @TableId(type = IdType.AUTO)
    private Long blacklistId;

    /** 租户ID */
    @TableField("tenant_id")
    private Long tenantId;

    /** 姓名 */
    @TableField("tenant_name")
    private String tenantName;

    /** 身份证号 */
    @TableField("id_card")
    private String idCard;

    /** 手机号 */
    @TableField("phone")
    private String phone;

    /** 加入原因 */
    @TableField("reason")
    private String reason;

    /** 加入类型(1:违约 2:欠费 3:违规 4:其他) */
    @TableField("blacklist_type")
    private String blacklistType;

    /** 关联合同ID */
    @TableField("contract_id")
    private Long contractId;

    /** 加入时间 */
    @TableField("blacklist_time")
    private String blacklistTime;

    /** 解除时间 */
    @TableField("remove_time")
    private String removeTime;

    /** 解除原因 */
    @TableField("remove_reason")
    private String removeReason;

    /** 状态(0:生效中 1:已解除) */
    @TableField("status")
    private String status;

    /** 删除标志(0:存在 2:删除) */
    @TableField("del_flag")
    private String delFlag;

    public void setBlacklistId(Long blacklistId) {
        this.blacklistId = blacklistId;
    }

    public Long getBlacklistId() {
        return blacklistId;
    }

    public void setTenantId(Long tenantId) {
        this.tenantId = tenantId;
    }

    public Long getTenantId() {
        return tenantId;
    }

    public void setTenantName(String tenantName) {
        this.tenantName = tenantName;
    }

    public String getTenantName() {
        return tenantName;
    }

    public void setIdCard(String idCard) {
        this.idCard = idCard;
    }

    public String getIdCard() {
        return idCard;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPhone() {
        return phone;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getReason() {
        return reason;
    }

    public void setBlacklistType(String blacklistType) {
        this.blacklistType = blacklistType;
    }

    public String getBlacklistType() {
        return blacklistType;
    }

    public void setContractId(Long contractId) {
        this.contractId = contractId;
    }

    public Long getContractId() {
        return contractId;
    }

    public void setBlacklistTime(String blacklistTime) {
        this.blacklistTime = blacklistTime;
    }

    public String getBlacklistTime() {
        return blacklistTime;
    }

    public void setRemoveTime(String removeTime) {
        this.removeTime = removeTime;
    }

    public String getRemoveTime() {
        return removeTime;
    }

    public void setRemoveReason(String removeReason) {
        this.removeReason = removeReason;
    }

    public String getRemoveReason() {
        return removeReason;
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
            .append("blacklistId", getBlacklistId())
            .append("tenantId", getTenantId())
            .append("tenantName", getTenantName())
            .append("idCard", getIdCard())
            .append("phone", getPhone())
            .append("reason", getReason())
            .append("blacklistType", getBlacklistType())
            .append("contractId", getContractId())
            .append("blacklistTime", getBlacklistTime())
            .append("removeTime", getRemoveTime())
            .append("removeReason", getRemoveReason())
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
