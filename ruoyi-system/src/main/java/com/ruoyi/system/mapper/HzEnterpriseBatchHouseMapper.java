package com.ruoyi.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ruoyi.system.domain.HzEnterpriseBatchHouse;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * 企业批次房源Mapper接口
 *
 * @author ruoyi
 */
@Mapper
public interface HzEnterpriseBatchHouseMapper extends BaseMapper<HzEnterpriseBatchHouse> {

    /**
     * 根据批次ID查询房源列表
     *
     * @param batchId 批次ID
     * @return 房源列表
     */
    List<HzEnterpriseBatchHouse> selectHousesByBatchId(@Param("batchId") Long batchId);

    /**
     * 根据批次ID查询房源详细信息(含房间号和项目名称)
     *
     * @param batchId 批次ID
     * @return 房源详细信息列表
     */
    List<Map<String, Object>> selectHouseDetailsByBatchId(@Param("batchId") Long batchId);
}
