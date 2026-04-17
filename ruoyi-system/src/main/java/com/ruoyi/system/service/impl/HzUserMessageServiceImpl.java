package com.ruoyi.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.system.domain.HzUserMessage;
import com.ruoyi.system.mapper.HzUserMessageMapper;
import com.ruoyi.system.service.IHzUserMessageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 用户消息Service业务层处理
 *
 * @author ruoyi
 */
@Service
public class HzUserMessageServiceImpl extends ServiceImpl<HzUserMessageMapper, HzUserMessage> implements IHzUserMessageService
{
    private static final Logger log = LoggerFactory.getLogger(HzUserMessageServiceImpl.class);

    @Override
    public HzUserMessage selectMessageById(Long messageId)
    {
        LambdaQueryWrapper<HzUserMessage> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(HzUserMessage::getMessageId, messageId)
               .eq(HzUserMessage::getDelFlag, "0");
        return this.getOne(wrapper);
    }

    @Override
    public List<HzUserMessage> selectMessageListByUserId(Long userId)
    {
        LambdaQueryWrapper<HzUserMessage> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(HzUserMessage::getUserId, userId)
               .eq(HzUserMessage::getDelFlag, "0")
               .orderByDesc(HzUserMessage::getCreateTime);
        return this.list(wrapper);
    }

    @Override
    public List<HzUserMessage> selectUnreadMessageListByUserId(Long userId)
    {
        LambdaQueryWrapper<HzUserMessage> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(HzUserMessage::getUserId, userId)
               .eq(HzUserMessage::getIsRead, "0")
               .eq(HzUserMessage::getDelFlag, "0")
               .orderByDesc(HzUserMessage::getCreateTime);
        return this.list(wrapper);
    }

    @Override
    public long countUnreadMessageByUserId(Long userId)
    {
        LambdaQueryWrapper<HzUserMessage> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(HzUserMessage::getUserId, userId)
               .eq(HzUserMessage::getIsRead, "0")
               .eq(HzUserMessage::getDelFlag, "0");
        return this.count(wrapper);
    }

    @Override
    public IPage<HzUserMessage> selectMessagePage(HzUserMessage message, int pageNum, int pageSize)
    {
        LambdaQueryWrapper<HzUserMessage> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(message.getUserId() != null, HzUserMessage::getUserId, message.getUserId())
               .eq(StringUtils.isNotEmpty(message.getMessageType()), HzUserMessage::getMessageType, message.getMessageType())
               .eq(StringUtils.isNotEmpty(message.getIsRead()), HzUserMessage::getIsRead, message.getIsRead())
               .eq(HzUserMessage::getDelFlag, "0")
               .orderByDesc(HzUserMessage::getCreateTime);

        Page<HzUserMessage> page = new Page<>(pageNum, pageSize);
        return this.page(page, wrapper);
    }

    @Override
    public List<HzUserMessage> selectMessageList(HzUserMessage message)
    {
        LambdaQueryWrapper<HzUserMessage> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(message.getUserId() != null, HzUserMessage::getUserId, message.getUserId())
               .eq(StringUtils.isNotEmpty(message.getMessageType()), HzUserMessage::getMessageType, message.getMessageType())
               .eq(StringUtils.isNotEmpty(message.getIsRead()), HzUserMessage::getIsRead, message.getIsRead())
               .eq(StringUtils.isNotEmpty(message.getMessageTitle()), HzUserMessage::getMessageTitle, message.getMessageTitle())
               .eq(HzUserMessage::getDelFlag, "0")
               .orderByDesc(HzUserMessage::getCreateTime);
        return this.list(wrapper);
    }

    @Override
    public int insertMessage(HzUserMessage message)
    {
        message.setDelFlag("0");
        message.setIsRead("0"); // 默认未读
        message.setCreateTime(DateUtils.getNowDate());
        return this.save(message) ? 1 : 0;
    }

    @Override
    public int updateMessage(HzUserMessage message)
    {
        message.setUpdateTime(DateUtils.getNowDate());
        return this.updateById(message) ? 1 : 0;
    }

    @Override
    public int markMessageAsRead(Long messageId)
    {
        HzUserMessage message = new HzUserMessage();
        message.setMessageId(messageId);
        message.setIsRead("1");
        message.setReadTime(DateUtils.getTime());
        message.setUpdateTime(DateUtils.getNowDate());
        return this.updateById(message) ? 1 : 0;
    }

    @Override
    public int deleteMessageById(Long messageId)
    {
        HzUserMessage message = new HzUserMessage();
        message.setMessageId(messageId);
        message.setDelFlag("2");
        message.setUpdateTime(DateUtils.getNowDate());
        return this.updateById(message) ? 1 : 0;
    }

    @Override
    public void sendMessage(Long userId, String messageType, String messageTitle, String messageContent)
    {
        try
        {
            HzUserMessage message = new HzUserMessage();
            message.setUserId(userId);
            message.setMessageType(messageType);
            message.setMessageTitle(messageTitle);
            message.setMessageContent(messageContent);
            message.setIsRead("0");
            message.setDelFlag("0");
            message.setCreateTime(DateUtils.getNowDate());
            this.save(message);
        }
        catch (Exception e)
        {
            log.error("发送消息失败，userId={}, title={}", userId, messageTitle, e);
        }
    }
}
