package com.ruoyi.system.service;

import com.ruoyi.system.domain.HzBlacklist;

/**
 * 黑名单Service接口
 *
 * @author ruoyi
 */
public interface IHzBlacklistService {
    /**
     * 查询黑名单
     *
     * @param blacklistId 黑名单ID
     * @return 黑名单
     */
    HzBlacklist selectBlacklistById(Long blacklistId);

    /**
     * 根据租户ID查询是否在黑名单中
     *
     * @param tenantId 租户ID
     * @return 黑名单记录
     */
    HzBlacklist selectBlacklistByTenantId(Long tenantId);

    /**
     * 根据身份证号查询是否在黑名单中
     *
     * @param idCard 身份证号
     * @return 黑名单记录
     */
    HzBlacklist selectBlacklistByIdCard(String idCard);

    /**
     * 新增黑名单
     *
     * @param blacklist 黑名单
     * @return 结果
     */
    int insertBlacklist(HzBlacklist blacklist);

    /**
     * 修改黑名单
     *
     * @param blacklist 黑名单
     * @return 结果
     */
    int updateBlacklist(HzBlacklist blacklist);

    /**
     * 解除黑名单
     *
     * @param blacklistId 黑名单ID
     * @param removeReason 解除原因
     * @return 结果
     */
    int removeBlacklist(Long blacklistId, String removeReason);
}
