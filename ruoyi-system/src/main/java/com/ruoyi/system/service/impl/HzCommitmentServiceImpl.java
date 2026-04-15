package com.ruoyi.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.system.domain.HzCommitment;
import com.ruoyi.system.domain.HzCommitmentVO;
import com.ruoyi.system.mapper.HzCommitmentMapper;
import com.ruoyi.system.service.IHzCommitmentService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 承诺书Service业务层处理
 *
 * @author ruoyi
 */
@Service
public class HzCommitmentServiceImpl extends ServiceImpl<HzCommitmentMapper, HzCommitment> implements IHzCommitmentService {

    @Override
    public HzCommitment selectCommitmentById(Long commitmentId) {
        return this.getById(commitmentId);
    }

    @Override
    public List<HzCommitment> selectCommitmentListByTenantId(Long tenantId) {
        LambdaQueryWrapper<HzCommitment> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(HzCommitment::getTenantId, tenantId)
               .eq(HzCommitment::getDelFlag, "0")
               .orderByDesc(HzCommitment::getSignTime);
        return this.list(wrapper);
    }

    @Override
    public HzCommitment selectCommitmentByTenantIdAndType(Long tenantId, String commitmentType) {
        LambdaQueryWrapper<HzCommitment> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(HzCommitment::getTenantId, tenantId)
               .eq(HzCommitment::getCommitmentType, commitmentType)
               .eq(HzCommitment::getStatus, "0") // 有效的承诺书
               .eq(HzCommitment::getDelFlag, "0")
               .orderByDesc(HzCommitment::getSignTime)
               .last("LIMIT 1");
        return this.getOne(wrapper);
    }

    @Override
    public int insertCommitment(HzCommitment commitment) {
        commitment.setDelFlag("0");
        commitment.setStatus("0"); // 默认有效
        return this.save(commitment) ? 1 : 0;
    }

    @Override
    public int updateCommitment(HzCommitment commitment) {
        return this.updateById(commitment) ? 1 : 0;
    }

    @Override
    public int deleteCommitmentById(Long commitmentId) {
        HzCommitment commitment = new HzCommitment();
        commitment.setCommitmentId(commitmentId);
        commitment.setDelFlag("2");
        return this.updateById(commitment) ? 1 : 0;
    }

    @Override
    public HzCommitmentVO selectCommitmentVOById(Long commitmentId) {
        return this.baseMapper.selectCommitmentVOById(commitmentId);
    }

    @Override
    public List<HzCommitmentVO> selectCommitmentVOList(HzCommitment commitment) {
        return this.baseMapper.selectCommitmentVOList(commitment);
    }

    @Override
    public IPage<HzCommitmentVO> selectCommitmentVOPage(Page<HzCommitmentVO> page, HzCommitment commitment) {
        return this.baseMapper.selectCommitmentVOPage(page, commitment);
    }
}
