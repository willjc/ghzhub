package com.ruoyi.system.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ruoyi.system.domain.HzContract;

import java.util.List;
import java.util.Map;

/**
 * 合同Mapper接口
 *
 * @author ruoyi
 */
@Mapper
public interface HzContractMapper extends BaseMapper<HzContract> {

    /**
     * 根据用户ID查询合同列表（关联项目、楼栋、单元信息）
     *
     * @param userId 用户ID
     * @return 合同列表
     */
    List<Map<String, Object>> selectContractVOByUserId(@Param("userId") Long userId);
}
