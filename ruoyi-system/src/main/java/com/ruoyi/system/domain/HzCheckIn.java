package com.ruoyi.system.domain;

import java.math.BigDecimal;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

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
    @Excel(name = "入住单号")
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
    @Excel(name = "入住日期")
    @TableField("checkin_date")
    private String checkinDate;

    /** 实际入住日期(用户选择) */
    @Excel(name = "实际入住日期")
    @TableField("actual_checkin_date")
    private String actualCheckinDate;

    /** 合住人信息(JSON数组) */
    @TableField("roommate_info")
    private String roommateInfo;

    /** 紧急联系人姓名 */
    @Excel(name = "紧急联系人")
    @TableField("emergency_contact_name")
    private String emergencyContactName;

    /** 紧急联系人关系 */
    @Excel(name = "紧急联系人关系")
    @TableField("emergency_contact_relation")
    private String emergencyContactRelation;

    /** 紧急联系人电话 */
    @Excel(name = "紧急联系人电话")
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

    /** 用户确认的设施快照(JSON) */
    @TableField("confirmed_facilities")
    private String confirmedFacilities;

    /** 管理员签名 */
    @TableField("manager_signature")
    private String managerSignature;

    /** 管理员ID */
    @TableField("manager_id")
    private Long managerId;

    /** 管理员姓名 */
    @TableField("manager_name")
    private String managerName;

    /** 状态(0=待办理,1=待审核,2=待入住确认,3=已拒绝,4=已入住确认) */
    @Excel(name = "入住状态", readConverterExp = "0=待办理,1=待审核,2=待入住确认,3=已拒绝,4=已入住确认")
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

    /** 取消原因（系统自动取消时填充） */
    @TableField("cancel_reason")
    private String cancelReason;

    /** 取消时间（系统自动取消时填充） */
    @TableField("cancel_time")
    private String cancelTime;

    /** 合同编号（关联查询字段，非数据库字段） */
    @Excel(name = "合同编号")
    @TableField(exist = false)
    private String contractNo;

    /** 用户昵称（关联查询字段，非数据库字段） */
    @TableField(exist = false)
    private String tenantNickname;

    /** 房源名称（关联查询字段，非数据库字段） */
    @Excel(name = "房间号")
    @TableField(exist = false)
    private String houseName;

    /** 房间号（仅查询参数透传） */
    @TableField(exist = false)
    private String houseNo;

    /** 真实姓名 hz_user.real_name */
    @Excel(name = "真实姓名")
    @TableField(exist = false)
    private String realName;

    /** 身份证号 hz_user.id_card */
    @TableField(exist = false)
    private String idCard;

    /** 年龄（由身份证号计算，非数据库字段） */
    @Excel(name = "年龄")
    @TableField(exist = false)
    private Integer age;

    /** 联系电话 hz_user.phone */
    @Excel(name = "联系电话")
    @TableField(exist = false)
    private String phone;

    /** 学历 hz_user.education */
    @Excel(name = "学历", readConverterExp = "1=小学,2=初中,3=高中,4=大专,5=本科,6=硕士,7=博士")
    @TableField(exist = false)
    private String education;

    /** 身份类型 hz_user.identity_type */
    @Excel(name = "身份类型", readConverterExp = "1=在职人员,2=应届毕业生")
    @TableField(exist = false)
    private String identityType;

    /** 工作单位 hz_user.work_unit */
    @Excel(name = "工作单位")
    @TableField(exist = false)
    private String workUnit;

    /** 单位性质 hz_user.unit_nature */
    @Excel(name = "单位性质", readConverterExp = "1=机关事业单位,2=国有企业,3=私营企业,4=其他")
    @TableField(exist = false)
    private String unitNature;

    /** 项目ID hz_project.project_id */
    @TableField(exist = false)
    private Long projectId;

    /** 项目名 hz_project.project_name */
    @Excel(name = "项目名称")
    @TableField(exist = false)
    private String projectName;

    /** 楼栋名 hz_building.building_name */
    @Excel(name = "楼栋")
    @TableField(exist = false)
    private String buildingName;

    /** 单元名 hz_unit.unit_name */
    @Excel(name = "单元")
    @TableField(exist = false)
    private String unitName;

    /** 楼层 hz_house.floor */
    @Excel(name = "楼层")
    @TableField(exist = false)
    private String floor;

    /** 朝向 hz_house.orientation */
    @Excel(name = "朝向")
    @TableField(exist = false)
    private String orientation;

    /** 面积 hz_house.area */
    @Excel(name = "面积(㎡)")
    @TableField(exist = false)
    private BigDecimal area;

    /** 合同开始日期 hz_contract.start_date */
    @Excel(name = "合同开始日期")
    @TableField(exist = false)
    private String startDate;

    /** 合同结束日期 hz_contract.end_date */
    @Excel(name = "合同结束日期")
    @TableField(exist = false)
    private String endDate;

    /** 剩余天数 DATEDIFF(end_date, CURDATE()) */
    @Excel(name = "剩余天数")
    @TableField(exist = false)
    private Integer remainingDays;

    /** 是否续租(0:否 1:是)，来自 hz_contract.is_renewed */
    @TableField(exist = false)
    private String isRenewed;

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

    public void setConfirmedFacilities(String confirmedFacilities) {
        this.confirmedFacilities = confirmedFacilities;
    }

    public String getConfirmedFacilities() {
        return confirmedFacilities;
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

    public String getCancelReason() {
        return cancelReason;
    }

    public void setCancelReason(String cancelReason) {
        this.cancelReason = cancelReason;
    }

    public String getCancelTime() {
        return cancelTime;
    }

    public void setCancelTime(String cancelTime) {
        this.cancelTime = cancelTime;
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

    public void setHouseNo(String houseNo) {
        this.houseNo = houseNo;
    }

    public String getHouseNo() {
        return houseNo;
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

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEducation() {
        return education;
    }

    public void setEducation(String education) {
        this.education = education;
    }

    public String getIdentityType() {
        return identityType;
    }

    public void setIdentityType(String identityType) {
        this.identityType = identityType;
    }

    public String getWorkUnit() {
        return workUnit;
    }

    public void setWorkUnit(String workUnit) {
        this.workUnit = workUnit;
    }

    public String getUnitNature() {
        return unitNature;
    }

    public void setUnitNature(String unitNature) {
        this.unitNature = unitNature;
    }

    public Long getProjectId() {
        return projectId;
    }

public void setProjectId(Long projectId) {
        this.projectId = projectId;
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

    public String getIsRenewed() {
        return isRenewed;
    }

    public void setIsRenewed(String isRenewed) {
        this.isRenewed = isRenewed;
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
