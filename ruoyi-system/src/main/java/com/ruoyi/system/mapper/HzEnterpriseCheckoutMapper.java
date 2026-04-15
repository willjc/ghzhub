package com.ruoyi.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.ruoyi.system.domain.HzEnterpriseCheckout;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 企业退租申请Mapper接口
 *
 * @author ruoyi
 */
public interface HzEnterpriseCheckoutMapper extends BaseMapper<HzEnterpriseCheckout> {

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
     * 删除企业退租申请
     *
     * @param checkoutId 退租申请ID
     * @return 结果
     */
    int deleteCheckoutById(Long checkoutId);

    /**
     * 批量删除企业退租申请
     *
     * @param checkoutIds 需要删除的退租申请ID数组
     * @return 结果
     */
    int deleteCheckoutByIds(Long[] checkoutIds);

    /**
     * 根据账单ID查询退租申请
     *
     * @param billId 账单ID
     * @return 企业退租申请
     */
    HzEnterpriseCheckout selectCheckoutByBillId(@Param("billId") Long billId);

    /**
     * 根据联系电话查询退租申请列表（用户端使用）
     *
     * @param contactPhone 联系电话
     * @return 企业退租申请集合
     */
    List<HzEnterpriseCheckout> selectCheckoutsByContactPhone(@Param("contactPhone") String contactPhone);

    /**
     * 分页查询企业退租申请列表
     */
    IPage<HzEnterpriseCheckout> selectCheckoutPage(IPage<HzEnterpriseCheckout> page, @Param("entity") HzEnterpriseCheckout checkout);
}
