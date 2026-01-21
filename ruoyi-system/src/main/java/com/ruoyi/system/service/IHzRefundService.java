package com.ruoyi.system.service;

import com.ruoyi.system.domain.HzRefundApplyVO;

import java.util.List;

/**
 * 退款申请Service接口
 *
 * @author ruoyi
 */
public interface IHzRefundService {

    /**
     * 查询退款申请列表（包含关联信息）
     *
     * @param refundNo 退款编号
     * @param contractNo 合同编号
     * @param refundStatus 退款状态
     * @return 退款申请VO集合
     */
    List<HzRefundApplyVO> selectRefundList(String refundNo, String contractNo, String refundStatus);

    /**
     * 查询退款申请详情
     *
     * @param refundId 退款申请ID
     * @return 退款申请VO
     */
    HzRefundApplyVO selectRefundById(Long refundId);

    /**
     * 审核退款申请
     *
     * @param refundId 退款申请ID
     * @param approveStatus 审批状态 (1=通过, 2=驳回)
     * @param approveOpinion 审批意见
     * @return 结果
     */
    int auditRefund(Long refundId, String approveStatus, String approveOpinion);

    /**
     * 删除退款申请
     *
     * @param refundId 退款申请ID
     * @return 结果
     */
    int deleteRefundById(Long refundId);
}
