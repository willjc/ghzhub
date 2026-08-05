package com.ruoyi.system.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ruoyi.system.domain.HzBill;
import com.ruoyi.system.domain.HzBillVO;

/**
 * 账单Mapper接口
 *
 * @author ruoyi
 */
@Mapper
public interface HzBillMapper extends BaseMapper<HzBill> {

    /**
     * 查询账单列表（包含关联信息）
     *
     * @param bill 账单查询条件
     * @return 账单VO列表
     */
    List<HzBillVO> selectBillVOList(@Param("bill") HzBill bill);

    /**
     * 分页查询账单列表（包含关联信息）
     *
     * @param page 分页对象
     * @param bill 账单查询条件
     * @return 账单VO分页数据
     */
    IPage<HzBillVO> selectBillVOPage(Page<HzBillVO> page, @Param("bill") HzBill bill);

    /**
     * 查询账单详情（包含关联信息）
     *
     * @param billId 账单ID
     * @return 账单VO
     */
    HzBillVO selectBillVOById(@Param("billId") Long billId);

    /**
     * 按账单ID列表查询账单VO（包含关联信息，用于勾选导出）
     *
     * @param billIds 账单ID数组
     * @return 账单VO列表
     */
    List<HzBillVO> selectBillVOListByIds(@Param("billIds") Long[] billIds);

    /**
     * 根据用户ID查询账单列表（包含关联信息）
     *
     * @param userId 用户ID
     * @param billType 账单类型（可选）
     * @param billStatus 账单状态（可选）
     * @param projectType 项目类型（可选，1:人才公寓 2:保租房 3:市场租赁），为空则不过滤
     * @return 账单VO列表
     */
    List<HzBillVO> selectBillVOByUserId(@Param("userId") Long userId,
                                        @Param("billType") String billType,
                                        @Param("billStatus") String billStatus,
                                        @Param("projectType") String projectType);

    /**
     * 查询押金账单（微信已支付），绕过全局逻辑删除。
     * 用于合同失效/退租后原路退款场景——此时账单已被软删除(del_flag=2)，
     * 走 BaseMapper 会被全局逻辑删除过滤掉，故用原生 SQL 忽略 del_flag。
     *
     * @param contractId 合同ID
     * @return 押金账单（不存在返回 null）
     */
    @Select("SELECT * FROM hz_bill WHERE contract_id = #{contractId} AND bill_type = '1' " +
            "AND pay_method = 'wechat' AND bill_status = '1' ORDER BY pay_time DESC LIMIT 1")
    HzBill selectWechatDepositBillForRefund(@Param("contractId") Long contractId);

    /**
     * 查询已付租金账单（微信已支付、有交易号），按支付时间升序，绕过全局逻辑删除。
     * 用于合同失效/退租后原路退款场景。
     *
     * @param contractId 合同ID
     * @return 租金账单列表
     */
    @Select("SELECT * FROM hz_bill WHERE contract_id = #{contractId} AND bill_type = '2' " +
            "AND pay_method = 'wechat' AND bill_status = '1' AND transaction_no IS NOT NULL " +
            "ORDER BY pay_time ASC")
    List<HzBill> selectWechatRentBillsForRefund(@Param("contractId") Long contractId);
}
