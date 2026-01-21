package com.ruoyi.system.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.ruoyi.system.domain.HzMessage;

import java.util.List;

/**
 * 消息Service接口
 *
 * @author ruoyi
 */
public interface IHzMessageService
{
    /**
     * 根据ID查询消息
     *
     * @param messageId 消息ID
     * @return 消息
     */
    public HzMessage selectMessageById(Long messageId);

    /**
     * 根据租户ID查询消息列表
     *
     * @param tenantId 租户ID
     * @return 消息列表
     */
    public List<HzMessage> selectMessageListByTenantId(Long tenantId);

    /**
     * 根据租户ID查询未读消息列表
     *
     * @param tenantId 租户ID
     * @return 未读消息列表
     */
    public List<HzMessage> selectUnreadMessageListByTenantId(Long tenantId);

    /**
     * 根据租户ID查询未读消息数量
     *
     * @param tenantId 租户ID
     * @return 未读消息数量
     */
    public long countUnreadMessageByTenantId(Long tenantId);

    /**
     * 分页查询消息列表
     *
     * @param message 消息
     * @param pageNum 页码
     * @param pageSize 每页数量
     * @return 消息列表
     */
    public IPage<HzMessage> selectMessagePage(HzMessage message, int pageNum, int pageSize);

    /**
     * 新增消息
     *
     * @param message 消息
     * @return 结果
     */
    public int insertMessage(HzMessage message);

    /**
     * 修改消息
     *
     * @param message 消息
     * @return 结果
     */
    public int updateMessage(HzMessage message);

    /**
     * 标记消息为已读
     *
     * @param messageId 消息ID
     * @return 结果
     */
    public int markMessageAsRead(Long messageId);

    /**
     * 批量标记消息为已读
     *
     * @param messageIds 消息ID数组
     * @return 结果
     */
    public int markMessagesAsRead(Long[] messageIds);

    /**
     * 删除消息
     *
     * @param messageId 消息ID
     * @return 结果
     */
    public int deleteMessageById(Long messageId);
}
