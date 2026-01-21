package com.ruoyi.system.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 退租记录对象 hz_checkout_record
 *
 * @author ruoyi
 */
public class HzCheckoutRecord extends BaseEntity {
    private static final long serialVersionUID = 1L;

    private Long recordId;
    private Long applyId;
    private Long contractId;
    private Long tenantId;
    private Long houseId;

    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "退租日期", width = 30, dateFormat = "yyyy-MM-dd")
    private Date checkoutDate;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Excel(name = "���租时间", width = 30, dateFormat = "yyyy-MM-dd HH:mm:ss")
    private Date checkoutTime;

    @Excel(name = "水表读数")
    private BigDecimal meterReadingElectric;

    @Excel(name = "水表读数")
    private BigDecimal meterReadingWater;

    @Excel(name = "燃气表读数")
    private BigDecimal meterReadingGas;

    @Excel(name = "钥匙归还数量")
    private Integer keyReturned;

    private Long inventoryListId;

    @Excel(name = "退租照片")
    private String checkoutPhotos;

    @Excel(name = "损坏描述")
    private String damageDescription;

    @Excel(name = "损坏扣款")
    private BigDecimal damageDeduction;

    @Excel(name = "水电费")
    private BigDecimal utilityBill;

    @Excel(name = "未付租金")
    private BigDecimal unpaidRent;

    @Excel(name = "违约金")
    private BigDecimal penaltyAmount;

    @Excel(name = "押金退款")
    private BigDecimal depositRefund;

    @Excel(name = "退款状态", readConverterExp = "0=待退还,1=已退还")
    private String refundStatus;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Excel(name = "退款时间", width = 30, dateFormat = "yyyy-MM-dd HH:mm:ss")
    private Date refundTime;

    /** 付款方式（1=现金 2=支付宝 3=微信 4=银行转账） */
    private String paymentMethod;

    /** 付款凭证（图片路径） */
    private String paymentVoucher;

    /** 付款备注 */
    private String paymentRemark;

    @Excel(name = "租户签字")
    private String tenantSignature;

    @Excel(name = "管理员签字")
    private String managerSignature;

    private Long managerId;
    private String managerName;
    private String delFlag;

    public void setRecordId(Long recordId) {
        this.recordId = recordId;
    }

    public Long getRecordId() {
        return recordId;
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

    public void setCheckoutDate(Date checkoutDate) {
        this.checkoutDate = checkoutDate;
    }

    public Date getCheckoutDate() {
        return checkoutDate;
    }

    public void setCheckoutTime(Date checkoutTime) {
        this.checkoutTime = checkoutTime;
    }

    public Date getCheckoutTime() {
        return checkoutTime;
    }

    public void setMeterReadingElectric(BigDecimal meterReadingElectric) {
        this.meterReadingElectric = meterReadingElectric;
    }

    public BigDecimal getMeterReadingElectric() {
        return meterReadingElectric;
    }

    public void setMeterReadingWater(BigDecimal meterReadingWater) {
        this.meterReadingWater = meterReadingWater;
    }

    public BigDecimal getMeterReadingWater() {
        return meterReadingWater;
    }

    public void setMeterReadingGas(BigDecimal meterReadingGas) {
        this.meterReadingGas = meterReadingGas;
    }

    public BigDecimal getMeterReadingGas() {
        return meterReadingGas;
    }

    public void setKeyReturned(Integer keyReturned) {
        this.keyReturned = keyReturned;
    }

    public Integer getKeyReturned() {
        return keyReturned;
    }

    public void setInventoryListId(Long inventoryListId) {
        this.inventoryListId = inventoryListId;
    }

    public Long getInventoryListId() {
        return inventoryListId;
    }

    public void setCheckoutPhotos(String checkoutPhotos) {
        this.checkoutPhotos = checkoutPhotos;
    }

    public String getCheckoutPhotos() {
        return checkoutPhotos;
    }

    public void setDamageDescription(String damageDescription) {
        this.damageDescription = damageDescription;
    }

    public String getDamageDescription() {
        return damageDescription;
    }

    public void setDamageDeduction(BigDecimal damageDeduction) {
        this.damageDeduction = damageDeduction;
    }

    public BigDecimal getDamageDeduction() {
        return damageDeduction;
    }

    public void setUtilityBill(BigDecimal utilityBill) {
        this.utilityBill = utilityBill;
    }

    public BigDecimal getUtilityBill() {
        return utilityBill;
    }

    public void setUnpaidRent(BigDecimal unpaidRent) {
        this.unpaidRent = unpaidRent;
    }

    public BigDecimal getUnpaidRent() {
        return unpaidRent;
    }

    public void setPenaltyAmount(BigDecimal penaltyAmount) {
        this.penaltyAmount = penaltyAmount;
    }

    public BigDecimal getPenaltyAmount() {
        return penaltyAmount;
    }

    public void setDepositRefund(BigDecimal depositRefund) {
        this.depositRefund = depositRefund;
    }

    public BigDecimal getDepositRefund() {
        return depositRefund;
    }

    public void setRefundStatus(String refundStatus) {
        this.refundStatus = refundStatus;
    }

    public String getRefundStatus() {
        return refundStatus;
    }

    public void setRefundTime(Date refundTime) {
        this.refundTime = refundTime;
    }

    public Date getRefundTime() {
        return refundTime;
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

    public void setDelFlag(String delFlag) {
        this.delFlag = delFlag;
    }

    public String getDelFlag() {
        return delFlag;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public String getPaymentVoucher() {
        return paymentVoucher;
    }

    public void setPaymentVoucher(String paymentVoucher) {
        this.paymentVoucher = paymentVoucher;
    }

    public String getPaymentRemark() {
        return paymentRemark;
    }

    public void setPaymentRemark(String paymentRemark) {
        this.paymentRemark = paymentRemark;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
                .append("recordId", getRecordId())
                .append("applyId", getApplyId())
                .append("contractId", getContractId())
                .append("tenantId", getTenantId())
                .append("houseId", getHouseId())
                .append("checkoutDate", getCheckoutDate())
                .append("checkoutTime", getCheckoutTime())
                .append("meterReadingElectric", getMeterReadingElectric())
                .append("meterReadingWater", getMeterReadingWater())
                .append("meterReadingGas", getMeterReadingGas())
                .append("keyReturned", getKeyReturned())
                .append("inventoryListId", getInventoryListId())
                .append("checkoutPhotos", getCheckoutPhotos())
                .append("damageDescription", getDamageDescription())
                .append("damageDeduction", getDamageDeduction())
                .append("utilityBill", getUtilityBill())
                .append("unpaidRent", getUnpaidRent())
                .append("penaltyAmount", getPenaltyAmount())
                .append("depositRefund", getDepositRefund())
                .append("refundStatus", getRefundStatus())
                .append("refundTime", getRefundTime())
                .append("tenantSignature", getTenantSignature())
                .append("managerSignature", getManagerSignature())
                .append("managerId", getManagerId())
                .append("managerName", getManagerName())
                .append("createBy", getCreateBy())
                .append("createTime", getCreateTime())
                .append("updateBy", getUpdateBy())
                .append("updateTime", getUpdateTime())
                .append("remark", getRemark())
                .append("delFlag", getDelFlag())
                .toString();
    }
}
