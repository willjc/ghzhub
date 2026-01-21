package com.ruoyi.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.system.domain.HzEnterprise;
import com.ruoyi.system.mapper.HzEnterpriseMapper;
import com.ruoyi.system.service.IHzEnterpriseService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 企业客户Service业务层处理
 *
 * @author ruoyi
 */
@Service
public class HzEnterpriseServiceImpl extends ServiceImpl<HzEnterpriseMapper, HzEnterprise> implements IHzEnterpriseService {

    @Override
    public List<HzEnterprise> selectEnterpriseList(HzEnterprise enterprise) {
        LambdaQueryWrapper<HzEnterprise> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StringUtils.isNotEmpty(enterprise.getEnterpriseName()),
                    HzEnterprise::getEnterpriseName, enterprise.getEnterpriseName())
               .like(StringUtils.isNotEmpty(enterprise.getSocialCreditCode()),
                    HzEnterprise::getSocialCreditCode, enterprise.getSocialCreditCode())
               .eq(StringUtils.isNotEmpty(enterprise.getContactPhone()),
                  HzEnterprise::getContactPhone, enterprise.getContactPhone())
               .eq(StringUtils.isNotEmpty(enterprise.getStatus()),
                  HzEnterprise::getStatus, enterprise.getStatus())
               .eq(HzEnterprise::getDelFlag, "0")
               .orderByDesc(HzEnterprise::getCreateTime);
        return this.list(wrapper);
    }

    @Override
    public HzEnterprise selectEnterpriseById(Long enterpriseId) {
        return this.getById(enterpriseId);
    }

    @Override
    public int insertEnterprise(HzEnterprise enterprise) {
        enterprise.setDelFlag("0");
        if (enterprise.getStatus() == null) {
            enterprise.setStatus("0");
        }
        if (enterprise.getEmployeeCount() == null) {
            enterprise.setEmployeeCount(0);
        }
        if (enterprise.getRentedCount() == null) {
            enterprise.setRentedCount(0);
        }
        return this.save(enterprise) ? 1 : 0;
    }

    @Override
    public int updateEnterprise(HzEnterprise enterprise) {
        return this.updateById(enterprise) ? 1 : 0;
    }

    @Override
    public int deleteEnterpriseById(Long enterpriseId) {
        // 逻辑删除
        HzEnterprise enterprise = new HzEnterprise();
        enterprise.setEnterpriseId(enterpriseId);
        enterprise.setDelFlag("2");
        return this.updateById(enterprise) ? 1 : 0;
    }

    @Override
    public int deleteEnterpriseByIds(Long[] enterpriseIds) {
        int count = 0;
        for (Long enterpriseId : enterpriseIds) {
            count += deleteEnterpriseById(enterpriseId);
        }
        return count;
    }
}
