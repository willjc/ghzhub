package com.ruoyi.system.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.ruoyi.system.domain.HzSubsidyApply;

import java.util.List;

/**
 * 代购补贴申请 Service 接口
 *
 * @author ruoyi
 */
public interface IHzSubsidyApplyService extends IService<HzSubsidyApply>
{
    IPage<HzSubsidyApply> selectApplyPage(HzSubsidyApply apply, int pageNum, int pageSize);

    List<HzSubsidyApply> selectApplyList(HzSubsidyApply apply);

    HzSubsidyApply selectApplyById(Long applyId);

    /**
     * 租户提交申请（H5）
     * 要求 commitmentId 已就绪（前端先调签署接口拿到 ID）
     */
    int submitApply(HzSubsidyApply apply);

    /**
     * 审批：1=通过 2=驳回
     */
    int approveApply(Long applyId, String approveStatus, String approveRemark);

    int deleteApplyByIds(Long[] applyIds);

    List<HzSubsidyApply> selectMyApplies(Long tenantId, String approveStatus);
}
