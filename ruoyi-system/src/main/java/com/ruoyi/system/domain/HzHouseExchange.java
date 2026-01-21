package com.ruoyi.system.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.ruoyi.common.core.domain.BaseEntity;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.math.BigDecimal;

/**
 * 调换房申请对象 hz_house_exchange
 *
 * @author ruoyi
 */
@TableName("hz_house_exchange")
public class HzHouseExchange extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /** 调换房ID */
    @TableId(type = IdType.AUTO)
    private Long exchangeId;

    /** 租户ID */
    @TableField("tenant_id")
    private Long tenantId;

    /** 原合同ID */
    @TableField("old_contract_id")
    private Long oldContractId;

    /** 原房源ID */
    @TableField("old_house_id")
    private Long oldHouseId;

    /** 原房源编号 */
    @TableField("old_house_code")
    private String oldHouseCode;

    /** 新房源ID */
    @TableField("new_house_id")
    private Long newHouseId;

    /** 新房源编号 */
    @TableField("new_house_code")
    private String newHouseCode;

    /** 调换原因 */
    @TableField("exchange_reason")
    private String exchangeReason;

    /** 申请调换时间 */
    @TableField("apply_time")
    private String applyTime;

    /** 审核结果(0=待审核,1=通过,2=拒绝) */
    @TableField("approve_result")
    private String approveResult;

    /** 审核意见 */
    @TableField("approve_opinion")
    private String approveOpinion;

    /** 审核人 */
    @TableField("approve_by")
    private String approveBy;

    /** 审核时间 */
    @TableField("approve_time")
    private String approveTime;

    /** 调换时间 */
    @TableField("exchange_time")
    private String exchangeTime;

    /** 新合同ID */
    @TableField("new_contract_id")
    private Long newContractId;

    /** 状态(0=待审核,1=已完成,2=已拒绝) */
    @TableField("status")
    private String status;

    /** 删除标志(0:存在 2:删除) */
    @TableField("del_flag")
    private String delFlag;

    // ==================== 关联查询字段（非数据库字段） ====================

    /** 合同编号 */
    @TableField(exist = false)
    private String contractNo;

    /** 申请人姓名 */
    @TableField(exist = false)
    private String tenantName;

    /** 身份证号 */
    @TableField(exist = false)
    private String tenantIdCard;

    /** 联系电话 */
    @TableField(exist = false)
    private String tenantPhone;

    /** 房间地址 */
    @TableField(exist = false)
    private String roomAddress;

    /** 原租金 */
    @TableField(exist = false)
    private BigDecimal oldRent;

    /** 新租金 */
    @TableField(exist = false)
    private BigDecimal newRent;

    public void setExchangeId(Long exchangeId) {
        this.exchangeId = exchangeId;
    }

    public Long getExchangeId() {
        return exchangeId;
    }

    public void setTenantId(Long tenantId) {
        this.tenantId = tenantId;
    }

    public Long getTenantId() {
        return tenantId;
    }

    public void setOldContractId(Long oldContractId) {
        this.oldContractId = oldContractId;
    }

    public Long getOldContractId() {
        return oldContractId;
    }

    public void setOldHouseId(Long oldHouseId) {
        this.oldHouseId = oldHouseId;
    }

    public Long getOldHouseId() {
        return oldHouseId;
    }

    public void setOldHouseCode(String oldHouseCode) {
        this.oldHouseCode = oldHouseCode;
    }

    public String getOldHouseCode() {
        return oldHouseCode;
    }

    public void setNewHouseId(Long newHouseId) {
        this.newHouseId = newHouseId;
    }

    public Long getNewHouseId() {
        return newHouseId;
    }

    public void setNewHouseCode(String newHouseCode) {
        this.newHouseCode = newHouseCode;
    }

    public String getNewHouseCode() {
        return newHouseCode;
    }

    public void setExchangeReason(String exchangeReason) {
        this.exchangeReason = exchangeReason;
    }

    public String getExchangeReason() {
        return exchangeReason;
    }

    public void setApplyTime(String applyTime) {
        this.applyTime = applyTime;
    }

    public String getApplyTime() {
        return applyTime;
    }

    public void setApproveResult(String approveResult) {
        this.approveResult = approveResult;
    }

    public String getApproveResult() {
        return approveResult;
    }

    public void setApproveOpinion(String approveOpinion) {
        this.approveOpinion = approveOpinion;
    }

    public String getApproveOpinion() {
        return approveOpinion;
    }

    public void setApproveBy(String approveBy) {
        this.approveBy = approveBy;
    }

    public String getApproveBy() {
        return approveBy;
    }

    public void setApproveTime(String approveTime) {
        this.approveTime = approveTime;
    }

    public String getApproveTime() {
        return approveTime;
    }

    public void setExchangeTime(String exchangeTime) {
        this.exchangeTime = exchangeTime;
    }

    public String getExchangeTime() {
        return exchangeTime;
    }

    public void setNewContractId(Long newContractId) {
        this.newContractId = newContractId;
    }

    public Long getNewContractId() {
        return newContractId;
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

    public void setTenantName(String tenantName) {
        this.tenantName = tenantName;
    }

    public String getTenantName() {
        return tenantName;
    }

    public void setTenantIdCard(String tenantIdCard) {
        this.tenantIdCard = tenantIdCard;
    }

    public String getTenantIdCard() {
        return tenantIdCard;
    }

    public void setTenantPhone(String tenantPhone) {
        this.tenantPhone = tenantPhone;
    }

    public String getTenantPhone() {
        return tenantPhone;
    }

    public void setRoomAddress(String roomAddress) {
        this.roomAddress = roomAddress;
    }

    public String getRoomAddress() {
        return roomAddress;
    }

    public void setOldRent(BigDecimal oldRent) {
        this.oldRent = oldRent;
    }

    public BigDecimal getOldRent() {
        return oldRent;
    }

    public void setNewRent(BigDecimal newRent) {
        this.newRent = newRent;
    }

    public BigDecimal getNewRent() {
        return newRent;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
            .append("exchangeId", getExchangeId())
            .append("tenantId", getTenantId())
            .append("oldContractId", getOldContractId())
            .append("oldHouseId", getOldHouseId())
            .append("oldHouseCode", getOldHouseCode())
            .append("newHouseId", getNewHouseId())
            .append("newHouseCode", getNewHouseCode())
            .append("exchangeReason", getExchangeReason())
            .append("applyTime", getApplyTime())
            .append("approveResult", getApproveResult())
            .append("approveOpinion", getApproveOpinion())
            .append("approveBy", getApproveBy())
            .append("approveTime", getApproveTime())
            .append("exchangeTime", getExchangeTime())
            .append("newContractId", getNewContractId())
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
