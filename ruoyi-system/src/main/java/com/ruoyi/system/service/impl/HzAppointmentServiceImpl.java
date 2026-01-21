package com.ruoyi.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.system.domain.HzAppointment;
import com.ruoyi.system.domain.HzAppointmentVO;
import com.ruoyi.system.mapper.HzAppointmentMapper;
import com.ruoyi.system.service.IHzAppointmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 预约看房Service业务层处理
 *
 * @author ruoyi
 */
@Service
public class HzAppointmentServiceImpl extends ServiceImpl<HzAppointmentMapper, HzAppointment> implements IHzAppointmentService {

    @Autowired
    private HzAppointmentMapper appointmentMapper;

    @Override
    public HzAppointment selectAppointmentById(Long appointmentId) {
        return this.getById(appointmentId);
    }

    @Override
    public HzAppointment selectAppointmentByAppointmentNo(String appointmentNo) {
        LambdaQueryWrapper<HzAppointment> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(HzAppointment::getAppointmentNo, appointmentNo)
               .eq(HzAppointment::getDelFlag, "0")
               .last("LIMIT 1");
        return this.getOne(wrapper);
    }

    @Override
    public List<HzAppointment> selectAppointmentListByTenantId(Long tenantId) {
        LambdaQueryWrapper<HzAppointment> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(HzAppointment::getTenantId, tenantId)
               .eq(HzAppointment::getDelFlag, "0")
               .orderByDesc(HzAppointment::getCreateTime);
        return this.list(wrapper);
    }

    @Override
    public List<HzAppointment> selectAppointmentListByUserId(Long userId) {
        // 通过Mapper查询，需要关联hz_user表
        return appointmentMapper.selectAppointmentListByUserId(userId);
    }

    @Override
    public List<HzAppointment> selectAppointmentListByHouseId(Long houseId) {
        LambdaQueryWrapper<HzAppointment> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(HzAppointment::getHouseId, houseId)
               .eq(HzAppointment::getDelFlag, "0")
               .orderByDesc(HzAppointment::getAppointmentDate);
        return this.list(wrapper);
    }

    @Override
    public boolean checkTimeSlotAvailable(Long houseId, String appointmentDate, String timeSlot) {
        LambdaQueryWrapper<HzAppointment> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(HzAppointment::getHouseId, houseId)
               .eq(HzAppointment::getAppointmentDate, appointmentDate)
               .eq(HzAppointment::getTimeSlot, timeSlot)
               .in(HzAppointment::getAppointmentStatus, "0", "1") // 待确认或已确认
               .eq(HzAppointment::getDelFlag, "0");
        long count = this.count(wrapper);
        // 每个时间段最多允许3个预约
        return count < 3;
    }

    @Override
    public List<HzAppointment> selectAppointmentList(HzAppointment appointment) {
        LambdaQueryWrapper<HzAppointment> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(appointment.getTenantId() != null, HzAppointment::getTenantId, appointment.getTenantId())
               .eq(appointment.getHouseId() != null, HzAppointment::getHouseId, appointment.getHouseId())
               .eq(appointment.getProjectId() != null, HzAppointment::getProjectId, appointment.getProjectId())
               .like(StringUtils.isNotEmpty(appointment.getAppointmentNo()), HzAppointment::getAppointmentNo, appointment.getAppointmentNo())
               .eq(StringUtils.isNotEmpty(appointment.getAppointmentStatus()), HzAppointment::getAppointmentStatus, appointment.getAppointmentStatus())
               .eq(HzAppointment::getDelFlag, "0")
               .orderByDesc(HzAppointment::getCreateTime);
        return this.list(wrapper);
    }

    @Override
    public IPage<HzAppointment> selectAppointmentPage(HzAppointment appointment, int pageNum, int pageSize) {
        Page<HzAppointment> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<HzAppointment> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(appointment.getTenantId() != null, HzAppointment::getTenantId, appointment.getTenantId())
               .eq(appointment.getHouseId() != null, HzAppointment::getHouseId, appointment.getHouseId())
               .eq(appointment.getProjectId() != null, HzAppointment::getProjectId, appointment.getProjectId())
               .like(StringUtils.isNotEmpty(appointment.getAppointmentNo()), HzAppointment::getAppointmentNo, appointment.getAppointmentNo())
               .eq(StringUtils.isNotEmpty(appointment.getAppointmentStatus()), HzAppointment::getAppointmentStatus, appointment.getAppointmentStatus())
               .eq(HzAppointment::getDelFlag, "0")
               .orderByDesc(HzAppointment::getCreateTime);
        return this.page(page, wrapper);
    }

