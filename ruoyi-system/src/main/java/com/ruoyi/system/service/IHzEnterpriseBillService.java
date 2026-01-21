package com.ruoyi.system.service;

import com.ruoyi.system.domain.HzEnterpriseBill;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * 企业账单Service接口
 *
 * @author ruoyi
 */
public interface IHzEnterpriseBillService {

    /**
     * 查询企业账单列表
     *
     * @param enterpriseBill 企业账单
     * @return 企业账单集合
     */
    List<HzEnterpriseBill> selectEnterpriseBillList(HzEnterpriseBill enterpriseBill);

    /**
     * 查询企业账单详情
     *
     * @param billId 账单ID
     * @return 企业账单
     */
    HzEnterpriseBill selectEnterpriseBillById(Long billId);

    /**
     * 新增企业账单
     *
     * @param enterpriseBill 企业账单
     * @return 结果
     */
    int insertEnterpriseBill(HzEnterpriseBill enterpriseBill);

    /**
     * 修改企业账单
     *
     * @param enterpriseBill 企业账单
     * @return 结果
     */
    int updateEnterpriseBill(HzEnterpriseBill enterpriseBill);

    /**
     * 批量删除企业账单
     *
     * @param billIds 需要删除的账单ID数组
     * @return 结果
     */
    int deleteEnterpriseBillByIds(Long[] billIds);

    /**
     * 删除企业账单信息
     *
     * @param billId 账单ID
     * @return 结果
     */
    int deleteEnterpriseBillById(Long billId);

    /**
     * 根据联系方式查询账单列表（用户端使用）
     *
     * @param contactPhone 联系方式
     * @return 企业账单集合
     */
    List<HzEnterpriseBill> selectBillsByContactPhone(String contactPhone);

    /**
     * 根据批次ID查询账单
     *
     * @param batchId 批次ID
     * @return 企业账单
     */
    HzEnterpriseBill selectBillByBatchId(Long batchId);

    /**
     * 计算月数（不满一个月算一个月）
     *
     * @param checkInDate  入驻日期
     * @param checkOutDate 截止日期
     * @return 月数
     */
    int calculateMonths(Date checkInDate, Date checkOutDate);

    /**
     * 生成账单编号
     *
     * @return 账单编号
     */
    String generateBillNo();

    /**
     * 审核账单
     *
     * @param billId   账单ID
     * @param approved 是否通过
     * @param approveBy 审核人
     * @return 结果
     */
    int approveBill(Long billId, boolean approved, String approveBy);

    /**
     * 支付账单
     *
     * @param billId        账单ID
     * @param payMethod     支付方式
     * @param transactionNo 交易流水号
     * @return 结果
     */
    int payBill(Long billId, String payMethod, String transactionNo);

    /**
     * 管理端线下支付账单
     *
     * @param billId     账单ID
     * @param payMethod  支付方式
     * @param payVoucher 支付凭证文件路径
     * @return 结果
     */
    int adminPayBill(Long billId, String payMethod, String payVoucher);

    /**
     * 提交入住办理（上传人员名单）
     *
     * @param billId        账单ID
     * @param personnelFile 人员名单文件路径
     * @return 结果
     */
    int submitCheckin(Long billId, String personnelFile);
}
