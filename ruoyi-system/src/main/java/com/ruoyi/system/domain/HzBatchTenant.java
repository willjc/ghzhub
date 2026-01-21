package com.ruoyi.system.domain;

import com.baomidou.mybatisplus.annotation.*;
import com.ruoyi.common.core.domain.BaseEntity;

import java.util.Date;

/**
 * 批次租户对象 hz_batch_tenant
 *
 * @author ruoyi
 */
@TableName("hz_batch_tenant")
public class HzBatchTenant extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /** 主键ID */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /** 批次ID */
    private Long batchId;

    /** 租户姓名 */
    private String tenantName;

    /** 身份证号 */
    private String idCard;

    /** 手机号 */
    private String phone;

    /** 工号 */
    private String workNo;

    /** 部门名称 */
    private String deptName;

    /** 房源ID */
    private Long houseId;

    /** 分配状态(0未分配 1已分配) */
    private String allocationStatus;

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

    public String getTenantName() {
        return tenantName;
    }

    public void setTenantName(String tenantName) {
        this.tenantName = tenantName;
    }

    public String getIdCard() {
        return idCard;
    }

    public void setIdCard(String idCard) {
        this.idCard = idCard;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getWorkNo() {
        return workNo;
    }

    public void setWorkNo(String workNo) {
        this.workNo = workNo;
    }

    public String getDeptName() {
        return deptName;
    }

    public void setDeptName(String deptName) {
        this.deptName = deptName;
    }

    public Long getHouseId() {
        return houseId;
    }

    public void setHouseId(Long houseId) {
        this.houseId = houseId;
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
        return "HzBatchTenant{" +
                "id=" + id +
                ", batchId=" + batchId +
                ", tenantName='" + tenantName + '\'' +
                ", idCard='" + idCard + '\'' +
                ", phone='" + phone + '\'' +
                ", workNo='" + workNo + '\'' +
                ", deptName='" + deptName + '\'' +
                ", houseId=" + houseId +
                ", allocationStatus='" + allocationStatus + '\'' +
                ", allocationTime=" + allocationTime +
                ", delFlag='" + delFlag + '\'' +
                '}';
    }
}
