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
     * 审核资格申诉（双材料独立审核）
     *
     * 入参 appeal 中需提供：appealId、educationAuditStatus、educationAuditOpinion、
     * socialAuditStatus、socialAuditOpinion 等字段。任一侧状态为 null 表示「本次不动该侧」。
     * 同时要求传入 newEducation（仅当学历审核通过时回写到 hz_user.education）。
     *
     * 兼容旧字段：方法内部会同步更新 handle_result/handle_opinion（取两侧的「最差状态」做摘要：
     * 任一驳回 → 整体驳回；都通过 → 整体通过；其他 → 待处理）。
     *
     * @param appeal 申诉对象（需含 appealId 与四个新审核字段）
     * @param newEducation 学历审核通过时回写到用户表的新学历值（可为空）
     * @return 1=成功
     */
    int handleAppealSplit(HzQualificationAppeal appeal, String newEducation);

    /**
     * 删除资格申诉
     *
     * @param appealId 申诉ID
     * @return 结果
     */
    int deleteAppealById(Long appealId);

    /**
     * 判断该用户是否存在「学历」已通过的申诉
     * （供资格校验对学历项做人工豁免判定）
     */
    boolean existsPassedEducationAppeal(Long userId);

    /**
     * 判断该用户是否存在「社保」已通过的申诉
     * （供资格校验对社保项做人工豁免判定）
     */
    boolean existsPassedSocialAppeal(Long userId);
}
