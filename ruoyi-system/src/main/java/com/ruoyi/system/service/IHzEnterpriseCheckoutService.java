package com.ruoyi.system.service;

import com.ruoyi.system.domain.HzEnterpriseCheckout;

import java.util.List;

/**
 * 企业退租申请Service接口
 *
 * @author ruoyi
 */
public interface IHzEnterpriseCheckoutService {

    /**
     * 查询企业退租申请列表
     *
     * @param checkout 企业退租申请
     * @return 企业退租申请集合
     */
    List<HzEnterpriseCheckout> selectCheckoutList(HzEnterpriseCheckout checkout);

    /**
     * 查询企业退租申请详情
     *
     * @param checkoutId 退租申请ID
     * @return 企业退租申请
     */
    HzEnterpriseCheckout selectCheckoutById(Long checkoutId);

    /**
     * 新增企业退租申请
     *
     * @param checkout 企业退租申请
     * @return 结果
     */
    int insertCheckout(HzEnterpriseCheckout checkout);

    /**
     * 修改企业退租申请
     *
     * @param checkout 企业退租申请
     * @return 结果
     */
    int updateCheckout(HzEnterpriseCheckout checkout);

    /**
     * 批量删除企业退租申请
     *
     * @param checkoutIds 需要删除的退租申请ID数组
     * @return 结果
     */
    int deleteCheckoutByIds(Long[] checkoutIds);

    /**
     * 删除企业退租申请信息
     *
     * @param checkoutId 退租申请ID
     * @return 结果
     */
    int deleteCheckoutById(Long checkoutId);

    /**
     * 根据账单ID查询退租申请
     *
     * @param billId 账单ID
     * @return 企业退租申请
     */
    HzEnterpriseCheckout selectCheckoutByBillId(Long billId);

    /**
     * 根据联系电话查询退租申请列表（用户端使用）
     *
     * @param contactPhone 联系电话
     * @return 企业退租申请集合
     */
    List<HzEnterpriseCheckout> selectCheckoutsByContactPhone(String contactPhone);

    /**
     * 生成退租申请编号
     *
     * @return 退租申请编号 (格式: QYTZ + yyyyMMdd + 6位序号)
     */
    String generateCheckoutNo();

    /**
     * 提交退租申请
     *
     * @param billId 账单ID
     * @return 结果
     */
    int submitCheckout(Long billId);
}
