package com.ruoyi.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.ruoyi.system.domain.HzEnterpriseBatch;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 企业批次Mapper接口
 *
 * @author ruoyi
 */
@Mapper
public interface HzEnterpriseBatchMapper extends BaseMapper<HzEnterpriseBatch> {

    /**
     * 查询企业批次列表
     *
     * @param enterpriseBatch 企业批次
     * @return 企业批次集合
     */
    List<HzEnterpriseBatch> selectEnterpriseBatchList(HzEnterpriseBatch enterpriseBatch);

    /**
     * 查询企业批次详情（包含项目名称）
     *
     * @param batchId 批次ID
     * @return 企业批次
     */
    HzEnterpriseBatch selectEnterpriseBatchById(Long batchId);

    /**
     * 分页查询企业批次列表
     */
    IPage<HzEnterpriseBatch> selectEnterpriseBatchPage(IPage<HzEnterpriseBatch> page, @Param("entity") HzEnterpriseBatch enterpriseBatch);

}
