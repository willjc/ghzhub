package com.ruoyi.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.system.domain.HzCoupon;
import com.ruoyi.system.domain.HzCouponReceive;
import com.ruoyi.system.mapper.HzCouponMapper;
import com.ruoyi.system.mapper.HzCouponReceiveMapper;
import com.ruoyi.system.service.IHzCouponService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 优惠券 Service 业务层处理
 *
 * @author ruoyi
 */
@Service
public class HzCouponServiceImpl extends ServiceImpl<HzCouponMapper, HzCoupon> implements IHzCouponService
{
    @Autowired
    private HzCouponReceiveMapper couponReceiveMapper;

    @Override
    public IPage<HzCoupon> selectCouponPage(HzCoupon coupon, int pageNum, int pageSize)
    {
        LambdaQueryWrapper<HzCoupon> wrapper = buildQueryWrapper(coupon);
        wrapper.orderByDesc(HzCoupon::getCouponId);
        return baseMapper.selectPage(new Page<>(pageNum, pageSize), wrapper);
    }

    @Override
    public List<HzCoupon> selectCouponList(HzCoupon coupon)
    {
        LambdaQueryWrapper<HzCoupon> wrapper = buildQueryWrapper(coupon);
        wrapper.orderByDesc(HzCoupon::getCouponId);
        return baseMapper.selectList(wrapper);
    }

    private LambdaQueryWrapper<HzCoupon> buildQueryWrapper(HzCoupon coupon)
    {
        LambdaQueryWrapper<HzCoupon> wrapper = new LambdaQueryWrapper<>();
        if (coupon != null)
        {
            if (coupon.getCouponName() != null && !coupon.getCouponName().isEmpty())
            {
                wrapper.like(HzCoupon::getCouponName, coupon.getCouponName());
            }
            if (coupon.getCouponType() != null && !coupon.getCouponType().isEmpty())
            {
                wrapper.eq(HzCoupon::getCouponType, coupon.getCouponType());
            }
            if (coupon.getStatus() != null && !coupon.getStatus().isEmpty())
            {
                wrapper.eq(HzCoupon::getStatus, coupon.getStatus());
            }
        }
        return wrapper;
    }

    @Override
    public HzCoupon selectCouponById(Long couponId)
    {
        return baseMapper.selectById(couponId);
    }

    @Override
    public int insertCoupon(HzCoupon coupon)
    {
        coupon.setCreateTime(DateUtils.getNowDate());
        try { coupon.setCreateBy(SecurityUtils.getUsername()); } catch (Exception ignore) {}
        if (coupon.getReceivedCount() == null) coupon.setReceivedCount(0);
        if (coupon.getUsedCount() == null) coupon.setUsedCount(0);
        if (coupon.getStatus() == null || coupon.getStatus().isEmpty()) coupon.setStatus("0");
        if (coupon.getDelFlag() == null) coupon.setDelFlag("0");
        return baseMapper.insert(coupon);
    }

    @Override
    public int updateCoupon(HzCoupon coupon)
    {
        coupon.setUpdateTime(DateUtils.getNowDate());
        try { coupon.setUpdateBy(SecurityUtils.getUsername()); } catch (Exception ignore) {}
        return baseMapper.updateById(coupon);
    }

    @Override
    public int deleteCouponByIds(Long[] couponIds)
    {
        int rows = 0;
        for (Long id : couponIds)
        {
            rows += baseMapper.deleteById(id);
        }
        return rows;
    }

    @Override
    public List<Map<String, Object>> selectAvailableCoupons(Long tenantId)
    {
        Date now = new Date();
        LambdaQueryWrapper<HzCoupon> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(HzCoupon::getStatus, "0")
                .le(HzCoupon::getValidStartDate, now)
                .ge(HzCoupon::getValidEndDate, now)
                .orderByDesc(HzCoupon::getCouponId);
        List<HzCoupon> list = baseMapper.selectList(wrapper);

        // 过滤掉已达上限的（total_count != 0 && received_count >= total_count）
        list = list.stream().filter(c -> {
            Integer total = c.getTotalCount();
            Integer received = c.getReceivedCount() == null ? 0 : c.getReceivedCount();
            return total == null || total == 0 || received < total;
        }).collect(Collectors.toList());

        // 标记当前用户是否已领取
        List<Long> ids = list.stream().map(HzCoupon::getCouponId).collect(Collectors.toList());
        final java.util.Set<Long> receivedSet = new java.util.HashSet<>();
        if (tenantId != null && !ids.isEmpty())
        {
            LambdaQueryWrapper<HzCouponReceive> rw = new LambdaQueryWrapper<>();
            rw.eq(HzCouponReceive::getTenantId, tenantId).in(HzCouponReceive::getCouponId, ids);
            List<HzCouponReceive> rec = couponReceiveMapper.selectList(rw);
            for (HzCouponReceive r : rec) receivedSet.add(r.getCouponId());
        }

        return list.stream().map(c -> {
            Map<String, Object> m = toMap(c);
            m.put("hasReceived", receivedSet.contains(c.getCouponId()));
            return m;
        }).collect(Collectors.toList());
    }

