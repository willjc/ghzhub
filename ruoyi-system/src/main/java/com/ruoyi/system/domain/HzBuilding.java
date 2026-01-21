package com.ruoyi.system.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.ruoyi.common.core.domain.BaseEntity;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * 楼栋对象 hz_building
 *
 * @author ruoyi
 */
@TableName("hz_building")
public class HzBuilding extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /** 楼栋ID */
    @TableId(type = IdType.AUTO)
    private Long buildingId;

    /** 项目ID */
    @TableField("project_id")
    private Long projectId;

    /** 楼栋名称 */
    @TableField("building_name")
    private String buildingName;

    /** 楼栋编码 */
    @TableField("building_code")
    private String buildingCode;

    /** 总楼层数 */
    @TableField("total_floors")
    private Integer totalFloors;

    /** 总单元数 */
    @TableField("total_units")
    private Integer totalUnits;

    /** 总房源数 */
    @TableField("total_houses")
    private Integer totalHouses;

    /** 状态(0:正常 1:停用) */
    @TableField("status")
    private String status;

    /** 排序 */
    @TableField("sort_order")
    private Integer sortOrder;

    /** 删除标志(0:存在 2:删除) */
    @TableField("del_flag")
    private String delFlag;

    /** 项目名称(关联查询字段,不存在于数据库表) */
    @TableField(exist = false)
    private String projectName;

    public void setBuildingId(Long buildingId) {
        this.buildingId = buildingId;
    }

    public Long getBuildingId() {
        return buildingId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    public Long getProjectId() {
        return projectId;
    }

    public void setBuildingName(String buildingName) {
        this.buildingName = buildingName;
    }

    public String getBuildingName() {
        return buildingName;
    }

    public void setBuildingCode(String buildingCode) {
        this.buildingCode = buildingCode;
    }

    public String getBuildingCode() {
        return buildingCode;
    }

    public void setTotalFloors(Integer totalFloors) {
        this.totalFloors = totalFloors;
    }

    public Integer getTotalFloors() {
        return totalFloors;
    }

    public void setTotalUnits(Integer totalUnits) {
        this.totalUnits = totalUnits;
    }

    public Integer getTotalUnits() {
        return totalUnits;
    }

    public void setTotalHouses(Integer totalHouses) {
        this.totalHouses = totalHouses;
    }

    public Integer getTotalHouses() {
        return totalHouses;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }

    public void setSortOrder(Integer sortOrder) {
        this.sortOrder = sortOrder;
    }

    public Integer getSortOrder() {
        return sortOrder;
    }

    public void setDelFlag(String delFlag) {
        this.delFlag = delFlag;
    }

    public String getDelFlag() {
        return delFlag;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public String getProjectName() {
        return projectName;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
            .append("buildingId", getBuildingId())
            .append("projectId", getProjectId())
            .append("buildingName", getBuildingName())
            .append("buildingCode", getBuildingCode())
            .append("totalFloors", getTotalFloors())
            .append("totalUnits", getTotalUnits())
            .append("totalHouses", getTotalHouses())
            .append("status", getStatus())
            .append("sortOrder", getSortOrder())
            .append("delFlag", getDelFlag())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("updateBy", getUpdateBy())
            .append("updateTime", getUpdateTime())
            .append("remark", getRemark())
            .toString();
    }
}
