package com.ruoyi.system.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.ruoyi.common.core.domain.BaseEntity;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * 房源图片对象 hz_house_image
 *
 * @author ruoyi
 * @date 2025-11-17
 */
@TableName("hz_house_image")
public class HzHouseImage extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 图片ID */
    @TableId(type = IdType.AUTO)
    private Long imageId;

    /** 房源ID */
    @TableField("house_id")
    private Long houseId;

    /** 图片URL */
    @TableField("image_url")
    private String imageUrl;

    /** 图片类型(1:实景图 2:户型图) */
    @TableField("image_type")
    private String imageType;

    /** 是否封面(0:否 1:是) */
    @TableField("is_cover")
    private String isCover;

    /** 显示顺序 */
    @TableField("sort_order")
    private Integer sortOrder;

    /** 删除标志(0:正常 2:删除) */
    @TableField("del_flag")
    private String delFlag;

    public void setImageId(Long imageId)
    {
        this.imageId = imageId;
    }

    public Long getImageId()
    {
        return imageId;
    }

    public void setHouseId(Long houseId)
    {
        this.houseId = houseId;
    }

    public Long getHouseId()
    {
        return houseId;
    }

    public void setImageUrl(String imageUrl)
    {
        this.imageUrl = imageUrl;
    }

    public String getImageUrl()
    {
        return imageUrl;
    }

    public void setImageType(String imageType)
    {
        this.imageType = imageType;
    }

    public String getImageType()
    {
        return imageType;
    }

    public void setIsCover(String isCover)
    {
        this.isCover = isCover;
    }

    public String getIsCover()
    {
        return isCover;
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
            .append("imageId", getImageId())
            .append("houseId", getHouseId())
            .append("imageUrl", getImageUrl())
            .append("imageType", getImageType())
            .append("isCover", getIsCover())
            .append("sortOrder", getSortOrder())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("updateBy", getUpdateBy())
            .append("updateTime", getUpdateTime())
            .append("remark", getRemark())
            .toString();
    }
}
