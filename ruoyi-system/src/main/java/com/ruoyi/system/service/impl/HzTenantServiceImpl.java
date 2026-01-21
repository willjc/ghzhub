package com.ruoyi.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.system.domain.HzTenant;
import com.ruoyi.system.mapper.HzTenantMapper;
import com.ruoyi.system.service.IHzTenantService;
import org.springframework.stereotype.Service;

/**
 * 租户信息Service业务层处理
 *
 * @author ruoyi
 */
@Service
public class HzTenantServiceImpl extends ServiceImpl<HzTenantMapper, HzTenant> implements IHzTenantService {

    @Override
    public HzTenant selectTenantById(Long tenantId) {
        return this.getById(tenantId);
    }

    @Override
    public HzTenant selectTenantByUserId(Long userId) {
        LambdaQueryWrapper<HzTenant> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(HzTenant::getUserId, userId)
               .eq(HzTenant::getDelFlag, "0")
               .last("LIMIT 1");
        return this.getOne(wrapper);
    }

    @Override
    public HzTenant selectTenantByIdCard(String idCard) {
        LambdaQueryWrapper<HzTenant> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(HzTenant::getIdCard, idCard)
               .eq(HzTenant::getDelFlag, "0")
               .last("LIMIT 1");
        return this.getOne(wrapper);
    }

    @Override
    public java.util.List<HzTenant> selectTenantList(HzTenant tenant) {
        LambdaQueryWrapper<HzTenant> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StringUtils.isNotEmpty(tenant.getTenantName()), HzTenant::getTenantName, tenant.getTenantName())
               .eq(StringUtils.isNotEmpty(tenant.getIdCard()), HzTenant::getIdCard, tenant.getIdCard())
               .eq(StringUtils.isNotEmpty(tenant.getPhone()), HzTenant::getPhone, tenant.getPhone())
               .eq(StringUtils.isNotEmpty(tenant.getStatus()), HzTenant::getStatus, tenant.getStatus())
               .eq(HzTenant::getDelFlag, "0")
               .orderByDesc(HzTenant::getCreateTime);
        return this.list(wrapper);
    }

    @Override
    public IPage<HzTenant> selectTenantPage(HzTenant tenant, int pageNum, int pageSize) {
        Page<HzTenant> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<HzTenant> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StringUtils.isNotEmpty(tenant.getTenantName()), HzTenant::getTenantName, tenant.getTenantName())
               .eq(StringUtils.isNotEmpty(tenant.getIdCard()), HzTenant::getIdCard, tenant.getIdCard())
               .eq(StringUtils.isNotEmpty(tenant.getPhone()), HzTenant::getPhone, tenant.getPhone())
               .eq(StringUtils.isNotEmpty(tenant.getStatus()), HzTenant::getStatus, tenant.getStatus())
               .eq(HzTenant::getDelFlag, "0")
               .orderByDesc(HzTenant::getCreateTime);
        return this.page(page, wrapper);
    }

    @Override
    public int insertTenant(HzTenant tenant) {
        tenant.setDelFlag("0");
        return this.save(tenant) ? 1 : 0;
    }

    @Override
    public int updateTenant(HzTenant tenant) {
        return this.updateById(tenant) ? 1 : 0;
    }

    @Override
    public int deleteTenantById(Long tenantId) {
        HzTenant tenant = new HzTenant();
        tenant.setTenantId(tenantId);
        tenant.setDelFlag("2");
        return this.updateById(tenant) ? 1 : 0;
    }
}
