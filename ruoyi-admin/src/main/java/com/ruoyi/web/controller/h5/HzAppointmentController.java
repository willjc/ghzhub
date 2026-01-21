package com.ruoyi.web.controller.h5;

import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.system.domain.HzAppointment;
import com.ruoyi.system.domain.HzHouse;
import com.ruoyi.system.service.IHzAppointmentService;
import com.ruoyi.system.service.IHzHouseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * H5端预约看房Controller
 *
 * @author ruoyi
 */
@RestController
@RequestMapping("/h5/appointment")
public class HzAppointmentController extends BaseController {

    @Autowired
    private IHzAppointmentService appointmentService;

    @Autowired
    private IHzHouseService houseService;

    /**
     * 查询指定用户的预约列表
     * @param userId 用户ID
     */
    @GetMapping("/user/{userId}")
    public AjaxResult list(@PathVariable Long userId) {
        List<HzAppointment> list = appointmentService.selectAppointmentListByUserId(userId);
        return success(list);
    }

    /**
     * 获取预约详情
     */
    @GetMapping("/{appointmentId}")
    public AjaxResult getInfo(@PathVariable("appointmentId") Long appointmentId) {
        HzAppointment appointment = appointmentService.selectAppointmentById(appointmentId);
        return success(appointment);
    }

    /**
     * 提交预约申请
     */
    @PostMapping("/apply")
    public AjaxResult apply(@RequestBody HzAppointment appointment) {
        // 校验房源是否存在
        HzHouse house = houseService.selectHouseById(appointment.getHouseId());
        if (house == null) {
            return error("房源不存在");
        }

        // 校验房源状态(空置)
        if (!"1".equals(house.getHouseStatus())) {
            return error("该房源暂不可预约");
        }

        // 检查时间段是否可预约
        if (!appointmentService.checkTimeSlotAvailable(
                appointment.getHouseId(),
                appointment.getAppointmentDate(),
                appointment.getTimeSlot())) {
            return error("该时间段预约已满,请选择其他时间");
        }

        // 设置项目信息
        appointment.setProjectId(house.getProjectId());

        int result = appointmentService.insertAppointment(appointment);
        return result > 0 ? success() : error("提交失败");
    }

    /**
     * 修改预约
     */
    @PutMapping("/update")
    public AjaxResult update(@RequestBody HzAppointment appointment) {
        // 校验预约是否存在
        HzAppointment existAppointment = appointmentService.selectAppointmentById(appointment.getAppointmentId());
        if (existAppointment == null) {
            return error("预约不存在");
        }

        // 只有待确认状态可以修改
        if (!"0".equals(existAppointment.getAppointmentStatus())) {
            return error("当前状态不允许修改");
        }

        // 如果修改了时间,需要重新检查时间段
        if (!existAppointment.getAppointmentDate().equals(appointment.getAppointmentDate()) ||
            !existAppointment.getTimeSlot().equals(appointment.getTimeSlot())) {
            if (!appointmentService.checkTimeSlotAvailable(
                    appointment.getHouseId(),
                    appointment.getAppointmentDate(),
                    appointment.getTimeSlot())) {
                return error("该时间段预约已满,请选择其他时间");
            }
        }

        int result = appointmentService.updateAppointment(appointment);
        return result > 0 ? success() : error("修改失败");
    }

    /**
     * 取消预约
     */
    @PostMapping("/cancel")
    public AjaxResult cancel(@RequestBody HzAppointment appointment) {
        // 校验预约是否存在
        HzAppointment existAppointment = appointmentService.selectAppointmentById(appointment.getAppointmentId());
        if (existAppointment == null) {
            return error("预约不存在");
        }

        // 只有待确认或已确认状态可以取消
        if (!"0".equals(existAppointment.getAppointmentStatus()) &&
            !"1".equals(existAppointment.getAppointmentStatus())) {
            return error("当前状态不允许取消");
        }

        int result = appointmentService.cancelAppointment(
                appointment.getAppointmentId(),
                appointment.getCancelReason());
        return result > 0 ? success() : error("取消失败");
    }

    /**
     * 用户确认看房
     */
    @PostMapping("/confirmViewing")
    public AjaxResult confirmViewing(@RequestParam Long appointmentId) {
        // 校验预约是否存在
        HzAppointment existAppointment = appointmentService.selectAppointmentById(appointmentId);
        if (existAppointment == null) {
            return error("预约不存在");
        }

        try {
            int result = appointmentService.confirmViewing(appointmentId);
            return result > 0 ? success("确认成功，等待管理员审核") : error("确认失败");
        } catch (RuntimeException e) {
            return error(e.getMessage());
        }
    }

    /**
     * 检查时间段是否可预约
     */
    @GetMapping("/checkTimeSlot")
    public AjaxResult checkTimeSlot(
            @RequestParam Long houseId,
            @RequestParam String appointmentDate,
            @RequestParam String timeSlot) {
        boolean available = appointmentService.checkTimeSlotAvailable(houseId, appointmentDate, timeSlot);
        return success(available);
    }
}
