package com.ruoyi.system.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ruoyi.system.domain.HzRoleProject;

import java.util.List;

/**
 * 角色-项目绑定Mapper接口
 *
 * @author ruoyi
 */
@Mapper
public interface HzRoleProjectMapper extends BaseMapper<HzRoleProject> {

    /**
     * 根据角色ID查询绑定的项目ID列表
     *
     * @param roleId 角色ID
     * @return 项目ID列表
     */
    @Select("SELECT project_id FROM hz_role_project WHERE role_id = #{roleId}")
    List<Long> selectProjectIdsByRoleId(@Param("roleId") Long roleId);

    /**
     * 根据多个角色ID查询绑定的项目ID列表（去重）
     *
     * @param roleIds 角色ID列表
     * @return 项目ID列表
     */
    List<Long> selectProjectIdsByRoleIds(@Param("roleIds") List<Long> roleIds);
}
