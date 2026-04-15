package com.ruoyi.system.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
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

    /**
     * 分页查询配租批次列表
     *
     * @param page 分页参数
     * @param batch 配租批次
     * @return 配租批次分页结果
     */
    IPage<HzBatchAllocation> selectBatchAllocationPage(Page<HzBatchAllocation> page, @Param("entity") HzBatchAllocation batch);
}
