package com.ruoyi.gangzhu.activity.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ruoyi.gangzhu.activity.domain.HzActivityRegistration;

import java.util.List;

/**
 * 活动报名记录Service接口
 *
 * @author ruoyi
 */
public interface IHzActivityRegistrationService extends IService<HzActivityRegistration> {

    /**
     * 活动报名
     *
     * @param activityId 活动ID
     * @param userId 用户ID
     * @param realName 姓名
     * @param phone 联系电话
     * @return 结果
     */
    String register(Long activityId, Long userId, String realName, String phone);

    /**
     * 检查用户是否已报名
     *
     * @param activityId 活动ID
     * @param userId 用户ID
     * @return true-已报名 false-未报名
     */
    boolean isRegistered(Long activityId, Long userId);

    /**
     * 获取活动的所有报名记录
     *
     * @param activityId 活动ID
     * @return 报名记录列表
     */
    List<HzActivityRegistration> getRegistrationsByActivityId(Long activityId);

    /**
     * 获取用户的所有报名记录
     *
     * @param userId 用户ID
     * @return 报名记录列表
     */
    List<HzActivityRegistration> getMyRegistrations(Long userId);
}
