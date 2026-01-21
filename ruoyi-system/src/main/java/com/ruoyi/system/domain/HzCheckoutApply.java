package com.ruoyi.system.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 退租申请对象 hz_checkout_apply
 *
 * @author ruoyi
 */
@TableName("hz_checkout_apply")
public class HzCheckoutApply extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /** 申请ID */
    @TableId(type = IdType.AUTO)
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
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Excel(name = "申请时间", width = 30, dateFormat = "yyyy-MM-dd HH:mm:ss")
    @TableField("apply_time")
    private Date applyTime;

    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "计划退租日期", width = 30, dateFormat = "yyyy-MM-dd")
    @TableField("plan_checkout_date")
    private Date planCheckoutDate;

    @Excel(name = "退租原因")
    @TableField("checkout_reason")
    private String checkoutReason;

    @Excel(name = "是否提前退租", readConverterExp = "0=否,1=是")
    @TableField("is_early_termination")
    private String isEarlyTermination;

    @Excel(name = "违约金")
    @TableField("penalty_amount")
    private BigDecimal penaltyAmount;

    @Excel(name = "未付账单")
    @TableField("unpaid_bills")
    private BigDecimal unpaidBills;

    @Excel(name = "押金退款")
    @TableField("deposit_refund")
    private BigDecimal depositRefund;

    @Excel(name = "申请状态", readConverterExp = "0=审批中,1=审批通过,2=审批驳回,3=已取消,4=待确认")
    @TableField("apply_status")
    private String applyStatus;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Excel(name = "审批时间", width = 30, dateFormat = "yyyy-MM-dd HH:mm:ss")
    @TableField("approve_time")
    private Date approveTime;

    @Excel(name = "审批人")
    @TableField("approve_by")
    private String approveBy;

    @Excel(name = "审批意见")
    @TableField("approve_opinion")
    private String approveOpinion;

    @Excel(name = "水费")
    @TableField("water_fee")
    private BigDecimal waterFee;

    @Excel(name = "电费")
    @TableField("electric_fee")
    private BigDecimal electricFee;

    @Excel(name = "燃气费")
    @TableField("gas_fee")
    private BigDecimal gasFee;

    @Excel(name = "暖气费")
    @TableField("heating_fee")
    private BigDecimal heatingFee;

    @Excel(name = "物业费")
    @TableField("property_fee")
    private BigDecimal propertyFee;

    @Excel(name = "损坏扣款")
    @TableField("damage_deduction")
    private BigDecimal damageDeduction;

    @TableField("damage_description")
    private String damageDescription;

    /** 应退总额（管理员可编辑） */
    @Excel(name = "应退总额")
    @TableField("refund_amount")
    private BigDecimal refundAmount;

    @TableField("inventory_list_id")
    private Long inventoryListId;

    @Excel(name = "水表读数")
    @TableField("meter_reading_water")
    private BigDecimal meterReadingWater;

    @Excel(name = "电表读数")
    @TableField("meter_reading_electric")
    private BigDecimal meterReadingElectric;

    @Excel(name = "燃气表读数")
    @TableField("meter_reading_gas")
    private BigDecimal meterReadingGas;

    @Excel(name = "钥匙归还数量")
    @TableField("key_returned")
    private Integer keyReturned;

    @TableField("checkout_photos")
    private String checkoutPhotos;

    /** 租户签名（base64图片数据） */
    @TableField("tenant_signature")
    private String tenantSignature;

    @TableField("del_flag")
    private String delFlag;

    public void setApplyId(Long applyId) { this.applyId = applyId; }
    public Long getApplyId() { return applyId; }
    
    public void setContractId(Long contractId) { this.contractId = contractId; }
    public Long getContractId() { return contractId; }
    
    public void setTenantId(Long tenantId) { this.tenantId = tenantId; }
    public Long getTenantId() { return tenantId; }
    
    public void setHouseId(Long houseId) { this.houseId = houseId; }
    public Long getHouseId() { return houseId; }
    
    public void setApplyTime(Date applyTime) { this.applyTime = applyTime; }
    public Date getApplyTime() { return applyTime; }
    
    public void setPlanCheckoutDate(Date planCheckoutDate) { this.planCheckoutDate = planCheckoutDate; }
    public Date getPlanCheckoutDate() { return planCheckoutDate; }
    
    public void setCheckoutReason(String checkoutReason) { this.checkoutReason = checkoutReason; }
    public String getCheckoutReason() { return checkoutReason; }
    
    public void setIsEarlyTermination(String isEarlyTermination) { this.isEarlyTermination = isEarlyTermination; }
    public String getIsEarlyTermination() { return isEarlyTermination; }
    
    public void setPenaltyAmount(BigDecimal penaltyAmount) { this.penaltyAmount = penaltyAmount; }
    public BigDecimal getPenaltyAmount() { return penaltyAmount; }
    
    public void setUnpaidBills(BigDecimal unpaidBills) { this.unpaidBills = unpaidBills; }
    public BigDecimal getUnpaidBills() { return unpaidBills; }
    
    public void setDepositRefund(BigDecimal depositRefund) { this.depositRefund = depositRefund; }
    public BigDecimal getDepositRefund() { return depositRefund; }
    
    public void setApplyStatus(String applyStatus) { this.applyStatus = applyStatus; }
    public String getApplyStatus() { return applyStatus; }
    
    public void setApproveBy(String approveBy) { this.approveBy = approveBy; }
    public String getApproveBy() { return approveBy; }
    
    public void setApproveTime(Date approveTime) { this.approveTime = approveTime; }
    public Date getApproveTime() { return approveTime; }
    
    public void setApproveOpinion(String approveOpinion) { this.approveOpinion = approveOpinion; }
    public String getApproveOpinion() { return approveOpinion; }
    
    public void setWaterFee(BigDecimal waterFee) { this.waterFee = waterFee; }
    public BigDecimal getWaterFee() { return waterFee; }
    
    public void setElectricFee(BigDecimal electricFee) { this.electricFee = electricFee; }
    public BigDecimal getElectricFee() { return electricFee; }
    
    public void setGasFee(BigDecimal gasFee) { this.gasFee = gasFee; }
    public BigDecimal getGasFee() { return gasFee; }
    
    public void setHeatingFee(BigDecimal heatingFee) { this.heatingFee = heatingFee; }
    public BigDecimal getHeatingFee() { return heatingFee; }
    
    public void setPropertyFee(BigDecimal propertyFee) { this.propertyFee = propertyFee; }
    public BigDecimal getPropertyFee() { return propertyFee; }
    
    public void setDamageDeduction(BigDecimal damageDeduction) { this.damageDeduction = damageDeduction; }
    public BigDecimal getDamageDeduction() { return damageDeduction; }
    
    public void setDamageDescription(String damageDescription) { this.damageDescription = damageDescription; }
    public String getDamageDescription() { return damageDescription; }

    public void setRefundAmount(BigDecimal refundAmount) { this.refundAmount = refundAmount; }
    public BigDecimal getRefundAmount() { return refundAmount; }

    public void setInventoryListId(Long inventoryListId) { this.inventoryListId = inventoryListId; }
    public Long getInventoryListId() { return inventoryListId; }
    
    public void setMeterReadingWater(BigDecimal meterReadingWater) { this.meterReadingWater = meterReadingWater; }
    public BigDecimal getMeterReadingWater() { return meterReadingWater; }
    
    public void setMeterReadingElectric(BigDecimal meterReadingElectric) { this.meterReadingElectric = meterReadingElectric; }
    public BigDecimal getMeterReadingElectric() { return meterReadingElectric; }
    
    public void setMeterReadingGas(BigDecimal meterReadingGas) { this.meterReadingGas = meterReadingGas; }
    public BigDecimal getMeterReadingGas() { return meterReadingGas; }
    
    public void setKeyReturned(Integer keyReturned) { this.keyReturned = keyReturned; }
    public Integer getKeyReturned() { return keyReturned; }
    
    public void setCheckoutPhotos(String checkoutPhotos) { this.checkoutPhotos = checkoutPhotos; }
    public String getCheckoutPhotos() { return checkoutPhotos; }

    public void setTenantSignature(String tenantSignature) { this.tenantSignature = tenantSignature; }
    public String getTenantSignature() { return tenantSignature; }

    public void setDelFlag(String delFlag) { this.delFlag = delFlag; }
    public String getDelFlag() { return delFlag; }
}
