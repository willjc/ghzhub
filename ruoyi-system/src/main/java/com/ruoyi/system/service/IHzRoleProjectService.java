package com.ruoyi.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ruoyi.system.domain.HzRoleProject;

import java.util.List;

/**
 * 用户-项目绑定Service接口
 *
 * @author ruoyi
 */
public interface IHzRoleProjectService extends IService<HzRoleProject> {

    /**
     * 获取当前登录用户绑定的项目ID列表
     * - 管理方/超级管理员：返回null（表示不限制，查询全部）
     * - 物业角色：返回其绑定的项目ID列表
     *
     * @return 项目ID列表，null表示不限制
     */
    List<Long> getCurrentUserProjectIds();

    /**
     * 根据用户ID查询绑定的项目ID列表
     *
     * @param userId 用户ID
     * @return 项目ID列表
     */
    List<Long> getProjectIdsByUserId(Long userId);

    /**
     * 保存用户-项目绑定关系（先删后增）
     *
     * @param userId 用户ID
     * @param projectIds 项目ID列表
     */
    void saveUserProjects(Long userId, List<Long> projectIds);
}
