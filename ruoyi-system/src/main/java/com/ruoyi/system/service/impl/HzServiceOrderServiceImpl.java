package com.ruoyi.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.system.domain.HzServiceCompany;
import com.ruoyi.system.domain.HzServiceOrder;
import com.ruoyi.system.mapper.HzServiceOrderMapper;
import com.ruoyi.system.service.IHzServiceCompanyService;
import com.ruoyi.system.service.IHzServiceOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Random;

/**
 * 服务订单 Service 实现
 *
 * @author ruoyi
 */
@Service
public class HzServiceOrderServiceImpl
        extends ServiceImpl<HzServiceOrderMapper, HzServiceOrder>
        implements IHzServiceOrderService
{
    @Autowired
    private IHzServiceCompanyService companyService;

    private static final String STATUS_PENDING   = "0";
    private static final String STATUS_ASSIGNED  = "1";
    private static final String STATUS_SERVICING = "2";
    private static final String STATUS_FINISHED  = "3";
    private static final String STATUS_CANCELED  = "4";

    @Override
    public IPage<HzServiceOrder> selectOrderPage(HzServiceOrder order, int pageNum, int pageSize)
    {
        Page<HzServiceOrder> page = new Page<>(pageNum, pageSize);
        return this.page(page, buildQueryWrapper(order));
    }

    @Override
    public List<HzServiceOrder> selectOrderList(HzServiceOrder order)
    {
        return this.list(buildQueryWrapper(order));
    }

    @Override
    public List<HzServiceOrder> selectMyOrders(String phone, String orderType, String status, String keyword)
    {
        LambdaQueryWrapper<HzServiceOrder> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(HzServiceOrder::getApplicantPhone, phone)
               .eq(StringUtils.isNotEmpty(orderType), HzServiceOrder::getOrderType, orderType)
               .eq(StringUtils.isNotEmpty(status), HzServiceOrder::getStatus, status)
               .eq(HzServiceOrder::getDelFlag, "0");
        if (StringUtils.isNotEmpty(keyword))
        {
            wrapper.and(w -> w.like(HzServiceOrder::getOrderNo, keyword)
                              .or().like(HzServiceOrder::getHouseAddress, keyword)
                              .or().like(HzServiceOrder::getCompanyName, keyword));
        }
        wrapper.orderByDesc(HzServiceOrder::getCreateTime);
        return this.list(wrapper);
    }

    @Override
    public HzServiceOrder selectOrderById(Long orderId)
    {
        return this.getById(orderId);
    }

    @Override
    @Transactional
    public int submitOrder(HzServiceOrder order)
    {
        // 基础校验
        if (order == null) throw new ServiceException("参数不能为空");
        if (StringUtils.isEmpty(order.getOrderType())
                || (!"1".equals(order.getOrderType()) && !"2".equals(order.getOrderType())))
        {
            throw new ServiceException("订单类型不合法");
        }
        if (StringUtils.isEmpty(order.getApplicantName())) throw new ServiceException("申请人姓名不能为空");
        if (StringUtils.isEmpty(order.getApplicantPhone())) throw new ServiceException("申请人手机号不能为空");
        if (order.getExpectTime() == null) throw new ServiceException("期望服务时间不能为空");

        // 保洁专属校验
        if ("1".equals(order.getOrderType()))
        {
            if (StringUtils.isEmpty(order.getCleanType())) throw new ServiceException("请选择保洁类型");
            if (StringUtils.isEmpty(order.getHouseAddress())) throw new ServiceException("请填写房间地址");
        }
        // 搬家专属校验
        if ("2".equals(order.getOrderType()))
        {
            if (StringUtils.isEmpty(order.getFromAddress())) throw new ServiceException("请填写起运地址");
            if (StringUtils.isEmpty(order.getToAddress())) throw new ServiceException("请填写目的地址");
            if (order.getNeedDisassembly() == null) order.setNeedDisassembly("0");
        }

        order.setOrderNo(generateOrderNo(order.getOrderType()));
        order.setStatus(STATUS_PENDING);
        order.setDelFlag("0");
        order.setCreateBy(order.getApplicantPhone());
        order.setCreateTime(DateUtils.getNowDate());
        return this.save(order) ? 1 : 0;
    }

    @Override
    @Transactional
    public int cancelOrder(Long orderId, String phone, String cancelReason)
    {
        HzServiceOrder existing = this.getById(orderId);
        if (existing == null) throw new ServiceException("订单不存在");
        if (!phone.equals(existing.getApplicantPhone())) throw new ServiceException("无权操作");
        if (!STATUS_PENDING.equals(existing.getStatus()))
        {
            throw new ServiceException("仅待处理订单可取消");
        }
        HzServiceOrder upd = new HzServiceOrder();
        upd.setOrderId(orderId);
        upd.setStatus(STATUS_CANCELED);
        upd.setCancelReason(cancelReason);
        upd.setUpdateBy(phone);
        upd.setUpdateTime(DateUtils.getNowDate());
        return this.updateById(upd) ? 1 : 0;
    }

    @Override
    @Transactional
    public int assignOrder(Long orderId, Long companyId, String assignRemark)
    {
        HzServiceOrder existing = this.getById(orderId);
        if (existing == null) throw new ServiceException("订单不存在");
        if (STATUS_FINISHED.equals(existing.getStatus())
                || STATUS_CANCELED.equals(existing.getStatus()))
        {
            throw new ServiceException("订单已结束，无法重新分配");
        }
        HzServiceCompany company = companyService.selectCompanyById(companyId);
        if (company == null) throw new ServiceException("服务公司不存在");
        if (!"0".equals(company.getStatus())) throw new ServiceException("该服务公司已停用");
        // 类型匹配校验：保洁订单不可派给纯搬家公司，反之亦然（综合除外）
        if ("1".equals(existing.getOrderType()) && "2".equals(company.getCompanyType()))
        {
            throw new ServiceException("该公司不承接保洁服务");
        }
        if ("2".equals(existing.getOrderType()) && "1".equals(company.getCompanyType()))
        {
            throw new ServiceException("该公司不承接搬家服务");
        }

        HzServiceOrder upd = new HzServiceOrder();
        upd.setOrderId(orderId);
        upd.setCompanyId(companyId);
        upd.setCompanyName(company.getCompanyName());
        upd.setAssignedBy(SecurityUtils.getUsername());
        upd.setAssignedTime(DateUtils.getNowDate());
        upd.setAssignRemark(assignRemark);
        upd.setStatus(STATUS_ASSIGNED);
        upd.setUpdateBy(SecurityUtils.getUsername());
        upd.setUpdateTime(DateUtils.getNowDate());
        return this.updateById(upd) ? 1 : 0;
    }

    @Override
    @Transactional
    public int finishOrder(Long orderId)
    {
        HzServiceOrder existing = this.getById(orderId);
        if (existing == null) throw new ServiceException("订单不存在");
        if (!STATUS_ASSIGNED.equals(existing.getStatus())
                && !STATUS_SERVICING.equals(existing.getStatus()))
        {
            throw new ServiceException("仅已分配/服务中的订单可标记完成");
        }
        HzServiceOrder upd = new HzServiceOrder();
        upd.setOrderId(orderId);
        upd.setStatus(STATUS_FINISHED);
        upd.setFinishTime(DateUtils.getNowDate());
        upd.setUpdateBy(SecurityUtils.getUsername());
        upd.setUpdateTime(DateUtils.getNowDate());
        return this.updateById(upd) ? 1 : 0;
    }

    @Override
    @Transactional
    public int rateOrder(Long orderId, String phone, Integer rateScore, String rateContent)
    {
        if (rateScore == null || rateScore < 1 || rateScore > 5)
        {
            throw new ServiceException("评分必须为 1-5");
        }
        HzServiceOrder existing = this.getById(orderId);
        if (existing == null) throw new ServiceException("订单不存在");
        if (!phone.equals(existing.getApplicantPhone())) throw new ServiceException("无权评价");
        if (!STATUS_FINISHED.equals(existing.getStatus())) throw new ServiceException("仅已完成订单可评价");
        if (existing.getRateScore() != null) throw new ServiceException("订单已评价");

        HzServiceOrder upd = new HzServiceOrder();
        upd.setOrderId(orderId);
        upd.setRateScore(rateScore);
        upd.setRateContent(rateContent);
        upd.setRateTime(DateUtils.getNowDate());
        upd.setUpdateBy(phone);
        upd.setUpdateTime(DateUtils.getNowDate());
        return this.updateById(upd) ? 1 : 0;
    }

    @Override
    public int deleteOrderByIds(Long[] orderIds)
    {
        if (orderIds == null || orderIds.length == 0) return 0;
        return this.removeByIds(Arrays.asList(orderIds)) ? orderIds.length : 0;
    }

    @Override
    public String generateOrderNo(String orderType)
    {
        String prefix = "1".equals(orderType) ? "CL" : "MV";
        String dateStr = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
        String rand = String.format("%04d", new Random().nextInt(10000));
        return prefix + dateStr + rand;
    }

    private LambdaQueryWrapper<HzServiceOrder> buildQueryWrapper(HzServiceOrder o)
    {
        LambdaQueryWrapper<HzServiceOrder> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(StringUtils.isNotEmpty(o.getOrderType()),
                        HzServiceOrder::getOrderType, o.getOrderType())
               .eq(StringUtils.isNotEmpty(o.getOrderNo()),
                        HzServiceOrder::getOrderNo, o.getOrderNo())
               .like(StringUtils.isNotEmpty(o.getApplicantName()),
                        HzServiceOrder::getApplicantName, o.getApplicantName())
               .like(StringUtils.isNotEmpty(o.getApplicantPhone()),
                        HzServiceOrder::getApplicantPhone, o.getApplicantPhone())
               .eq(StringUtils.isNotEmpty(o.getStatus()),
                        HzServiceOrder::getStatus, o.getStatus())
               .eq(o.getCompanyId() != null,
                        HzServiceOrder::getCompanyId, o.getCompanyId())
               .like(StringUtils.isNotEmpty(o.getHouseAddress()),
                        HzServiceOrder::getHouseAddress, o.getHouseAddress())
               .eq(HzServiceOrder::getDelFlag, "0")
               .orderByDesc(HzServiceOrder::getCreateTime);
        return wrapper;
    }
}
