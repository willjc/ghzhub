package com.ruoyi.web.controller.h5;

import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.system.domain.HzComplaint;
import com.ruoyi.system.service.IHzComplaintService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * H5端投诉建议Controller
 *
 * @author ruoyi
 */
@RestController
@RequestMapping("/h5/app/complaint")
public class HzComplaintAppController extends BaseController
{
    @Autowired
    private IHzComplaintService complaintService;

    @Autowired
    private HttpServletRequest request;

    /**
     * 提交投诉建议
     */
    @Log(title = "提交投诉建议", businessType = BusinessType.INSERT)
    @PostMapping("/submit")
    public AjaxResult submit(@RequestBody HzComplaint complaint)
    {
        // 从请求体获取userId（前端已传递）
        Long userId = complaint.getUserId();
        if (userId == null)
        {
            // 兜底：从请求参数或请求头获取
            userId = getCurrentUserId();
        }
        if (userId == null)
        {
            return error("用户未登录");
        }

        complaint.setUserId(userId);
        return toAjax(complaintService.insertComplaint(complaint));
    }

    /**
     * 查询当前用户的投诉建议列表
     */
    @GetMapping("/myList")
    public AjaxResult myList()
    {
        Long userId = getCurrentUserId();
        if (userId == null)
        {
            return error("用户未登录");
        }

        List<HzComplaint> list = complaintService.selectComplaintListByUserId(userId);
        return success(list);
    }

    /**
     * 获取投诉建议详情
     */
    @GetMapping("/{complaintId}")
    public AjaxResult getInfo(@PathVariable("complaintId") Long complaintId)
    {
        HzComplaint complaint = complaintService.selectComplaintById(complaintId);

        // 验证是否为本人提交
        if (complaint != null)
        {
            Long userId = getCurrentUserId();
            if (userId != null && !userId.equals(complaint.getUserId()))
            {
                return error("无权查看");
            }
        }

        return success(complaint);
    }

    /**
     * 催办投诉建议
     */
    @Log(title = "催办投诉建议", businessType = BusinessType.UPDATE)
    @PutMapping("/urge/{complaintId}")
    public AjaxResult urge(@PathVariable("complaintId") Long complaintId)
    {
        HzComplaint complaint = complaintService.selectComplaintById(complaintId);

        // 验证是否为本人提交
        if (complaint != null)
        {
            Long userId = getCurrentUserId();
            if (userId == null || !userId.equals(complaint.getUserId()))
            {
                return error("无权操作");
            }
        }

        int result = complaintService.urgeComplaint(complaintId);
        return result > 0 ? success("催办成功") : error(complaint == null || "1".equals(complaint.getStatus()) ? "无法催办" : "催办失败");
    }

    /**
     * 取消投诉建议
     */
    @Log(title = "取消投诉建议", businessType = BusinessType.UPDATE)
    @PutMapping("/cancel/{complaintId}")
    public AjaxResult cancel(@PathVariable("complaintId") Long complaintId)
    {
        Long userId = getCurrentUserId();
        if (userId == null)
        {
            return error("用户未登录");
        }

        int result = complaintService.cancelComplaint(complaintId, userId);
        return result > 0 ? success("取消成功") : error("取消失败，仅待处理的投诉可取消");
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
