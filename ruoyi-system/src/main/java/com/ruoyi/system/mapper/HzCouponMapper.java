package com.ruoyi.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ruoyi.system.domain.HzCoupon;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

/**
 * 优惠券 Mapper 接口
 *
 * @author ruoyi
 */
public interface HzCouponMapper extends BaseMapper<HzCoupon>
{
    /**
     * 原子领取：只有当 received_count < total_count（或不限量）且状态正常时才 +1。
     * 影响行数=1 表示领取成功；=0 表示已领完或已停用。
     */
    @Update("UPDATE hz_coupon SET received_count = received_count + 1, " +
            "       update_time = NOW() " +
            " WHERE coupon_id = #{couponId} " +
            "   AND status = '0' " +
            "   AND del_flag = '0' " +
            "   AND (total_count = 0 OR received_count < total_count)")
    int incrementReceivedCount(@Param("couponId") Long couponId);
}
