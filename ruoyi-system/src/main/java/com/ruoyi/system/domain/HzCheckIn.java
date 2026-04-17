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
 * 入住记录对象 hz_checkin_record
 *
 * @author ruoyi
 */
@TableName("hz_checkin_record")
public class HzCheckIn extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /** 入住记录ID */
    @TableId(type = IdType.AUTO)
    private Long recordId;

    /** 入住单号 */
    @TableField("checkin_no")
    private String checkinNo;

    /** 申请ID */
    @TableField("apply_id")
    private Long applyId;

    /** 合同ID */
    @TableField("contract_id")
    private Long contractId;

    /** 租户ID */
    @TableField("tenant_id")
    private Long tenantId;

    /** 房源ID */
    @TableField("house_id")
    private Long houseId;

    /** 入住日期 */
    @TableField("checkin_date")
    private String checkinDate;

    /** 实际入住日期(用户选择) */
    @TableField("actual_checkin_date")
    private String actualCheckinDate;

    /** 合住人信息(JSON数组) */
    @TableField("roommate_info")
    private String roommateInfo;

    /** 紧急联系人姓名 */
    @TableField("emergency_contact_name")
    private String emergencyContactName;

    /** 紧急联系人关系 */
    @TableField("emergency_contact_relation")
    private String emergencyContactRelation;

    /** 紧急联系人电话 */
    @TableField("emergency_contact_phone")
    private String emergencyContactPhone;

    /** 入住时间 */
    @TableField("checkin_time")
    private String checkinTime;

    /** 电表读数 */
    @TableField("meter_reading_electric")
    private String meterReadingElectric;

    /** 水表读数 */
    @TableField("meter_reading_water")
    private String meterReadingWater;

    /** 燃气表读数 */
    @TableField("meter_reading_gas")
    private String meterReadingGas;

    /** 钥匙数量 */
    @TableField("key_count")
    private Integer keyCount;

    /** 物品清单ID */
    @TableField("inventory_list_id")
    private Long inventoryListId;

    /** 入住照片 */
    @TableField("checkin_photos")
    private String checkinPhotos;

    /** 租户签名 */
    @TableField("tenant_signature")
    private String tenantSignature;

    /** 管理员签名 */
    @TableField("manager_signature")
    private String managerSignature;

    /** 管理员ID */
    @TableField("manager_id")
    private Long managerId;

    /** 管理员姓名 */
    @TableField("manager_name")
    private String managerName;

    /** 状态(0=待办理,1=待审核,2=已入住,3=已拒绝) */
    @TableField("status")
    private String status;

    /** 审核人 */
    @TableField("audit_by")
    private String auditBy;

    /** 审核时间 */
    @TableField("audit_time")
    private String auditTime;

    /** 审核备注 */
    @TableField("audit_remark")
    private String auditRemark;

    /** 删除标志(0:存在 2:删除) */
    @TableField("del_flag")
    private String delFlag;

    /** 合同编号（关联查询字段，非数据库字段） */
    @TableField(exist = false)
    private String contractNo;

    /** 用户昵称（关联查询字段，非数据库字段） */
    @TableField(exist = false)
    private String tenantNickname;

    /** 房源名称（关联查询字段，非数据库字段） */
    @TableField(exist = false)
    private String houseName;

    /** 真实姓名 hz_user.real_name */
    @TableField(exist = false)
    private String realName;

    /** 身份证号 hz_user.id_card */
    @TableField(exist = false)
    private String idCard;

    /** 联系电话 hz_user.phone */
    @TableField(exist = false)
    private String phone;

    /** 项目名 hz_project.project_name */
    @TableField(exist = false)
    private String projectName;

    /** 楼栋名 hz_building.building_name */
    @TableField(exist = false)
    private String buildingName;

    /** 单元名 hz_unit.unit_name */
    @TableField(exist = false)
    private String unitName;

    /** 楼层 hz_house.floor */
    @TableField(exist = false)
    private String floor;

    /** 朝向 hz_house.orientation */
    @TableField(exist = false)
    private String orientation;

    /** 面积 hz_house.area */
    @TableField(exist = false)
    private BigDecimal area;

    /** 合同开始日期 hz_contract.start_date */
    @TableField(exist = false)
    private String startDate;

    /** 合同结束日期 hz_contract.end_date */
    @TableField(exist = false)
    private String endDate;

    /** 剩余天数 DATEDIFF(end_date, CURDATE()) */
    @TableField(exist = false)
    private Integer remainingDays;

    public void setRecordId(Long recordId) {
        this.recordId = recordId;
    }

    public Long getRecordId() {
        return recordId;
    }

    public void setCheckinNo(String checkinNo) {
        this.checkinNo = checkinNo;
    }

    public String getCheckinNo() {
        return checkinNo;
    }

    public void setApplyId(Long applyId) {
        this.applyId = applyId;
    }

    public Long getApplyId() {
        return applyId;
    }

    public void setContractId(Long contractId) {
        this.contractId = contractId;
    }

    public Long getContractId() {
        return contractId;
    }

    public void setTenantId(Long tenantId) {
        this.tenantId = tenantId;
    }

    public Long getTenantId() {
        return tenantId;
    }

    public void setHouseId(Long houseId) {
        this.houseId = houseId;
    }

    public Long getHouseId() {
        return houseId;
    }

    public void setCheckinDate(String checkinDate) {
        this.checkinDate = checkinDate;
    }

    public String getCheckinDate() {
        return checkinDate;
    }

    public void setActualCheckinDate(String actualCheckinDate) {
        this.actualCheckinDate = actualCheckinDate;
    }

    public String getActualCheckinDate() {
        return actualCheckinDate;
    }

    public void setRoommateInfo(String roommateInfo) {
        this.roommateInfo = roommateInfo;
    }

    public String getRoommateInfo() {
        return roommateInfo;
    }

    public void setEmergencyContactName(String emergencyContactName) {
        this.emergencyContactName = emergencyContactName;
    }

    public String getEmergencyContactName() {
        return emergencyContactName;
    }

    public void setEmergencyContactRelation(String emergencyContactRelation) {
        this.emergencyContactRelation = emergencyContactRelation;
    }

    public String getEmergencyContactRelation() {
        return emergencyContactRelation;
    }

    public void setEmergencyContactPhone(String emergencyContactPhone) {
        this.emergencyContactPhone = emergencyContactPhone;
    }

    public String getEmergencyContactPhone() {
        return emergencyContactPhone;
    }

    public void setCheckinTime(String checkinTime) {
        this.checkinTime = checkinTime;
    }

    public String getCheckinTime() {
        return checkinTime;
    }

    public void setMeterReadingElectric(String meterReadingElectric) {
        this.meterReadingElectric = meterReadingElectric;
    }

    public String getMeterReadingElectric() {
        return meterReadingElectric;
    }

    public void setMeterReadingWater(String meterReadingWater) {
        this.meterReadingWater = meterReadingWater;
    }

    public String getMeterReadingWater() {
        return meterReadingWater;
    }

    public void setMeterReadingGas(String meterReadingGas) {
        this.meterReadingGas = meterReadingGas;
    }

    public String getMeterReadingGas() {
        return meterReadingGas;
    }

    public void setKeyCount(Integer keyCount) {
        this.keyCount = keyCount;
    }

    public Integer getKeyCount() {
        return keyCount;
    }

    public void setInventoryListId(Long inventoryListId) {
        this.inventoryListId = inventoryListId;
    }

    public Long getInventoryListId() {
        return inventoryListId;
    }

    public void setCheckinPhotos(String checkinPhotos) {
        this.checkinPhotos = checkinPhotos;
    }

    public String getCheckinPhotos() {
        return checkinPhotos;
    }

    public void setTenantSignature(String tenantSignature) {
        this.tenantSignature = tenantSignature;
    }

    public String getTenantSignature() {
        return tenantSignature;
    }

    public void setManagerSignature(String managerSignature) {
        this.managerSignature = managerSignature;
    }

    public String getManagerSignature() {
        return managerSignature;
    }

    public void setManagerId(Long managerId) {
        this.managerId = managerId;
    }

    public Long getManagerId() {
        return managerId;
    }

    public void setManagerName(String managerName) {
        this.managerName = managerName;
    }

    public String getManagerName() {
        return managerName;
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

    public void setAuditBy(String auditBy) {
        this.auditBy = auditBy;
    }

    public String getAuditBy() {
        return auditBy;
    }

    public void setAuditTime(String auditTime) {
        this.auditTime = auditTime;
    }

    public String getAuditTime() {
        return auditTime;
    }

    public void setAuditRemark(String auditRemark) {
        this.auditRemark = auditRemark;
    }

    public String getAuditRemark() {
        return auditRemark;
    }

    public void setContractNo(String contractNo) {
        this.contractNo = contractNo;
    }

    public String getContractNo() {
        return contractNo;
    }

    public void setTenantNickname(String tenantNickname) {
        this.tenantNickname = tenantNickname;
    }

    public String getTenantNickname() {
        return tenantNickname;
    }

    public void setHouseName(String houseName) {
        this.houseName = houseName;
    }

    public String getHouseName() {
        return houseName;
    }

    public String getRealName() {
        return realName;
    }

    public void setRealName(String realName) {
        this.realName = realName;
    }

    public String getIdCard() {
        return idCard;
    }

    public void setIdCard(String idCard) {
        this.idCard = idCard;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public String getBuildingName() {
        return buildingName;
    }

    public void setBuildingName(String buildingName) {
        this.buildingName = buildingName;
    }

    public String getUnitName() {
        return unitName;
    }

    public void setUnitName(String unitName) {
        this.unitName = unitName;
    }

    public String getFloor() {
        return floor;
    }

    public void setFloor(String floor) {
        this.floor = floor;
    }

    public String getOrientation() {
        return orientation;
    }

    public void setOrientation(String orientation) {
        this.orientation = orientation;
    }

    public BigDecimal getArea() {
        return area;
    }

    public void setArea(BigDecimal area) {
        this.area = area;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public Integer getRemainingDays() {
        return remainingDays;
    }

    public void setRemainingDays(Integer remainingDays) {
        this.remainingDays = remainingDays;
    }


    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
            .append("recordId", getRecordId())
            .append("checkinNo", getCheckinNo())
            .append("applyId", getApplyId())
            .append("contractId", getContractId())
            .append("tenantId", getTenantId())
            .append("houseId", getHouseId())
            .append("checkinDate", getCheckinDate())
            .append("actualCheckinDate", getActualCheckinDate())
            .append("roommateInfo", getRoommateInfo())
            .append("emergencyContactName", getEmergencyContactName())
            .append("emergencyContactRelation", getEmergencyContactRelation())
            .append("emergencyContactPhone", getEmergencyContactPhone())
            .append("checkinTime", getCheckinTime())
            .append("meterReadingElectric", getMeterReadingElectric())
            .append("meterReadingWater", getMeterReadingWater())
            .append("meterReadingGas", getMeterReadingGas())
            .append("keyCount", getKeyCount())
            .append("inventoryListId", getInventoryListId())
            .append("checkinPhotos", getCheckinPhotos())
            .append("tenantSignature", getTenantSignature())
            .append("managerSignature", getManagerSignature())
            .append("managerId", getManagerId())
            .append("managerName", getManagerName())
            .append("status", getStatus())
            .append("auditBy", getAuditBy())
            .append("auditTime", getAuditTime())
            .append("auditRemark", getAuditRemark())
            .append("delFlag", getDelFlag())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("updateBy", getUpdateBy())
            .append("updateTime", getUpdateTime())
            .append("remark", getRemark())
            .toString();
    }
}
