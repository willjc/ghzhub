package com.ruoyi.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.system.domain.HzInvoiceApply;
import com.ruoyi.system.mapper.HzInvoiceApplyMapper;
import com.ruoyi.system.service.IHzInvoiceApplyService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 发票申请Service业务层处理
 *
 * @author ruoyi
 */
@Service
public class HzInvoiceApplyServiceImpl extends ServiceImpl<HzInvoiceApplyMapper, HzInvoiceApply> implements IHzInvoiceApplyService
{
    @Override
    public HzInvoiceApply selectInvoiceApplyById(Long applyId)
    {
        LambdaQueryWrapper<HzInvoiceApply> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(HzInvoiceApply::getApplyId, applyId)
               .eq(HzInvoiceApply::getDelFlag, "0");
        return this.getOne(wrapper);
    }

    @Override
    public List<HzInvoiceApply> selectInvoiceApplyListByTenantId(Long tenantId)
    {
        LambdaQueryWrapper<HzInvoiceApply> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(HzInvoiceApply::getTenantId, tenantId)
               .eq(HzInvoiceApply::getDelFlag, "0")
               .orderByDesc(HzInvoiceApply::getCreateTime);
        return this.list(wrapper);
    }

    @Override
    public HzInvoiceApply selectInvoiceApplyByBillId(Long billId)
    {
        LambdaQueryWrapper<HzInvoiceApply> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(HzInvoiceApply::getBillId, billId)
               .eq(HzInvoiceApply::getDelFlag, "0")
               .orderByDesc(HzInvoiceApply::getCreateTime)
               .last("LIMIT 1");
        return this.getOne(wrapper);
    }

    @Override
    public IPage<HzInvoiceApply> selectInvoiceApplyPage(HzInvoiceApply invoiceApply, int pageNum, int pageSize)
    {
        LambdaQueryWrapper<HzInvoiceApply> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(invoiceApply.getTenantId() != null, HzInvoiceApply::getTenantId, invoiceApply.getTenantId())
               .eq(StringUtils.isNotEmpty(invoiceApply.getInvoiceType()), HzInvoiceApply::getInvoiceType, invoiceApply.getInvoiceType())
               .eq(StringUtils.isNotEmpty(invoiceApply.getApplyStatus()), HzInvoiceApply::getApplyStatus, invoiceApply.getApplyStatus())
               .eq(HzInvoiceApply::getDelFlag, "0")
               .orderByDesc(HzInvoiceApply::getCreateTime);

        Page<HzInvoiceApply> page = new Page<>(pageNum, pageSize);
        return this.page(page, wrapper);
    }

    @Override
    public int insertInvoiceApply(HzInvoiceApply invoiceApply)
    {
        invoiceApply.setDelFlag("0");
        invoiceApply.setApplyStatus("1"); // 默认开票中
        invoiceApply.setApplyNo(generateApplyNo());
        invoiceApply.setCreateTime(DateUtils.getNowDate());
        return this.save(invoiceApply) ? 1 : 0;
    }

    @Override
    public int updateInvoiceApply(HzInvoiceApply invoiceApply)
    {
        invoiceApply.setUpdateTime(DateUtils.getNowDate());
        return this.updateById(invoiceApply) ? 1 : 0;
    }

    @Override
    public int deleteInvoiceApplyById(Long applyId)
    {
        HzInvoiceApply invoiceApply = new HzInvoiceApply();
        invoiceApply.setApplyId(applyId);
        invoiceApply.setDelFlag("2");
        invoiceApply.setUpdateTime(DateUtils.getNowDate());
        return this.updateById(invoiceApply) ? 1 : 0;
    }

    @Override
    public String generateApplyNo()
    {
        // 生成申请编号: FP + 日期时间 + 4位随机数
        return "FP" + DateUtils.dateTimeNow() + String.format("%04d", (int)(Math.random() * 10000));
    }
}
