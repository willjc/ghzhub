package com.ruoyi.system.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.ruoyi.system.domain.HzTransaction;

import java.util.List;

/**
 * 交易流水Service接口
 *
 * @author ruoyi
 */
public interface IHzTransactionService {

    /**
     * 查询交易流水
     *
     * @param transactionId 交易流水主键
     * @return 交易流水
     */
    public HzTransaction selectTransactionById(Long transactionId);

    /**
     * 根据交易流水号查询交易流水
     *
     * @param transactionNo 交易流水号
     * @return 交易流水
     */
    public HzTransaction selectTransactionByTransactionNo(String transactionNo);

    /**
     * 根据租户ID查询交易流水列表
     *
     * @param tenantId 租户ID
     * @return 交易流水列表
     */
    public List<HzTransaction> selectTransactionListByTenantId(Long tenantId);

    /**
     * 根据账单ID查询交易流水列表
     *
     * @param billId 账单ID
     * @return 交易流水列表
     */
    public List<HzTransaction> selectTransactionListByBillId(Long billId);

    /**
     * 分页查询交易流水列表
     *
     * @param transaction 交易流水
     * @param pageNum 页码
     * @param pageSize 每页条数
     * @return 交易流水分页数据
     */
    public IPage<HzTransaction> selectTransactionPage(HzTransaction transaction, int pageNum, int pageSize);

    /**
     * 新增交易流水
     *
     * @param transaction 交易流水
     * @return 结果
     */
    public int insertTransaction(HzTransaction transaction);

    /**
     * 修改交易流水
     *
     * @param transaction 交易流水
     * @return 结果
     */
    public int updateTransaction(HzTransaction transaction);

    /**
     * 删除交易流水
     *
     * @param transactionId 交易流水主键
     * @return 结果
     */
    public int deleteTransactionById(Long transactionId);

    /**
     * 生成交易流水号
     *
     * @return 交易流水号
     */
    public String generateTransactionNo();
}
