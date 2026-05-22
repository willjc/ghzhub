package com.ruoyi.system.domain;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 优惠券对象 hz_coupon
 *
 * @author ruoyi
 */
@TableName("hz_coupon")
public class HzCoupon extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 优惠券ID */
    @TableId(type = IdType.AUTO)
    @Excel(name = "优惠券ID")
    private Long couponId;

    /** 优惠券名称 */
    @Excel(name = "名称")
    @TableField("coupon_name")
    private String couponName;

    /** 优惠券编码 */
    @TableField("coupon_code")
    private String couponCode;

    /** 类型：1=满减 2=折扣 3=抵扣 */
    @Excel(name = "类型", readConverterExp = "1=满减,2=折扣,3=抵扣")
    @TableField("coupon_type")
    private String couponType;

    /** 优惠金额 */
    @Excel(name = "优惠金额")
    @TableField("discount_amount")
    private BigDecimal discountAmount;

    /** 折扣率(%) */
    @TableField("discount_rate")
    private BigDecimal discountRate;

    /** 最低使用金额 */
    @TableField("min_amount")
    private BigDecimal minAmount;

    /** 最高优惠金额 */
    @TableField("max_discount")
    private BigDecimal maxDiscount;

    /** 发行总量（0=不限） */
    @Excel(name = "发行总量")
    @TableField("total_count")
    private Integer totalCount;

    /** 已领取数量 */
    @Excel(name = "已领取")
    @TableField("received_count")
    private Integer receivedCount;

    /** 已使用数量 */
    @TableField("used_count")
    private Integer usedCount;

    /** 有效期开始 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "生效日", width = 15, dateFormat = "yyyy-MM-dd")
    @TableField("valid_start_date")
    private Date validStartDate;

    /** 有效期结束 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "失效日", width = 15, dateFormat = "yyyy-MM-dd")
    @TableField("valid_end_date")
    private Date validEndDate;

    /** 适用类型（自由文本） */
    @TableField("applicable_type")
    private String applicableType;

    /** 状态：0=正常 1=停用 */
    @Excel(name = "状态", readConverterExp = "0=启用,1=停用")
    @TableField("status")
    private String status;

    /** 删除标志 */
    @TableField("del_flag")
    @TableLogic
    private String delFlag;

    public Long getCouponId() { return couponId; }
    public void setCouponId(Long couponId) { this.couponId = couponId; }

    public String getCouponName() { return couponName; }
    public void setCouponName(String couponName) { this.couponName = couponName; }

    public String getCouponCode() { return couponCode; }
    public void setCouponCode(String couponCode) { this.couponCode = couponCode; }

    public String getCouponType() { return couponType; }
    public void setCouponType(String couponType) { this.couponType = couponType; }

    public BigDecimal getDiscountAmount() { return discountAmount; }
    public void setDiscountAmount(BigDecimal discountAmount) { this.discountAmount = discountAmount; }

    public BigDecimal getDiscountRate() { return discountRate; }
    public void setDiscountRate(BigDecimal discountRate) { this.discountRate = discountRate; }

    public BigDecimal getMinAmount() { return minAmount; }
    public void setMinAmount(BigDecimal minAmount) { this.minAmount = minAmount; }

    public BigDecimal getMaxDiscount() { return maxDiscount; }
    public void setMaxDiscount(BigDecimal maxDiscount) { this.maxDiscount = maxDiscount; }

    public Integer getTotalCount() { return totalCount; }
    public void setTotalCount(Integer totalCount) { this.totalCount = totalCount; }

    public Integer getReceivedCount() { return receivedCount; }
    public void setReceivedCount(Integer receivedCount) { this.receivedCount = receivedCount; }

    public Integer getUsedCount() { return usedCount; }
    public void setUsedCount(Integer usedCount) { this.usedCount = usedCount; }

    public Date getValidStartDate() { return validStartDate; }
    public void setValidStartDate(Date validStartDate) { this.validStartDate = validStartDate; }

    public Date getValidEndDate() { return validEndDate; }
    public void setValidEndDate(Date validEndDate) { this.validEndDate = validEndDate; }

    public String getApplicableType() { return applicableType; }
    public void setApplicableType(String applicableType) { this.applicableType = applicableType; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getDelFlag() { return delFlag; }
    public void setDelFlag(String delFlag) { this.delFlag = delFlag; }
}
