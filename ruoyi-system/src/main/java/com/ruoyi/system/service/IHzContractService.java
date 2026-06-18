package com.ruoyi.system.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.ruoyi.system.domain.HzContract;

import java.util.List;

/**
 * 合同Service接口
 *
 * @author ruoyi
 */
public interface IHzContractService {
    /**
     * 查询合同
     *
     * @param contractId 合同ID
     * @return 合同
     */
    HzContract selectContractById(Long contractId);

    /**
     * 根据合同编号查询合同
     *
     * @param contractNo 合同编号
     * @return 合同
     */
    HzContract selectContractByContractNo(String contractNo);

    /**
     * 根据租户ID查询合同列表
     *
     * @param tenantId 租户ID
     * @return 合同列表
     */
    List<HzContract> selectContractListByTenantId(Long tenantId);

    /**
     * 获取租户的待退租合同列表（履行中且无进行中的退租申请）
     *
     * @param tenantId 租户ID
     * @return 待退租合同列表
     */
    List<HzContract> selectCheckoutableContractList(Long tenantId);


    /**
     * 根据房源ID查询当前有效合同
     *
     * @param houseId 房源ID
     * @return 合同
     */
    HzContract selectValidContractByHouseId(Long houseId);

    /**
     * 查询合同列表
     *
     * @param contract 合同
     * @return 合同列表
     */
    List<HzContract> selectContractList(HzContract contract);

    /**
     * 按合同ID列表批量查询合同（用于勾选导出）
     *
     * @param contractIds 合同ID数组
     * @return 合同列表
     */
    List<HzContract> selectContractListByIds(Long[] contractIds);

    /**
     * 分页查询合同
     *
     * @param contract 合同
     * @param pageNum 页码
     * @param pageSize 每页数量
     * @return 合同集合
     */
    IPage<HzContract> selectContractPage(HzContract contract, int pageNum, int pageSize);

    /**
     * 新增合同
     *
     * @param contract 合同
     * @return 结果
     */
    int insertContract(HzContract contract);

    /**
     * 修改合同
     *
     * @param contract 合同
     * @return 结果
     */
    int updateContract(HzContract contract);

    /**
     * 删除合同
     *
     * @param contractId 合同ID
     * @return 结果
     */
    int deleteContractById(Long contractId);

    /**
     * 生成合同编号
     *
     * @return 合同编号
     */
    String generateContractNo();

    /**
     * 原子锁定房源并创建合同（事务内执行，保证一致性）
     *
     * @param contract 合同
     * @return 结果
     */
    int createContractWithLockHouse(HzContract contract);

    /**
     * 合同失效并释放房源（事务内执行，保证一致性）
     *
     * @param contractId 合同ID
     * @param houseId 房源ID
     */
    void expireContractAndReleaseHouse(Long contractId, Long houseId);

    /**
     * 入住超时自动解约（DB 部分，事务内执行）。
     * 包含：合同状态=5、释放房源、软删未办理的入住单、写 hz_checkout_apply（applyStatus='5'）、
     *      写 hz_checkout_record（refundStatus='0' 待退还）、发站内消息。
     * 不调用微信退款 API，由调用方在事务外发起退款，再调 markCheckoutRecordRefunded 更新记录。
     *
     * @param contractId   合同ID
     * @param totalRefund  应退总额（押金 + 首期租金）
     * @param depositAmt   押金金额（用于 hz_checkout_apply.deposit_refund 字段）
     * @return 新建的 hz_checkout_apply.apply_id；如合同已被处理或不满足条件返回 null
     */
    Long createAutoCancelCheckoutApplyTx(Long contractId, java.math.BigDecimal totalRefund, java.math.BigDecimal depositAmt);

    /**
     * 标记退款已完成（事务内执行）。
     *
     * @param applyId       hz_checkout_apply.apply_id
     * @param paymentRemark 备注（含退款单号）
     */
    void markCheckoutRecordRefunded(Long applyId, String paymentRemark);

    /**
     * 标记退款失败（事务内执行），refundStatus 仍保持 0=待退还，便于管理员在退款管理页面手动重试。
     *
     * @param applyId       hz_checkout_apply.apply_id
     * @param paymentRemark 失败原因
     */
    void markCheckoutRecordRefundFailed(Long applyId, String paymentRemark);
}
