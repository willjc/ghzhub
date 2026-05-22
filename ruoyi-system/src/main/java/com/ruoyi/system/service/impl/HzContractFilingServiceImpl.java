package com.ruoyi.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.system.domain.HzContractFiling;
import com.ruoyi.system.mapper.HzContractFilingMapper;
import com.ruoyi.system.service.IHzContractFilingService;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Random;

/**
 * 合同备案 Service 实现
 *
 * @author ruoyi
 */
@Service
public class HzContractFilingServiceImpl
        extends ServiceImpl<HzContractFilingMapper, HzContractFiling>
        implements IHzContractFilingService
{
    @Override
    public IPage<HzContractFiling> selectFilingPage(HzContractFiling filing, int pageNum, int pageSize)
    {
        Page<HzContractFiling> page = new Page<>(pageNum, pageSize);
        return this.page(page, buildQueryWrapper(filing));
    }

    @Override
    public List<HzContractFiling> selectFilingList(HzContractFiling filing)
    {
        return this.list(buildQueryWrapper(filing));
    }

    @Override
    public HzContractFiling selectFilingById(Long filingId)
    {
        return this.getById(filingId);
    }

    @Override
    public int submitFiling(HzContractFiling filing)
    {
        if (filing.getTenantId() == null)
        {
            throw new ServiceException("租户ID不能为空");
        }
        if (StringUtils.isEmpty(filing.getSignName()))
        {
            throw new ServiceException("签约人姓名不能为空");
        }
        if (StringUtils.isEmpty(filing.getContractFiles()))
        {
            throw new ServiceException("请上传合同附件");
        }
        filing.setFilingNo(generateFilingNo());
        filing.setApproveStatus("0");
        filing.setDelFlag("0");
        filing.setCreateTime(DateUtils.getNowDate());
        return this.save(filing) ? 1 : 0;
    }

    @Override
    public int approveFiling(Long filingId, String approveStatus, String approveRemark)
    {
        if (filingId == null)
        {
            throw new ServiceException("备案ID不能为空");
        }
        if (!"1".equals(approveStatus) && !"2".equals(approveStatus))
        {
            throw new ServiceException("审批状态参数非法");
        }
        HzContractFiling exist = this.getById(filingId);
        if (exist == null)
        {
            throw new ServiceException("备案记录不存在");
        }
        if (!"0".equals(exist.getApproveStatus()))
        {
            throw new ServiceException("该记录已审批，不可重复操作");
        }
        if ("2".equals(approveStatus) && StringUtils.isEmpty(approveRemark))
        {
            throw new ServiceException("驳回必须填写原因");
        }
        HzContractFiling update = new HzContractFiling();
        update.setFilingId(filingId);
        update.setApproveStatus(approveStatus);
        update.setApproveBy(SecurityUtils.getUsername());
        update.setApproveTime(new Date());
        update.setApproveRemark(approveRemark);
        update.setUpdateBy(SecurityUtils.getUsername());
        update.setUpdateTime(DateUtils.getNowDate());
        return this.updateById(update) ? 1 : 0;
    }

    @Override
    public int deleteFilingByIds(Long[] filingIds)
    {
        if (filingIds == null || filingIds.length == 0) return 0;
        return this.removeByIds(Arrays.asList(filingIds)) ? filingIds.length : 0;
    }

    @Override
    public List<HzContractFiling> selectMyFilings(Long tenantId, String approveStatus)
    {
        if (tenantId == null) return java.util.Collections.emptyList();
        LambdaQueryWrapper<HzContractFiling> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(HzContractFiling::getTenantId, tenantId)
               .eq(HzContractFiling::getDelFlag, "0")
               .eq(StringUtils.isNotEmpty(approveStatus),
                       HzContractFiling::getApproveStatus, approveStatus)
               .orderByDesc(HzContractFiling::getCreateTime);
        return this.list(wrapper);
    }

    private LambdaQueryWrapper<HzContractFiling> buildQueryWrapper(HzContractFiling f)
    {
        LambdaQueryWrapper<HzContractFiling> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StringUtils.isNotEmpty(f.getFilingNo()),
                        HzContractFiling::getFilingNo, f.getFilingNo())
               .like(StringUtils.isNotEmpty(f.getSignName()),
                        HzContractFiling::getSignName, f.getSignName())
               .like(StringUtils.isNotEmpty(f.getSignUnit()),
                        HzContractFiling::getSignUnit, f.getSignUnit())
               .eq(StringUtils.isNotEmpty(f.getApproveStatus()),
                        HzContractFiling::getApproveStatus, f.getApproveStatus())
               .eq(f.getTenantId() != null,
                        HzContractFiling::getTenantId, f.getTenantId())
               .eq(HzContractFiling::getDelFlag, "0")
               .orderByDesc(HzContractFiling::getCreateTime);
        return wrapper;
    }

    private String generateFilingNo()
    {
        return "FL" + new SimpleDateFormat("yyyyMMddHHmmss").format(new Date())
                + String.format("%04d", new Random().nextInt(10000));
    }
}
