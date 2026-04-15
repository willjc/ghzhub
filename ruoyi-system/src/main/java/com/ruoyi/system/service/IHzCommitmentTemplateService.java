package com.ruoyi.system.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ruoyi.system.domain.HzCommitmentTemplate;

import java.util.List;

/**
 * 承诺书模板Service接口
 *
 * @author ruoyi
 */
public interface IHzCommitmentTemplateService {
    /**
     * 查询承诺书模板
     *
     * @param templateId 模板ID
     * @return 承诺书模板
     */
    HzCommitmentTemplate selectTemplateById(Long templateId);

    /**
     * 查询承诺书模板列表
     *
     * @param template 承诺书模板
     * @return 承诺书模板列表
     */
    List<HzCommitmentTemplate> selectTemplateList(HzCommitmentTemplate template);

    /**
     * 根据承诺书类型查询默认模板
     *
     * @param commitmentType 承诺书类型
     * @return 承诺书模板
     */
    HzCommitmentTemplate selectDefaultTemplateByType(String commitmentType);

    /**
     * 新增承诺书模板
     *
     * @param template 承诺书模板
     * @return 结果
     */
    int insertTemplate(HzCommitmentTemplate template);

    /**
     * 修改承诺书模板
     *
     * @param template 承诺书模板
     * @return 结果
     */
    int updateTemplate(HzCommitmentTemplate template);

    /**
     * 删除承诺书模板
     *
     * @param templateId 模板ID
     * @return 结果
     */
    int deleteTemplateById(Long templateId);

    /**
     * 批量删除承诺书模板
     *
     * @param templateIds 模板ID数组
     * @return 结果
     */
    int deleteTemplateByIds(Long[] templateIds);

    /**
     * 分页查询承诺书模板列表
     *
     * @param page 分页参数
     * @param template 承诺书模板
     * @return 承诺书模板分页结果
     */
    IPage<HzCommitmentTemplate> selectCommitmentTemplatePage(Page<HzCommitmentTemplate> page, HzCommitmentTemplate template);
}
