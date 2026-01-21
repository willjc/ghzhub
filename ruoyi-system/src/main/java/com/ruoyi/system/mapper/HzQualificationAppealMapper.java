package com.ruoyi.system.mapper;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.ruoyi.system.domain.HzQualificationAppeal;
import com.ruoyi.system.domain.HzQualificationAppealVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 资格申诉Mapper接口
 *
 * @author ruoyi
 */
@Mapper
public interface HzQualificationAppealMapper extends BaseMapper<HzQualificationAppeal> {

    /**
     * 分页查询资格申诉VO（包含用户信息）
     *
     * @param page 分页对象
     * @param wrapper 查询条件
     * @return 资格申诉VO列表
     */
    IPage<HzQualificationAppealVO> selectAppealVOPage(IPage<HzQualificationAppealVO> page, @Param(Constants.WRAPPER) QueryWrapper<HzQualificationAppeal> wrapper);

    /**
     * 查询资格申诉VO详情（包含用户信息和处理人信息）
     *
     * @param appealId 申诉ID
     * @return 资格申诉VO
     */
    HzQualificationAppealVO selectAppealVOById(@Param("appealId") Long appealId);

    /**
     * 根据用户ID查询申诉VO列表（H5端，包含用户信息）
     *
     * @param userId 用户ID
     * @return 申诉VO列表
     */
    List<HzQualificationAppealVO> selectAppealVOListByUserId(@Param("userId") Long userId);
}
