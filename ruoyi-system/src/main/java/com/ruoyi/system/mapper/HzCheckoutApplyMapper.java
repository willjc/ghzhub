package com.ruoyi.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ruoyi.system.domain.HzCheckoutApply;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 退租申请Mapper接口
 *
 * @author ruoyi
 */
@Mapper
public interface HzCheckoutApplyMapper extends BaseMapper<HzCheckoutApply> {

    /**
     * 根据租户ID查询退租申请列表
     *
     * @param tenantId 租户ID
     * @return 退租申请集合
     */
    @Select("SELECT * FROM hz_checkout_apply WHERE tenant_id = #{tenantId} AND del_flag = '0' ORDER BY create_time DESC")
    List<HzCheckoutApply> selectByTenantId(@Param("tenantId") Long tenantId);
}
