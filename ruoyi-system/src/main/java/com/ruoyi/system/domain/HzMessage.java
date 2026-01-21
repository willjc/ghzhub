package com.ruoyi.system.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 消息对象 hz_message
 *
 * @author ruoyi
 */
@TableName("hz_message")
public class HzMessage extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 消息ID */
    @TableId(type = IdType.AUTO)
    private Long messageId;

    /** 租户ID */
    @TableField("tenant_id")
    private Long tenantId;

    /** 消息类型(1:系统通知 2:账单提醒 3:合同提醒 4:审核通知 5:其他) */
    @TableField("message_type")
    private String messageType;

    /** 消息标题 */
    @TableField("message_title")
    private String messageTitle;

    /** 消息内容 */
    @TableField("message_content")
    private String messageContent;

    /** 关联业务ID */
    @TableField("business_id")
    private Long businessId;

    /** 关联业务类型(1:账单 2:合同 3:资格审核 4:入住申请 5:退租申请 6:预约) */
    @TableField("business_type")
    private String businessType;

    /** 是否已读(0:未读 1:已读) */
    @TableField("is_read")
    private String isRead;

    /** 阅读时间 */
    @TableField("read_time")
    private String readTime;

    /** 发送方式(1:站内信 2:短信 3:邮件 4:微信) */
    @TableField("send_method")
    private String sendMethod;

    /** 发送状态(0:待发送 1:已发送 2:发送失败) */
    @TableField("send_status")
    private String sendStatus;

    /** 发送时间 */
    @TableField("send_time")
    private String sendTime;

    /** 删除标志(0:正常 2:删除) */
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

    public Long getTenantId()
    {
        return tenantId;
    }

    public void setTenantId(Long tenantId)
    {
        this.tenantId = tenantId;
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

    public Long getBusinessId()
    {
        return businessId;
    }

    public void setBusinessId(Long businessId)
    {
        this.businessId = businessId;
    }

    public String getBusinessType()
    {
        return businessType;
    }

    public void setBusinessType(String businessType)
    {
        this.businessType = businessType;
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

    public String getSendMethod()
    {
        return sendMethod;
    }

    public void setSendMethod(String sendMethod)
    {
        this.sendMethod = sendMethod;
    }

    public String getSendStatus()
    {
        return sendStatus;
    }

    public void setSendStatus(String sendStatus)
    {
        this.sendStatus = sendStatus;
    }

    public String getSendTime()
    {
        return sendTime;
    }

    public void setSendTime(String sendTime)
    {
        this.sendTime = sendTime;
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
        return "HzMessage{" +
                "messageId=" + messageId +
                ", tenantId=" + tenantId +
                ", messageType='" + messageType + '\'' +
                ", messageTitle='" + messageTitle + '\'' +
                ", messageContent='" + messageContent + '\'' +
                ", businessId=" + businessId +
                ", businessType='" + businessType + '\'' +
                ", isRead='" + isRead + '\'' +
                ", readTime='" + readTime + '\'' +
                ", sendMethod='" + sendMethod + '\'' +
                ", sendStatus='" + sendStatus + '\'' +
                ", sendTime='" + sendTime + '\'' +
                ", delFlag='" + delFlag + '\'' +
                '}';
    }
}
