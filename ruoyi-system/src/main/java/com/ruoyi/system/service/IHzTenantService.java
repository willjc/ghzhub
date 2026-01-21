package com.ruoyi.system.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.ruoyi.system.domain.HzTenant;

import java.util.List;

/**
 * 租户信息Service接口
 *
 * @author ruoyi
 */
public interface IHzTenantService {
    /**
     * 查询租户信息
     *
     * @param tenantId 租户ID
     * @return 租户信息
     */
    HzTenant selectTenantById(Long tenantId);

    /**
     * 根据用户ID查询租户信息
     *
     * @param userId 用户ID
     * @return 租户信息
     */
    HzTenant selectTenantByUserId(Long userId);

    /**
     * 根据身份证号查询租户信息
     *
     * @param idCard 身份证号
     * @return 租户信息
     */
    HzTenant selectTenantByIdCard(String idCard);

    /**
     * 查询租户列表
     *
     * @param tenant 租户信息
     * @return 租户信息集合
     */
    List<HzTenant> selectTenantList(HzTenant tenant);

    /**
     * 分页查询租户信息
     *
     * @param tenant 租户信息
     * @param pageNum 页码
     * @param pageSize 每页数量
     * @return 租户信息集合
     */
    IPage<HzTenant> selectTenantPage(HzTenant tenant, int pageNum, int pageSize);

    /**
     * 新增租户信息
     *
     * @param tenant 租户信息
     * @return 结果
     */
    int insertTenant(HzTenant tenant);

    /**
     * 修改租户信息
     *
     * @param tenant 租户信息
     * @return 结果
     */
    int updateTenant(HzTenant tenant);

    /**
     * 删除租户信息
     *
     * @param tenantId 租户ID
     * @return 结果
     */
    int deleteTenantById(Long tenantId);
}
