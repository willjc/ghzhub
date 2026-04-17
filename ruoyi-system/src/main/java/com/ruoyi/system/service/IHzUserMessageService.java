package com.ruoyi.system.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.ruoyi.system.domain.HzUserMessage;

import java.util.List;

/**
 * 用户消息Service接口
 *
 * @author ruoyi
 */
public interface IHzUserMessageService
{
    /**
     * 根据ID查询消息
     *
     * @param messageId 消息ID
     * @return 消息
     */
    public HzUserMessage selectMessageById(Long messageId);

    /**
     * 根据用户ID查询消息列��
     *
     * @param userId 用户ID
     * @return 消息列表
     */
    public List<HzUserMessage> selectMessageListByUserId(Long userId);

    /**
     * 根据用户ID查询未读消息列表
     *
     * @param userId 用户ID
     * @return 未读消息列表
     */
    public List<HzUserMessage> selectUnreadMessageListByUserId(Long userId);

    /**
     * 根据用户ID查询未读消息数量
     *
     * @param userId 用户ID
     * @return 未读消息数量
     */
    public long countUnreadMessageByUserId(Long userId);

    /**
     * 分页查询消息列表
     *
     * @param message 消息
     * @param pageNum 页码
     * @param pageSize 每页数量
     * @return 消息列表
     */
    public IPage<HzUserMessage> selectMessagePage(HzUserMessage message, int pageNum, int pageSize);

    /**
     * 查询消息列表（后台管理使用）
     *
     * @param message 查询条件
     * @return 消息列表
     */
    public List<HzUserMessage> selectMessageList(HzUserMessage message);

    /**
     * 新增消息
     *
     * @param message 消息
     * @return 结果
     */
    public int insertMessage(HzUserMessage message);

    /**
     * 修改消息
     *
     * @param message 消息
     * @return 结果
     */
    public int updateMessage(HzUserMessage message);

    /**
     * 标记消息为已读
     *
     * @param messageId 消息ID
     * @return 结果
     */
    public int markMessageAsRead(Long messageId);

    /**
     * 删除消息
     *
     * @param messageId 消息ID
     * @return 结果
     */
    public int deleteMessageById(Long messageId);

    /**
     * 通用发送消息方法（各业务节点调用）
     *
     * @param userId 用户ID
     * @param messageType 消息类型：login/system/bill/contract
     * @param messageTitle 消息标题
     * @param messageContent 消息内容
     */
    void sendMessage(Long userId, String messageType, String messageTitle, String messageContent);
}
