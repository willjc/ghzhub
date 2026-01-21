package com.ruoyi.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.system.domain.HzContractTemplate;
import com.ruoyi.system.mapper.HzContractTemplateMapper;
import com.ruoyi.system.service.IHzContractTemplateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 合同模版Service业务层处理
 *
 * @author ruoyi
 */
@Service
public class HzContractTemplateServiceImpl implements IHzContractTemplateService {

    @Autowired
    private HzContractTemplateMapper templateMapper;

    /**
     * 查询合同模版列表
     *
     * @param template 合同模版
     * @return 合同模版
     */
    @Override
    public List<HzContractTemplate> selectTemplateList(HzContractTemplate template) {
        return templateMapper.selectTemplateList(template);
    }

    /**
     * 查询合同模版详细信息
     *
     * @param templateId 合同模版主键
     * @return 合同模版
     */
    @Override
    public HzContractTemplate selectTemplateById(Long templateId) {
        return templateMapper.selectById(templateId);
    }

    /**
     * 根据合同类型查询模版
     *
     * @param contractType 合同类型
     * @return 合同模版
     */
    @Override
    public HzContractTemplate selectTemplateByType(String contractType) {
        return templateMapper.selectTemplateByType(contractType);
    }

    /**
     * 新增合同模版
     *
     * @param template 合同模版
     * @return 结果
     */
    @Override
    public int insertTemplate(HzContractTemplate template) {
        template.setCreateTime(DateUtils.getNowDate());
        return templateMapper.insert(template);
    }

    /**
     * 修改合同模版
     *
     * @param template 合同模版
     * @return 结果
     */
    @Override
    public int updateTemplate(HzContractTemplate template) {
        template.setUpdateTime(DateUtils.getNowDate());
        return templateMapper.updateById(template);
    }

    /**
     * 删除合同模版（逻辑删除）
     *
     * @param templateId 合同模版主键
     * @return 结果
     */
    @Override
    public int deleteTemplateById(Long templateId) {
        LambdaUpdateWrapper<HzContractTemplate> wrapper = new LambdaUpdateWrapper<>();
        wrapper.set(HzContractTemplate::getDelFlag, "2")
               .set(HzContractTemplate::getUpdateTime, DateUtils.getNowDate())
               .eq(HzContractTemplate::getTemplateId, templateId)
               .eq(HzContractTemplate::getDelFlag, "0");
        return templateMapper.update(null, wrapper);
    }

    /**
     * 批量删除合同模版（逻辑删除）
     *
     * @param templateIds 需要删除的合同模版主键
     * @return 结果
     */
    @Override
    public int deleteTemplateByIds(Long[] templateIds) {
        int count = 0;
        for (Long templateId : templateIds) {
            count += deleteTemplateById(templateId);
        }
        return count;
    }
}
