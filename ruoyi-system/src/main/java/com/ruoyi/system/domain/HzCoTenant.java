package com.ruoyi.system.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.ruoyi.common.core.domain.BaseEntity;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * 合租户申请对象 hz_co_tenant
 *
 * @author ruoyi
 */
@TableName("hz_co_tenant")
public class HzCoTenant extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /** 合租户ID */
    @TableId(type = IdType.AUTO)
    private Long coTenantId;

    /** 合同ID */
    @TableField("contract_id")
    private Long contractId;

    /** 姓名 */
    @TableField("tenant_name")
    private String tenantName;

    /** 身份证号 */
    @TableField("id_card")
    private String idCard;

    /** 联系电话 */
    @TableField("phone")
    private String phone;

    /** 与主承租人关系 */
    @TableField("relationship")
    private String relationship;

    /** 审核状态(0=待审核,1=通过,2=拒绝) */
    @TableField("status")
    private String status;

    /** 删除标志(0:存在 2:删除) */
    @TableField("del_flag")
    private String delFlag;

    // ==================== 关联查询字段（非数据库字段） ====================

    /** 合同编号 */
    @TableField(exist = false)
    private String contractNo;

    /** 房间地址 */
    @TableField(exist = false)
    private String roomAddress;

    /** 申请人（主承租人姓名） */
    @TableField(exist = false)
    private String mainTenantName;

    /** 联系电话 */
    @TableField(exist = false)
    private String mainTenantPhone;

    /** 申请时间 */
    @TableField(exist = false)
    private String applyTime;

    /** 审核意见 */
    @TableField(exist = false)
    private String auditOpinion;

    /** 审核时间 */
    @TableField(exist = false)
    private String auditTime;

    public void setCoTenantId(Long coTenantId) {
        this.coTenantId = coTenantId;
    }

    public Long getCoTenantId() {
        return coTenantId;
    }

    public void setContractId(Long contractId) {
        this.contractId = contractId;
    }

    public Long getContractId() {
        return contractId;
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

    public void setRelationship(String relationship) {
        this.relationship = relationship;
    }

    public String getRelationship() {
        return relationship;
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

    public void setContractNo(String contractNo) {
        this.contractNo = contractNo;
    }

    public String getContractNo() {
        return contractNo;
    }

    public void setRoomAddress(String roomAddress) {
        this.roomAddress = roomAddress;
    }

    public String getRoomAddress() {
        return roomAddress;
    }

    public void setMainTenantName(String mainTenantName) {
        this.mainTenantName = mainTenantName;
    }

    public String getMainTenantName() {
        return mainTenantName;
    }

    public void setMainTenantPhone(String mainTenantPhone) {
        this.mainTenantPhone = mainTenantPhone;
    }

    public String getMainTenantPhone() {
        return mainTenantPhone;
    }

    public void setApplyTime(String applyTime) {
        this.applyTime = applyTime;
    }

    public String getApplyTime() {
        return applyTime;
    }

    public void setAuditOpinion(String auditOpinion) {
        this.auditOpinion = auditOpinion;
    }

    public String getAuditOpinion() {
        return auditOpinion;
    }

    public void setAuditTime(String auditTime) {
        this.auditTime = auditTime;
    }

    public String getAuditTime() {
        return auditTime;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
            .append("coTenantId", getCoTenantId())
            .append("contractId", getContractId())
            .append("tenantName", getTenantName())
            .append("idCard", getIdCard())
            .append("phone", getPhone())
            .append("relationship", getRelationship())
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
