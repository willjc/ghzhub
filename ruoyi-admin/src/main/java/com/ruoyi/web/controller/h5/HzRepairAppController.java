package com.ruoyi.web.controller.h5;

import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.system.domain.HzRepair;
import com.ruoyi.system.service.IHzRepairService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * H5端物业报修Controller
 *
 * @author ruoyi
 */
@RestController
@RequestMapping("/h5/app/repair")
public class HzRepairAppController extends BaseController
{
    @Autowired
    private IHzRepairService repairService;

    @Autowired
    private HttpServletRequest request;

    /**
     * 提交物业报修
     */
    @Log(title = "提交物业报修", businessType = BusinessType.INSERT)
    @PostMapping("/submit")
    public AjaxResult submit(@RequestBody HzRepair repair)
    {
        // 从请求体获取userId（前端已传递）
        Long userId = repair.getUserId();
        if (userId == null)
        {
            // 兜底：从请求参数或请求头获取
            userId = getCurrentUserId();
        }
        if (userId == null)
        {
            return error("用户未登录");
        }

        repair.setUserId(userId);
        return toAjax(repairService.insertRepair(repair));
    }

    /**
     * 查询当前用户的物业报修列表
     */
    @GetMapping("/myList")
    public AjaxResult myList()
    {
        Long userId = getCurrentUserId();
        if (userId == null)
        {
            return error("用户未登录");
        }

        List<HzRepair> list = repairService.selectRepairListByUserId(userId);
        return success(list);
    }

    /**
     * 获取物业报修详情
     */
    @GetMapping("/{repairId}")
    public AjaxResult getInfo(@PathVariable("repairId") Long repairId)
    {
        HzRepair repair = repairService.selectRepairById(repairId);

        // 验证是否为本人提交
        if (repair != null)
        {
            Long userId = getCurrentUserId();
            if (userId != null && !userId.equals(repair.getUserId()))
            {
                return error("无权查看");
            }
        }

        return success(repair);
    }

    /**
     * 取消物业报修
     */
    @Log(title = "取消物业报修", businessType = BusinessType.UPDATE)
    @PutMapping("/cancel/{repairId}")
    public AjaxResult cancel(@PathVariable("repairId") Long repairId)
    {
        Long userId = getCurrentUserId();
        if (userId == null)
        {
            return error("用户未登录");
        }

        int result = repairService.cancelRepair(repairId, userId);
        return result > 0 ? success("取消成功") : error("取消失败，仅待处理的报修可取消");
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
