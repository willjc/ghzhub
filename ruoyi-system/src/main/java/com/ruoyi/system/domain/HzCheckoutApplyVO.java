package com.ruoyi.system.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.ruoyi.common.annotation.Excel;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 退租申请视图对象
 * 包含退租申请、合同、房源的完整信息
 *
 * @author ruoyi
 */
public class HzCheckoutApplyVO {

    /** 退租申请ID */
    private Long applyId;

    /** 合同ID */
    private Long contractId;

    /** 租户ID */
    private Long tenantId;

    /** 房源ID */
    private Long houseId;

    /** 申请时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Excel(name = "申请时间", width = 30, dateFormat = "yyyy-MM-dd HH:mm:ss")
    private Date applyTime;

    /** 计划退租日期 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "计划退租日期", width = 30, dateFormat = "yyyy-MM-dd")
    private Date planCheckoutDate;

    /** 退租原因 */
    @Excel(name = "退租原因")
    private String checkoutReason;

    /** 申请状态 */
    private String applyStatus;

    /** 审批人 */
    private String approveBy;

    /** 审批时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date approveTime;

    /** 审批意见 */
    private String approveOpinion;

    // ========== 合同信息 ==========

    /** 合同编号 */
    @Excel(name = "合同编号")
    private String contractNo;

    /** 合同开始日期 */
    private String startDate;

    /** 合同结束日期 */
    private String endDate;

    /** 租期(月) */
    @Excel(name = "租期(月)")
    private Integer rentMonths;

    /** 月租金 */
    @Excel(name = "月租金")
    private BigDecimal rentPrice;

    /** 押金 */
    @Excel(name = "押金")
    private BigDecimal deposit;

    /** 缴费周期 (1=押一付一, 2=押一付二, 3=押一付三, 6=半年付, 12=年付) */
    private String paymentCycle;

    /** 缴费周期文本 */
    private String paymentCycleText;

    // ========== 房源信息 ==========

    /** 房源编号 */
    @Excel(name = "房源编号")
    private String houseCode;

    /** 房间号 */
    @Excel(name = "房间号")
    private String houseNo;

    /** 楼层 */
    private Integer floor;

    /** 户型名称 */
    private String houseTypeName;

    /** 面积 */
    private BigDecimal area;

    /** 朝向 */
    private String orientation;

    /** 装修情况 */
    private String decoration;

    /** 房间设施 */
    @Excel(name = "房间设施")
    private String facilities;

    // ========== 费用信息 ==========

    /** 违约金 */
    @Excel(name = "违约金(元)")
    private BigDecimal penaltyAmount;

    /** 未付账单 */
    @Excel(name = "未付账单(元)")
    private BigDecimal unpaidBills;

    /** 押金退款 */
    @Excel(name = "押金退款(元)")
    private BigDecimal depositRefund;

    /** 水费 */
    private BigDecimal waterFee;

    /** 电费 */
    private BigDecimal electricFee;

    /** 燃气费 */
    private BigDecimal gasFee;

    /** 暖气费 */
    private BigDecimal heatingFee;

    /** 物业费 */
    private BigDecimal propertyFee;

    /** 损坏扣款 */
    private BigDecimal damageDeduction;

    /** 物品损坏情况 */
    private String damageDescription;

    /** 应退总额（管理员可编辑） */
    @Excel(name = "应退总额(元)")
    private BigDecimal refundAmount;

    // ========== 表读数 ==========

    /** 水表读数 */
    private BigDecimal meterReadingWater;

    /** 电表读数 */
    private BigDecimal meterReadingElectric;

    /** 燃气表读数 */
    private BigDecimal meterReadingGas;

    /** 钥匙归还数量 */
    private Integer keyReturned;

    /** 租户签名（base64图片数据） */
    private String tenantSignature;

    // Getters and Setters

    public Long getApplyId() {
        return applyId;
    }

    public void setApplyId(Long applyId) {
        this.applyId = applyId;
    }

    public Long getContractId() {
        return contractId;
    }

    public void setContractId(Long contractId) {
        this.contractId = contractId;
    }

    public Long getTenantId() {
        return tenantId;
    }

    public void setTenantId(Long tenantId) {
        this.tenantId = tenantId;
    }

    public Long getHouseId() {
        return houseId;
    }

    public void setHouseId(Long houseId) {
        this.houseId = houseId;
    }

    public Date getApplyTime() {
        return applyTime;
    }

    public void setApplyTime(Date applyTime) {
        this.applyTime = applyTime;
    }

    public Date getPlanCheckoutDate() {
        return planCheckoutDate;
    }

    public void setPlanCheckoutDate(Date planCheckoutDate) {
        this.planCheckoutDate = planCheckoutDate;
    }

    public String getCheckoutReason() {
        return checkoutReason;
    }

    public void setCheckoutReason(String checkoutReason) {
        this.checkoutReason = checkoutReason;
    }

