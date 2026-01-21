package com.ruoyi.web.controller.h5;

import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.gangzhu.activity.domain.HzActivity;
import com.ruoyi.gangzhu.activity.service.IHzActivityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 用户端 - 人才家园活动Controller
 *
 * @author ruoyi
 */
@RestController
@RequestMapping("/h5/activity")
public class HzActivityAppController extends BaseController {

    @Autowired
    private IHzActivityService activityService;

    /**
     * 查询人才家园活动列表（用户端）
     * 只返回状态为正常的活动
     */
    @GetMapping("/list")
    public TableDataInfo list(HzActivity activity) {
        startPage();
        // 只查询状态正常的活动
        activity.setStatus("0");
        List<HzActivity> list = activityService.selectActivityList(activity);
        return getDataTable(list);
    }

    /**
     * 获取人才家园活动详细信息（用户端）
     */
    @GetMapping(value = "/{activityId}")
    public AjaxResult getInfo(@PathVariable("activityId") Long activityId) {
        HzActivity activity = activityService.getById(activityId);
        if (activity == null) {
            return error("活动不存在");
        }
        // 检查活动状态
        if (!"0".equals(activity.getStatus())) {
            return error("活动已停用");
        }
        // 增加浏览次数
        activityService.increaseViewCount(activityId);
        return success(activity);
    }
}
