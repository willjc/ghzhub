package com.ruoyi.system.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.math.BigDecimal;

/**
 * 房源对象 hz_house
 *
 * @author ruoyi
 * @date 2025-11-17
 */
@TableName("hz_house")
public class HzHouse extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 房源ID */
    @TableId(type = IdType.AUTO)
    private Long houseId;

    /** 项目ID */
    @Excel(name = "项目ID", type = Excel.Type.IMPORT, prompt = "必填，填写已存在的项目ID，如：1")
    @TableField("project_id")
    private Long projectId;

    /** 楼栋ID */
    @Excel(name = "楼栋ID", type = Excel.Type.IMPORT, prompt = "必填，填写已存在的楼栋ID，如：1")
    @TableField("building_id")
    private Long buildingId;

    /** 单元ID */
    @Excel(name = "单元ID", type = Excel.Type.IMPORT, prompt = "必填，填写已存在的单元ID，如：1")
    @TableField("unit_id")
    private Long unitId;

    /** 房源编码 */
    @Excel(name = "房源编码", type = Excel.Type.IMPORT, prompt = "必填，唯一标识，如：FY001")
    @TableField("house_code")
    private String houseCode;

    /** 房间号 */
    @Excel(name = "房间号", type = Excel.Type.IMPORT, prompt = "必填，如：101、202")
    @TableField("house_no")
    private String houseNo;

    /** 楼层 */
    @Excel(name = "楼层", type = Excel.Type.IMPORT, prompt = "必填，填写数字，如：1、2、3")
    @TableField("floor")
    private Integer floor;

    /** 户型ID */
    @Excel(name = "户型ID", type = Excel.Type.IMPORT, prompt = "必填，填写已存在的户型ID，如：1")
    @TableField("house_type_id")
    private Long houseTypeId;

    /** 户型名称 */
    @TableField("house_type_name")
    private String houseTypeName;

    /** 户型详情(非数据库字段,用于前端显示,格式: 一室一厅(1室1厅1卫)) */
    @TableField(exist = false)
    private String houseTypeDetail;

    /** 建筑面积(平方米) */
    @Excel(name = "建筑面积", type = Excel.Type.IMPORT, prompt = "必填，单位：平方米，如：50.5、88.0")
    @TableField("area")
    private BigDecimal area;

    /** 租金(元/月) */
    @Excel(name = "租金", type = Excel.Type.IMPORT, prompt = "必填，单位：元/月，如：1500、2000")
    @TableField("rent_price")
    private BigDecimal rentPrice;

    /** 押金(元) */
    @Excel(name = "押金", type = Excel.Type.IMPORT, prompt = "必填，单位：元，如：3000、4000")
    @TableField("deposit")
    private BigDecimal deposit;

    /** 朝向 */
    @Excel(name = "朝向", type = Excel.Type.IMPORT, prompt = "选填，如：南、北、东、西、东南、西南、东北、西北")
    @TableField("orientation")
    private String orientation;

    /** 装修情况 */
    @Excel(name = "装修情况", type = Excel.Type.IMPORT, prompt = "选填，如：毛坯、简装、精装、豪装")
    @TableField("decoration")
    private String decoration;

    /** 房间设施(多个用逗号分隔) */
    @Excel(name = "房间设施", type = Excel.Type.IMPORT, prompt = "选填，多个用逗号分隔，如：空调,冰箱,洗衣机,热水器")
    @TableField("facilities")
    private String facilities;

    /** 房源状态(0:空置 1:已预订 2:已出租 3:维修中 4:下架) */
    @Excel(name = "房源状态", type = Excel.Type.IMPORT, readConverterExp = "0=空置,1=已预订,2=已出租,3=维修中,4=下架", prompt = "必填，填写：0=空置、1=已预订、2=已出租、3=维修中、4=下架")
    @TableField("house_status")
    private String houseStatus;

    /** 是否精选房源(0:否 1:是) */
    @Excel(name = "是否精选房源", type = Excel.Type.IMPORT, readConverterExp = "0=否,1=是", prompt = "选填，填写：0=否、1=是，默认：0")
    @TableField("is_featured")
    private String isFeatured;

    /** 浏览次数 */
    @TableField("view_count")
    private Integer viewCount;

    /** 状态(0:正常 1:停用) */
    @Excel(name = "状态", type = Excel.Type.IMPORT, readConverterExp = "0=正常,1=停用", prompt = "必填，填写：0=正常、1=停用，默认：0")
    @TableField("status")
    private String status;

    public void setHouseId(Long houseId)
    {
        this.houseId = houseId;
    }

    public Long getHouseId()
    {
        return houseId;
    }

    public void setProjectId(Long projectId)
    {
        this.projectId = projectId;
    }

    public Long getProjectId()
    {
        return projectId;
    }

    public void setBuildingId(Long buildingId)
    {
        this.buildingId = buildingId;
    }

    public Long getBuildingId()
    {
        return buildingId;
    }

    public void setUnitId(Long unitId)
    {
        this.unitId = unitId;
    }

    public Long getUnitId()
    {
        return unitId;
    }

    public void setHouseCode(String houseCode)
    {
        this.houseCode = houseCode;
    }

    public String getHouseCode()
    {
        return houseCode;
    }

    public void setHouseNo(String houseNo)
    {
        this.houseNo = houseNo;
    }

    public String getHouseNo()
    {
        return houseNo;
    }

    public void setFloor(Integer floor)
    {
        this.floor = floor;
    }

    public Integer getFloor()
    {
        return floor;
    }

    public void setHouseTypeId(Long houseTypeId)
    {
        this.houseTypeId = houseTypeId;
    }

    public Long getHouseTypeId()
    {
        return houseTypeId;
    }

    public void setHouseTypeName(String houseTypeName)
    {
        this.houseTypeName = houseTypeName;
    }

    public String getHouseTypeName()
    {
        return houseTypeName;
    }

    public void setHouseTypeDetail(String houseTypeDetail)
    {
        this.houseTypeDetail = houseTypeDetail;
    }

    public String getHouseTypeDetail()
    {
        return houseTypeDetail;
    }

    public void setArea(BigDecimal area)
    {
        this.area = area;
    }

    public BigDecimal getArea()
    {
        return area;
    }

    public void setRentPrice(BigDecimal rentPrice)
    {
        this.rentPrice = rentPrice;
    }

    public BigDecimal getRentPrice()
    {
        return rentPrice;
    }

    public void setDeposit(BigDecimal deposit)
    {
        this.deposit = deposit;
    }

    public BigDecimal getDeposit()
    {
        return deposit;
    }

    public void setOrientation(String orientation)
    {
        this.orientation = orientation;
    }

    public String getOrientation()
    {
        return orientation;
    }

    public void setDecoration(String decoration)
    {
        this.decoration = decoration;
    }

    public String getDecoration()
    {
        return decoration;
    }

    public void setFacilities(String facilities)
    {
        this.facilities = facilities;
    }

    public String getFacilities()
    {
        return facilities;
    }

    public void setHouseStatus(String houseStatus)
    {
        this.houseStatus = houseStatus;
    }

    public String getHouseStatus()
    {
        return houseStatus;
    }

    public void setIsFeatured(String isFeatured)
    {
        this.isFeatured = isFeatured;
    }

    public String getIsFeatured()
    {
        return isFeatured;
    }

    public void setViewCount(Integer viewCount)
    {
        this.viewCount = viewCount;
    }

    public Integer getViewCount()
    {
        return viewCount;
    }

    public void setStatus(String status)
    {
        this.status = status;
    }

    public String getStatus()
    {
        return status;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("houseId", getHouseId())
            .append("projectId", getProjectId())
            .append("buildingId", getBuildingId())
            .append("unitId", getUnitId())
            .append("houseCode", getHouseCode())
            .append("houseNo", getHouseNo())
            .append("floor", getFloor())
            .append("houseTypeId", getHouseTypeId())
            .append("houseTypeName", getHouseTypeName())
            .append("area", getArea())
            .append("rentPrice", getRentPrice())
            .append("deposit", getDeposit())
            .append("orientation", getOrientation())
            .append("decoration", getDecoration())
            .append("facilities", getFacilities())
            .append("houseStatus", getHouseStatus())
            .append("isFeatured", getIsFeatured())
            .append("viewCount", getViewCount())
            .append("status", getStatus())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("updateBy", getUpdateBy())
            .append("updateTime", getUpdateTime())
            .append("remark", getRemark())
            .toString();
    }
}
