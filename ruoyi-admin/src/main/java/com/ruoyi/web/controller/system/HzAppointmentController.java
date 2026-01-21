package com.ruoyi.web.controller.system;

import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.system.domain.HzAppointment;
import com.ruoyi.system.domain.HzAppointmentVO;
import com.ruoyi.system.service.IHzAppointmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * 预约管理Controller
 *
 * @author ruoyi
 */
@RestController("adminAppointmentController")
@RequestMapping("/system/appointment")
public class HzAppointmentController extends BaseController
{
    @Autowired
    private IHzAppointmentService appointmentService;

    /**
     * 查询预约看房列表
     */
    @PreAuthorize("@ss.hasPermi('gangzhu:appointment:list')")
    @GetMapping("/list")
    public TableDataInfo list(HzAppointment appointment)
    {
        startPage();
        List<HzAppointmentVO> list = appointmentService.selectAppointmentVOList(appointment);
        return getDataTable(list);
    }

    /**
     * 导出预约看房列表
     */
    @PreAuthorize("@ss.hasPermi('gangzhu:appointment:export')")
    @Log(title = "预约管理", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, HzAppointment appointment)
    {
        List<HzAppointment> list = appointmentService.selectAppointmentList(appointment);
        ExcelUtil<HzAppointment> util = new ExcelUtil<HzAppointment>(HzAppointment.class);
        util.exportExcel(response, list, "预约看房数据");
    }

    /**
     * 获取预约看房详细信息
     */
    @PreAuthorize("@ss.hasPermi('gangzhu:appointment:query')")
    @GetMapping(value = "/{appointmentId}")
    public AjaxResult getInfo(@PathVariable("appointmentId") Long appointmentId)
    {
        return success(appointmentService.selectAppointmentVOById(appointmentId));
    }

    /**
     * 新增预约看房
     */
    @PreAuthorize("@ss.hasPermi('gangzhu:appointment:add')")
    @Log(title = "预约管理", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody HzAppointment appointment)
    {
        return toAjax(appointmentService.insertAppointment(appointment));
    }

    /**
     * 修改预约看房
     */
    @PreAuthorize("@ss.hasPermi('gangzhu:appointment:edit')")
    @Log(title = "预约管理", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody HzAppointment appointment)
    {
        return toAjax(appointmentService.updateAppointment(appointment));
    }

    /**
     * 删除预约看房
     */
    @PreAuthorize("@ss.hasPermi('gangzhu:appointment:remove')")
    @Log(title = "预约管理", businessType = BusinessType.DELETE)
    @DeleteMapping("/{appointmentIds}")
    public AjaxResult remove(@PathVariable Long[] appointmentIds)
    {
        for (Long appointmentId : appointmentIds) {
            appointmentService.deleteAppointmentById(appointmentId);
        }
        return success();
    }

    /**
     * 确认预约
     */
    @PreAuthorize("@ss.hasPermi('gangzhu:appointment:confirm')")
    @Log(title = "确认预约", businessType = BusinessType.UPDATE)
    @PutMapping("/confirm/{appointmentId}")
    public AjaxResult confirm(@PathVariable Long appointmentId)
    {
        return toAjax(appointmentService.updateAppointmentStatus(appointmentId, "1"));
    }

    /**
     * 取消预约
     */
    @PreAuthorize("@ss.hasPermi('gangzhu:appointment:cancel')")
    @Log(title = "取消预约", businessType = BusinessType.UPDATE)
    @PutMapping("/cancel/{appointmentId}")
    public AjaxResult cancel(@PathVariable Long appointmentId, @RequestBody String cancelReason)
    {
        return toAjax(appointmentService.cancelAppointment(appointmentId, cancelReason));
    }

    /**
     * 审核完成看房
     */
    @PreAuthorize("@ss.hasPermi('gangzhu:appointment:complete')")
    @Log(title = "审核完成看房", businessType = BusinessType.UPDATE)
    @PutMapping("/complete/{appointmentId}")
    public AjaxResult complete(@PathVariable Long appointmentId)
    {
        try {
            return toAjax(appointmentService.completeViewing(appointmentId));
        } catch (RuntimeException e) {
            return error(e.getMessage());
        }
    }
}
