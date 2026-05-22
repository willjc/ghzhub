package com.ruoyi.system.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.ruoyi.system.domain.HzCoupon;
import com.ruoyi.system.domain.HzCouponReceive;

import java.util.List;
import java.util.Map;

/**
 * 优惠券 Service 接口
 *
 * @author ruoyi
 */
public interface IHzCouponService extends IService<HzCoupon>
{
    IPage<HzCoupon> selectCouponPage(HzCoupon coupon, int pageNum, int pageSize);

    List<HzCoupon> selectCouponList(HzCoupon coupon);

    HzCoupon selectCouponById(Long couponId);

    int insertCoupon(HzCoupon coupon);

    int updateCoupon(HzCoupon coupon);

    int deleteCouponByIds(Long[] couponIds);

    /**
     * 可领取列表（H5）：状态=正常 + 在有效期内 + 未达上限
     */
    List<Map<String, Object>> selectAvailableCoupons(Long tenantId);

    /**
     * 我已领取列表（H5）
     */
    List<Map<String, Object>> selectMyReceivedCoupons(Long tenantId, String receiveStatus);

    /**
     * 领取（H5）：使用原子 UPDATE 控制并发，唯一键限制重复领取
     */
    HzCouponReceive receiveCoupon(Long couponId, Long tenantId);

    /**
     * 领取记录列表（管理端）
     */
    IPage<Map<String, Object>> selectReceiveRecordPage(Long couponId, Long tenantId,
                                                        String receiveStatus, int pageNum, int pageSize);
}
