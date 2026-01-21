package com.ruoyi.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.system.domain.HzQualification;
import com.ruoyi.system.mapper.HzQualificationMapper;
import com.ruoyi.system.service.IHzQualificationService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 资格审核Service业务层处理
 *
 * @author ruoyi
 */
@Service
public class HzQualificationServiceImpl extends ServiceImpl<HzQualificationMapper, HzQualification> implements IHzQualificationService {

    @Override
    public HzQualification selectQualificationById(Long qualificationId) {
        return this.getById(qualificationId);
    }

    @Override
    public List<HzQualification> selectQualificationListByTenantId(Long tenantId) {
        LambdaQueryWrapper<HzQualification> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(HzQualification::getTenantId, tenantId)
               .eq(HzQualification::getDelFlag, "0")
               .orderByDesc(HzQualification::getCreateTime);
        return this.list(wrapper);
    }

    @Override
    public HzQualification selectQualificationByTenantIdAndType(Long tenantId, String applyType) {
        LambdaQueryWrapper<HzQualification> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(HzQualification::getTenantId, tenantId)
               .eq(HzQualification::getApplyType, applyType)
               .eq(HzQualification::getDelFlag, "0")
               .orderByDesc(HzQualification::getCreateTime)
               .last("LIMIT 1");
        return this.getOne(wrapper);
    }

    @Override
    public List<HzQualification> selectQualificationList(HzQualification qualification) {
        LambdaQueryWrapper<HzQualification> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(qualification.getTenantId() != null, HzQualification::getTenantId, qualification.getTenantId())
               .eq(StringUtils.isNotEmpty(qualification.getApplyType()), HzQualification::getApplyType, qualification.getApplyType())
               .eq(StringUtils.isNotEmpty(qualification.getFinalResult()), HzQualification::getFinalResult, qualification.getFinalResult())
               .eq(StringUtils.isNotEmpty(qualification.getStatus()), HzQualification::getStatus, qualification.getStatus())
               .eq(HzQualification::getDelFlag, "0")
               .orderByDesc(HzQualification::getCreateTime);
        return this.list(wrapper);
    }

    @Override
    public IPage<HzQualification> selectQualificationPage(HzQualification qualification, int pageNum, int pageSize) {
        Page<HzQualification> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<HzQualification> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(qualification.getTenantId() != null, HzQualification::getTenantId, qualification.getTenantId())
               .eq(StringUtils.isNotEmpty(qualification.getApplyType()), HzQualification::getApplyType, qualification.getApplyType())
               .eq(StringUtils.isNotEmpty(qualification.getFinalResult()), HzQualification::getFinalResult, qualification.getFinalResult())
               .eq(StringUtils.isNotEmpty(qualification.getStatus()), HzQualification::getStatus, qualification.getStatus())
               .eq(HzQualification::getDelFlag, "0")
               .orderByDesc(HzQualification::getCreateTime);
        return this.page(page, wrapper);
    }

    @Override
    public int insertQualification(HzQualification qualification) {
        qualification.setDelFlag("0");
        qualification.setFinalResult("0"); // 默认未通过,待审核
        qualification.setAutoCheckResult("0"); // 默认自动审核未通过
        return this.save(qualification) ? 1 : 0;
    }

    @Override
    public int updateQualification(HzQualification qualification) {
        return this.updateById(qualification) ? 1 : 0;
    }

    @Override
    public int deleteQualificationById(Long qualificationId) {
        HzQualification qualification = new HzQualification();
        qualification.setQualificationId(qualificationId);
        qualification.setDelFlag("2");
        return this.updateById(qualification) ? 1 : 0;
    }
}
