package com.ruoyi.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ruoyi.system.domain.HzRefundApply;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 退款申请Mapper接口
 *
 * @author ruoyi
 * @date 2025-01-18
 */
@Mapper
public interface HzRefundApplyMapper extends BaseMapper<HzRefundApply> {

    /**
     * 根据退款编号前缀统计数量（用于生成序号）
     *
     * @param applyNoPrefix 退款编号前缀
     * @return 数量
     */
    Long countByApplyNo(@Param("applyNoPrefix") String applyNoPrefix);
}
