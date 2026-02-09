package com.ruoyi.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ruoyi.system.domain.HzBatchHouse;
import com.ruoyi.system.domain.vo.BatchPreferenceVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 批次房源Mapper接口
 *
 * @author ruoyi
 */
@Mapper
public interface HzBatchHouseMapper extends BaseMapper<HzBatchHouse> {

    /**
     * 根据房源ID查找配租批次的优惠信息
     *
     * @param houseId 房源ID
     * @return 批次优惠信息（批次ID、优惠类型、免租期数）
     */
    BatchPreferenceVo selectBatchPreferenceByHouseId(@Param("houseId") Long houseId);

}
