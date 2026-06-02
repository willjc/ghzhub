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
import java.util.stream.Collectors;

/**
 * 角色-项目绑定Service业务层处理
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

        // 获取当前用户的角色列表
        List<SysRole> roles = loginUser.getUser().getRoles();
        if (roles == null || roles.isEmpty()) {
            return new ArrayList<>();
        }

        // 检查是否包含管理方角色（管理方不限制）
        for (SysRole role : roles) {
            if ("manager".equals(role.getRoleKey()) || "admin".equals(role.getRoleKey())) {
                return null;
            }
        }

        // 物业角色：查询绑定的项目ID列表
        List<Long> roleIds = roles.stream().map(SysRole::getRoleId).collect(Collectors.toList());
        List<Long> projectIds = baseMapper.selectProjectIdsByRoleIds(roleIds);
        return projectIds != null ? projectIds : new ArrayList<>();
    }

    @Override
    public List<Long> getProjectIdsByRoleId(Long roleId) {
        return baseMapper.selectProjectIdsByRoleId(roleId);
    }

    @Override
    @Transactional
    public void saveRoleProjects(Long roleId, List<Long> projectIds) {
        // 先删除该角色的所有绑定
        this.remove(new LambdaQueryWrapper<HzRoleProject>()
                .eq(HzRoleProject::getRoleId, roleId));

        // 重新插入
        if (projectIds != null && !projectIds.isEmpty()) {
            List<HzRoleProject> list = new ArrayList<>();
            for (Long projectId : projectIds) {
                HzRoleProject rp = new HzRoleProject();
                rp.setRoleId(roleId);
                rp.setProjectId(projectId);
                rp.setCreateTime(new Date());
                list.add(rp);
            }
            this.saveBatch(list);
        }
    }
}
