package com.ruoyi.system.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 户型图片对象 hz_house_type_image
 *
 * @author ruoyi
 * @date 2025-11-18
 */
@TableName("hz_house_type_image")
public class HzHouseTypeImage extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 图片ID */
    @TableId(type = IdType.AUTO)
    private Long imageId;

    /** 户型ID */
    private Long houseTypeId;

    /** 图片URL */
    private String imageUrl;

    /** 图片类型(1:户型图 2:实景图) */
    private String imageType;

    /** 是否封面(0:否 1:是) */
    private String isCover;

    /** 显示顺序 */
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

    public void setHouseTypeId(Long houseTypeId)
    {
        this.houseTypeId = houseTypeId;
    }

    public Long getHouseTypeId()
    {
        return houseTypeId;
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
        return "HzHouseTypeImage{" +
                "imageId=" + imageId +
                ", houseTypeId=" + houseTypeId +
                ", imageUrl='" + imageUrl + '\'' +
                ", imageType='" + imageType + '\'' +
                ", isCover='" + isCover + '\'' +
                ", sortOrder=" + sortOrder +
                ", delFlag='" + delFlag + '\'' +
                '}';
    }
}
