package com.ruoyi.system.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.ruoyi.common.core.domain.BaseEntity;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * 房源VR对象 hz_house_vr
 *
 * @author ruoyi
 * @date 2026-01-11
 */
@TableName("hz_house_vr")
public class HzHouseVr extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** VRID */
    @TableId(type = IdType.AUTO)
    private Long vrId;

    /** 房源ID */
    @TableField("house_id")
    private Long houseId;

    /** VR名称 */
    @TableField("vr_name")
    private String vrName;

    /** VR图片URL */
    @TableField("vr_url")
    private String vrUrl;

    /** 显示顺序 */
    @TableField("sort_order")
    private Integer sortOrder;

    /** 删除标志(0:正常 2:删除) */
    @TableField("del_flag")
    private String delFlag;

    public void setVrId(Long vrId)
    {
        this.vrId = vrId;
    }

    public Long getVrId()
    {
        return vrId;
    }

    public void setHouseId(Long houseId)
    {
        this.houseId = houseId;
    }

    public Long getHouseId()
    {
        return houseId;
    }

    public void setVrName(String vrName)
    {
        this.vrName = vrName;
    }

    public String getVrName()
    {
        return vrName;
    }

    public void setVrUrl(String vrUrl)
    {
        this.vrUrl = vrUrl;
    }

    public String getVrUrl()
    {
        return vrUrl;
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
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("vrId", getVrId())
            .append("houseId", getHouseId())
            .append("vrName", getVrName())
            .append("vrUrl", getVrUrl())
            .append("sortOrder", getSortOrder())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("updateBy", getUpdateBy())
            .append("updateTime", getUpdateTime())
            .append("remark", getRemark())
            .append("delFlag", getDelFlag())
            .toString();
    }
}
