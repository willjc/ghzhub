package com.ruoyi.gangzhu.activity.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ruoyi.gangzhu.activity.domain.HzActivityRegistration;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

/**
 * 活动报名记录Mapper接口
 *
 * @author ruoyi
 */
@Mapper
public interface HzActivityRegistrationMapper extends BaseMapper<HzActivityRegistration> {

    /**
     * 原子性增加活动当前报名人数
     *
     * @param activityId 活动ID
     * @param maxParticipants 最大参与人数（用于条件校验）
     * @return 受影响行数
     */
    @Update("UPDATE hz_activity SET current_participants = current_participants + 1 " +
            "WHERE activity_id = #{activityId} AND del_flag = '0' " +
            "AND (max_participants IS NULL OR max_participants = 0 OR current_participants < max_participants)")
    int incrementParticipants(@Param("activityId") Long activityId, @Param("maxParticipants") Integer maxParticipants);
}
