package com.ruoyi.system.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.ruoyi.system.domain.HzContractFiling;

import java.util.List;

/**
 * 合同备案 Service 接口
 *
 * @author ruoyi
 */
public interface IHzContractFilingService extends IService<HzContractFiling>
{
    /**
     * 管理端分页查询
     */
    IPage<HzContractFiling> selectFilingPage(HzContractFiling filing, int pageNum, int pageSize);

    /**
     * 管理端列表（不分页，用于导出）
     */
    List<HzContractFiling> selectFilingList(HzContractFiling filing);

    /**
     * 按 ID 查询
     */
    HzContractFiling selectFilingById(Long filingId);

    /**
     * 租户提交备案（H5 端）
     */
    int submitFiling(HzContractFiling filing);

    /**
     * 审批通过/驳回
     * @param approveStatus 1=通过 2=驳回
     */
    int approveFiling(Long filingId, String approveStatus, String approveRemark);

    /**
     * 删除（软删除）
     */
    int deleteFilingByIds(Long[] filingIds);

    /**
     * 我的备案列表（H5 端，按租户）
     */
    List<HzContractFiling> selectMyFilings(Long tenantId, String approveStatus);
}
