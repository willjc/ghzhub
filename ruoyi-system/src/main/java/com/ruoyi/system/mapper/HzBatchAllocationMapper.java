package com.ruoyi.system.mapper;

import org.apache.ibatis.annotations.Mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ruoyi.system.domain.HzBatchAllocation;

import java.util.List;

/**
 * 配租批次Mapper接口
 *
 * @author ruoyi
 */
@Mapper
public interface HzBatchAllocationMapper extends BaseMapper<HzBatchAllocation> {

    /**
     * 查询配租批次列表(关联项目表获取项目名称)
     *
     * @param batch 配租批次
     * @return 配租批次集合
     */
    List<HzBatchAllocation> selectBatchAllocationList(HzBatchAllocation batch);
}
