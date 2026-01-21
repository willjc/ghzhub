package com.ruoyi.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.system.domain.HzInvoice;
import com.ruoyi.system.mapper.HzInvoiceMapper;
import com.ruoyi.system.service.IHzInvoiceService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 发票Service业务层处理
 *
 * @author ruoyi
 */
@Service
public class HzInvoiceServiceImpl extends ServiceImpl<HzInvoiceMapper, HzInvoice> implements IHzInvoiceService
{
    @Override
    public HzInvoice selectInvoiceById(Long invoiceId)
    {
        LambdaQueryWrapper<HzInvoice> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(HzInvoice::getInvoiceId, invoiceId)
               .eq(HzInvoice::getDelFlag, "0");
        return this.getOne(wrapper);
    }

    @Override
    public HzInvoice selectInvoiceByApplyId(Long applyId)
    {
        LambdaQueryWrapper<HzInvoice> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(HzInvoice::getApplyId, applyId)
               .eq(HzInvoice::getDelFlag, "0")
               .orderByDesc(HzInvoice::getCreateTime)
               .last("LIMIT 1");
        return this.getOne(wrapper);
    }

    @Override
    public List<HzInvoice> selectInvoiceListByTenantId(Long tenantId)
    {
        LambdaQueryWrapper<HzInvoice> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(HzInvoice::getTenantId, tenantId)
               .eq(HzInvoice::getDelFlag, "0")
               .orderByDesc(HzInvoice::getInvoiceDate);
        return this.list(wrapper);
    }

    @Override
    public int insertInvoice(HzInvoice invoice)
    {
        invoice.setDelFlag("0");
        invoice.setInvoiceStatus("0"); // 默认已开具
        invoice.setCreateTime(DateUtils.getNowDate());
        return this.save(invoice) ? 1 : 0;
    }

    @Override
    public int updateInvoice(HzInvoice invoice)
    {
        invoice.setUpdateTime(DateUtils.getNowDate());
        return this.updateById(invoice) ? 1 : 0;
    }

    @Override
    public int deleteInvoiceById(Long invoiceId)
    {
        HzInvoice invoice = new HzInvoice();
        invoice.setInvoiceId(invoiceId);
        invoice.setDelFlag("2");
        invoice.setUpdateTime(DateUtils.getNowDate());
        return this.updateById(invoice) ? 1 : 0;
    }
}
