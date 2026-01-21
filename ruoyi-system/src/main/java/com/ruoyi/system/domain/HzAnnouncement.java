package com.ruoyi.system.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 公告通知对象 hz_announcement
 *
 * @author ruoyi
 */
@TableName("hz_announcement")
public class HzAnnouncement extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 公告ID */
    @TableId(type = IdType.AUTO)
    private Long announcementId;

    /** 公告标题 */
    @TableField("announcement_title")
    private String announcementTitle;

    /** 公告类型(1:通知 2:公告 3:活动) */
    @TableField("announcement_type")
    private String announcementType;

    /** 公告内容 */
    @TableField("announcement_content")
    private String announcementContent;

    /** 封面图片 */
    @TableField("cover_image")
    private String coverImage;

    /** 是否置顶(0:否 1:是) */
    @TableField("is_top")
    private String isTop;

    /** 发布时间 */
    @TableField("publish_time")
    private String publishTime;

    /** 浏览次数 */
    @TableField("view_count")
    private Long viewCount;

    /** 状态(0:草稿 1:已发布 2:已下架) */
    @TableField("status")
    private String status;

    /** 删除标志(0:正常 2:删除) */
    @TableField("del_flag")
    private String delFlag;

    public Long getAnnouncementId()
    {
        return announcementId;
    }

    public void setAnnouncementId(Long announcementId)
    {
        this.announcementId = announcementId;
    }

    public String getAnnouncementTitle()
    {
        return announcementTitle;
    }

    public void setAnnouncementTitle(String announcementTitle)
    {
        this.announcementTitle = announcementTitle;
    }

    public String getAnnouncementType()
    {
        return announcementType;
    }

    public void setAnnouncementType(String announcementType)
    {
        this.announcementType = announcementType;
    }

    public String getAnnouncementContent()
    {
        return announcementContent;
    }

    public void setAnnouncementContent(String announcementContent)
    {
        this.announcementContent = announcementContent;
    }

    public String getCoverImage()
    {
        return coverImage;
    }

    public void setCoverImage(String coverImage)
    {
        this.coverImage = coverImage;
    }

    public String getIsTop()
    {
        return isTop;
    }

    public void setIsTop(String isTop)
    {
        this.isTop = isTop;
    }

    public String getPublishTime()
    {
        return publishTime;
    }

    public void setPublishTime(String publishTime)
    {
        this.publishTime = publishTime;
    }

    public Long getViewCount()
    {
        return viewCount;
    }

    public void setViewCount(Long viewCount)
    {
        this.viewCount = viewCount;
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
        return "HzAnnouncement{" +
                "announcementId=" + announcementId +
                ", announcementTitle='" + announcementTitle + '\'' +
                ", announcementType='" + announcementType + '\'' +
                ", announcementContent='" + announcementContent + '\'' +
                ", coverImage='" + coverImage + '\'' +
                ", isTop='" + isTop + '\'' +
                ", publishTime='" + publishTime + '\'' +
                ", viewCount=" + viewCount +
                ", status='" + status + '\'' +
                ", delFlag='" + delFlag + '\'' +
                '}';
    }
}
