package com.ruoyi.system.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 设施物品总表 hz_facility_item
 *
 * @author ruoyi
 * @date 2026-04-17
 */
@TableName("hz_facility_item")
public class HzFacilityItem extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 设施物品ID */
    @TableId(value = "facility_item_id", type = IdType.AUTO)
    private Long facilityItemId;

    /** 物品名称 */
    @TableField("facility_name")
    private String facilityName;

    /** 分类 */
    @TableField("facility_category")
    private String facilityCategory;

    /** 排序 */
    @TableField("sort_order")
    private Integer sortOrder;

    /** 状态(0正常 1停用) */
    @TableField("status")
    private String status;

    /** 删除标志(0正常 1删除) */
    @TableField("del_flag")
    @TableLogic
    private String delFlag;

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

    public Integer getSortOrder()
    {
        return sortOrder;
    }

    public void setSortOrder(Integer sortOrder)
    {
        this.sortOrder = sortOrder;
    }

    public String getStatus()
    {
        return status;
    }

    public void setStatus(String status)
    {
        this.status = status;
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
        return "HzFacilityItem{" +
                "facilityItemId=" + facilityItemId +
                ", facilityName='" + facilityName + '\'' +
                ", facilityCategory='" + facilityCategory + '\'' +
                ", sortOrder=" + sortOrder +
                ", status='" + status + '\'' +
                ", delFlag='" + delFlag + '\'' +
                '}';
    }
}
