package com.ruoyi.system.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ruoyi.system.domain.HzBill;
import com.ruoyi.system.domain.HzBillVO;

import java.util.List;

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
     * 根据用户ID查询账单列表（包含关联信息）
     *
     * @param userId 用户ID
     * @param billType 账单类型（可选）
     * @param billStatus 账单状态（可选）
     * @return 账单VO列表
     */
    List<HzBillVO> selectBillVOByUserId(@Param("userId") Long userId,
                                        @Param("billType") String billType,
                                        @Param("billStatus") String billStatus);
}
