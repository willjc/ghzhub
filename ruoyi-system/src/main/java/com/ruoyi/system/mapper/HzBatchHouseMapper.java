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

    /**
     * 根据房源ID + 签约人身份证号查找配租批次信息。
     * 仅当签约人确为该房源在批次中的分配对象时命中，避免房源上残留的批次分配记录
     * 影响非批次租户的签约日期、配租方式与优惠。
     *
     * @param houseId 房源ID
     * @param idCard  签约人身份证号
     * @return 批次优惠信息；签约人与批次分配对象不一致时返回 null
     */
    BatchPreferenceVo selectBatchPreferenceByHouseIdAndIdCard(@Param("houseId") Long houseId,
                                                              @Param("idCard") String idCard);

}
