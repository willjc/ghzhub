package com.ruoyi.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.system.domain.HzCoTenant;
import com.ruoyi.system.mapper.HzCoTenantMapper;
import com.ruoyi.system.service.IHzCoTenantService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 合租户申请Service业务层处理
 *
 * @author ruoyi
 */
@Service
public class HzCoTenantServiceImpl extends ServiceImpl<HzCoTenantMapper, HzCoTenant> implements IHzCoTenantService {

    @Override
    public HzCoTenant selectCoTenantById(Long coTenantId) {
        return baseMapper.selectCoTenantByIdWithRelations(coTenantId);
    }

    @Override
    public List<HzCoTenant> selectCoTenantListByTenantId(Long tenantId) {
        HzCoTenant query = new HzCoTenant();
        // TODO: 通过 tenantId 先查询关联的合同，然后查询合租户
        // 目前简单查询所有记录
        return baseMapper.selectCoTenantListWithRelations(query);
    }

    @Override
    public List<HzCoTenant> selectCoTenantList(HzCoTenant coTenant) {
        return baseMapper.selectCoTenantListWithRelations(coTenant);
    }

    @Override
    public IPage<HzCoTenant> selectCoTenantPage(HzCoTenant coTenant, int pageNum, int pageSize) {
        Page<HzCoTenant> page = new Page<>(pageNum, pageSize);
        return baseMapper.selectCoTenantPageWithRelations(page, coTenant);
    }

    @Override
    public int insertCoTenant(HzCoTenant coTenant) {
        coTenant.setDelFlag("0");
        if (StringUtils.isEmpty(coTenant.getStatus())) {
            coTenant.setStatus("0"); // 默认待审核状态
        }
        return this.save(coTenant) ? 1 : 0;
    }

    @Override
    public int updateCoTenant(HzCoTenant coTenant) {
        return this.updateById(coTenant) ? 1 : 0;
    }

    @Override
    public int deleteCoTenantById(Long coTenantId) {
        LambdaUpdateWrapper<HzCoTenant> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(HzCoTenant::getCoTenantId, coTenantId)
               .set(HzCoTenant::getDelFlag, "2");
        return this.update(wrapper) ? 1 : 0;
    }

    @Override
    public int auditCoTenant(Long coTenantId, String status, String auditOpinion) {
        HzCoTenant coTenant = new HzCoTenant();
        coTenant.setCoTenantId(coTenantId);
        coTenant.setStatus(status);
        coTenant.setRemark(auditOpinion);
        coTenant.setUpdateBy(SecurityUtils.getUsername());
        coTenant.setUpdateTime(DateUtils.getNowDate());

        return this.updateById(coTenant) ? 1 : 0;
    }
}
