package com.ruoyi.system.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.ruoyi.common.core.domain.BaseEntity;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * 单元对象 hz_unit
 *
 * @author ruoyi
 */
@TableName("hz_unit")
public class HzUnit extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /** 单元ID */
    @TableId(type = IdType.AUTO)
    private Long unitId;

    /** 楼栋ID */
    @TableField("building_id")
    private Long buildingId;

    /** 单元名称 */
    @TableField("unit_name")
    private String unitName;

    /** 单元编码 */
    @TableField("unit_code")
    private String unitCode;

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

    /** 楼栋名称(关联查询字段,不存在于数据库表) */
    @TableField(exist = false)
    private String buildingName;

    /** 项目ID(关联查询字段,不存在于数据库表,用于查询过滤) */
    @TableField(exist = false)
    private Long projectId;

    /** 项目名称(关联查询字段,不存在于数据库表) */
    @TableField(exist = false)
    private String projectName;

    public void setUnitId(Long unitId) {
        this.unitId = unitId;
    }

    public Long getUnitId() {
        return unitId;
    }

    public void setBuildingId(Long buildingId) {
        this.buildingId = buildingId;
    }

    public Long getBuildingId() {
        return buildingId;
    }

    public void setUnitName(String unitName) {
        this.unitName = unitName;
    }

    public String getUnitName() {
        return unitName;
    }

    public void setUnitCode(String unitCode) {
        this.unitCode = unitCode;
    }

    public String getUnitCode() {
        return unitCode;
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

    public void setBuildingName(String buildingName) {
        this.buildingName = buildingName;
    }

    public String getBuildingName() {
        return buildingName;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    public Long getProjectId() {
        return projectId;
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
            .append("unitId", getUnitId())
            .append("buildingId", getBuildingId())
            .append("unitName", getUnitName())
            .append("unitCode", getUnitCode())
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
