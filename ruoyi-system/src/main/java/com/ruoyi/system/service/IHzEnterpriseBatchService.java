package com.ruoyi.system.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ruoyi.system.domain.HzEnterpriseBatch;

import java.util.List;
import java.util.Map;

/**
 * 企业批次Service接口
 *
 * @author ruoyi
 */
public interface IHzEnterpriseBatchService {

    /**
     * 查询企业批次列表
     *
     * @param enterpriseBatch 企业批次
     * @return 企业批次集合
     */
    List<HzEnterpriseBatch> selectEnterpriseBatchList(HzEnterpriseBatch enterpriseBatch);

    /**
     * 查询企业批次详情
     *
     * @param batchId 企业批次主键
     * @return 企业批次
     */
    HzEnterpriseBatch selectEnterpriseBatchById(Long batchId);

    /**
     * 新增企业批次
     *
     * @param enterpriseBatch 企业批次
     * @return 结果
     */
    int insertEnterpriseBatch(HzEnterpriseBatch enterpriseBatch);

    /**
     * 修改企业批次
     *
     * @param enterpriseBatch 企业批次
     * @return 结果
     */
    int updateEnterpriseBatch(HzEnterpriseBatch enterpriseBatch);

    /**
     * 删除企业批次
     *
     * @param batchId 企业批次主键
     * @return 结果
     */
    int deleteEnterpriseBatchById(Long batchId);

    /**
     * 批量删除企业批次
     *
     * @param batchIds 需要删除的企业批次主键集合
     * @return 结果
     */
    int deleteEnterpriseBatchByIds(Long[] batchIds);

    /**
     * 保存企业批次及房源关联
     *
     * @param enterpriseBatch 企业批次
     * @param houseIds 房源ID列表
     * @return 结果
     */
    int saveEnterpriseBatchWithHouses(HzEnterpriseBatch enterpriseBatch, List<Long> houseIds);

    /**
     * 查询企业批次关联的房源列表
     *
     * @param batchId 批次ID
     * @return 房源列表
     */
    List<Map<String, Object>> getBatchHouses(Long batchId);

    /**
     * 分页查询企业批次列表
     */
    IPage<HzEnterpriseBatch> selectEnterpriseBatchPage(Page<HzEnterpriseBatch> page, HzEnterpriseBatch enterpriseBatch);
}
