package com.ruoyi.system.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ruoyi.system.domain.HzRoleProject;

import java.util.List;

/**
 * 用户-项目绑定Mapper接口
 *
 * @author ruoyi
 */
@Mapper
public interface HzRoleProjectMapper extends BaseMapper<HzRoleProject> {

    /**
     * 根据用户ID查询绑定的项目ID列表
     *
     * @param userId 用户ID
     * @return 项目ID列表
     */
    @Select("SELECT project_id FROM hz_user_project WHERE user_id = #{userId}")
    List<Long> selectProjectIdsByUserId(@Param("userId") Long userId);
}
