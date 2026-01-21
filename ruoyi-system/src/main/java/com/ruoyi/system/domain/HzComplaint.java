package com.ruoyi.system.domain;

import com.baomidou.mybatisplus.annotation.*;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 投诉建议对象 hz_complaint
 *
 * @author ruoyi
 */
@TableName("hz_complaint")
public class HzComplaint extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 投诉ID */
    @TableId(type = IdType.AUTO)
    private Long complaintId;

    /** 用户ID */
    @TableField("user_id")
    private Long userId;

    /** 投诉标题 */
    @TableField("title")
    private String title;

    /** 投诉内容 */
    @TableField("content")
    private String content;

    /** 联系方式 */
    @TableField("contact_phone")
    private String contactPhone;

    /** 上传图片(多个用逗号分隔) */
    @TableField("images")
    private String images;

    /** 状态：0-待处理，1-已处理 */
    @TableField("status")
    private String status;

    /** 是否已催办：0-未催办，1-已催办 */
    @TableField("is_urged")
    private String isUrged;

    /** 催办次数 */
    @TableField("urge_count")
    private Integer urgeCount;

    /** 最后催办时间 */
    @TableField("urge_time")
    private String urgeTime;

    /** 处理结果 */
    @TableField("handle_result")
    private String handleResult;

    /** 处理时间 */
    @TableField("handle_time")
    private String handleTime;

    /** 处理人 */
    @TableField("handle_by")
    private String handleBy;

    /** 删除标志：0-正常，2-删除 */
    @TableField("del_flag")
    private String delFlag;

    public Long getComplaintId()
    {
        return complaintId;
    }

    public void setComplaintId(Long complaintId)
    {
        this.complaintId = complaintId;
    }

    public Long getUserId()
    {
        return userId;
    }

    public void setUserId(Long userId)
    {
        this.userId = userId;
    }

    public String getTitle()
    {
        return title;
    }

    public void setTitle(String title)
    {
        this.title = title;
    }

    public String getContent()
    {
        return content;
    }

    public void setContent(String content)
    {
        this.content = content;
    }

    public String getContactPhone()
    {
        return contactPhone;
    }

    public void setContactPhone(String contactPhone)
    {
        this.contactPhone = contactPhone;
    }

    public String getImages()
    {
        return images;
    }

    public void setImages(String images)
    {
        this.images = images;
    }

    public String getStatus()
    {
        return status;
    }

    public void setStatus(String status)
    {
        this.status = status;
    }

    public String getIsUrged()
    {
        return isUrged;
    }

    public void setIsUrged(String isUrged)
    {
        this.isUrged = isUrged;
    }

    public Integer getUrgeCount()
    {
        return urgeCount;
    }

    public void setUrgeCount(Integer urgeCount)
    {
        this.urgeCount = urgeCount;
    }

    public String getUrgeTime()
    {
        return urgeTime;
    }

    public void setUrgeTime(String urgeTime)
    {
        this.urgeTime = urgeTime;
    }

    public String getHandleResult()
    {
        return handleResult;
    }

    public void setHandleResult(String handleResult)
    {
        this.handleResult = handleResult;
    }

    public String getHandleTime()
    {
        return handleTime;
    }

    public void setHandleTime(String handleTime)
    {
        this.handleTime = handleTime;
    }

    public String getHandleBy()
    {
        return handleBy;
    }

    public void setHandleBy(String handleBy)
    {
        this.handleBy = handleBy;
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
        return "HzComplaint{" +
                "complaintId=" + complaintId +
                ", userId=" + userId +
                ", title='" + title + '\'' +
                ", content='" + content + '\'' +
                ", contactPhone='" + contactPhone + '\'' +
                ", images='" + images + '\'' +
                ", status='" + status + '\'' +
                ", isUrged='" + isUrged + '\'' +
                ", urgeCount=" + urgeCount +
                ", urgeTime='" + urgeTime + '\'' +
                ", handleResult='" + handleResult + '\'' +
                ", handleTime='" + handleTime + '\'' +
                ", handleBy='" + handleBy + '\'' +
                ", delFlag='" + delFlag + '\'' +
                '}';
    }
}
