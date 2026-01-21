package com.ruoyi.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ruoyi.system.domain.HzContractTemplate;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 合同模版Mapper接口
 *
 * @author ruoyi
 */
@Mapper
public interface HzContractTemplateMapper extends BaseMapper<HzContractTemplate> {

    /**
     * 查询合同模版列表
     *
     * @param template 合同模版
     * @return 合同模版集合
     */
    List<HzContractTemplate> selectTemplateList(HzContractTemplate template);

    /**
     * 根据合同类型查询模版
     *
     * @param contractType 合同类型
     * @return 合同模版
     */
    HzContractTemplate selectTemplateByType(String contractType);
}
