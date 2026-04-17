package com.ruoyi.system.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 房源设施 hz_house_facility
 *
 * @author ruoyi
 * @date 2026-04-17
 */
@TableName("hz_house_facility")
public class HzHouseFacility extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 主键ID */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /** 房源ID */
    @TableField("house_id")
    private Long houseId;

    /** 设施ID */
    @TableField("facility_item_id")
    private Long facilityItemId;

    /** 物品名称（冗余） */
    @TableField("facility_name")
    private String facilityName;

    /** 分类（冗余） */
    @TableField("facility_category")
    private String facilityCategory;

    /** 数量 */
    @TableField("quantity")
    private Integer quantity;

    /** 状态 */
    @TableField("item_status")
    private String itemStatus;

    /** 说明 */
    @TableField("remark")
    private String remark;

    /** 删除标志(0正常 1删除) */
    @TableField("del_flag")
    @TableLogic
    private String delFlag;

    public Long getId()
    {
        return id;
    }

    public void setId(Long id)
    {
        this.id = id;
    }

    public Long getHouseId()
    {
        return houseId;
    }

    public void setHouseId(Long houseId)
    {
        this.houseId = houseId;
    }

    public Long getFacilityItemId()
    {
        return facilityItemId;
    }

    public void setFacilityItemId(Long facilityItemId)
    {
        this.facilityItemId = facilityItemId;
    }

    public String getFacilityName()
    {
        return facilityName;
    }

    public void setFacilityName(String facilityName)
    {
        this.facilityName = facilityName;
    }

    public String getFacilityCategory()
    {
        return facilityCategory;
    }

    public void setFacilityCategory(String facilityCategory)
    {
        this.facilityCategory = facilityCategory;
    }

    public Integer getQuantity()
    {
        return quantity;
    }

    public void setQuantity(Integer quantity)
    {
        this.quantity = quantity;
    }

    public String getItemStatus()
    {
        return itemStatus;
    }

    public void setItemStatus(String itemStatus)
    {
        this.itemStatus = itemStatus;
    }

    @Override
    public String getRemark()
    {
        return remark;
    }

    @Override
    public void setRemark(String remark)
    {
        this.remark = remark;
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
        return "HzHouseFacility{" +
                "id=" + id +
                ", houseId=" + houseId +
                ", facilityItemId=" + facilityItemId +
                ", facilityName='" + facilityName + '\'' +
                ", facilityCategory='" + facilityCategory + '\'' +
                ", quantity=" + quantity +
                ", itemStatus='" + itemStatus + '\'' +
                ", remark='" + remark + '\'' +
                ", delFlag='" + delFlag + '\'' +
                '}';
    }
}
