package com.ruoyi.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.common.core.domain.entity.SysRole;
import com.ruoyi.common.core.domain.model.LoginUser;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.system.domain.HzRoleProject;
import com.ruoyi.system.mapper.HzRoleProjectMapper;
import com.ruoyi.system.service.IHzRoleProjectService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 用户-项目绑定Service业务层处理
 *
 * @author ruoyi
 */
@Service
public class HzRoleProjectServiceImpl extends ServiceImpl<HzRoleProjectMapper, HzRoleProject> implements IHzRoleProjectService {

    @Override
    public List<Long> getCurrentUserProjectIds() {
        // 管理员不限制
        LoginUser loginUser = SecurityUtils.getLoginUser();
        if (SecurityUtils.isAdmin(loginUser.getUserId())) {
            return null;
        }

        // 检查是否包含管理方角色（管理方不限制）
        List<SysRole> roles = loginUser.getUser().getRoles();
        if (roles != null) {
            for (SysRole role : roles) {
                if ("manager".equals(role.getRoleKey()) || "admin".equals(role.getRoleKey())) {
                    return null;
                }
            }
        }

        // 物业角色：按用户ID查询绑定的项目
        List<Long> projectIds = baseMapper.selectProjectIdsByUserId(loginUser.getUserId());
        return projectIds != null ? projectIds : new ArrayList<>();
    }

    @Override
    public List<Long> getProjectIdsByUserId(Long userId) {
        return baseMapper.selectProjectIdsByUserId(userId);
    }

    @Override
    @Transactional
    public void saveUserProjects(Long userId, List<Long> projectIds) {
        // 先删除该用户的所有绑定
        this.remove(new LambdaQueryWrapper<HzRoleProject>()
                .eq(HzRoleProject::getUserId, userId));

        // 重新插入
        if (projectIds != null && !projectIds.isEmpty()) {
            List<HzRoleProject> list = new ArrayList<>();
            for (Long projectId : projectIds) {
                HzRoleProject rp = new HzRoleProject();
                rp.setUserId(userId);
                rp.setProjectId(projectId);
                rp.setCreateTime(new Date());
                list.add(rp);
            }
            this.saveBatch(list);
        }
    }
}