    public String getApplyStatus() {
        return applyStatus;
    }

    public void setApplyStatus(String applyStatus) {
        this.applyStatus = applyStatus;
    }

    public String getApproveBy() {
        return approveBy;
    }

    public void setApproveBy(String approveBy) {
        this.approveBy = approveBy;
    }

    public Date getApproveTime() {
        return approveTime;
    }

    public void setApproveTime(Date approveTime) {
        this.approveTime = approveTime;
    }

    public String getApproveOpinion() {
        return approveOpinion;
    }

    public void setApproveOpinion(String approveOpinion) {
        this.approveOpinion = approveOpinion;
    }

    public String getContractNo() {
        return contractNo;
    }

    public void setContractNo(String contractNo) {
        this.contractNo = contractNo;
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

    public Integer getRentMonths() {
        return rentMonths;
    }

    public void setRentMonths(Integer rentMonths) {
        this.rentMonths = rentMonths;
    }

    public BigDecimal getRentPrice() {
        return rentPrice;
    }

    public void setRentPrice(BigDecimal rentPrice) {
        this.rentPrice = rentPrice;
    }

    public BigDecimal getDeposit() {
        return deposit;
    }

    public void setDeposit(BigDecimal deposit) {
        this.deposit = deposit;
    }

    public String getPaymentCycle() {
        return paymentCycle;
    }

    public void setPaymentCycle(String paymentCycle) {
        this.paymentCycle = paymentCycle;
    }

    public String getPaymentCycleText() {
        return paymentCycleText;
    }

    public void setPaymentCycleText(String paymentCycleText) {
        this.paymentCycleText = paymentCycleText;
    }

    public String getHouseCode() {
        return houseCode;
    }

    public void setHouseCode(String houseCode) {
        this.houseCode = houseCode;
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

    public BigDecimal getArea() {
        return area;
    }

    public void setArea(BigDecimal area) {
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

    public BigDecimal getPenaltyAmount() {
        return penaltyAmount;
    }

    public void setPenaltyAmount(BigDecimal penaltyAmount) {
        this.penaltyAmount = penaltyAmount;
    }

    public BigDecimal getUnpaidBills() {
        return unpaidBills;
    }

    public void setUnpaidBills(BigDecimal unpaidBills) {
        this.unpaidBills = unpaidBills;
    }

    public BigDecimal getDepositRefund() {
        return depositRefund;
    }

    public void setDepositRefund(BigDecimal depositRefund) {
        this.depositRefund = depositRefund;
    }

    public BigDecimal getWaterFee() {
        return waterFee;
    }

    public void setWaterFee(BigDecimal waterFee) {
        this.waterFee = waterFee;
    }

    public BigDecimal getElectricFee() {
        return electricFee;
    }

    public void setElectricFee(BigDecimal electricFee) {
        this.electricFee = electricFee;
    }

    public BigDecimal getGasFee() {
        return gasFee;
    }

    public void setGasFee(BigDecimal gasFee) {
        this.gasFee = gasFee;
    }

    public BigDecimal getHeatingFee() {
        return heatingFee;
    }

    public void setHeatingFee(BigDecimal heatingFee) {
        this.heatingFee = heatingFee;
    }

    public BigDecimal getPropertyFee() {
        return propertyFee;
    }

    public void setPropertyFee(BigDecimal propertyFee) {
        this.propertyFee = propertyFee;
    }

    public BigDecimal getDamageDeduction() {
        return damageDeduction;
    }

    public void setDamageDeduction(BigDecimal damageDeduction) {
        this.damageDeduction = damageDeduction;
    }

    public String getDamageDescription() {
        return damageDescription;
    }

    public void setDamageDescription(String damageDescription) {
        this.damageDescription = damageDescription;
    }

    public BigDecimal getRefundAmount() {
        return refundAmount;
    }

    public void setRefundAmount(BigDecimal refundAmount) {
        this.refundAmount = refundAmount;
    }

    public BigDecimal getMeterReadingWater() {
        return meterReadingWater;
    }

    public void setMeterReadingWater(BigDecimal meterReadingWater) {
        this.meterReadingWater = meterReadingWater;
    }

    public BigDecimal getMeterReadingElectric() {
        return meterReadingElectric;
    }

    public void setMeterReadingElectric(BigDecimal meterReadingElectric) {
        this.meterReadingElectric = meterReadingElectric;
    }

    public BigDecimal getMeterReadingGas() {
        return meterReadingGas;
    }

    public void setMeterReadingGas(BigDecimal meterReadingGas) {
        this.meterReadingGas = meterReadingGas;
    }

    public Integer getKeyReturned() {
        return keyReturned;
    }

    public void setKeyReturned(Integer keyReturned) {
        this.keyReturned = keyReturned;
    }

    public String getTenantSignature() {
        return tenantSignature;
    }

    public void setTenantSignature(String tenantSignature) {
        this.tenantSignature = tenantSignature;
    }
}
