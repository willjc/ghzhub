package com.ruoyi.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.system.domain.HzSubsidyApply;
import com.ruoyi.system.mapper.HzSubsidyApplyMapper;
import com.ruoyi.system.service.IHzSubsidyApplyService;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Random;

/**
 * 代购补贴申请 Service 实现
 *
 * @author ruoyi
 */
@Service
public class HzSubsidyApplyServiceImpl
        extends ServiceImpl<HzSubsidyApplyMapper, HzSubsidyApply>
        implements IHzSubsidyApplyService
{
    @Override
    public IPage<HzSubsidyApply> selectApplyPage(HzSubsidyApply apply, int pageNum, int pageSize)
    {
        Page<HzSubsidyApply> page = new Page<>(pageNum, pageSize);
        return this.page(page, buildQueryWrapper(apply));
    }

    @Override
    public List<HzSubsidyApply> selectApplyList(HzSubsidyApply apply)
    {
        return this.list(buildQueryWrapper(apply));
    }

    @Override
    public HzSubsidyApply selectApplyById(Long applyId)
    {
        return this.getById(applyId);
    }

    @Override
    public int submitApply(HzSubsidyApply apply)
    {
        if (apply.getTenantId() == null)
        {
            throw new ServiceException("租户ID不能为空");
        }
        if (StringUtils.isEmpty(apply.getApplyName()))
        {
            throw new ServiceException("申请人姓名不能为空");
        }
        if (StringUtils.isEmpty(apply.getPurchaseContractFiles()))
        {
            throw new ServiceException("请上传购房合同附件");
        }
        if (apply.getCommitmentId() == null)
        {
            throw new ServiceException("请先签署承诺书");
        }
        apply.setApplyNo(generateApplyNo());
        apply.setApproveStatus("0");
        apply.setDelFlag("0");
        apply.setCreateTime(DateUtils.getNowDate());
        return this.save(apply) ? 1 : 0;
    }

    @Override
    public int approveApply(Long applyId, String approveStatus, String approveRemark)
    {
        if (applyId == null)
        {
            throw new ServiceException("申请ID不能为空");
        }
        if (!"1".equals(approveStatus) && !"2".equals(approveStatus))
        {
            throw new ServiceException("审批状态参数非法");
        }
        HzSubsidyApply exist = this.getById(applyId);
        if (exist == null)
        {
            throw new ServiceException("申请记录不存在");
        }
        if (!"0".equals(exist.getApproveStatus()))
        {
            throw new ServiceException("该记录已审批，不可重复操作");
        }
        if ("2".equals(approveStatus) && StringUtils.isEmpty(approveRemark))
        {
            throw new ServiceException("驳回必须填写原因");
        }
        HzSubsidyApply update = new HzSubsidyApply();
        update.setApplyId(applyId);
        update.setApproveStatus(approveStatus);
        update.setApproveBy(SecurityUtils.getUsername());
        update.setApproveTime(new Date());
        update.setApproveRemark(approveRemark);
        update.setUpdateBy(SecurityUtils.getUsername());
        update.setUpdateTime(DateUtils.getNowDate());
        return this.updateById(update) ? 1 : 0;
    }

    @Override
    public int deleteApplyByIds(Long[] applyIds)
    {
        if (applyIds == null || applyIds.length == 0) return 0;
        return this.removeByIds(Arrays.asList(applyIds)) ? applyIds.length : 0;
    }

    @Override
    public List<HzSubsidyApply> selectMyApplies(Long tenantId, String approveStatus)
    {
        if (tenantId == null) return java.util.Collections.emptyList();
        LambdaQueryWrapper<HzSubsidyApply> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(HzSubsidyApply::getTenantId, tenantId)
               .eq(HzSubsidyApply::getDelFlag, "0")
               .eq(StringUtils.isNotEmpty(approveStatus),
                       HzSubsidyApply::getApproveStatus, approveStatus)
               .orderByDesc(HzSubsidyApply::getCreateTime);
        return this.list(wrapper);
    }

    private LambdaQueryWrapper<HzSubsidyApply> buildQueryWrapper(HzSubsidyApply a)
    {
        LambdaQueryWrapper<HzSubsidyApply> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StringUtils.isNotEmpty(a.getApplyNo()),
                        HzSubsidyApply::getApplyNo, a.getApplyNo())
               .like(StringUtils.isNotEmpty(a.getApplyName()),
                        HzSubsidyApply::getApplyName, a.getApplyName())
               .like(StringUtils.isNotEmpty(a.getPhone()),
                        HzSubsidyApply::getPhone, a.getPhone())
               .like(StringUtils.isNotEmpty(a.getIdCard()),
                        HzSubsidyApply::getIdCard, a.getIdCard())
               .eq(StringUtils.isNotEmpty(a.getApproveStatus()),
                        HzSubsidyApply::getApproveStatus, a.getApproveStatus())
               .eq(a.getTenantId() != null,
                        HzSubsidyApply::getTenantId, a.getTenantId())
               .eq(HzSubsidyApply::getDelFlag, "0")
               .orderByDesc(HzSubsidyApply::getCreateTime);
        return wrapper;
    }

    private String generateApplyNo()
    {
        return "SB" + new SimpleDateFormat("yyyyMMddHHmmss").format(new Date())
                + String.format("%04d", new Random().nextInt(10000));
    }
}
