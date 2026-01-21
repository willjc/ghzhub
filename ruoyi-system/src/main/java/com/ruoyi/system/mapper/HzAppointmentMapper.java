package com.ruoyi.system.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ruoyi.system.domain.HzAppointment;
import com.ruoyi.system.domain.HzAppointmentVO;

import java.util.List;

/**
 * 预约看房Mapper接口
 *
 * @author ruoyi
 */
@Mapper
public interface HzAppointmentMapper extends BaseMapper<HzAppointment> {

    /**
     * 查询预约列表（包含关联信息）
     *
     * @param appointment 预约查询条件
     * @return 预约VO列表
     */
    List<HzAppointmentVO> selectAppointmentVOList(@Param("appointment") HzAppointment appointment);

    /**
     * 查询预约详情（包含关联信息）
     *
     * @param appointmentId 预约ID
     * @return 预约VO
     */
    HzAppointmentVO selectAppointmentVOById(@Param("appointmentId") Long appointmentId);

    /**
     * 根据用户ID查询预约列表（H5端使用）
     * 通过tenant_id字段匹配user_id
     *
     * @param userId 用户ID
     * @return 预约列表
     */
    List<HzAppointment> selectAppointmentListByUserId(@Param("userId") Long userId);
}
