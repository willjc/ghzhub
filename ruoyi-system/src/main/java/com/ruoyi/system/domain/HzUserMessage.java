package com.ruoyi.system.domain;

import com.baomidou.mybatisplus.annotation.*;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 用户消息对象 hz_user_message
 *
 * @author ruoyi
 */
@TableName("hz_user_message")
public class HzUserMessage extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 消息ID */
    @TableId(type = IdType.AUTO)
    private Long messageId;

    /** 用户ID */
    @TableField("user_id")
    private Long userId;

    /** 消息类型：login-登录提醒, system-系统通知, bill-账单提醒, contract-合同提醒 */
    @TableField("message_type")
    private String messageType;

    /** 消息标题 */
    @TableField("message_title")
    private String messageTitle;

    /** 消息内容 */
    @TableField("message_content")
    private String messageContent;

    /** 是否已读：0-未读，1-已读 */
    @TableField("is_read")
    private String isRead;

    /** 阅读时间 */
    @TableField("read_time")
    private String readTime;

    /** 删除标志：0-正常，2-删除 */
    @TableField("del_flag")
    private String delFlag;

    public Long getMessageId()
    {
        return messageId;
    }

    public void setMessageId(Long messageId)
    {
        this.messageId = messageId;
    }

    public Long getUserId()
    {
        return userId;
    }

    public void setUserId(Long userId)
    {
        this.userId = userId;
    }

    public String getMessageType()
    {
        return messageType;
    }

    public void setMessageType(String messageType)
    {
        this.messageType = messageType;
    }

    public String getMessageTitle()
    {
        return messageTitle;
    }

    public void setMessageTitle(String messageTitle)
    {
        this.messageTitle = messageTitle;
    }

    public String getMessageContent()
    {
        return messageContent;
    }

    public void setMessageContent(String messageContent)
    {
        this.messageContent = messageContent;
    }

    public String getIsRead()
    {
        return isRead;
    }

    public void setIsRead(String isRead)
    {
        this.isRead = isRead;
    }

    public String getReadTime()
    {
        return readTime;
    }

    public void setReadTime(String readTime)
    {
        this.readTime = readTime;
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
        return "HzUserMessage{" +
                "messageId=" + messageId +
                ", userId=" + userId +
                ", messageType='" + messageType + '\'' +
                ", messageTitle='" + messageTitle + '\'' +
                ", messageContent='" + messageContent + '\'' +
                ", isRead='" + isRead + '\'' +
                ", readTime='" + readTime + '\'' +
                ", delFlag='" + delFlag + '\'' +
                '}';
    }
}
