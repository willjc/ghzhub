package com.ruoyi.gangzhu.activity.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.gangzhu.activity.domain.HzActivity;
import com.ruoyi.gangzhu.activity.domain.HzActivityRegistration;
import com.ruoyi.gangzhu.activity.mapper.HzActivityMapper;
import com.ruoyi.gangzhu.activity.mapper.HzActivityRegistrationMapper;
import com.ruoyi.gangzhu.activity.service.IHzActivityRegistrationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

/**
 * 活动报名记录Service业务层处理
 *
 * @author ruoyi
 */
@Service
public class HzActivityRegistrationServiceImpl extends ServiceImpl<HzActivityRegistrationMapper, HzActivityRegistration>
        implements IHzActivityRegistrationService {

    @Autowired
    private HzActivityRegistrationMapper registrationMapper;

    @Autowired
    private HzActivityMapper activityMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public String register(Long activityId, Long userId, String realName, String phone) {
        // 1. 查询活动是否存在
        HzActivity activity = activityMapper.selectById(activityId);
        if (activity == null) {
            return "活动不存在";
        }

        // 2. 检查活动状态
        if (!"0".equals(activity.getStatus())) {
            return "活动已停用";
        }

        // 3. 检查是否在报名时间内
        Date now = new Date();
        if (activity.getRegistrationStartTime() != null && now.before(activity.getRegistrationStartTime())) {
            return "报名尚未开始";
        }
        if (activity.getRegistrationEndTime() != null && now.after(activity.getRegistrationEndTime())) {
            return "报名已结束";
        }

        // 4. 检查是否重复报名
        if (isRegistered(activityId, userId)) {
            return "您已报名该活动，请勿重复报名";
        }

        // 5. 原子性增加报名人数（同时校验人数上限）
        int rows = registrationMapper.incrementParticipants(activityId, activity.getMaxParticipants());
        if (rows == 0) {
            return "报名人数已满";
        }

        // 6. 插入报名记录
        HzActivityRegistration registration = new HzActivityRegistration();
        registration.setActivityId(activityId);
        registration.setUserId(userId);
        registration.setRealName(realName);
        registration.setPhone(phone);
        registration.setRegistrationStatus("0");
        registration.setCreateTime(new Date());
        registration.setDelFlag("0");
        registrationMapper.insert(registration);

        return null; // null表示成功
    }

    @Override
    public boolean isRegistered(Long activityId, Long userId) {
        LambdaQueryWrapper<HzActivityRegistration> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(HzActivityRegistration::getActivityId, activityId)
                .eq(HzActivityRegistration::getUserId, userId)
                .eq(HzActivityRegistration::getRegistrationStatus, "0");
        return registrationMapper.selectCount(wrapper) > 0;
    }

    @Override
    public List<HzActivityRegistration> getRegistrationsByActivityId(Long activityId) {
        LambdaQueryWrapper<HzActivityRegistration> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(HzActivityRegistration::getActivityId, activityId)
                .orderByDesc(HzActivityRegistration::getCreateTime);
        return registrationMapper.selectList(wrapper);
    }

    @Override
    public List<HzActivityRegistration> getMyRegistrations(Long userId) {
        LambdaQueryWrapper<HzActivityRegistration> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(HzActivityRegistration::getUserId, userId)
                .eq(HzActivityRegistration::getRegistrationStatus, "0")
                .orderByDesc(HzActivityRegistration::getCreateTime);
        return registrationMapper.selectList(wrapper);
    }
}
