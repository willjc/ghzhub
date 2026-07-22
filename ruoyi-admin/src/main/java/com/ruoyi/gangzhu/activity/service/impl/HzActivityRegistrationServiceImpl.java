package com.ruoyi.gangzhu.activity.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.gangzhu.activity.domain.HzActivity;
import com.ruoyi.gangzhu.activity.domain.HzActivityRegistration;
import com.ruoyi.gangzhu.activity.mapper.HzActivityMapper;
import com.ruoyi.gangzhu.activity.mapper.HzActivityRegistrationMapper;
import com.ruoyi.gangzhu.activity.service.IHzActivityRegistrationService;
import com.ruoyi.system.mapper.HzContractMapper;
import com.ruoyi.system.mapper.HzProjectMapper;
import com.ruoyi.system.domain.HzContract;
import com.ruoyi.system.domain.HzProject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

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

    @Autowired
    private HzContractMapper contractMapper;

    @Autowired
    private HzProjectMapper projectMapper;

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

        // 5. 检查报名范围（指定项目租户才能报名）
        if ("1".equals(activity.getRegistrationScope()) && activity.getScopeProjectIds() != null
                && !activity.getScopeProjectIds().isEmpty()) {
            // 查询该用户关联的项目ID（通过有效合同）
            Set<String> userProjectIds = getUserProjectIds(userId);
            if (userProjectIds.isEmpty()) {
                return "该活动仅限指定小区租户报名";
            }
            // 活动允许的项目ID列表
            Set<String> allowedProjectIds = Arrays.stream(activity.getScopeProjectIds().split(","))
                    .map(String::trim)
                    .filter(s -> !s.isEmpty())
                    .collect(Collectors.toSet());
            // 判断用户是否有任意一个项目在允许列表中
            boolean match = allowedProjectIds.stream().anyMatch(userProjectIds::contains);
            if (!match) {
                return "该活动仅限指定小区租户报名";
            }
        }

        // 6. 原子性增加报名人数（同时校验人数上限）
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
        List<HzActivityRegistration> list = registrationMapper.selectList(wrapper);
        fillProjectNames(list);
        return list;
    }

    @Override
    public List<HzActivityRegistration> getMyRegistrations(Long userId) {
        LambdaQueryWrapper<HzActivityRegistration> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(HzActivityRegistration::getUserId, userId)
                .eq(HzActivityRegistration::getRegistrationStatus, "0")
                .orderByDesc(HzActivityRegistration::getCreateTime);
        return registrationMapper.selectList(wrapper);
    }

    /**
     * 批量填充报名记录的所属项目名称。
     * 通过有效合同(status∈2,3,4, del_flag=0)推导，一人只有一个有效项目。
     * 一次性查询避免 N+1。
     */
    private void fillProjectNames(List<HzActivityRegistration> list) {
        if (list == null || list.isEmpty()) {
            return;
        }
        Set<Long> userIds = list.stream()
                .map(HzActivityRegistration::getUserId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
        if (userIds.isEmpty()) {
            return;
        }
        // 查有效合同：user_id -> project_id
        LambdaQueryWrapper<HzContract> cw = new LambdaQueryWrapper<>();
        cw.in(HzContract::getTenantId, userIds)
                .in(HzContract::getContractStatus, "2", "3", "4")
                .eq(HzContract::getDelFlag, "0")
                .select(HzContract::getTenantId, HzContract::getProjectId);
        List<HzContract> contracts = contractMapper.selectList(cw);
        Map<Long, Long> userToProject = new HashMap<>();
        for (HzContract c : contracts) {
            if (c.getTenantId() != null && c.getProjectId() != null) {
                userToProject.putIfAbsent(c.getTenantId(), c.getProjectId());
            }
        }
        if (userToProject.isEmpty()) {
            return;
        }
        // 查项目名称：project_id -> project_name
        Set<Long> projectIds = new HashSet<>(userToProject.values());
        LambdaQueryWrapper<HzProject> pw = new LambdaQueryWrapper<>();
        pw.in(HzProject::getProjectId, projectIds)
                .select(HzProject::getProjectId, HzProject::getProjectName);
        List<HzProject> projects = projectMapper.selectList(pw);
        Map<Long, String> projectNames = projects.stream()
                .collect(Collectors.toMap(HzProject::getProjectId, HzProject::getProjectName, (a, b) -> a));
        // 回填
        for (HzActivityRegistration r : list) {
            Long pid = userToProject.get(r.getUserId());
            if (pid != null) {
                r.setProjectName(projectNames.get(pid));
            }
        }
    }

    /**
     * 查询用户关联的项目ID集合（通过有效合同）
     */
    @Override
    public Set<String> getUserProjectIds(Long userId) {
        LambdaQueryWrapper<HzContract> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(HzContract::getTenantId, userId)
                .in(HzContract::getContractStatus, "2", "3", "4") // 已签署、履行中、已到期
                .eq(HzContract::getDelFlag, "0")
                .select(HzContract::getProjectId)
                .groupBy(HzContract::getProjectId);
        List<HzContract> contracts = contractMapper.selectList(wrapper);
        return contracts.stream()
                .map(c -> c.getProjectId() != null ? c.getProjectId().toString() : null)
                .filter(s -> s != null)
                .collect(Collectors.toSet());
    }
}
