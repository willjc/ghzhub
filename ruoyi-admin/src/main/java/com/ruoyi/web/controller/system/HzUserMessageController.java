package com.ruoyi.web.controller.system;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.system.domain.HzUserMessage;
import com.ruoyi.system.service.IHzUserMessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * 用户消息Controller
 *
 * @author ruoyi
 */
@RestController
@RequestMapping("/gangzhu/userMessage")
public class HzUserMessageController extends BaseController
{
    @Autowired
    private IHzUserMessageService messageService;

    /**
     * 查询用户消息列表
     */
    @PreAuthorize("@ss.hasPermi('gangzhu:userMessage:list')")
    @GetMapping("/list")
    public TableDataInfo list(HzUserMessage message)
    {
        startPage();
        List<HzUserMessage> list = messageService.selectMessageList(message);
        return getDataTable(list);
    }

    /**
     * 导出用户消息列表
     */
    @PreAuthorize("@ss.hasPermi('gangzhu:userMessage:export')")
    @Log(title = "用户消息", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, HzUserMessage message)
    {
        IPage<HzUserMessage> page = messageService.selectMessagePage(message, 1, 10000);
        ExcelUtil<HzUserMessage> util = new ExcelUtil<>(HzUserMessage.class);
        util.exportExcel(response, page.getRecords(), "用户消息数据");
    }

    /**
     * 获取用户消息详细信息
     */
    @PreAuthorize("@ss.hasPermi('gangzhu:userMessage:query')")
    @GetMapping(value = "/{messageId}")
    public AjaxResult getInfo(@PathVariable("messageId") Long messageId)
    {
        return success(messageService.selectMessageById(messageId));
    }

    /**
     * 新增用户消息
     */
    @PreAuthorize("@ss.hasPermi('gangzhu:userMessage:add')")
    @Log(title = "用户消息", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody HzUserMessage message)
    {
        return toAjax(messageService.insertMessage(message));
    }

    /**
     * 修改用户消息
     */
    @PreAuthorize("@ss.hasPermi('gangzhu:userMessage:edit')")
    @Log(title = "用户消息", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody HzUserMessage message)
    {
        return toAjax(messageService.updateMessage(message));
    }

    /**
     * 删除用户消息
     */
    @PreAuthorize("@ss.hasPermi('gangzhu:userMessage:remove')")
    @Log(title = "用户消息", businessType = BusinessType.DELETE)
    @DeleteMapping("/{messageIds}")
    public AjaxResult remove(@PathVariable Long[] messageIds)
    {
        int result = 0;
        for (Long messageId : messageIds)
        {
            result += messageService.deleteMessageById(messageId);
        }
        return toAjax(result > 0);
    }
}
