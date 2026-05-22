package com.ruoyi.system.domain;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

import java.util.Date;

/**
 * 服务订单对象 hz_service_order
 * 保洁(order_type=1) 与 搬家(order_type=2) 合表，按类型使用对应业务字段
 *
 * @author ruoyi
 */
@TableName("hz_service_order")
public class HzServiceOrder extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 订单ID */
    @TableId(type = IdType.AUTO)
    @Excel(name = "订单ID")
    private Long orderId;

    /** 订单号 */
    @Excel(name = "订单号")
    @TableField("order_no")
    private String orderNo;

    /** 订单类型：1=保洁 2=搬家 */
    @Excel(name = "订单类型", readConverterExp = "1=保洁,2=搬家")
    @TableField("order_type")
    private String orderType;

    /** 租户ID */
    @TableField("tenant_id")
    private Long tenantId;

    /** 申请人姓名 */
    @Excel(name = "申请人")
    @TableField("applicant_name")
    private String applicantName;

    /** 申请人手机号 */
    @Excel(name = "手机号")
    @TableField("applicant_phone")
    private String applicantPhone;

    /** 房源ID */
    @TableField("house_id")
    private Long houseId;

    /** 房间地址 */
    @Excel(name = "房间地址")
    @TableField("house_address")
    private String houseAddress;

    /** 期望服务时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Excel(name = "期望时间", width = 20, dateFormat = "yyyy-MM-dd HH:mm")
    @TableField("expect_time")
    private Date expectTime;

    /** 申请备注 */
    @TableField("apply_remark")
    private String applyRemark;

    /** 状态：0=待处理 1=已分配 2=服务中 3=已完成 4=已取消 */
    @Excel(name = "状态", readConverterExp = "0=待处理,1=已分配,2=服务中,3=已完成,4=已取消")
    @TableField("status")
    private String status;

    /** 服务公司ID */
    @TableField("company_id")
    private Long companyId;

    /** 服务公司名（冗余） */
    @Excel(name = "服务公司")
    @TableField("company_name")
    private String companyName;

    /** 分配人 */
    @TableField("assigned_by")
    private String assignedBy;

    /** 分配时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @TableField("assigned_time")
    private Date assignedTime;

    /** 分配备注 */
    @TableField("assign_remark")
    private String assignRemark;

    /** 完成时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @TableField("finish_time")
    private Date finishTime;

    /** 取消原因 */
    @TableField("cancel_reason")
    private String cancelReason;

    /** 评分 1-5 */
    @TableField("rate_score")
    private Integer rateScore;

    /** 评价内容 */
    @TableField("rate_content")
    private String rateContent;

    /** 评价时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @TableField("rate_time")
    private Date rateTime;

    /** 保洁类型 */
    @TableField("clean_type")
    private String cleanType;

    /** 房间数 */
    @TableField("room_count")
    private Integer roomCount;

    /** 起运地址（搬家） */
    @TableField("from_address")
    private String fromAddress;

    /** 目的地址（搬家） */
    @TableField("to_address")
    private String toAddress;

    /** 物品描述（搬家） */
    @TableField("move_item_desc")
    private String moveItemDesc;

    /** 是否拆装家具：0=否 1=是 */
    @TableField("need_disassembly")
    private String needDisassembly;

    /** 删除标志 */
    @TableField("del_flag")
    @TableLogic
    private String delFlag;

    public Long getOrderId() { return orderId; }
    public void setOrderId(Long orderId) { this.orderId = orderId; }

    public String getOrderNo() { return orderNo; }
    public void setOrderNo(String orderNo) { this.orderNo = orderNo; }

    public String getOrderType() { return orderType; }
    public void setOrderType(String orderType) { this.orderType = orderType; }

    public Long getTenantId() { return tenantId; }
    public void setTenantId(Long tenantId) { this.tenantId = tenantId; }

    public String getApplicantName() { return applicantName; }
    public void setApplicantName(String applicantName) { this.applicantName = applicantName; }

    public String getApplicantPhone() { return applicantPhone; }
    public void setApplicantPhone(String applicantPhone) { this.applicantPhone = applicantPhone; }

    public Long getHouseId() { return houseId; }
    public void setHouseId(Long houseId) { this.houseId = houseId; }

    public String getHouseAddress() { return houseAddress; }
    public void setHouseAddress(String houseAddress) { this.houseAddress = houseAddress; }

    public Date getExpectTime() { return expectTime; }
    public void setExpectTime(Date expectTime) { this.expectTime = expectTime; }

    public String getApplyRemark() { return applyRemark; }
    public void setApplyRemark(String applyRemark) { this.applyRemark = applyRemark; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Long getCompanyId() { return companyId; }
    public void setCompanyId(Long companyId) { this.companyId = companyId; }

    public String getCompanyName() { return companyName; }
    public void setCompanyName(String companyName) { this.companyName = companyName; }

    public String getAssignedBy() { return assignedBy; }
    public void setAssignedBy(String assignedBy) { this.assignedBy = assignedBy; }

    public Date getAssignedTime() { return assignedTime; }
    public void setAssignedTime(Date assignedTime) { this.assignedTime = assignedTime; }

    public String getAssignRemark() { return assignRemark; }
    public void setAssignRemark(String assignRemark) { this.assignRemark = assignRemark; }

    public Date getFinishTime() { return finishTime; }
    public void setFinishTime(Date finishTime) { this.finishTime = finishTime; }

    public String getCancelReason() { return cancelReason; }
    public void setCancelReason(String cancelReason) { this.cancelReason = cancelReason; }

    public Integer getRateScore() { return rateScore; }
    public void setRateScore(Integer rateScore) { this.rateScore = rateScore; }

    public String getRateContent() { return rateContent; }
    public void setRateContent(String rateContent) { this.rateContent = rateContent; }

    public Date getRateTime() { return rateTime; }
    public void setRateTime(Date rateTime) { this.rateTime = rateTime; }

    public String getCleanType() { return cleanType; }
    public void setCleanType(String cleanType) { this.cleanType = cleanType; }

    public Integer getRoomCount() { return roomCount; }
    public void setRoomCount(Integer roomCount) { this.roomCount = roomCount; }

    public String getFromAddress() { return fromAddress; }
    public void setFromAddress(String fromAddress) { this.fromAddress = fromAddress; }

    public String getToAddress() { return toAddress; }
    public void setToAddress(String toAddress) { this.toAddress = toAddress; }

    public String getMoveItemDesc() { return moveItemDesc; }
    public void setMoveItemDesc(String moveItemDesc) { this.moveItemDesc = moveItemDesc; }

    public String getNeedDisassembly() { return needDisassembly; }
    public void setNeedDisassembly(String needDisassembly) { this.needDisassembly = needDisassembly; }

    public String getDelFlag() { return delFlag; }
    public void setDelFlag(String delFlag) { this.delFlag = delFlag; }
}
