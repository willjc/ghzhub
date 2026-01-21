package com.ruoyi.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.system.domain.HzTransaction;
import com.ruoyi.system.mapper.HzTransactionMapper;
import com.ruoyi.system.service.IHzTransactionService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 交易流水Service业务层处理
 *
 * @author ruoyi
 */
@Service
public class HzTransactionServiceImpl extends ServiceImpl<HzTransactionMapper, HzTransaction> implements IHzTransactionService {

    @Override
    public HzTransaction selectTransactionById(Long transactionId) {
        return this.getById(transactionId);
    }

    @Override
    public HzTransaction selectTransactionByTransactionNo(String transactionNo) {
        LambdaQueryWrapper<HzTransaction> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(HzTransaction::getTransactionNo, transactionNo)
               .eq(HzTransaction::getDelFlag, "0")
               .last("LIMIT 1");
        return this.getOne(wrapper);
    }

    @Override
    public List<HzTransaction> selectTransactionListByTenantId(Long tenantId) {
        LambdaQueryWrapper<HzTransaction> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(HzTransaction::getTenantId, tenantId)
               .eq(HzTransaction::getDelFlag, "0")
               .orderByDesc(HzTransaction::getCreateTime);
        return this.list(wrapper);
    }

    @Override
    public List<HzTransaction> selectTransactionListByBillId(Long billId) {
        LambdaQueryWrapper<HzTransaction> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(HzTransaction::getBillId, billId)
               .eq(HzTransaction::getDelFlag, "0")
               .orderByDesc(HzTransaction::getCreateTime);
        return this.list(wrapper);
    }

    @Override
    public IPage<HzTransaction> selectTransactionPage(HzTransaction transaction, int pageNum, int pageSize) {
        Page<HzTransaction> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<HzTransaction> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(transaction.getTenantId() != null, HzTransaction::getTenantId, transaction.getTenantId())
               .eq(transaction.getBillId() != null, HzTransaction::getBillId, transaction.getBillId())
               .eq(transaction.getPaymentId() != null, HzTransaction::getPaymentId, transaction.getPaymentId())
               .like(StringUtils.isNotEmpty(transaction.getTransactionNo()), HzTransaction::getTransactionNo, transaction.getTransactionNo())
               .eq(StringUtils.isNotEmpty(transaction.getTransactionType()), HzTransaction::getTransactionType, transaction.getTransactionType())
               .eq(StringUtils.isNotEmpty(transaction.getTransactionStatus()), HzTransaction::getTransactionStatus, transaction.getTransactionStatus())
               .eq(HzTransaction::getDelFlag, "0")
               .orderByDesc(HzTransaction::getCreateTime);
        return this.page(page, wrapper);
    }

    @Override
    public int insertTransaction(HzTransaction transaction) {
        transaction.setDelFlag("0");
        transaction.setTransactionStatus("0"); // 默认处理中状态
        if (StringUtils.isEmpty(transaction.getTransactionNo())) {
            transaction.setTransactionNo(generateTransactionNo());
        }
        if (StringUtils.isEmpty(transaction.getTransactionTime())) {
            transaction.setTransactionTime(DateUtils.getTime());
        }
        return this.save(transaction) ? 1 : 0;
    }

    @Override
    public int updateTransaction(HzTransaction transaction) {
        return this.updateById(transaction) ? 1 : 0;
    }

    @Override
    public int deleteTransactionById(Long transactionId) {
        HzTransaction transaction = new HzTransaction();
        transaction.setTransactionId(transactionId);
        transaction.setDelFlag("2");
        return this.updateById(transaction) ? 1 : 0;
    }

    @Override
    public String generateTransactionNo() {
        return "LS" + DateUtils.dateTimeNow() + String.format("%04d", (int)(Math.random() * 10000));
    }
}
