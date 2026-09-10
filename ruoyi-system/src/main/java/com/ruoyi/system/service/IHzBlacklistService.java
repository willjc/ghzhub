package com.ruoyi.system.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.ruoyi.system.domain.HzBlacklist;

import java.util.List;

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
     * 分页查询黑名单（管理端，含手机号回填）
     *
     * @param query 查询条件（tenantName 姓名 / phone 手机号 / idCard 身份证 / status 状态）
     * @param pageNum 页码
     * @param pageSize 每页数量
     * @return 分页结果
     */
    IPage<HzBlacklist> selectBlacklistPage(HzBlacklist query, int pageNum, int pageSize);

    /**
     * 查询黑名单列表（管理端导出用，含手机号回填）
     *
     * @param query 查询条件
     * @return 列表
     */
    List<HzBlacklist> selectBlacklistList(HzBlacklist query);

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
