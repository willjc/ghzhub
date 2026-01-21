package com.ruoyi.system.domain;

import com.baomidou.mybatisplus.annotation.*;
import com.ruoyi.common.core.domain.BaseEntity;

import java.util.Date;

/**
 * 批次房源对象 hz_batch_house
 *
 * @author ruoyi
 */
@TableName("hz_batch_house")
public class HzBatchHouse extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /** 主键ID */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /** 批次ID */
    private Long batchId;

    /** 房源ID */
    private Long houseId;

    /** 房源编号 */
    private String houseCode;

    /** 分配状态(0未分配 1已分配) */
    private String allocationStatus;

    /** 租户ID */
    private Long tenantId;

    /** 分配时间 */
    private Date allocationTime;

    /** 删除标志(0代表存在 2代表删除) */
    @TableLogic
    private String delFlag;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getBatchId() {
        return batchId;
    }

    public void setBatchId(Long batchId) {
        this.batchId = batchId;
    }

    public Long getHouseId() {
        return houseId;
    }

    public void setHouseId(Long houseId) {
        this.houseId = houseId;
    }

    public String getHouseCode() {
        return houseCode;
    }

    public void setHouseCode(String houseCode) {
        this.houseCode = houseCode;
    }

    public String getAllocationStatus() {
        return allocationStatus;
    }

    public void setAllocationStatus(String allocationStatus) {
        this.allocationStatus = allocationStatus;
    }

    public Long getTenantId() {
        return tenantId;
    }

    public void setTenantId(Long tenantId) {
        this.tenantId = tenantId;
    }

    public Date getAllocationTime() {
        return allocationTime;
    }

    public void setAllocationTime(Date allocationTime) {
        this.allocationTime = allocationTime;
    }

    public String getDelFlag() {
        return delFlag;
    }

    public void setDelFlag(String delFlag) {
        this.delFlag = delFlag;
    }

    @Override
    public String toString() {
        return "HzBatchHouse{" +
                "id=" + id +
                ", batchId=" + batchId +
                ", houseId=" + houseId +
                ", houseCode='" + houseCode + '\'' +
                ", allocationStatus='" + allocationStatus + '\'' +
                ", tenantId=" + tenantId +
                ", allocationTime=" + allocationTime +
                ", delFlag='" + delFlag + '\'' +
                '}';
    }
}
