package com.ruoyi.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.system.domain.HzPayment;
import com.ruoyi.system.mapper.HzPaymentMapper;
import com.ruoyi.system.service.IHzPaymentService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 支付记录Service业务层处理
 *
 * @author ruoyi
 */
@Service
public class HzPaymentServiceImpl extends ServiceImpl<HzPaymentMapper, HzPayment> implements IHzPaymentService {

    @Override
    public HzPayment selectPaymentById(Long paymentId) {
        return this.getById(paymentId);
    }

    @Override
    public HzPayment selectPaymentByPaymentNo(String paymentNo) {
        LambdaQueryWrapper<HzPayment> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(HzPayment::getPaymentNo, paymentNo)
               .eq(HzPayment::getDelFlag, "0")
               .last("LIMIT 1");
        return this.getOne(wrapper);
    }

    @Override
    public List<HzPayment> selectPaymentListByBillId(Long billId) {
        LambdaQueryWrapper<HzPayment> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(HzPayment::getBillId, billId)
               .eq(HzPayment::getDelFlag, "0")
               .orderByDesc(HzPayment::getCreateTime);
        return this.list(wrapper);
    }

    @Override
    public IPage<HzPayment> selectPaymentPage(HzPayment payment, int pageNum, int pageSize) {
        Page<HzPayment> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<HzPayment> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(payment.getBillId() != null, HzPayment::getBillId, payment.getBillId())
               .like(StringUtils.isNotEmpty(payment.getPaymentNo()), HzPayment::getPaymentNo, payment.getPaymentNo())
               .eq(StringUtils.isNotEmpty(payment.getPaymentMethod()), HzPayment::getPaymentMethod, payment.getPaymentMethod())
               .eq(StringUtils.isNotEmpty(payment.getPaymentStatus()), HzPayment::getPaymentStatus, payment.getPaymentStatus())
               .eq(HzPayment::getDelFlag, "0")
               .orderByDesc(HzPayment::getCreateTime);
        return this.page(page, wrapper);
    }

    @Override
    public int insertPayment(HzPayment payment) {
        payment.setDelFlag("0");
        payment.setPaymentStatus("0"); // 默认待支付状态
        if (StringUtils.isEmpty(payment.getPaymentNo())) {
            payment.setPaymentNo(generatePaymentNo());
        }
        return this.save(payment) ? 1 : 0;
    }

    @Override
    public int updatePayment(HzPayment payment) {
        return this.updateById(payment) ? 1 : 0;
    }

    @Override
    public int deletePaymentById(Long paymentId) {
        HzPayment payment = new HzPayment();
        payment.setPaymentId(paymentId);
        payment.setDelFlag("2");
        return this.updateById(payment) ? 1 : 0;
    }

    @Override
    public String generatePaymentNo() {
        return "ZF" + DateUtils.dateTimeNow() + String.format("%04d", (int)(Math.random() * 10000));
    }

    @Override
    public int updatePaymentStatus(Long paymentId, String paymentStatus) {
        HzPayment payment = new HzPayment();
        payment.setPaymentId(paymentId);
        payment.setPaymentStatus(paymentStatus);
        if ("2".equals(paymentStatus)) {
            payment.setPayTime(DateUtils.getTime());
        }
        return this.updateById(payment) ? 1 : 0;
    }
}
