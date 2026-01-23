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

    // ==================== 原房源详细信息 ====================

    /** 原项目ID */
    @TableField(exist = false)
    private Long oldProjectId;

    /** 原项目名称 */
    @TableField(exist = false)
    private String oldProjectName;

    /** 原楼栋ID */
    @TableField(exist = false)
    private Long oldBuildingId;

    /** 原楼栋名称 */
    @TableField(exist = false)
    private String oldBuildingName;

    /** 原单元ID */
    @TableField(exist = false)
    private Long oldUnitId;

    /** 原单元名称 */
    @TableField(exist = false)
    private String oldUnitName;

    /** 原房间号 */
    @TableField(exist = false)
    private String oldHouseNo;

    /** 原房源完整地址 */
    @TableField(exist = false)
    private String oldFullAddress;

    /** 合同开始日期 */
    @TableField(exist = false)
    private String contractStartDate;

    /** 合同结束日期 */
    @TableField(exist = false)
    private String contractEndDate;

    // ==================== 目标房源详细信息 ====================

    /** 目标项目ID */
    @TableField(exist = false)
    private Long newProjectId;

    /** 目标项目名称 */
    @TableField(exist = false)
    private String newProjectName;

    /** 目标楼栋ID */
    @TableField(exist = false)
    private Long newBuildingId;

    /** 目标楼栋名称 */
    @TableField(exist = false)
    private String newBuildingName;

    /** 目标单元ID */
    @TableField(exist = false)
    private Long newUnitId;

    /** 目标单元名称 */
    @TableField(exist = false)
    private String newUnitName;

    /** 目标房间号 */
    @TableField(exist = false)
    private String newHouseNo;

    /** 目标房源完整地址 */
    @TableField(exist = false)
    private String newFullAddress;

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

    // ==================== 原房源详细信息 getter/setter ====================

    public Long getOldProjectId() {
        return oldProjectId;
    }

    public void setOldProjectId(Long oldProjectId) {
        this.oldProjectId = oldProjectId;
    }

    public String getOldProjectName() {
        return oldProjectName;
    }

    public void setOldProjectName(String oldProjectName) {
        this.oldProjectName = oldProjectName;
    }

    public Long getOldBuildingId() {
        return oldBuildingId;
    }

    public void setOldBuildingId(Long oldBuildingId) {
        this.oldBuildingId = oldBuildingId;
    }

    public String getOldBuildingName() {
        return oldBuildingName;
    }

    public void setOldBuildingName(String oldBuildingName) {
        this.oldBuildingName = oldBuildingName;
    }

    public Long getOldUnitId() {
        return oldUnitId;
    }

    public void setOldUnitId(Long oldUnitId) {
        this.oldUnitId = oldUnitId;
    }

    public String getOldUnitName() {
        return oldUnitName;
    }

    public void setOldUnitName(String oldUnitName) {
        this.oldUnitName = oldUnitName;
    }

    public String getOldHouseNo() {
        return oldHouseNo;
    }

    public void setOldHouseNo(String oldHouseNo) {
        this.oldHouseNo = oldHouseNo;
    }

    public String getOldFullAddress() {
        return oldFullAddress;
    }

    public void setOldFullAddress(String oldFullAddress) {
        this.oldFullAddress = oldFullAddress;
    }

    public String getContractStartDate() {
        return contractStartDate;
    }

    public void setContractStartDate(String contractStartDate) {
        this.contractStartDate = contractStartDate;
    }

    public String getContractEndDate() {
        return contractEndDate;
    }

    public void setContractEndDate(String contractEndDate) {
        this.contractEndDate = contractEndDate;
    }

    // ==================== 目标房源详细信息 getter/setter ====================

    public Long getNewProjectId() {
        return newProjectId;
    }

    public void setNewProjectId(Long newProjectId) {
        this.newProjectId = newProjectId;
    }

    public String getNewProjectName() {
        return newProjectName;
    }

    public void setNewProjectName(String newProjectName) {
        this.newProjectName = newProjectName;
    }

    public Long getNewBuildingId() {
        return newBuildingId;
    }

    public void setNewBuildingId(Long newBuildingId) {
        this.newBuildingId = newBuildingId;
    }

    public String getNewBuildingName() {
        return newBuildingName;
    }

    public void setNewBuildingName(String newBuildingName) {
        this.newBuildingName = newBuildingName;
    }

    public Long getNewUnitId() {
        return newUnitId;
    }

    public void setNewUnitId(Long newUnitId) {
        this.newUnitId = newUnitId;
    }

    public String getNewUnitName() {
        return newUnitName;
    }

    public void setNewUnitName(String newUnitName) {
        this.newUnitName = newUnitName;
    }

    public String getNewHouseNo() {
        return newHouseNo;
    }

    public void setNewHouseNo(String newHouseNo) {
        this.newHouseNo = newHouseNo;
    }

    public String getNewFullAddress() {
        return newFullAddress;
    }

    public void setNewFullAddress(String newFullAddress) {
        this.newFullAddress = newFullAddress;
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
