package com.ruoyi.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.system.domain.HzMessage;
import com.ruoyi.system.mapper.HzMessageMapper;
import com.ruoyi.system.service.IHzMessageService;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

/**
 * 消息Service业务层处理
 *
 * @author ruoyi
 */
@Service
public class HzMessageServiceImpl extends ServiceImpl<HzMessageMapper, HzMessage> implements IHzMessageService
{
    @Override
    public HzMessage selectMessageById(Long messageId)
    {
        LambdaQueryWrapper<HzMessage> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(HzMessage::getMessageId, messageId)
               .eq(HzMessage::getDelFlag, "0");
        return this.getOne(wrapper);
    }

    @Override
    public List<HzMessage> selectMessageListByTenantId(Long tenantId)
    {
        LambdaQueryWrapper<HzMessage> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(HzMessage::getTenantId, tenantId)
               .eq(HzMessage::getDelFlag, "0")
               .orderByDesc(HzMessage::getCreateTime);
        return this.list(wrapper);
    }

    @Override
    public List<HzMessage> selectUnreadMessageListByTenantId(Long tenantId)
    {
        LambdaQueryWrapper<HzMessage> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(HzMessage::getTenantId, tenantId)
               .eq(HzMessage::getIsRead, "0")
               .eq(HzMessage::getDelFlag, "0")
               .orderByDesc(HzMessage::getCreateTime);
        return this.list(wrapper);
    }

    @Override
    public long countUnreadMessageByTenantId(Long tenantId)
    {
        LambdaQueryWrapper<HzMessage> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(HzMessage::getTenantId, tenantId)
               .eq(HzMessage::getIsRead, "0")
               .eq(HzMessage::getDelFlag, "0");
        return this.count(wrapper);
    }

    @Override
    public IPage<HzMessage> selectMessagePage(HzMessage message, int pageNum, int pageSize)
    {
        LambdaQueryWrapper<HzMessage> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(message.getTenantId() != null, HzMessage::getTenantId, message.getTenantId())
               .eq(StringUtils.isNotEmpty(message.getMessageType()), HzMessage::getMessageType, message.getMessageType())
               .eq(StringUtils.isNotEmpty(message.getIsRead()), HzMessage::getIsRead, message.getIsRead())
               .eq(HzMessage::getDelFlag, "0")
               .orderByDesc(HzMessage::getCreateTime);

        Page<HzMessage> page = new Page<>(pageNum, pageSize);
        return this.page(page, wrapper);
    }

    @Override
    public int insertMessage(HzMessage message)
    {
        message.setDelFlag("0");
        message.setIsRead("0"); // 默认未读
        message.setSendStatus("1"); // 默认已发送(站内信)
        message.setCreateTime(DateUtils.getNowDate());
        return this.save(message) ? 1 : 0;
    }

    @Override
    public int updateMessage(HzMessage message)
    {
        message.setUpdateTime(DateUtils.getNowDate());
        return this.updateById(message) ? 1 : 0;
    }

    @Override
    public int markMessageAsRead(Long messageId)
    {
        HzMessage message = new HzMessage();
        message.setMessageId(messageId);
        message.setIsRead("1");
        message.setReadTime(DateUtils.getTime());
        message.setUpdateTime(DateUtils.getNowDate());
        return this.updateById(message) ? 1 : 0;
    }

    @Override
    public int markMessagesAsRead(Long[] messageIds)
    {
        if (messageIds == null || messageIds.length == 0)
        {
            return 0;
        }

        LambdaQueryWrapper<HzMessage> wrapper = new LambdaQueryWrapper<>();
        wrapper.in(HzMessage::getMessageId, Arrays.asList(messageIds));

        HzMessage message = new HzMessage();
        message.setIsRead("1");
        message.setReadTime(DateUtils.getTime());
        message.setUpdateTime(DateUtils.getNowDate());

        return this.update(message, wrapper) ? 1 : 0;
    }

    @Override
    public int deleteMessageById(Long messageId)
    {
        HzMessage message = new HzMessage();
        message.setMessageId(messageId);
        message.setDelFlag("2");
        message.setUpdateTime(DateUtils.getNowDate());
        return this.updateById(message) ? 1 : 0;
    }
}
