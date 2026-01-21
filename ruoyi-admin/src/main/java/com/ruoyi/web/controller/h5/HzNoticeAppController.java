package com.ruoyi.web.controller.h5;

import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.system.service.ISysNoticeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * H5端公告Controller
 *
 * @author ruoyi
 */
@RestController
@RequestMapping("/h5/notice")
public class HzNoticeAppController extends BaseController
{
    @Autowired
    private ISysNoticeService noticeService;

    /**
     * 获取关于我们公告内容（noticeId=10）
     */
    @GetMapping("/about")
    public AjaxResult getAboutContent()
    {
        try {
            // 固定获取notice_id=10的关于我们公告
            Object notice = noticeService.selectNoticeById(10L);
            if (notice != null)
            {
                return success(notice);
            }
            else
            {
                return error("未找到关于我们内容");
            }
        }
        catch (Exception e)
        {
            logger.error("获取关于我们内容失败:", e);
            return error("获取内容失败");
        }
    }

    /**
     * 根据noticeId获取公告详情
     */
    @GetMapping("/{noticeId}")
    public AjaxResult getInfo(@PathVariable("noticeId") Long noticeId)
    {
        try {
            Object notice = noticeService.selectNoticeById(noticeId);
            return success(notice);
        }
        catch (Exception e)
        {
            logger.error("获取公告详情失败:", e);
            return error("获取详情失败");
        }
    }
}
