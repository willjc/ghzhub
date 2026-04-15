package com.ruoyi.system.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ruoyi.system.domain.HzCommitment;
import com.ruoyi.system.domain.HzCommitmentVO;

import java.util.List;

/**
 * 承诺书Service接口
 *
 * @author ruoyi
 */
public interface IHzCommitmentService {
    /**
     * 查询承诺书
     *
     * @param commitmentId 承诺书ID
     * @return 承诺书
     */
    HzCommitment selectCommitmentById(Long commitmentId);

    /**
     * 查询承诺书VO（包含租户和项目信息）
     *
     * @param commitmentId 承诺书ID
     * @return 承诺书VO
     */
    HzCommitmentVO selectCommitmentVOById(Long commitmentId);

    /**
     * 查询承诺书列表（VO）
     *
     * @param commitment 承诺书
     * @return 承诺书VO列表
     */
    List<HzCommitmentVO> selectCommitmentVOList(HzCommitment commitment);

    /**
     * 根据租户ID查询承诺书列表
     *
     * @param tenantId 租户ID
     * @return 承诺书列表
     */
    List<HzCommitment> selectCommitmentListByTenantId(Long tenantId);

    /**
     * 根据租户ID和类型查询承诺书
     *
     * @param tenantId 租户ID
     * @param commitmentType 承诺书类型
     * @return 承诺书
     */
    HzCommitment selectCommitmentByTenantIdAndType(Long tenantId, String commitmentType);

    /**
     * 新增承诺书
     *
     * @param commitment 承诺书
     * @return 结果
     */
    int insertCommitment(HzCommitment commitment);

    /**
     * 修改承诺书
     *
     * @param commitment 承诺书
     * @return 结果
     */
    int updateCommitment(HzCommitment commitment);

    /**
     * 删除承诺书
     *
     * @param commitmentId 承诺书ID
     * @return 结果
     */
    int deleteCommitmentById(Long commitmentId);

    /**
     * 分页查询承诺书VO列表
     */
    IPage<HzCommitmentVO> selectCommitmentVOPage(Page<HzCommitmentVO> page, HzCommitment commitment);
}
