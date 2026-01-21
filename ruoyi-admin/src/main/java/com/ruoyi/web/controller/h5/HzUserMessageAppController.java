package com.ruoyi.web.controller.h5;

import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.system.domain.HzUserMessage;
import com.ruoyi.system.service.IHzUserMessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
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
    private HttpServletRequest request;

    /**
     * 查询当前用户的消息列表
     */
    @GetMapping("/list")
    public AjaxResult list()
    {
        Long userId = getCurrentUserId();
        if (userId == null)
        {
            return error("用户未登录");
        }

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
        if (userId == null)
        {
            return error("用户未登录");
        }

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
        if (userId == null)
        {
            return success(0);
        }

        long count = messageService.countUnreadMessageByUserId(userId);
        return success(count);
    }

    /**
     * 获取消息详情
     */
    @GetMapping("/{messageId}")
    public AjaxResult getInfo(@PathVariable("messageId") Long messageId)
    {
        HzUserMessage message = messageService.selectMessageById(messageId);

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
        int result = messageService.markMessageAsRead(messageId);
        return result > 0 ? success() : error("操作失败");
    }

    /**
     * 删除消息
     */
    @DeleteMapping("/{messageId}")
    public AjaxResult delete(@PathVariable("messageId") Long messageId)
    {
        int result = messageService.deleteMessageById(messageId);
        return result > 0 ? success() : error("删除失败");
    }

    /**
     * 临时方法：获取当前用户ID
     * TODO: 实现JWT认证后，从token中获取
     */
    protected Long getCurrentUserId()
    {
        // 临时方案：从请求头或参数获取
        String userIdStr = request.getParameter("userId");
        if (userIdStr == null)
        {
            userIdStr = request.getHeader("userId");
        }
        if (userIdStr != null)
        {
            try
            {
                return Long.parseLong(userIdStr);
            }
            catch (NumberFormatException e)
            {
                return null;
            }
        }
        return null;
    }
}
