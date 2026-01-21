package com.ruoyi.gangzhu.policy.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ruoyi.gangzhu.policy.domain.HzPolicy;

import java.util.List;

/**
 * 政策文件Service接口
 *
 * @author ruoyi
 */
public interface IHzPolicyService extends IService<HzPolicy> {

    /**
     * 查询政策文件列表
     *
     * @param policy 政策文件
     * @return 政策文件集合
     */
    List<HzPolicy> selectPolicyList(HzPolicy policy);

    /**
     * 新增政策文件
     *
     * @param policy 政策文件
     * @return 结果
     */
    int insertPolicy(HzPolicy policy);

    /**
     * 修改政策文件
     *
     * @param policy 政策文件
     * @return 结果
     */
    int updatePolicy(HzPolicy policy);

    /**
     * 批量删除政策文件
     *
     * @param policyIds 需要删除的政策文件主键集合
     * @return 结果
     */
    int deletePolicyByIds(Long[] policyIds);

    /**
     * 删除政策文件信息
     *
     * @param policyId 政策文件主键
     * @return 结果
     */
    int deletePolicyById(Long policyId);

    /**
     * 增加浏览次数
     *
     * @param policyId 政策文件主键
     * @return 结果
     */
    int increaseViewCount(Long policyId);
}
