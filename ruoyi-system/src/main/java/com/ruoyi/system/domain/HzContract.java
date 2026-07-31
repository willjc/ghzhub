package com.ruoyi.system.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.math.BigDecimal;

/**
 * 合同对象 hz_contract
 *
 * @author ruoyi
 */
@TableName("hz_contract")
public class HzContract extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /** 合同ID */
    @TableId(type = IdType.AUTO)
    private Long contractId;

    /** 合同编号 */
    @Excel(name = "合同编号")
    @TableField("contract_no")
    private String contractNo;

    /** 合同类型(1:首次签约 2:续租 3:换房) */
    @Excel(name = "合同类型", readConverterExp = "1=首次签约,2=续租,3=换房")
    @TableField("contract_type")
    private String contractType;

    /** 模板ID */
    @TableField("template_id")
    private Long templateId;

    /** 配租批次ID */
    @TableField("batch_id")
    private Long batchId;

    /** 租户ID */
    @TableField("tenant_id")
    private Long tenantId;

    /** 租户姓名 */
    @Excel(name = "租户姓名")
    @TableField("tenant_name")
    private String tenantName;

    /** 租户身份证号 */
    @Excel(name = "身份证号")
    @TableField("tenant_id_card")
    private String tenantIdCard;

    /** 租户电话 */
    @Excel(name = "联系电话")
    @TableField("tenant_phone")
    private String tenantPhone;

    /** 项目ID */
    @TableField("project_id")
    private Long projectId;

    /** 房源ID */
    @TableField("house_id")
    private Long houseId;

    /** 房源编号 */
    @TableField("house_code")
    private String houseCode;

    /** 房源地址 */
    @Excel(name = "房源地址")
    @TableField("house_address")
    private String houseAddress;

    /** 租金 */
    @Excel(name = "月租金")
    @TableField("rent_price")
    private BigDecimal rentPrice;

    /** 押金 */
    @Excel(name = "押金")
    @TableField("deposit")
    private BigDecimal deposit;

    /** 合同开始日期 */
    @Excel(name = "合同开始日期")
    @TableField("start_date")
    private String startDate;

    /** 合同结束日期 */
    @Excel(name = "合同结束日期")
    @TableField("end_date")
    private String endDate;

    /** 租期(月) */
    @Excel(name = "租期(月)")
    @TableField("rent_months")
    private Integer rentMonths;

    /** 缴费周期(1:月付 2:季付 3:半年付 4:年付) */
    @Excel(name = "缴费周期", readConverterExp = "1=月付,2=季付,3=半年付,4=年付")
    @TableField("payment_cycle")
    private String paymentCycle;

    /** 租金支付日 */
    @TableField("payment_day")
    private Integer paymentDay;

    /** 免租期数 */
    @TableField("free_rent_periods")
    private Integer freeRentPeriods;

    /** 合同内容 */
    @TableField("contract_content")
    private String contractContent;

    /** 合同文件 */
    @TableField("contract_file")
    private String contractFile;

    /** 租户签名 */
    @TableField("tenant_signature")
    private String tenantSignature;

    /** 房东签名 */
    @TableField("landlord_signature")
    private String landlordSignature;

    /** 签署时间 */
    @TableField("sign_time")
    private String signTime;

    /** 合同状态(0:草稿 1:待签署 2:已签署 3:履行中 4:已到期 5:已解约) */
    @Excel(name = "合同状态", readConverterExp = "0=草稿,1=待签署,2=已签署,3=履行中,4=已到期,5=已解约")
    @TableField("contract_status")
    private String contractStatus;

    /** 是否续租(0:否 1:是) */
    @TableField("is_renewed")
    private String isRenewed;

    /** 续租合同ID */
    @TableField("renewed_contract_id")
    private Long renewedContractId;

    /** 删除标志(0:存在 2:删除) */
    @TableField("del_flag")
    private String delFlag;

    /** e签宝签署流程ID */
    @TableField("esign_flow_id")
    private String esignFlowId;

    // ========== 以下字段来自房源表，不存储在合同表中 ==========
    /** 项目名称（来自房源表） */
    @Excel(name = "所属项目")
    @TableField(exist = false)
    private String projectName;

    /** 楼栋ID（来自房源表） */
    @TableField(exist = false)
    private Long buildingId;

    /** 楼栋名称（来自楼栋表） */
    @TableField(exist = false)
    private String buildingName;

    /** 单元ID（来自房源表） */
    @TableField(exist = false)
    private Long unitId;

    /** 单元名称（来自单元表） */
    @TableField(exist = false)
    private String unitName;

    /** 房间号（来自房源表） */
    @TableField(exist = false)
    private String houseNo;

    /** 楼层（来自房源表） */
    @TableField(exist = false)
    private Integer floor;

    /** 户型名称（来自房源表） */
    @TableField(exist = false)
    private String houseTypeName;

    /** 面积（来自房源表） */
    @TableField(exist = false)
    private java.math.BigDecimal area;

    /** 朝向（来自房源表） */
    @TableField(exist = false)
    private String orientation;

    /** 装修情况（来自房源表） */
    @TableField(exist = false)
    private String decoration;

    /** 房间设施（来自房源表） */
    @TableField(exist = false)
    private String facilities;

    /** 配租方式（常规分配/集中分配，由 batch_id + remark 首段推断，非持久化） */
    @Excel(name = "配租方式")
    @TableField(exist = false)
    private String allocationType;

    // ========== 以下退租信息来自退租申请表 hz_checkout_apply（仅已完成退租 apply_status=5 有值），非持久化，仅导出使用 ==========
    /** 退租日期（实际退租日期，空则计划退租日期） */
    @Excel(name = "退租日期", width = 20)
    @TableField(exist = false)
    private String checkoutDate;

    /** 应退押金（deposit_refund） */
    @Excel(name = "应退押金")
    @TableField(exist = false)
    private java.math.BigDecimal checkoutDepositRefund;

    /** 应退租金（rent_refund） */
    @Excel(name = "应退租金")
    @TableField(exist = false)
    private java.math.BigDecimal checkoutRentRefund;

    /** 应退总额（refund_amount） */
    @Excel(name = "应退总额")
    @TableField(exist = false)
    private java.math.BigDecimal checkoutRefundAmount;

    public void setContractId(Long contractId) {
        this.contractId = contractId;
    }

    public Long getContractId() {
        return contractId;
    }

    public void setContractNo(String contractNo) {
        this.contractNo = contractNo;
    }

    public String getContractNo() {
        return contractNo;
    }

    public void setContractType(String contractType) {
        this.contractType = contractType;
    }

    public String getContractType() {
        return contractType;
    }

    public void setTemplateId(Long templateId) {
        this.templateId = templateId;
    }

    public Long getTemplateId() {
        return templateId;
    }

    public void setBatchId(Long batchId) {
        this.batchId = batchId;
    }

    public Long getBatchId() {
        return batchId;
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

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    public Long getProjectId() {
        return projectId;
    }

    public void setHouseId(Long houseId) {
        this.houseId = houseId;
    }

    public Long getHouseId() {
        return houseId;
    }

    public void setHouseCode(String houseCode) {
        this.houseCode = houseCode;
    }

    public String getHouseCode() {
        return houseCode;
    }

    public void setHouseAddress(String houseAddress) {
        this.houseAddress = houseAddress;
    }

    public String getHouseAddress() {
        return houseAddress;
    }

    public void setRentPrice(BigDecimal rentPrice) {
        this.rentPrice = rentPrice;
    }

    public BigDecimal getRentPrice() {
        return rentPrice;
    }

    public void setDeposit(BigDecimal deposit) {
        this.deposit = deposit;
    }

    public BigDecimal getDeposit() {
        return deposit;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setRentMonths(Integer rentMonths) {
        this.rentMonths = rentMonths;
    }

    public Integer getRentMonths() {
        return rentMonths;
    }

    public void setPaymentCycle(String paymentCycle) {
        this.paymentCycle = paymentCycle;
    }

    public String getPaymentCycle() {
        return paymentCycle;
    }

    public void setPaymentDay(Integer paymentDay) {
        this.paymentDay = paymentDay;
    }

    public Integer getPaymentDay() {
        return paymentDay;
    }

    public Integer getFreeRentPeriods() {
        return freeRentPeriods;
    }

    public void setFreeRentPeriods(Integer freeRentPeriods) {
        this.freeRentPeriods = freeRentPeriods;
    }

    public void setContractContent(String contractContent) {
        this.contractContent = contractContent;
    }

    public String getContractContent() {
        return contractContent;
    }

    public void setContractFile(String contractFile) {
        this.contractFile = contractFile;
    }

    public String getContractFile() {
        return contractFile;
    }

    public void setTenantSignature(String tenantSignature) {
        this.tenantSignature = tenantSignature;
    }

    public String getTenantSignature() {
        return tenantSignature;
    }

    public void setLandlordSignature(String landlordSignature) {
        this.landlordSignature = landlordSignature;
    }

    public String getLandlordSignature() {
        return landlordSignature;
    }

    public void setSignTime(String signTime) {
        this.signTime = signTime;
    }

    public String getSignTime() {
        return signTime;
    }

    public void setContractStatus(String contractStatus) {
        this.contractStatus = contractStatus;
    }

    public String getContractStatus() {
        return contractStatus;
    }

    public void setIsRenewed(String isRenewed) {
        this.isRenewed = isRenewed;
    }

    public String getIsRenewed() {
        return isRenewed;
    }

    public void setRenewedContractId(Long renewedContractId) {
        this.renewedContractId = renewedContractId;
    }

    public Long getRenewedContractId() {
        return renewedContractId;
    }

    public void setDelFlag(String delFlag) {
        this.delFlag = delFlag;
    }

    public String getDelFlag() {
        return delFlag;
    }

    public String getEsignFlowId() {
        return esignFlowId;
    }

    public void setEsignFlowId(String esignFlowId) {
        this.esignFlowId = esignFlowId;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public Long getBuildingId() {
        return buildingId;
    }

    public void setBuildingId(Long buildingId) {
        this.buildingId = buildingId;
    }

    public String getBuildingName() {
        return buildingName;
    }

    public void setBuildingName(String buildingName) {
        this.buildingName = buildingName;
    }

    public Long getUnitId() {
        return unitId;
    }

    public void setUnitId(Long unitId) {
        this.unitId = unitId;
    }

    public String getUnitName() {
        return unitName;
    }

    public void setUnitName(String unitName) {
        this.unitName = unitName;
    }

    public String getHouseNo() {
        return houseNo;
    }

    public void setHouseNo(String houseNo) {
        this.houseNo = houseNo;
    }

    public Integer getFloor() {
        return floor;
    }

    public void setFloor(Integer floor) {
        this.floor = floor;
    }

    public String getHouseTypeName() {
        return houseTypeName;
    }

    public void setHouseTypeName(String houseTypeName) {
        this.houseTypeName = houseTypeName;
    }

    public java.math.BigDecimal getArea() {
        return area;
    }

    public void setArea(java.math.BigDecimal area) {
        this.area = area;
    }

    public String getOrientation() {
        return orientation;
    }

    public void setOrientation(String orientation) {
        this.orientation = orientation;
    }

    public String getDecoration() {
        return decoration;
    }

    public void setDecoration(String decoration) {
        this.decoration = decoration;
    }

    public String getFacilities() {
        return facilities;
    }

    public void setFacilities(String facilities) {
        this.facilities = facilities;
    }

    public String getAllocationType() {
        return allocationType;
    }

    public void setAllocationType(String allocationType) {
        this.allocationType = allocationType;
    }

    public String getCheckoutDate() {
        return checkoutDate;
    }

    public void setCheckoutDate(String checkoutDate) {
        this.checkoutDate = checkoutDate;
    }

    public java.math.BigDecimal getCheckoutDepositRefund() {
        return checkoutDepositRefund;
    }

    public void setCheckoutDepositRefund(java.math.BigDecimal checkoutDepositRefund) {
        this.checkoutDepositRefund = checkoutDepositRefund;
    }

    public java.math.BigDecimal getCheckoutRentRefund() {
        return checkoutRentRefund;
    }

    public void setCheckoutRentRefund(java.math.BigDecimal checkoutRentRefund) {
        this.checkoutRentRefund = checkoutRentRefund;
    }

    public java.math.BigDecimal getCheckoutRefundAmount() {
        return checkoutRefundAmount;
    }

    public void setCheckoutRefundAmount(java.math.BigDecimal checkoutRefundAmount) {
        this.checkoutRefundAmount = checkoutRefundAmount;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
            .append("contractId", getContractId())
            .append("contractNo", getContractNo())
            .append("contractType", getContractType())
            .append("templateId", getTemplateId())
            .append("tenantId", getTenantId())
            .append("tenantName", getTenantName())
            .append("tenantIdCard", getTenantIdCard())
            .append("tenantPhone", getTenantPhone())
            .append("projectId", getProjectId())
            .append("houseId", getHouseId())
            .append("houseCode", getHouseCode())
            .append("houseAddress", getHouseAddress())
            .append("rentPrice", getRentPrice())
            .append("deposit", getDeposit())
            .append("startDate", getStartDate())
            .append("endDate", getEndDate())
            .append("rentMonths", getRentMonths())
            .append("paymentCycle", getPaymentCycle())
            .append("paymentDay", getPaymentDay())
            .append("contractContent", getContractContent())
            .append("contractFile", getContractFile())
            .append("tenantSignature", getTenantSignature())
            .append("landlordSignature", getLandlordSignature())
            .append("signTime", getSignTime())
            .append("contractStatus", getContractStatus())
            .append("isRenewed", getIsRenewed())
            .append("renewedContractId", getRenewedContractId())
            .append("delFlag", getDelFlag())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("updateBy", getUpdateBy())
            .append("updateTime", getUpdateTime())
            .append("remark", getRemark())
            .toString();
    }
}
