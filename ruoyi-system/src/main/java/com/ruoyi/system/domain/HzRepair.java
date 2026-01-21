package com.ruoyi.system.domain;

import com.baomidou.mybatisplus.annotation.*;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 物业报修对象 hz_repair
 *
 * @author ruoyi
 */
@TableName("hz_repair")
public class HzRepair extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 报修ID */
    @TableId(type = IdType.AUTO)
    private Long repairId;

    /** 报修编号 */
    @TableField("repair_no")
    private String repairNo;

    /** 用户ID */
    @TableField("user_id")
    private Long userId;

    /** 项目ID */
    @TableField("project_id")
    private Long projectId;

    /** 楼栋ID */
    @TableField("building_id")
    private Long buildingId;

    /** 单元ID */
    @TableField("unit_id")
    private Long unitId;

    /** 所在位置(拼接字符串) */
    @TableField("location")
    private String location;

    /** 房间号 */
    @TableField("room_no")
    private String roomNo;

    /** 联系电话 */
    @TableField("phone")
    private String phone;

    /** 问题描述 */
    @TableField("description")
    private String description;

    /** 上传图片(多个用逗号分隔) */
    @TableField("images")
    private String images;

    /** 期望维修时间 */
    @TableField("repair_date")
    private String repairDate;

    /** 状态：0-待处理，1-已完成，2-已取消 */
    @TableField("status")
    private String status;

    /** 处理结果 */
    @TableField("handle_result")
    private String handleResult;

    /** 处理时间 */
    @TableField("handle_time")
    private String handleTime;

    /** 处理人 */
    @TableField("handle_by")
    private String handleBy;

    /** 删除标志：0-正常，2-删除 */
    @TableField("del_flag")
    private String delFlag;

    public Long getRepairId()
    {
        return repairId;
    }

    public void setRepairId(Long repairId)
    {
        this.repairId = repairId;
    }

    public String getRepairNo()
    {
        return repairNo;
    }

    public void setRepairNo(String repairNo)
    {
        this.repairNo = repairNo;
    }

    public Long getUserId()
    {
        return userId;
    }

    public void setUserId(Long userId)
    {
        this.userId = userId;
    }

    public Long getProjectId()
    {
        return projectId;
    }

    public void setProjectId(Long projectId)
    {
        this.projectId = projectId;
    }

    public Long getBuildingId()
    {
        return buildingId;
    }

    public void setBuildingId(Long buildingId)
    {
        this.buildingId = buildingId;
    }

    public Long getUnitId()
    {
        return unitId;
    }

    public void setUnitId(Long unitId)
    {
        this.unitId = unitId;
    }

    public String getLocation()
    {
        return location;
    }

    public void setLocation(String location)
    {
        this.location = location;
    }

    public String getRoomNo()
    {
        return roomNo;
    }

    public void setRoomNo(String roomNo)
    {
        this.roomNo = roomNo;
    }

    public String getPhone()
    {
        return phone;
    }

    public void setPhone(String phone)
    {
        this.phone = phone;
    }

    public String getDescription()
    {
        return description;
    }

    public void setDescription(String description)
    {
        this.description = description;
    }

    public String getImages()
    {
        return images;
    }

    public void setImages(String images)
    {
        this.images = images;
    }

    public String getRepairDate()
    {
        return repairDate;
    }

    public void setRepairDate(String repairDate)
    {
        this.repairDate = repairDate;
    }

    public String getStatus()
    {
        return status;
    }

    public void setStatus(String status)
    {
        this.status = status;
    }

    public String getHandleResult()
    {
        return handleResult;
    }

    public void setHandleResult(String handleResult)
    {
        this.handleResult = handleResult;
    }

    public String getHandleTime()
    {
        return handleTime;
    }

    public void setHandleTime(String handleTime)
    {
        this.handleTime = handleTime;
    }

    public String getHandleBy()
    {
        return handleBy;
    }

    public void setHandleBy(String handleBy)
    {
        this.handleBy = handleBy;
    }

    public String getDelFlag()
    {
        return delFlag;
    }

    public void setDelFlag(String delFlag)
    {
        this.delFlag = delFlag;
    }

    @Override
    public String toString()
    {
        return "HzRepair{" +
                "repairId=" + repairId +
                ", repairNo='" + repairNo + '\'' +
                ", userId=" + userId +
                ", projectId=" + projectId +
                ", buildingId=" + buildingId +
                ", unitId=" + unitId +
                ", location='" + location + '\'' +
                ", roomNo='" + roomNo + '\'' +
                ", phone='" + phone + '\'' +
                ", description='" + description + '\'' +
                ", images='" + images + '\'' +
                ", repairDate='" + repairDate + '\'' +
                ", status='" + status + '\'' +
                ", handleResult='" + handleResult + '\'' +
                ", handleTime='" + handleTime + '\'' +
                ", handleBy='" + handleBy + '\'' +
                ", delFlag='" + delFlag + '\'' +
                '}';
    }
}
