package com.ruoyi.system.domain;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

import java.util.Date;

/**
 * 优惠券领取记录对象 hz_coupon_receive
 *
 * @author ruoyi
 */
@TableName("hz_coupon_receive")
public class HzCouponReceive extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 领取ID */
    @TableId(type = IdType.AUTO)
    private Long receiveId;

    /** 优惠券ID */
    @Excel(name = "优惠券ID")
    @TableField("coupon_id")
    private Long couponId;

    /** 租户ID */
    @Excel(name = "租户ID")
    @TableField("tenant_id")
    private Long tenantId;

    /** 领取时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Excel(name = "领取时间", width = 20, dateFormat = "yyyy-MM-dd HH:mm:ss")
    @TableField("receive_time")
    private Date receiveTime;

    /** 状态：0=未使用 1=已使用 2=已过期 */
    @Excel(name = "状态", readConverterExp = "0=未使用,1=已使用,2=已过期")
    @TableField("receive_status")
    private String receiveStatus;

    /** 使用时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @TableField("use_time")
    private Date useTime;

    /** 使用订单ID */
    @TableField("order_id")
    private Long orderId;

    /** 删除标志 */
    @TableField("del_flag")
    @TableLogic
    private String delFlag;

    public Long getReceiveId() { return receiveId; }
    public void setReceiveId(Long receiveId) { this.receiveId = receiveId; }

    public Long getCouponId() { return couponId; }
    public void setCouponId(Long couponId) { this.couponId = couponId; }

    public Long getTenantId() { return tenantId; }
    public void setTenantId(Long tenantId) { this.tenantId = tenantId; }

    public Date getReceiveTime() { return receiveTime; }
    public void setReceiveTime(Date receiveTime) { this.receiveTime = receiveTime; }

    public String getReceiveStatus() { return receiveStatus; }
    public void setReceiveStatus(String receiveStatus) { this.receiveStatus = receiveStatus; }

    public Date getUseTime() { return useTime; }
    public void setUseTime(Date useTime) { this.useTime = useTime; }

    public Long getOrderId() { return orderId; }
    public void setOrderId(Long orderId) { this.orderId = orderId; }

    public String getDelFlag() { return delFlag; }
    public void setDelFlag(String delFlag) { this.delFlag = delFlag; }
}
