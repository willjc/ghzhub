package com.ruoyi.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ruoyi.system.domain.HzEnterpriseBill;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 企业账单Mapper接口
 *
 * @author ruoyi
 */
public interface HzEnterpriseBillMapper extends BaseMapper<HzEnterpriseBill> {

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
     * 删除企业账单
     *
     * @param billId 账单ID
     * @return 结果
     */
    int deleteEnterpriseBillById(Long billId);

    /**
     * 批量删除企业账单
     *
     * @param billIds 需要删除的账单ID数组
     * @return 结果
     */
    int deleteEnterpriseBillByIds(Long[] billIds);

    /**
     * 根据联系方式查询账单列表（用户端使用）
     *
     * @param contactPhone 联系方式
     * @return 企业账单集合
     */
    List<HzEnterpriseBill> selectBillsByContactPhone(@Param("contactPhone") String contactPhone);

    /**
     * 根据批次ID查询账单
     *
     * @param batchId 批次ID
     * @return 企业账单
     */
    HzEnterpriseBill selectBillByBatchId(@Param("batchId") Long batchId);
}
