package com.ruoyi.system.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.ruoyi.system.domain.HzQualificationAppeal;
import com.ruoyi.system.domain.HzQualificationAppealVO;

import java.util.List;

/**
 * 资格申诉Service接口
 *
 * @author ruoyi
 */
public interface IHzQualificationAppealService {
    /**
     * 查询资格申诉
     *
     * @param appealId 申诉ID
     * @return 资格申诉
     */
    HzQualificationAppeal selectAppealById(Long appealId);

    /**
     * 查询资格申诉详情（管理端，包含用户信息和处理人信息）
     *
     * @param appealId 申诉ID
     * @return 资格申诉VO
     */
    HzQualificationAppealVO selectAppealVOById(Long appealId);

    /**
     * 根据用户ID查询申诉VO列表（H5端，包含用户信息）
     *
     * @param userId 用户ID
     * @return 申诉VO列表
     */
    List<HzQualificationAppealVO> selectAppealVOListByUserId(Long userId);

    /**
     * 根据租户ID查询申诉列表
     *
     * @param tenantId 租户ID（实际是用户ID）
     * @return 申诉列表
     */
    List<HzQualificationAppeal> selectAppealListByTenantId(Long tenantId);

    /**
     * 分页查询资格申诉（管理端，包含用户信息）
     *
     * @param appeal 资格申诉
     * @param pageNum 页码
     * @param pageSize 每页数量
     * @return 资格申诉VO集合
     */
    IPage<HzQualificationAppealVO> selectAppealVOPage(HzQualificationAppeal appeal, int pageNum, int pageSize);

    /**
     * 分页查询资格申诉
     *
     * @param appeal 资格申诉
     * @param pageNum 页码
     * @param pageSize 每页数量
     * @return 资格申诉集合
     */
    IPage<HzQualificationAppeal> selectAppealPage(HzQualificationAppeal appeal, int pageNum, int pageSize);

    /**
     * 新增资格申诉
     *
     * @param appeal 资格申诉
     * @return 结果
     */
    int insertAppeal(HzQualificationAppeal appeal);

    /**
     * 修改资格申诉
     *
     * @param appeal 资格申诉
     * @return 结果
     */
    int updateAppeal(HzQualificationAppeal appeal);

    /**
     * 审核资格申诉
     *
     * @param appealId 申诉ID
     * @param handleResult 处理结果（1:通过，2:不通过）
     * @param handleOpinion 处理意见
     * @param newEducation 新学历（用户申诉的学历）
     * @return 结果
     */
    int handleAppeal(Long appealId, String handleResult, String handleOpinion, String newEducation);

    /**
     * 删除资格申诉
     *
     * @param appealId 申诉ID
     * @return 结果
     */
    int deleteAppealById(Long appealId);
}
