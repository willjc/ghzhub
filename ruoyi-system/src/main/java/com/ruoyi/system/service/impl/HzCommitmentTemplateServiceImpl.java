package com.ruoyi.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.system.domain.HzCommitmentTemplate;
import com.ruoyi.system.mapper.HzCommitmentTemplateMapper;
import com.ruoyi.system.service.IHzCommitmentTemplateService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 承诺书模板Service业务层处理
 *
 * @author ruoyi
 */
@Service
public class HzCommitmentTemplateServiceImpl extends ServiceImpl<HzCommitmentTemplateMapper, HzCommitmentTemplate> implements IHzCommitmentTemplateService {

    @Override
    public HzCommitmentTemplate selectTemplateById(Long templateId) {
        return this.getById(templateId);
    }

    @Override
    public List<HzCommitmentTemplate> selectTemplateList(HzCommitmentTemplate template) {
        LambdaQueryWrapper<HzCommitmentTemplate> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StringUtils.isNotBlank(template.getTemplateName()),
                    HzCommitmentTemplate::getTemplateName, template.getTemplateName())
               .eq(StringUtils.isNotBlank(template.getCommitmentType()),
                    HzCommitmentTemplate::getCommitmentType, template.getCommitmentType())
               .eq(StringUtils.isNotBlank(template.getStatus()),
                    HzCommitmentTemplate::getStatus, template.getStatus())
               .eq(HzCommitmentTemplate::getDelFlag, "0")
               .orderByDesc(HzCommitmentTemplate::getCreateTime);
        return this.list(wrapper);
    }

    @Override
    public HzCommitmentTemplate selectDefaultTemplateByType(String commitmentType) {
        LambdaQueryWrapper<HzCommitmentTemplate> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(HzCommitmentTemplate::getCommitmentType, commitmentType)
               .eq(HzCommitmentTemplate::getIsDefault, "1")
               .eq(HzCommitmentTemplate::getStatus, "0")
               .eq(HzCommitmentTemplate::getDelFlag, "0")
               .orderByDesc(HzCommitmentTemplate::getCreateTime)
               .last("LIMIT 1");
        return this.getOne(wrapper);
    }

    @Override
    public int insertTemplate(HzCommitmentTemplate template) {
        template.setDelFlag("0");
        if (StringUtils.isBlank(template.getStatus())) {
            template.setStatus("0");
        }
        if (StringUtils.isBlank(template.getIsDefault())) {
            template.setIsDefault("0");
        }
        if (StringUtils.isBlank(template.getVersion())) {
            template.setVersion("1.0");
        }
        return this.save(template) ? 1 : 0;
    }

    @Override
    public int updateTemplate(HzCommitmentTemplate template) {
        return this.updateById(template) ? 1 : 0;
    }

    @Override
    public int deleteTemplateById(Long templateId) {
        HzCommitmentTemplate template = new HzCommitmentTemplate();
        template.setTemplateId(templateId);
        template.setDelFlag("2");
        return this.updateById(template) ? 1 : 0;
    }

    @Override
    public int deleteTemplateByIds(Long[] templateIds) {
        int count = 0;
        for (Long templateId : templateIds) {
            count += deleteTemplateById(templateId);
        }
        return count;
    }
}
