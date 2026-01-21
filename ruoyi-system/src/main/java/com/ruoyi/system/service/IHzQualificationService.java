package com.ruoyi.system.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.ruoyi.system.domain.HzQualification;

import java.util.List;

/**
 * 资格审核Service接口
 *
 * @author ruoyi
 */
public interface IHzQualificationService {
    /**
     * 查询资格审核
     *
     * @param qualificationId 资格ID
     * @return 资格审核
     */
    HzQualification selectQualificationById(Long qualificationId);

    /**
     * 根据租户ID查询资格审核列表
     *
     * @param tenantId 租户ID
     * @return 资格审核列表
     */
    List<HzQualification> selectQualificationListByTenantId(Long tenantId);

    /**
     * 根据租户ID和申请类型查询资格审核
     *
     * @param tenantId 租户ID
     * @param applyType 申请类型
     * @return 资格审核
     */
    HzQualification selectQualificationByTenantIdAndType(Long tenantId, String applyType);

    /**
     * 查询资格审核列表
     *
     * @param qualification 资格审核
     * @return 资格审核列表
     */
    List<HzQualification> selectQualificationList(HzQualification qualification);

    /**
     * 分页查询资格审核
     *
     * @param qualification 资格审核
     * @param pageNum 页码
     * @param pageSize 每页数量
     * @return 资格审核集合
     */
    IPage<HzQualification> selectQualificationPage(HzQualification qualification, int pageNum, int pageSize);

    /**
     * 新增资格审核
     *
     * @param qualification 资格审核
     * @return 结果
     */
    int insertQualification(HzQualification qualification);

    /**
     * 修改资格审核
     *
     * @param qualification 资格审核
     * @return 结果
     */
    int updateQualification(HzQualification qualification);

    /**
     * 删除资格审核
     *
     * @param qualificationId 资格ID
     * @return 结果
     */
    int deleteQualificationById(Long qualificationId);
}
