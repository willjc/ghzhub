package com.ruoyi.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ruoyi.system.domain.HzEnterprise;

import java.util.List;

/**
 * 企业客户Service接口
 *
 * @author ruoyi
 */
public interface IHzEnterpriseService extends IService<HzEnterprise> {

    /**
     * 查询企业客户列表
     *
     * @param enterprise 企业客户
     * @return 企业客户集合
     */
    List<HzEnterprise> selectEnterpriseList(HzEnterprise enterprise);

    /**
     * 查询企业客户详情
     *
     * @param enterpriseId 企业客户ID
     * @return 企业客户
     */
    HzEnterprise selectEnterpriseById(Long enterpriseId);

    /**
     * 新增企业客户
     *
     * @param enterprise 企业客户
     * @return 结果
     */
    int insertEnterprise(HzEnterprise enterprise);

    /**
     * 修改企业客户
     *
     * @param enterprise 企业客户
     * @return 结果
     */
    int updateEnterprise(HzEnterprise enterprise);

    /**
     * 删除企业客户
     *
     * @param enterpriseId 企业客户ID
     * @return 结果
     */
    int deleteEnterpriseById(Long enterpriseId);

    /**
     * 批量删除企业客户
     *
     * @param enterpriseIds 企业客户ID数组
     * @return 结果
     */
    int deleteEnterpriseByIds(Long[] enterpriseIds);
}
