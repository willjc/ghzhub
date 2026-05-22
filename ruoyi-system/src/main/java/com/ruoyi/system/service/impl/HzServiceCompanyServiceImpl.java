package com.ruoyi.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.system.domain.HzServiceCompany;
import com.ruoyi.system.mapper.HzServiceCompanyMapper;
import com.ruoyi.system.service.IHzServiceCompanyService;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

/**
 * 服务公司 Service 实现
 *
 * @author ruoyi
 */
@Service
public class HzServiceCompanyServiceImpl
        extends ServiceImpl<HzServiceCompanyMapper, HzServiceCompany>
        implements IHzServiceCompanyService
{
    @Override
    public IPage<HzServiceCompany> selectCompanyPage(HzServiceCompany company, int pageNum, int pageSize)
    {
        Page<HzServiceCompany> page = new Page<>(pageNum, pageSize);
        return this.page(page, buildQueryWrapper(company));
    }

    @Override
    public List<HzServiceCompany> selectCompanyList(HzServiceCompany company)
    {
        return this.list(buildQueryWrapper(company));
    }

    @Override
    public List<HzServiceCompany> selectActiveCompaniesByOrderType(String orderType)
    {
        LambdaQueryWrapper<HzServiceCompany> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(HzServiceCompany::getStatus, "0")
               .eq(HzServiceCompany::getDelFlag, "0");
        if ("1".equals(orderType))
        {
            // 保洁订单：保洁专营 + 综合
            wrapper.in(HzServiceCompany::getCompanyType, Arrays.asList("1", "3"));
        }
        else if ("2".equals(orderType))
        {
            // 搬家订单：搬家专营 + 综合
            wrapper.in(HzServiceCompany::getCompanyType, Arrays.asList("2", "3"));
        }
        wrapper.orderByAsc(HzServiceCompany::getSortOrder)
               .orderByDesc(HzServiceCompany::getCreateTime);
        return this.list(wrapper);
    }

    @Override
    public HzServiceCompany selectCompanyById(Long companyId)
    {
        return this.getById(companyId);
    }

    @Override
    public int insertCompany(HzServiceCompany company)
    {
        if (company.getStatus() == null) company.setStatus("0");
        if (company.getDelFlag() == null) company.setDelFlag("0");
        if (company.getSortOrder() == null) company.setSortOrder(0);
        company.setCreateBy(SecurityUtils.getUsername());
        company.setCreateTime(DateUtils.getNowDate());
        return this.save(company) ? 1 : 0;
    }

    @Override
    public int updateCompany(HzServiceCompany company)
    {
        company.setUpdateBy(SecurityUtils.getUsername());
        company.setUpdateTime(DateUtils.getNowDate());
        return this.updateById(company) ? 1 : 0;
    }

    @Override
    public int deleteCompanyByIds(Long[] companyIds)
    {
        if (companyIds == null || companyIds.length == 0) return 0;
        return this.removeByIds(Arrays.asList(companyIds)) ? companyIds.length : 0;
    }

    private LambdaQueryWrapper<HzServiceCompany> buildQueryWrapper(HzServiceCompany c)
    {
        LambdaQueryWrapper<HzServiceCompany> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StringUtils.isNotEmpty(c.getCompanyName()),
                        HzServiceCompany::getCompanyName, c.getCompanyName())
               .eq(StringUtils.isNotEmpty(c.getCompanyType()),
                        HzServiceCompany::getCompanyType, c.getCompanyType())
               .eq(StringUtils.isNotEmpty(c.getStatus()),
                        HzServiceCompany::getStatus, c.getStatus())
               .like(StringUtils.isNotEmpty(c.getContactPerson()),
                        HzServiceCompany::getContactPerson, c.getContactPerson())
               .like(StringUtils.isNotEmpty(c.getContactPhone()),
                        HzServiceCompany::getContactPhone, c.getContactPhone())
               .eq(HzServiceCompany::getDelFlag, "0")
               .orderByAsc(HzServiceCompany::getSortOrder)
               .orderByDesc(HzServiceCompany::getCreateTime);
        return wrapper;
    }
}
