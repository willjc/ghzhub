package com.ruoyi.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ruoyi.system.domain.HzUser;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 用户信息Mapper接口
 *
 * @author ruoyi
 */
@Mapper
public interface HzUserMapper extends BaseMapper<HzUser> {

    /**
     * 查询用户列表
     *
     * @param hzUser 用户信息
     * @return 用户集合
     */
    List<HzUser> selectHzUserList(HzUser hzUser);

}
