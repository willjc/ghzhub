package com.ruoyi.system.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.ruoyi.system.domain.HzAppointment;
import com.ruoyi.system.domain.HzAppointmentVO;

import java.util.List;

/**
 * 预约看房Service接口
 *
 * @author ruoyi
 */
public interface IHzAppointmentService {

    /**
     * 查询预约看房
     *
     * @param appointmentId 预约看房主键
     * @return 预约看房
     */
    public HzAppointment selectAppointmentById(Long appointmentId);

    /**
     * 根据预约编号查询预约看房
     *
     * @param appointmentNo 预约编号
     * @return 预约看房
     */
    public HzAppointment selectAppointmentByAppointmentNo(String appointmentNo);

    /**
     * 根据租户ID查询预约看房列表
     *
     * @param tenantId 租户ID
     * @return 预约看房列表
     */
    public List<HzAppointment> selectAppointmentListByTenantId(Long tenantId);

    /**
     * 根据用户ID查询预约看房列表（H5端使用）
     * 通过tenant_id字段匹配user_id
     *
     * @param userId 用户ID
     * @return 预约看房列表
     */
    public List<HzAppointment> selectAppointmentListByUserId(Long userId);

    /**
     * 根据房源ID查询预约看房列表
     *
     * @param houseId 房源ID
     * @return 预约看房列表
     */
    public List<HzAppointment> selectAppointmentListByHouseId(Long houseId);

    /**
     * 检查时间段是否可预约
     *
     * @param houseId 房源ID
     * @param appointmentDate 预约日期
     * @param timeSlot 时间段
     * @return 是否可预约
     */
    public boolean checkTimeSlotAvailable(Long houseId, String appointmentDate, String timeSlot);

    /**
     * 查询预约看房列表
     *
     * @param appointment 预约看房
     * @return 预约看房列表
     */
    public List<HzAppointment> selectAppointmentList(HzAppointment appointment);

    /**
     * 分页查询预约看房列表
     *
     * @param appointment 预约看房
     * @param pageNum 页码
     * @param pageSize 每页条数
     * @return 预约看房分页数据
     */
    public IPage<HzAppointment> selectAppointmentPage(HzAppointment appointment, int pageNum, int pageSize);

    /**
     * 新增预约看房
     *
     * @param appointment 预约看房
     * @return 结果
     */
    public int insertAppointment(HzAppointment appointment);

    /**
     * 修改预约看房
     *
     * @param appointment 预约看房
     * @return 结果
     */
    public int updateAppointment(HzAppointment appointment);

    /**
     * 删除预约看房
     *
     * @param appointmentId 预约看房主键
     * @return 结果
     */
    public int deleteAppointmentById(Long appointmentId);

    /**
     * 生成预约编号
     *
     * @return 预约编号
     */
    public String generateAppointmentNo();

    /**
     * 更新预约状态
     *
     * @param appointmentId 预约ID
     * @param appointmentStatus 预约状态
     * @return 结果
     */
    public int updateAppointmentStatus(Long appointmentId, String appointmentStatus);

    /**
     * 取消预约
     *
     * @param appointmentId 预约ID
     * @param cancelReason 取消原因
     * @return 结果
     */
    public int cancelAppointment(Long appointmentId, String cancelReason);

    /**
     * 查询预约VO详情（包含关联信息）
     *
     * @param appointmentId 预约ID
     * @return 预约VO
     */
    public HzAppointmentVO selectAppointmentVOById(Long appointmentId);

    /**
     * 查询预约VO列表（包含关联信息）
     *
     * @param appointment 预约查询条件
     * @return 预约VO列表
     */
    public List<HzAppointmentVO> selectAppointmentVOList(HzAppointment appointment);

    /**
     * 用户确认看房（用户端调用）
     * 将状态从"已确认(1)"更新为"用户已确认看房(2)"
     *
     * @param appointmentId 预约ID
     * @return 结果
     */
    public int confirmViewing(Long appointmentId);

    /**
     * 管理员审核完成看房（管理端调用）
     * 将状态从"用户已确认看房(2)"更新为"已完成(3)"
     *
     * @param appointmentId 预约ID
     * @return 结果
     */
    public int completeViewing(Long appointmentId);

    /**
     * 自动过期处理（定时任务调用）
     * 将预约日期已过且状态为0/1/2的预约更新为已过期(5)
     *
     * @return 处理数量
     */
    public int autoExpireAppointments();
}
