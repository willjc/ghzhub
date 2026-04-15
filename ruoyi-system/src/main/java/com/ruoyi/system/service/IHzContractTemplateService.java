package com.ruoyi.system.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ruoyi.system.domain.HzContractTemplate;

import java.util.List;

/**
 * 合同模版Service接口
 *
 * @author ruoyi
 */
public interface IHzContractTemplateService {

    /**
     * 查询合同模版列表
     *
     * @param template 合同模版
     * @return 合同模版集合
     */
    List<HzContractTemplate> selectTemplateList(HzContractTemplate template);

    /**
     * 查询合同模版详细信息
     *
     * @param templateId 合同模版主键
     * @return 合同模版
     */
    HzContractTemplate selectTemplateById(Long templateId);

    /**
     * 根据合同类型查询模版
     *
     * @param contractType 合同类型
     * @return 合同模版
     */
    HzContractTemplate selectTemplateByType(String contractType);

    /**
     * 新增合同模版
     *
     * @param template 合同模版
     * @return 结果
     */
    int insertTemplate(HzContractTemplate template);

    /**
     * 修改合同模版
     *
     * @param template 合同模版
     * @return 结果
     */
    int updateTemplate(HzContractTemplate template);

    /**
     * 删除合同模版
     *
     * @param templateId 合同模版主键
     * @return 结果
     */
    int deleteTemplateById(Long templateId);

    /**
     * 批量删除合同模版
     *
     * @param templateIds 需要删除的合同模版主键集合
     * @return 结果
     */
    int deleteTemplateByIds(Long[] templateIds);

    /**
     * 分页查询合同模版列表
     */
    IPage<HzContractTemplate> selectTemplatePage(Page<HzContractTemplate> page, HzContractTemplate template);
}