    @Override
    public int insertAppointment(HzAppointment appointment) {
        appointment.setDelFlag("0");
        appointment.setAppointmentStatus("0"); // 默认待确认状态
        if (StringUtils.isEmpty(appointment.getAppointmentNo())) {
            appointment.setAppointmentNo(generateAppointmentNo());
        }
        return this.save(appointment) ? 1 : 0;
    }

    @Override
    public int updateAppointment(HzAppointment appointment) {
        return this.updateById(appointment) ? 1 : 0;
    }

    @Override
    public int deleteAppointmentById(Long appointmentId) {
        LambdaUpdateWrapper<HzAppointment> wrapper = new LambdaUpdateWrapper<>();
        wrapper.set(HzAppointment::getDelFlag, "2")
               .eq(HzAppointment::getAppointmentId, appointmentId)
               .eq(HzAppointment::getDelFlag, "0");
        return this.update(wrapper) ? 1 : 0;
    }

    @Override
    public String generateAppointmentNo() {
        return "YY" + DateUtils.dateTimeNow() + String.format("%04d", (int)(Math.random() * 10000));
    }

    @Override
    public int updateAppointmentStatus(Long appointmentId, String appointmentStatus) {
        HzAppointment appointment = new HzAppointment();
        appointment.setAppointmentId(appointmentId);
        appointment.setAppointmentStatus(appointmentStatus);
        if ("1".equals(appointmentStatus)) {
            appointment.setConfirmTime(DateUtils.getTime());
        }
        return this.updateById(appointment) ? 1 : 0;
    }

    @Override
    public int cancelAppointment(Long appointmentId, String cancelReason) {
        HzAppointment appointment = new HzAppointment();
        appointment.setAppointmentId(appointmentId);
        appointment.setAppointmentStatus("4"); // 已取消（修改为4）
        appointment.setCancelReason(cancelReason);
        appointment.setCancelTime(DateUtils.getTime());
        return this.updateById(appointment) ? 1 : 0;
    }

    @Override
    public int confirmViewing(Long appointmentId) {
        // 校验预约状态必须是已确认(1)
        HzAppointment existAppointment = this.selectAppointmentById(appointmentId);
        if (existAppointment == null) {
            throw new RuntimeException("预约不存在");
        }
        if (!"1".equals(existAppointment.getAppointmentStatus())) {
            throw new RuntimeException("当前状态不允许确认看房");
        }

        // 更新状态为用户已确认看房(2)
        HzAppointment appointment = new HzAppointment();
        appointment.setAppointmentId(appointmentId);
        appointment.setAppointmentStatus("2");
        return this.updateById(appointment) ? 1 : 0;
    }

    @Override
    public int completeViewing(Long appointmentId) {
        // 校验预约状态必须是用户已确认看房(2)
        HzAppointment existAppointment = this.selectAppointmentById(appointmentId);
        if (existAppointment == null) {
            throw new RuntimeException("预约不存在");
        }
        if (!"2".equals(existAppointment.getAppointmentStatus())) {
            throw new RuntimeException("当前状态不允许完成看房");
        }

        // 更新状态为已完成(3)
        HzAppointment appointment = new HzAppointment();
        appointment.setAppointmentId(appointmentId);
        appointment.setAppointmentStatus("3");
        return this.updateById(appointment) ? 1 : 0;
    }

    @Override
    public int autoExpireAppointments() {
        // 查询预约日期 < 当前日期 且 状态为 0/1/2 的预约
        LambdaUpdateWrapper<HzAppointment> wrapper = new LambdaUpdateWrapper<>();
        wrapper.set(HzAppointment::getAppointmentStatus, "5") // 已过期
               .lt(HzAppointment::getAppointmentDate, DateUtils.getDate())
               .in(HzAppointment::getAppointmentStatus, "0", "1", "2")
               .eq(HzAppointment::getDelFlag, "0");
        return this.update(wrapper) ? 1 : 0;
    }

    @Override
    public HzAppointmentVO selectAppointmentVOById(Long appointmentId) {
        return appointmentMapper.selectAppointmentVOById(appointmentId);
    }

    @Override
    public List<HzAppointmentVO> selectAppointmentVOList(HzAppointment appointment) {
        return appointmentMapper.selectAppointmentVOList(appointment);
    }
}
