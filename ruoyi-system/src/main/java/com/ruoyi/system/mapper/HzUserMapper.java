package com.ruoyi.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ruoyi.system.domain.HzUser;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

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

    /**
     * 按手机号查询用户（包含软删除记录，绕过 @TableLogic 过滤）
     * 用于登录时手机号冲突后复活账号场景
     */
    @Select("SELECT * FROM hz_user WHERE phone = #{phone} LIMIT 1")
    HzUser selectByPhoneIgnoreLogicDelete(@Param("phone") String phone);

    /**
     * 物理删除用户（绕过 @TableLogic，直接 DELETE）
     */
    @Delete("DELETE FROM hz_user WHERE user_id = #{userId}")
    int physicalDeleteById(@Param("userId") Long userId);

}
