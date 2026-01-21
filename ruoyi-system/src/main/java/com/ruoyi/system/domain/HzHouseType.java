package com.ruoyi.system.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import com.ruoyi.common.core.domain.BaseEntity;

import java.math.BigDecimal;

/**
 * 户型对象 hz_house_type
 *
 * @author ruoyi
 * @date 2025-11-18
 */
@TableName("hz_house_type")
public class HzHouseType extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 户型ID */
    @TableId(type = IdType.AUTO)
    private Long houseTypeId;

    /** 项目ID */
    @TableField("project_id")
    private Long projectId;

    /** 项目名称(非数据库字段,用于前端显示) */
    @TableField(exist = false)
    private String projectName;

    /** 户型名称(如:一室一厅) */
    private String houseTypeName;

    /** 户型编码 */
    private String houseTypeCode;

    /** 卧室数量 */
    private Integer bedroomCount;

    /** 客厅数量 */
    private Integer livingroomCount;

    /** 卫生间数量 */
    private Integer bathroomCount;

    /** 厨房数量 */
    private Integer kitchenCount;

    /** 阳台数量 */
    private Integer balconyCount;

    /** 典型面积(平方米) */
    private BigDecimal typicalArea;

    /** 户型图URL */
    private String layoutImage;

    /** 状态(0:正常 1:停用) */
    private String status;

    /** 显示顺序 */
    private Integer sortOrder;

    /** 删除标志(0:正常 2:删除) */
    @TableField("del_flag")
    @TableLogic
    private String delFlag;

    public void setHouseTypeId(Long houseTypeId)
    {
        this.houseTypeId = houseTypeId;
    }

    public Long getHouseTypeId()
    {
        return houseTypeId;
    }

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

    public void setHouseTypeName(String houseTypeName)
    {
        this.houseTypeName = houseTypeName;
    }

    public String getHouseTypeName()
    {
        return houseTypeName;
    }

    public void setHouseTypeCode(String houseTypeCode)
    {
        this.houseTypeCode = houseTypeCode;
    }

    public String getHouseTypeCode()
    {
        return houseTypeCode;
    }

    public void setBedroomCount(Integer bedroomCount)
    {
        this.bedroomCount = bedroomCount;
    }

    public Integer getBedroomCount()
    {
        return bedroomCount;
    }

    public void setLivingroomCount(Integer livingroomCount)
    {
        this.livingroomCount = livingroomCount;
    }

    public Integer getLivingroomCount()
    {
        return livingroomCount;
    }

    public void setBathroomCount(Integer bathroomCount)
    {
        this.bathroomCount = bathroomCount;
    }

    public Integer getBathroomCount()
    {
        return bathroomCount;
    }

    public void setKitchenCount(Integer kitchenCount)
    {
        this.kitchenCount = kitchenCount;
    }

    public Integer getKitchenCount()
    {
        return kitchenCount;
    }

    public void setBalconyCount(Integer balconyCount)
    {
        this.balconyCount = balconyCount;
    }

    public Integer getBalconyCount()
    {
        return balconyCount;
    }

    public void setTypicalArea(BigDecimal typicalArea)
    {
        this.typicalArea = typicalArea;
    }

    public BigDecimal getTypicalArea()
    {
        return typicalArea;
    }

    public void setLayoutImage(String layoutImage)
    {
        this.layoutImage = layoutImage;
    }

    public String getLayoutImage()
    {
        return layoutImage;
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

    @Override
    public String toString() {
        return "HzHouseType{" +
                "houseTypeId=" + houseTypeId +
                ", projectId=" + projectId +
                ", houseTypeName='" + houseTypeName + '\'' +
                ", houseTypeCode='" + houseTypeCode + '\'' +
                ", bedroomCount=" + bedroomCount +
                ", livingroomCount=" + livingroomCount +
                ", bathroomCount=" + bathroomCount +
                ", kitchenCount=" + kitchenCount +
                ", balconyCount=" + balconyCount +
                ", typicalArea=" + typicalArea +
                ", layoutImage='" + layoutImage + '\'' +
                ", status='" + status + '\'' +
                ", sortOrder=" + sortOrder +
                ", delFlag='" + delFlag + '\'' +
                '}';
    }
}
