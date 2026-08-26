package com.ruoyi.web.controller.h5;

import com.ruoyi.common.annotation.Anonymous;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.constant.HttpStatus;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.framework.web.service.HzUserTokenService;
import com.ruoyi.system.domain.HzUserMessage;
import com.ruoyi.system.service.IHzUserMessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * H5端用户消息Controller
 *
 * @author ruoyi
 */
@RestController
@RequestMapping("/h5/userMessage")
public class HzUserMessageAppController extends BaseController
{
    @Autowired
    private IHzUserMessageService messageService;

    @Autowired
    private HzUserTokenService hzUserTokenService;

    /**
     * 查询当前用户的消息列表
     */
    @Anonymous
    @GetMapping("/list")
    public AjaxResult list(@RequestParam(required = false) Long userId)
    {
        userId = currentOrLegacyUserId(userId);
        List<HzUserMessage> list = messageService.selectMessageListByUserId(userId);
        return success(list);
    }

    /**
     * 查询未读消息列表
     */
    @GetMapping("/unread")
    public AjaxResult unreadList()
    {
        Long userId = getCurrentUserId();
        List<HzUserMessage> list = messageService.selectUnreadMessageListByUserId(userId);
        return success(list);
    }

    /**
     * 获取未读消息数量
     */
    @GetMapping("/unreadCount")
    public AjaxResult unreadCount()
    {
        Long userId = getCurrentUserId();
        long count = messageService.countUnreadMessageByUserId(userId);
        return success(count);
    }

    /**
     * 获取消息详情
     */
    @Anonymous
    @GetMapping("/{messageId}")
    public AjaxResult getInfo(@PathVariable("messageId") Long messageId)
    {
        HzUserMessage message = messageService.selectMessageById(messageId);
        Long userId = SecurityUtils.getHzUserIdOrNull();
        if (userId != null && message != null && !userId.equals(message.getUserId()))
        {
            return error("无权查看");
        }
        if (userId == null)
        {
            requireLegacyCompatibility();
            logger.warn("旧版小程序无Token读取消息详情，messageId={}", messageId);
        }

        // 自动标记为已读
        if (message != null && "0".equals(message.getIsRead()))
        {
            messageService.markMessageAsRead(messageId);
        }

        return success(message);
    }

    /**
     * 标记消息为已读
     */
    @PutMapping("/read/{messageId}")
    public AjaxResult markAsRead(@PathVariable("messageId") Long messageId)
    {
        HzUserMessage message = messageService.selectMessageById(messageId);
        if (message == null || !getCurrentUserId().equals(message.getUserId()))
        {
            return error("无权操作");
        }
        int result = messageService.markMessageAsRead(messageId);
        return result > 0 ? success() : error("操作失败");
    }

    /**
     * 删除消息
     */
    @DeleteMapping("/{messageId}")
    public AjaxResult delete(@PathVariable("messageId") Long messageId)
    {
        HzUserMessage message = messageService.selectMessageById(messageId);
        if (message == null || !getCurrentUserId().equals(message.getUserId()))
        {
            return error("无权操作");
        }
        int result = messageService.deleteMessageById(messageId);
        return result > 0 ? success() : error("删除失败");
    }

    protected Long getCurrentUserId()
    {
        return SecurityUtils.getHzUserId();
    }

    private Long currentOrLegacyUserId(Long requestedUserId)
    {
        Long currentUserId = SecurityUtils.getHzUserIdOrNull();
        if (currentUserId != null)
        {
            return currentUserId;
        }
        requireLegacyCompatibility();
        if (requestedUserId == null)
        {
            throw new ServiceException("用户未登录", HttpStatus.UNAUTHORIZED);
        }
        logger.warn("旧版小程序无Token读取消息列表，userId={}", requestedUserId);
        return requestedUserId;
    }

    private void requireLegacyCompatibility()
    {
        if (!hzUserTokenService.isLegacyCompatibilityActive())
        {
            throw new ServiceException("用户未登录或登录已过期", HttpStatus.UNAUTHORIZED);
        }
    }
}
