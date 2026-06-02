package com.ruoyi.system.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.ruoyi.system.domain.HzHouseStatusAudit;

import java.util.List;

/**
 * 房源状态变更审批Service接口
 *
 * @author ruoyi
 */
public interface IHzHouseStatusAuditService extends IService<HzHouseStatusAudit> {

    /**
     * 物业提交状态变更申请
     *
     * @param houseId 房源ID
     * @param targetStatus 目标状态
     * @return 结果
     */
    int submitStatusChange(Long houseId, String targetStatus);

    /**
     * 批量提交状态变更申请
     *
     * @param houseIds 房源ID列表
     * @param targetStatus 目标状态
     * @return 提交数量
     */
    int batchSubmitStatusChange(List<Long> houseIds, String targetStatus);

    /**
     * 管理方审批状态变更
     *
     * @param auditId 审批ID
     * @param approveStatus 审批状态(1=通过,2=驳回)
     * @param opinion 审批意见
     * @return 结果
     */
    int approveStatusChange(Long auditId, String approveStatus, String opinion);

    /**
     * 分页查询审批列表
     *
     * @param audit 查询条件
     * @param pageNum 页码
     * @param pageSize 每页数量
     * @return 分页数据
     */
    IPage<HzHouseStatusAudit> selectAuditPage(HzHouseStatusAudit audit, int pageNum, int pageSize);

    /**
     * 查询审批详情
     *
     * @param auditId 审批ID
     * @return 审批详情
     */
    HzHouseStatusAudit selectAuditById(Long auditId);
}