    @Override
    public List<Map<String, Object>> selectMyReceivedCoupons(Long tenantId, String receiveStatus)
    {
        if (tenantId == null) return java.util.Collections.emptyList();
        LambdaQueryWrapper<HzCouponReceive> rw = new LambdaQueryWrapper<>();
        rw.eq(HzCouponReceive::getTenantId, tenantId);
        if (receiveStatus != null && !receiveStatus.isEmpty())
        {
            rw.eq(HzCouponReceive::getReceiveStatus, receiveStatus);
        }
        rw.orderByDesc(HzCouponReceive::getReceiveTime);
        List<HzCouponReceive> records = couponReceiveMapper.selectList(rw);
        if (records.isEmpty()) return java.util.Collections.emptyList();

        List<Long> couponIds = records.stream().map(HzCouponReceive::getCouponId).distinct().collect(Collectors.toList());
        Map<Long, HzCoupon> couponMap = baseMapper.selectBatchIds(couponIds).stream()
                .collect(Collectors.toMap(HzCoupon::getCouponId, c -> c));

        return records.stream().map(r -> {
            Map<String, Object> m = new HashMap<>();
            HzCoupon c = couponMap.get(r.getCouponId());
            if (c != null) m.putAll(toMap(c));
            m.put("receiveId", r.getReceiveId());
            m.put("receiveTime", r.getReceiveTime());
            m.put("receiveStatus", r.getReceiveStatus());
            m.put("useTime", r.getUseTime());
            m.put("orderId", r.getOrderId());
            return m;
        }).collect(Collectors.toList());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public HzCouponReceive receiveCoupon(Long couponId, Long tenantId)
    {
        if (couponId == null || tenantId == null)
        {
            throw new ServiceException("参数缺失");
        }
        HzCoupon c = baseMapper.selectById(couponId);
        if (c == null || "1".equals(c.getDelFlag()))
        {
            throw new ServiceException("优惠券不存在");
        }
        if (!"0".equals(c.getStatus()))
        {
            throw new ServiceException("优惠券已停用");
        }
        Date now = new Date();
        if (c.getValidStartDate() != null && now.before(c.getValidStartDate()))
        {
            throw new ServiceException("优惠券未到生效时间");
        }
        if (c.getValidEndDate() != null && now.after(c.getValidEndDate()))
        {
            throw new ServiceException("优惠券已过期");
        }

        // 1) 原子 +1，控制总量并发
        int rows = baseMapper.incrementReceivedCount(couponId);
        if (rows == 0)
        {
            throw new ServiceException("已领完或已停用");
        }

        // 2) 写领取记录（唯一键防重复）
        HzCouponReceive r = new HzCouponReceive();
        r.setCouponId(couponId);
        r.setTenantId(tenantId);
        r.setReceiveTime(now);
        r.setReceiveStatus("0");
        r.setDelFlag("0");
        r.setCreateTime(now);
        try
        {
            couponReceiveMapper.insert(r);
        }
        catch (DuplicateKeyException e)
        {
            // 回滚 +1
            throw new ServiceException("您已领取过该优惠券");
        }
        return r;
    }

    @Override
    public IPage<Map<String, Object>> selectReceiveRecordPage(Long couponId, Long tenantId,
                                                               String receiveStatus, int pageNum, int pageSize)
    {
        LambdaQueryWrapper<HzCouponReceive> rw = new LambdaQueryWrapper<>();
        if (couponId != null) rw.eq(HzCouponReceive::getCouponId, couponId);
        if (tenantId != null) rw.eq(HzCouponReceive::getTenantId, tenantId);
        if (receiveStatus != null && !receiveStatus.isEmpty())
        {
            rw.eq(HzCouponReceive::getReceiveStatus, receiveStatus);
        }
        rw.orderByDesc(HzCouponReceive::getReceiveTime);
        IPage<HzCouponReceive> page = couponReceiveMapper.selectPage(new Page<>(pageNum, pageSize), rw);

        List<HzCouponReceive> records = page.getRecords();
        Map<Long, HzCoupon> couponMap = new HashMap<>();
        if (!records.isEmpty())
        {
            List<Long> ids = records.stream().map(HzCouponReceive::getCouponId).distinct().collect(Collectors.toList());
            couponMap = baseMapper.selectBatchIds(ids).stream()
                    .collect(Collectors.toMap(HzCoupon::getCouponId, c -> c));
        }
        final Map<Long, HzCoupon> finalMap = couponMap;
        IPage<Map<String, Object>> resultPage = new Page<>(page.getCurrent(), page.getSize(), page.getTotal());
        List<Map<String, Object>> mapList = records.stream().map(r -> {
            Map<String, Object> m = new HashMap<>();
            m.put("receiveId", r.getReceiveId());
            m.put("couponId", r.getCouponId());
            m.put("tenantId", r.getTenantId());
            m.put("receiveTime", r.getReceiveTime());
            m.put("receiveStatus", r.getReceiveStatus());
            m.put("useTime", r.getUseTime());
            m.put("orderId", r.getOrderId());
            HzCoupon c = finalMap.get(r.getCouponId());
            if (c != null)
            {
                m.put("couponName", c.getCouponName());
                m.put("couponType", c.getCouponType());
                m.put("discountAmount", c.getDiscountAmount());
            }
            return m;
        }).collect(Collectors.toList());
        resultPage.setRecords(mapList);
        return resultPage;
    }

    private Map<String, Object> toMap(HzCoupon c)
    {
        Map<String, Object> m = new HashMap<>();
        m.put("couponId", c.getCouponId());
        m.put("couponName", c.getCouponName());
        m.put("couponCode", c.getCouponCode());
        m.put("couponType", c.getCouponType());
        m.put("discountAmount", c.getDiscountAmount());
        m.put("discountRate", c.getDiscountRate());
        m.put("minAmount", c.getMinAmount());
        m.put("maxDiscount", c.getMaxDiscount());
        m.put("totalCount", c.getTotalCount());
        m.put("receivedCount", c.getReceivedCount());
        m.put("validStartDate", c.getValidStartDate());
        m.put("validEndDate", c.getValidEndDate());
        m.put("applicableType", c.getApplicableType());
        m.put("status", c.getStatus());
        return m;
    }
}
