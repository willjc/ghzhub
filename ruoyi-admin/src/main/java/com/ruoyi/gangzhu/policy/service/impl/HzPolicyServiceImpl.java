package com.ruoyi.gangzhu.policy.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.gangzhu.policy.domain.HzPolicy;
import com.ruoyi.gangzhu.policy.mapper.HzPolicyMapper;
import com.ruoyi.gangzhu.policy.service.IHzPolicyService;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

/**
 * 政策文件Service业务层处理
 *
 * @author ruoyi
 */
@Service
public class HzPolicyServiceImpl extends ServiceImpl<HzPolicyMapper, HzPolicy> implements IHzPolicyService {

    /**
     * 查询政策文件列表
     *
     * @param policy 政策文件
     * @return 政策文件
     */
    @Override
    public List<HzPolicy> selectPolicyList(HzPolicy policy) {
        LambdaQueryWrapper<HzPolicy> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StringUtils.isNotEmpty(policy.getPolicyTitle()), HzPolicy::getPolicyTitle, policy.getPolicyTitle())
                .eq(StringUtils.isNotEmpty(policy.getPolicyType()), HzPolicy::getPolicyType, policy.getPolicyType())
                .like(StringUtils.isNotEmpty(policy.getPublishDept()), HzPolicy::getPublishDept, policy.getPublishDept())
                .eq(StringUtils.isNotEmpty(policy.getStatus()), HzPolicy::getStatus, policy.getStatus())
                .orderByDesc(HzPolicy::getCreateTime);
        return this.list(wrapper);
    }

    /**
     * 新增政策文件
     *
     * @param policy 政策文件
     * @return 结果
     */
    @Override
    public int insertPolicy(HzPolicy policy) {
        return this.save(policy) ? 1 : 0;
    }

    /**
     * 修改政策文件
     *
     * @param policy 政策文件
     * @return 结果
     */
    @Override
    public int updatePolicy(HzPolicy policy) {
        return this.updateById(policy) ? 1 : 0;
    }

    /**
     * 批量删除政策文件
     *
     * @param policyIds 需要删除的政策文件主键
     * @return 结果
     */
    @Override
    public int deletePolicyByIds(Long[] policyIds) {
        return this.removeByIds(Arrays.asList(policyIds)) ? 1 : 0;
    }

    /**
     * 删除政策文件信息
     *
     * @param policyId 政策文件主键
     * @return 结果
     */
    @Override
    public int deletePolicyById(Long policyId) {
        return this.removeById(policyId) ? 1 : 0;
    }

    /**
     * 增加浏览次数
     *
     * @param policyId 政策文件主键
     * @return 结果
     */
    @Override
    public int increaseViewCount(Long policyId) {
        LambdaUpdateWrapper<HzPolicy> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(HzPolicy::getPolicyId, policyId)
                .setSql("view_count = view_count + 1");
        return this.update(wrapper) ? 1 : 0;
    }

    @Override
    public IPage<HzPolicy> selectPolicyPage(Page<HzPolicy> page, HzPolicy policy) {
        LambdaQueryWrapper<HzPolicy> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StringUtils.isNotEmpty(policy.getPolicyTitle()), HzPolicy::getPolicyTitle, policy.getPolicyTitle())
                .eq(StringUtils.isNotEmpty(policy.getPolicyType()), HzPolicy::getPolicyType, policy.getPolicyType())
                .like(StringUtils.isNotEmpty(policy.getPublishDept()), HzPolicy::getPublishDept, policy.getPublishDept())
                .eq(StringUtils.isNotEmpty(policy.getStatus()), HzPolicy::getStatus, policy.getStatus())
                .orderByDesc(HzPolicy::getCreateTime);
        return this.page(page, wrapper);
    }
}
