package com.ruoyi.system.domain;

import com.baomidou.mybatisplus.annotation.*;
import com.ruoyi.common.core.domain.BaseEntity;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 选房预订单对象 hz_house_order
 *
 * @author ruoyi
 */
@TableName("hz_house_order")
public class HzHouseOrder extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /** 预订单ID */
    @TableId(type = IdType.AUTO)
    private Long orderId;

    /** 预订单号 */
    @TableField("order_no")
    private String orderNo;

    /** 租户ID */
    @TableField("tenant_id")
    private Long tenantId;

    /** 房源ID */
    @TableField("house_id")
    private Long houseId;

    /** 项目ID */
    @TableField("project_id")
    private Long projectId;

    /** 押金金额 */
    @TableField("deposit_amount")
    private BigDecimal depositAmount;

    /** 租金(元/月) */
    @TableField("rent_price")
    private BigDecimal rentPrice;

    /** 锁定过期时间 */
    @TableField("lock_expire_time")
    private Date lockExpireTime;

    /** 资料上传过期时间 */
    @TableField("doc_upload_expire_time")
    private Date docUploadExpireTime;

    /** 订单状态(0:待签约 1:待付押金 2:待上传资料 3:已完成 4:已过期 5:已取消) */
    @TableField("order_status")
    private String orderStatus;

    /** 合同ID */
    @TableField("contract_id")
    private Long contractId;

    /** e签宝流程ID */
    @TableField("esign_flow_id")
    private String esignFlowId;

    /** 押金账单ID */
    @TableField("deposit_bill_id")
    private Long depositBillId;

    /** 是否配租(0:否 1:是) */
    @TableField("is_batch_alloc")
    private String isBatchAlloc;

    /** 配租批次ID */
    @TableField("batch_id")
    private Long batchId;

    /** 删除标志(0:存在 2:删除) */
    @TableField("del_flag")
    @TableLogic
    private String delFlag;

    /** 非数据库字段：剩余锁定秒数 */
    @TableField(exist = false)
    private Long remainSeconds;

    /** 非数据库字段：资料上传剩余秒数 */
    @TableField(exist = false)
    private Long docRemainSeconds;

    public Long getOrderId() { return orderId; }
    public void setOrderId(Long orderId) { this.orderId = orderId; }

    public String getOrderNo() { return orderNo; }
    public void setOrderNo(String orderNo) { this.orderNo = orderNo; }

    public Long getTenantId() { return tenantId; }
    public void setTenantId(Long tenantId) { this.tenantId = tenantId; }

    public Long getHouseId() { return houseId; }
    public void setHouseId(Long houseId) { this.houseId = houseId; }

    public Long getProjectId() { return projectId; }
    public void setProjectId(Long projectId) { this.projectId = projectId; }

    public BigDecimal getDepositAmount() { return depositAmount; }
    public void setDepositAmount(BigDecimal depositAmount) { this.depositAmount = depositAmount; }

    public BigDecimal getRentPrice() { return rentPrice; }
    public void setRentPrice(BigDecimal rentPrice) { this.rentPrice = rentPrice; }

    public Date getLockExpireTime() { return lockExpireTime; }
    public void setLockExpireTime(Date lockExpireTime) { this.lockExpireTime = lockExpireTime; }

    public Date getDocUploadExpireTime() { return docUploadExpireTime; }
    public void setDocUploadExpireTime(Date docUploadExpireTime) { this.docUploadExpireTime = docUploadExpireTime; }

    public String getOrderStatus() { return orderStatus; }
    public void setOrderStatus(String orderStatus) { this.orderStatus = orderStatus; }

    public Long getContractId() { return contractId; }
    public void setContractId(Long contractId) { this.contractId = contractId; }

    public String getEsignFlowId() { return esignFlowId; }
    public void setEsignFlowId(String esignFlowId) { this.esignFlowId = esignFlowId; }

    public Long getDepositBillId() { return depositBillId; }
    public void setDepositBillId(Long depositBillId) { this.depositBillId = depositBillId; }

    public String getIsBatchAlloc() { return isBatchAlloc; }
    public void setIsBatchAlloc(String isBatchAlloc) { this.isBatchAlloc = isBatchAlloc; }

    public Long getBatchId() { return batchId; }
    public void setBatchId(Long batchId) { this.batchId = batchId; }

    public String getDelFlag() { return delFlag; }
    public void setDelFlag(String delFlag) { this.delFlag = delFlag; }

    public Long getRemainSeconds() { return remainSeconds; }
    public void setRemainSeconds(Long remainSeconds) { this.remainSeconds = remainSeconds; }

    public Long getDocRemainSeconds() { return docRemainSeconds; }
    public void setDocRemainSeconds(Long docRemainSeconds) { this.docRemainSeconds = docRemainSeconds; }
}
