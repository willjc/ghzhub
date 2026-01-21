package com.ruoyi.web.controller.h5;

import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.system.domain.HzMessage;
import com.ruoyi.system.domain.HzTenant;
import com.ruoyi.system.service.IHzMessageService;
import com.ruoyi.system.service.IHzTenantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * H5端消息Controller
 *
 * @author ruoyi
 */
@RestController
@RequestMapping("/h5/message")
public class HzMessageController extends BaseController
{
    @Autowired
    private IHzMessageService messageService;

    @Autowired
    private IHzTenantService tenantService;

    /**
     * 查询当前用户的消息列表
     */
    @GetMapping("/list")
    public AjaxResult list()
    {
        // TODO: 从登录态获取userId
        Long userId = 1L;
        HzTenant tenant = tenantService.selectTenantByUserId(userId);
        if (tenant == null)
        {
            return error("请先完善租户信息");
        }

        List<HzMessage> list = messageService.selectMessageListByTenantId(tenant.getTenantId());
        return success(list);
    }

    /**
     * 查询未读消息列表
     */
    @GetMapping("/unread")
    public AjaxResult unreadList()
    {
        // TODO: 从登录态获取userId
        Long userId = 1L;
        HzTenant tenant = tenantService.selectTenantByUserId(userId);
        if (tenant == null)
        {
            return error("请先完善租户信息");
        }

        List<HzMessage> list = messageService.selectUnreadMessageListByTenantId(tenant.getTenantId());
        return success(list);
    }

    /**
     * 获取未读消息数量
     */
    @GetMapping("/unreadCount")
    public AjaxResult unreadCount()
    {
        // TODO: 从登录态获取userId
        Long userId = 1L;
        HzTenant tenant = tenantService.selectTenantByUserId(userId);
        if (tenant == null)
        {
            return success(0);
        }

        long count = messageService.countUnreadMessageByTenantId(tenant.getTenantId());
        return success(count);
    }

    /**
     * 获取消息详情
     */
    @GetMapping("/{messageId}")
    public AjaxResult getInfo(@PathVariable("messageId") Long messageId)
    {
        HzMessage message = messageService.selectMessageById(messageId);

        // TODO: 校验消息是否属于当前用户

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
        // TODO: 校验消息是否属于当前用户

        int result = messageService.markMessageAsRead(messageId);
        return result > 0 ? success() : error("操作失败");
    }

    /**
     * 批量标记消息为已读
     */
    @PutMapping("/readBatch")
    public AjaxResult markAsReadBatch(@RequestBody Long[] messageIds)
    {
        // TODO: 校验消息是否属于当前用户

        int result = messageService.markMessagesAsRead(messageIds);
        return result > 0 ? success() : error("操作失败");
    }

    /**
     * 全部标记为已读
     */
    @PutMapping("/readAll")
    public AjaxResult markAllAsRead()
    {
        // TODO: 从登录态获取userId
        Long userId = 1L;
        HzTenant tenant = tenantService.selectTenantByUserId(userId);
        if (tenant == null)
        {
            return error("请先完善租户信息");
        }

        List<HzMessage> unreadList = messageService.selectUnreadMessageListByTenantId(tenant.getTenantId());
        if (unreadList.isEmpty())
        {
            return success();
        }

        Long[] messageIds = unreadList.stream()
                .map(HzMessage::getMessageId)
                .toArray(Long[]::new);

        int result = messageService.markMessagesAsRead(messageIds);
        return result > 0 ? success() : error("操作失败");
    }

    /**
     * 删除消息
     */
    @DeleteMapping("/{messageId}")
    public AjaxResult delete(@PathVariable("messageId") Long messageId)
    {
        // TODO: 校验消息是否属于当前用户

        int result = messageService.deleteMessageById(messageId);
        return result > 0 ? success() : error("删除失败");
    }
}
