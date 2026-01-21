package com.ruoyi.web.controller.h5;

import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.system.domain.HzAnnouncement;
import com.ruoyi.system.service.IHzAnnouncementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * H5端公告通知Controller
 *
 * @author ruoyi
 */
@RestController
@RequestMapping("/h5/announcement")
public class HzAnnouncementController extends BaseController
{
    @Autowired
    private IHzAnnouncementService announcementService;

    /**
     * 查询公告列表(已发布)
     */
    @GetMapping("/list")
    public AjaxResult list()
    {
        List<HzAnnouncement> list = announcementService.selectPublishedAnnouncementList();
        return success(list);
    }

    /**
     * 根据类型查询公告列表
     */
    @GetMapping("/type/{announcementType}")
    public AjaxResult listByType(@PathVariable("announcementType") String announcementType)
    {
        List<HzAnnouncement> list = announcementService.selectPublishedAnnouncementListByType(announcementType);
        return success(list);
    }

    /**
     * 获取公告详情
     */
    @GetMapping("/{announcementId}")
    public AjaxResult getInfo(@PathVariable("announcementId") Long announcementId)
    {
        HzAnnouncement announcement = announcementService.selectAnnouncementById(announcementId);

        // 增加浏览次数
        if (announcement != null)
        {
            announcementService.increaseViewCount(announcementId);
        }

        return success(announcement);
    }
}
