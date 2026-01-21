package com.ruoyi.system.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.ruoyi.common.core.domain.BaseEntity;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.math.BigDecimal;

/**
 * 项目对象 hz_project
 *
 * @author ruoyi
 * @date 2025-11-17
 */
@TableName("hz_project")
public class HzProject extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 项目ID */
    @TableId(type = IdType.AUTO)
    private Long projectId;

    /** 项目名称 */
    @TableField("project_name")
    private String projectName;

    /** 项目编码 */
    @TableField("project_code")
    private String projectCode;

    /** 项目类型(1:人才公寓 2:保租房 3:市场租赁) */
    @TableField("project_type")
    private String projectType;

    /** 项目地址 */
    @TableField("address")
    private String address;

    /** 经度 */
    @TableField("longitude")
    private BigDecimal longitude;

    /** 纬度 */
    @TableField("latitude")
    private BigDecimal latitude;

    /** 总楼栋数 */
    @TableField("total_buildings")
    private Integer totalBuildings;

    /** 总房源数 */
    @TableField("total_houses")
    private Integer totalHouses;

    /** 可用房源数 */
    @TableField("available_houses")
    private Integer availableHouses;

    /** 项目介绍 */
    @TableField("project_intro")
    private String projectIntro;

    /** 项目主图 */
    @TableField("cover_image")
    private String coverImage;

    /** 起租价格(元/月) */
    @TableField("price")
    private BigDecimal price;

    /** 配套设施(多个用逗号分隔) */
    @TableField("facilities")
    private String facilities;

    /** 交通信息 */
    @TableField("transportation")
    private String transportation;

    /** 项目负责人ID */
    @TableField("manager_id")
    private Long managerId;

    /** 项目负责人姓名 */
    @TableField("manager_name")
    private String managerName;

    /** 负责人电话 */
    @TableField("manager_phone")
    private String managerPhone;

    /** 状态(0:正常 1:停用) */
    @TableField("status")
    private String status;

    /** 显示顺序 */
    @TableField("sort_order")
    private Integer sortOrder;

    /** 删除标志(0:存在 2:删除) */
    @TableField("del_flag")
    private String delFlag;

    /** 房源分布 */
    @TableField("distribution")
    private String distribution;

    /** 户型 */
    @TableField("house_type")
    private String houseType;

    /** 面积范围 */
    @TableField("area")
    private String area;

    /** 租金详情 */
    @TableField("rent_detail")
    private String rentDetail;

    /** 物业公司 */
    @TableField("property_company")
    private String propertyCompany;

    /** 物业费 */
    @TableField("property_fee")
    private String propertyFee;

    /** 电费 */
    @TableField("electric_fee")
    private String electricFee;

    /** 水费 */
    @TableField("water_fee")
    private String waterFee;

    /** 燃气费 */
    @TableField("gas_fee")
    private String gasFee;

    public void setProjectId(Long projectId)
    {
        this.projectId = projectId;
    }

    public Long getProjectId()
    {
        return projectId;
    }

    public void setProjectName(String projectName)
    {
        this.projectName = projectName;
    }

    public String getProjectName()
    {
        return projectName;
    }

    public void setProjectCode(String projectCode)
    {
        this.projectCode = projectCode;
    }

    public String getProjectCode()
    {
        return projectCode;
    }

    public void setProjectType(String projectType)
    {
        this.projectType = projectType;
    }

    public String getProjectType()
    {
        return projectType;
    }

    public void setAddress(String address)
    {
        this.address = address;
    }

    public String getAddress()
    {
        return address;
    }

    public void setLongitude(BigDecimal longitude)
    {
        this.longitude = longitude;
    }

    public BigDecimal getLongitude()
    {
        return longitude;
    }

    public void setLatitude(BigDecimal latitude)
    {
        this.latitude = latitude;
    }

    public BigDecimal getLatitude()
    {
        return latitude;
    }

    public void setTotalBuildings(Integer totalBuildings)
    {
        this.totalBuildings = totalBuildings;
    }

    public Integer getTotalBuildings()
    {
        return totalBuildings;
    }

    public void setTotalHouses(Integer totalHouses)
    {
        this.totalHouses = totalHouses;
    }

    public Integer getTotalHouses()
    {
        return totalHouses;
    }

    public void setAvailableHouses(Integer availableHouses)
    {
        this.availableHouses = availableHouses;
    }

    public Integer getAvailableHouses()
    {
        return availableHouses;
    }

    public void setProjectIntro(String projectIntro)
    {
        this.projectIntro = projectIntro;
    }

    public String getProjectIntro()
    {
        return projectIntro;
    }

    public void setCoverImage(String coverImage)
    {
        this.coverImage = coverImage;
    }

    public String getCoverImage()
    {
        return coverImage;
    }

    public void setPrice(BigDecimal price)
    {
        this.price = price;
    }

    public BigDecimal getPrice()
    {
        return price;
    }

    public void setFacilities(String facilities)
    {
        this.facilities = facilities;
    }

    public String getFacilities()
    {
        return facilities;
    }

    public void setTransportation(String transportation)
    {
        this.transportation = transportation;
    }

    public String getTransportation()
    {
        return transportation;
    }

    public void setManagerId(Long managerId)
    {
        this.managerId = managerId;
    }

    public Long getManagerId()
    {
        return managerId;
    }

    public void setManagerName(String managerName)
    {
        this.managerName = managerName;
    }

    public String getManagerName()
    {
        return managerName;
    }

    public void setManagerPhone(String managerPhone)
    {
        this.managerPhone = managerPhone;
    }

    public String getManagerPhone()
    {
        return managerPhone;
    }

    public void setStatus(String status)
    {
        this.status = status;
    }

    public String getStatus()
    {
        return status;
    }

    public void setSortOrder(Integer sortOrder)
    {
        this.sortOrder = sortOrder;
    }

    public Integer getSortOrder()
    {
        return sortOrder;
    }

    public void setDelFlag(String delFlag)
    {
        this.delFlag = delFlag;
    }

    public String getDelFlag()
    {
        return delFlag;
    }

    public void setDistribution(String distribution)
    {
        this.distribution = distribution;
    }

    public String getDistribution()
    {
        return distribution;
    }

    public void setHouseType(String houseType)
    {
        this.houseType = houseType;
    }

    public String getHouseType()
    {
        return houseType;
    }

    public void setArea(String area)
    {
        this.area = area;
    }

    public String getArea()
    {
        return area;
    }

    public void setRentDetail(String rentDetail)
    {
        this.rentDetail = rentDetail;
    }

    public String getRentDetail()
    {
        return rentDetail;
    }

    public void setPropertyCompany(String propertyCompany)
    {
        this.propertyCompany = propertyCompany;
    }

    public String getPropertyCompany()
    {
        return propertyCompany;
    }

    public void setPropertyFee(String propertyFee)
    {
        this.propertyFee = propertyFee;
    }

    public String getPropertyFee()
    {
        return propertyFee;
    }

    public void setElectricFee(String electricFee)
    {
        this.electricFee = electricFee;
    }

    public String getElectricFee()
    {
        return electricFee;
    }

    public void setWaterFee(String waterFee)
    {
        this.waterFee = waterFee;
    }

    public String getWaterFee()
    {
        return waterFee;
    }

    public void setGasFee(String gasFee)
    {
        this.gasFee = gasFee;
    }

    public String getGasFee()
    {
        return gasFee;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("projectId", getProjectId())
            .append("projectName", getProjectName())
            .append("projectCode", getProjectCode())
            .append("projectType", getProjectType())
            .append("address", getAddress())
            .append("longitude", getLongitude())
            .append("latitude", getLatitude())
            .append("totalBuildings", getTotalBuildings())
            .append("totalHouses", getTotalHouses())
            .append("availableHouses", getAvailableHouses())
            .append("distribution", getDistribution())
            .append("houseType", getHouseType())
            .append("area", getArea())
            .append("rentDetail", getRentDetail())
            .append("propertyCompany", getPropertyCompany())
            .append("propertyFee", getPropertyFee())
            .append("electricFee", getElectricFee())
            .append("waterFee", getWaterFee())
            .append("gasFee", getGasFee())
            .append("projectIntro", getProjectIntro())
            .append("coverImage", getCoverImage())
            .append("price", getPrice())
            .append("facilities", getFacilities())
            .append("transportation", getTransportation())
            .append("managerId", getManagerId())
            .append("managerName", getManagerName())
            .append("managerPhone", getManagerPhone())
            .append("status", getStatus())
            .append("sortOrder", getSortOrder())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("updateBy", getUpdateBy())
            .append("updateTime", getUpdateTime())
            .append("remark", getRemark())
            .toString();
    }
}
