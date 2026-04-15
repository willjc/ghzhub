package com.ruoyi.system.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.ruoyi.system.domain.HzCommitment;
import com.ruoyi.system.domain.HzCommitmentVO;

import java.util.List;

/**
 * 承诺书Mapper接口
 *
 * @author ruoyi
 */
@Mapper
public interface HzCommitmentMapper extends BaseMapper<HzCommitment> {

    /**
     * 查询承诺书VO列表
     *
     * @param commitment 承诺书
     * @return 承诺书VO列表
     */
    List<HzCommitmentVO> selectCommitmentVOList(HzCommitment commitment);

    /**
     * 根据ID查询承诺书VO
     *
     * @param commitmentId 承诺书ID
     * @return 承诺书VO
     */
    HzCommitmentVO selectCommitmentVOById(Long commitmentId);

    /**
     * 分页查询承诺书VO列表
     */
    IPage<HzCommitmentVO> selectCommitmentVOPage(IPage<HzCommitmentVO> page, @Param("entity") HzCommitment commitment);
}
