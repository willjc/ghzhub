package com.ruoyi.system.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.ruoyi.system.domain.HzPayment;

import java.util.List;

/**
 * 支付记录Service接口
 *
 * @author ruoyi
 */
public interface IHzPaymentService {

    /**
     * 查询支付记录
     *
     * @param paymentId 支付记录主键
     * @return 支付记录
     */
    public HzPayment selectPaymentById(Long paymentId);

    /**
     * 根据支付流水号查询支付记录
     *
     * @param paymentNo 支付流水号
     * @return 支付记录
     */
    public HzPayment selectPaymentByPaymentNo(String paymentNo);

    /**
     * 根据账单ID查询支付记录列表
     *
     * @param billId 账单ID
     * @return 支付记录列表
     */
    public List<HzPayment> selectPaymentListByBillId(Long billId);

    /**
     * 分页查询支付记录列表
     *
     * @param payment 支付记录
     * @param pageNum 页码
     * @param pageSize 每页条数
     * @return 支付记录分页数据
     */
    public IPage<HzPayment> selectPaymentPage(HzPayment payment, int pageNum, int pageSize);

    /**
     * 新增支付记录
     *
     * @param payment 支付记录
     * @return 结果
     */
    public int insertPayment(HzPayment payment);

    /**
     * 修改支付记录
     *
     * @param payment 支付记录
     * @return 结果
     */
    public int updatePayment(HzPayment payment);

    /**
     * 删除支付记录
     *
     * @param paymentId 支付记录主键
     * @return 结果
     */
    public int deletePaymentById(Long paymentId);

    /**
     * 生成支付流水号
     *
     * @return 支付流水号
     */
    public String generatePaymentNo();

    /**
     * 更新支付状态
     *
     * @param paymentId 支付记录ID
     * @param paymentStatus 支付状态
     * @return 结果
     */
    public int updatePaymentStatus(Long paymentId, String paymentStatus);
}
