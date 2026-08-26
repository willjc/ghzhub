package com.ruoyi.web.controller.h5;

import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.system.domain.HzRepair;
import com.ruoyi.system.service.IHzRepairService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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

    /**
     * 提交物业报修
     */
    @Log(title = "提交物业报修", businessType = BusinessType.INSERT)
    @PostMapping("/submit")
    public AjaxResult submit(@RequestBody HzRepair repair)
    {
        repair.setUserId(getCurrentUserId());
        return toAjax(repairService.insertRepair(repair));
    }

    /**
     * 查询当前用户的物业报修列表
     */
    @GetMapping("/myList")
    public AjaxResult myList()
    {
        Long userId = getCurrentUserId();
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
            if (!userId.equals(repair.getUserId()))
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
        int result = repairService.cancelRepair(repairId, userId);
        return result > 0 ? success("取消成功") : error("取消失败，仅待处理的报修可取消");
    }

    protected Long getCurrentUserId()
    {
        return SecurityUtils.getHzUserId();
    }
}
