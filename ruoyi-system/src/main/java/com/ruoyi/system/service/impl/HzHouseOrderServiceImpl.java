package com.ruoyi.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.system.domain.HzDocument;
import com.ruoyi.system.domain.HzHouse;
import com.ruoyi.system.domain.HzHouseOrder;
import com.ruoyi.system.mapper.HzDocumentMapper;
import com.ruoyi.system.mapper.HzHouseMapper;
import com.ruoyi.system.mapper.HzHouseOrderMapper;
import com.ruoyi.system.service.IHzHouseOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 选房预订单Service业务层处理
 *
 * @author ruoyi
 */
@Service
public class HzHouseOrderServiceImpl
        extends ServiceImpl<HzHouseOrderMapper, HzHouseOrder>
        implements IHzHouseOrderService {

    @Autowired
    private HzHouseOrderMapper orderMapper;

    @Autowired
    private HzHouseMapper houseMapper;

    @Autowired
    private HzDocumentMapper documentMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public AjaxResult createOrder(Long tenantId, Long houseId) {
        // 1. 检查是否有进行中的活跃订单
        long activeCount = count(new LambdaQueryWrapper<HzHouseOrder>()
                .eq(HzHouseOrder::getTenantId, tenantId)
                .in(HzHouseOrder::getOrderStatus, "0", "1", "2")
                .eq(HzHouseOrder::getDelFlag, "0"));
        if (activeCount > 0) {
            return AjaxResult.error("您已有进行中的预订，请先处理");
        }

        // 2. 原子锁定房源（house_status: 空置→已预订），利用数据库行级锁保证并发安全
        int locked = orderMapper.lockHouse(houseId);
        if (locked == 0) {
            return AjaxResult.error("该房源已被他人选中，请重新选择");
        }

        // 3. 查询房源信息
        HzHouse house = houseMapper.selectById(houseId);

        // 4. 创建预订单
        HzHouseOrder order = new HzHouseOrder();
        order.setOrderNo(generateOrderNo());
        order.setTenantId(tenantId);
        order.setHouseId(houseId);
        order.setProjectId(house.getProjectId());
        order.setDepositAmount(house.getDeposit() != null ? house.getDeposit() : BigDecimal.ZERO);
        order.setRentPrice(house.getRentPrice() != null ? house.getRentPrice() : BigDecimal.ZERO);
        order.setOrderStatus("0"); // 待签约
        order.setIsBatchAlloc("0");

        // 锁定30分钟
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.MINUTE, 30);
        order.setLockExpireTime(cal.getTime());
        order.setDelFlag("0");
        order.setCreateTime(new Date());

        save(order);

        Map<String, Object> data = new HashMap<>();
        data.put("orderNo", order.getOrderNo());
        data.put("lockExpireTime", order.getLockExpireTime().getTime());
        data.put("depositAmount", order.getDepositAmount());
        return AjaxResult.success(data);
    }

    @Override
    public Map<String, Object> getOrderStatus(String orderNo) {
        HzHouseOrder order = getOne(new LambdaQueryWrapper<HzHouseOrder>()
                .eq(HzHouseOrder::getOrderNo, orderNo)
                .eq(HzHouseOrder::getDelFlag, "0"));
        if (order == null) {
            return null;
        }

        Map<String, Object> result = new HashMap<>();
        result.put("orderNo", order.getOrderNo());
        result.put("orderStatus", order.getOrderStatus());

        long now = System.currentTimeMillis();
        if (order.getLockExpireTime() != null) {
            result.put("remainSeconds", Math.max(0L,
                    (order.getLockExpireTime().getTime() - now) / 1000));
        }
        if (order.getDocUploadExpireTime() != null) {
            result.put("docRemainSeconds", Math.max(0L,
                    (order.getDocUploadExpireTime().getTime() - now) / 1000));
        }
        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void cancelOrder(String orderNo, Long tenantId) {
        HzHouseOrder order = getOne(new LambdaQueryWrapper<HzHouseOrder>()
                .eq(HzHouseOrder::getOrderNo, orderNo)
                .eq(HzHouseOrder::getTenantId, tenantId)
                .eq(HzHouseOrder::getDelFlag, "0"));
        if (order == null) {
            return;
        }
        order.setOrderStatus("5"); // 已取消
        order.setUpdateTime(new Date());
        updateById(order);
        orderMapper.releaseHouse(order.getHouseId());
    }

    @Override
    public List<HzHouseOrder> getPendingUploadOrders(Long tenantId) {
        List<HzHouseOrder> orders = list(new LambdaQueryWrapper<HzHouseOrder>()
                .eq(HzHouseOrder::getTenantId, tenantId)
                .eq(HzHouseOrder::getOrderStatus, "2")
                .eq(HzHouseOrder::getIsBatchAlloc, "0")
                .eq(HzHouseOrder::getDelFlag, "0"));
        long now = System.currentTimeMillis();
        orders.forEach(o -> {
            if (o.getDocUploadExpireTime() != null) {
                o.setDocRemainSeconds(Math.max(0L,
                        (o.getDocUploadExpireTime().getTime() - now) / 1000));
            }
        });
        return orders;
    }

    @Override
    public Map<String, Object> checkinCheck(Long tenantId) {
        Map<String, Object> result = new HashMap<>();
        HzHouseOrder order = getOne(new LambdaQueryWrapper<HzHouseOrder>()
                .eq(HzHouseOrder::getTenantId, tenantId)
                .eq(HzHouseOrder::getOrderStatus, "2")
                .eq(HzHouseOrder::getIsBatchAlloc, "0")
                .eq(HzHouseOrder::getDelFlag, "0")
                .last("LIMIT 1"));
        if (order == null) {
            result.put("canCheckin", true);
        } else {
            long now = System.currentTimeMillis();
            long remain = order.getDocUploadExpireTime() != null
                    ? Math.max(0L, (order.getDocUploadExpireTime().getTime() - now) / 1000)
                    : 0L;
            result.put("canCheckin", false);
            result.put("remainSeconds", remain);
            result.put("orderNo", order.getOrderNo());
        }
        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void onDepositPaid(String orderNo) {
        HzHouseOrder order = getOne(new LambdaQueryWrapper<HzHouseOrder>()
                .eq(HzHouseOrder::getOrderNo, orderNo)
                .eq(HzHouseOrder::getDelFlag, "0"));
        if (order == null || !"1".equals(order.getOrderStatus())) {
            return;
        }

        if ("1".equals(order.getIsBatchAlloc())) {
            // 配租用户：押金缴清后直接完成
            order.setOrderStatus("3");
        } else {
            // 自选用户：押金缴清后进入待上传资料阶段，72小时倒计时
            order.setOrderStatus("2");
            Calendar cal = Calendar.getInstance();
            cal.add(Calendar.HOUR, 72);
            order.setDocUploadExpireTime(cal.getTime());
        }
        order.setUpdateTime(new Date());
        updateById(order);

        // 押金缴清后，房源状态从「已预订(1)」更新为「已出租(2)」
        if (order.getHouseId() != null) {
            houseMapper.update(null, new com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper<HzHouse>()
                    .eq(HzHouse::getHouseId, order.getHouseId())
                    .eq(HzHouse::getHouseStatus, "1")  // 只在「已预订」状态下才更新，防止误改
                    .set(HzHouse::getHouseStatus, "2")); // 2=已出租
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void onDocumentsApproved(Long tenantId) {
        // 检查工作证明(type=3)和学历证明(type=2)是否均已审核通过
        long approvedCount = documentMapper.selectCount(
                new LambdaQueryWrapper<HzDocument>()
                        .eq(HzDocument::getTenantId, tenantId)
                        .in(HzDocument::getDocumentType, "2", "3")
                        .eq(HzDocument::getAuditStatus, "1")
                        .eq(HzDocument::getDelFlag, "0"));
        if (approvedCount >= 2) {
            // 两类资料均已通过，完成订单
            update(null, new LambdaUpdateWrapper<HzHouseOrder>()
                    .eq(HzHouseOrder::getTenantId, tenantId)
                    .eq(HzHouseOrder::getOrderStatus, "2")
                    .eq(HzHouseOrder::getDelFlag, "0")
                    .set(HzHouseOrder::getOrderStatus, "3")
                    .set(HzHouseOrder::getUpdateTime, new Date()));
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void processExpiredOrders() {
        List<HzHouseOrder> expired = orderMapper.selectExpiredOrders();
        for (HzHouseOrder order : expired) {
            order.setOrderStatus("4"); // 已过期
            order.setUpdateTime(new Date());
            updateById(order);
            orderMapper.releaseHouse(order.getHouseId());
        }
    }

    /**
     * 生成预订单号：HO + 时间戳 + 4位随机数
     */
    private String generateOrderNo() {
        String ts = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
        int rand = (int) (Math.random() * 9000) + 1000;
        return "HO" + ts + rand;
    }
}
