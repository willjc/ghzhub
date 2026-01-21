package com.ruoyi.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ruoyi.system.domain.HzCheckoutRecord;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * 退租记录Mapper接口
 *
 * @author ruoyi
 */
@Mapper
public interface HzCheckoutRecordMapper extends BaseMapper<HzCheckoutRecord> {

    /**
     * 根据申请ID查询退租记录
     *
     * @param applyId 申请ID
     * @return 退租记录
     */
    @Select("SELECT * FROM hz_checkout_record WHERE apply_id = #{applyId} AND del_flag = '0' LIMIT 1")
    HzCheckoutRecord selectByApplyId(@Param("applyId") Long applyId);
}
