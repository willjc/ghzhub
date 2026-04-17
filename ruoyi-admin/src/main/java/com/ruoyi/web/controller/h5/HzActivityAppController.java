package com.ruoyi.web.controller.h5;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ruoyi.common.constant.HttpStatus;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.utils.PageUtils;
import com.ruoyi.gangzhu.activity.domain.HzActivity;
import com.ruoyi.gangzhu.activity.domain.HzActivityRegistration;
import com.ruoyi.gangzhu.activity.service.IHzActivityRegistrationService;
import com.ruoyi.gangzhu.activity.service.IHzActivityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

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

    @Autowired
    private IHzActivityRegistrationService registrationService;

    /**
     * 查询人才家园活动列表（用户端）
     * 只返回状态为正常的活动
     */
    @GetMapping("/list")
    public TableDataInfo list(HzActivity activity) {
        // 只查询状态正常的活动
        activity.setStatus("0");
        Page<HzActivity> page = PageUtils.getPage();
        IPage<HzActivity> pageResult = activityService.selectActivityPage(page, activity);
        TableDataInfo data = new TableDataInfo();
        data.setCode(HttpStatus.SUCCESS);
        data.setRows(pageResult.getRecords());
        data.setTotal(pageResult.getTotal());
        data.setMsg("查询成功");
        return data;
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

    /**
     * 活动报名
     */
    @PostMapping("/register")
    public AjaxResult register(@RequestBody Map<String, Object> params) {
        Long activityId = Long.valueOf(params.get("activityId").toString());
        Long userId = Long.valueOf(params.get("userId").toString());
        String realName = params.get("realName") != null ? params.get("realName").toString() : null;
        String phone = params.get("phone") != null ? params.get("phone").toString() : null;

        String errorMsg = registrationService.register(activityId, userId, realName, phone);
        if (errorMsg != null) {
            return error(errorMsg);
        }
        return success("报名成功");
    }

    /**
     * 检查用户是否已报名
     */
    @GetMapping("/check-registered/{activityId}")
    public AjaxResult checkRegistered(@PathVariable("activityId") Long activityId,
                                       @RequestParam("userId") Long userId) {
        boolean registered = registrationService.isRegistered(activityId, userId);
        AjaxResult result = success();
        result.put("registered", registered);
        return result;
    }

    /**
     * 获取用户的所有报名记录
     */
    @GetMapping("/my-registrations")
    public AjaxResult myRegistrations(@RequestParam("userId") Long userId) {
        List<HzActivityRegistration> list = registrationService.getMyRegistrations(userId);
        return success(list);
    }
}
