package com.ruoyi.system.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.ruoyi.common.core.domain.BaseEntity;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.util.Date;

/**
 * 企业批次房源对象 hz_enterprise_batch_house
 *
 * @author ruoyi
 */
@TableName("hz_enterprise_batch_house")
public class HzEnterpriseBatchHouse extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /** 主键ID */
    @TableId(type = IdType.AUTO)
    @TableField("id")
    private Long id;

    /** 批次ID */
    @TableField("batch_id")
    private Long batchId;

    /** 房源ID */
    @TableField("house_id")
    private Long houseId;

    /** 房源编号 */
    @TableField("house_code")
    private String houseCode;

    /** 分配状态(0未分配 1已分配) */
    @TableField("allocation_status")
    private String allocationStatus;

    /** 分配时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @TableField("allocation_time")
    private Date allocationTime;

    /** 删除标志(0正常 2删除) */
    @TableField("del_flag")
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
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
                .append("id", getId())
                .append("batchId", getBatchId())
                .append("houseId", getHouseId())
                .append("houseCode", getHouseCode())
                .append("allocationStatus", getAllocationStatus())
                .append("allocationTime", getAllocationTime())
                .append("delFlag", getDelFlag())
                .append("createBy", getCreateBy())
                .append("createTime", getCreateTime())
                .append("updateBy", getUpdateBy())
                .append("updateTime", getUpdateTime())
                .append("remark", getRemark())
                .toString();
    }
}
