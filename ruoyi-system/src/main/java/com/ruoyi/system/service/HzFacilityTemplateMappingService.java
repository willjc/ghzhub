package com.ruoyi.system.service;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.ruoyi.system.domain.HzFacilityItem;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.system.domain.HzFacilityTemplateItem;
import com.ruoyi.system.mapper.HzFacilityItemMapper;
import com.ruoyi.system.mapper.HzFacilityTemplateItemMapper;

/** 点验单模板设施映射的共用校验。 */
@Service
public class HzFacilityTemplateMappingService
{
    public static final String TALENT = "TALENT";
    public static final String RENTAL = "RENTAL";

    private final HzFacilityTemplateItemMapper mappingMapper;
    private final HzFacilityItemMapper facilityItemMapper;

    public HzFacilityTemplateMappingService(HzFacilityTemplateItemMapper mappingMapper,
                                            HzFacilityItemMapper facilityItemMapper)
    {
        this.mappingMapper = mappingMapper;
        this.facilityItemMapper = facilityItemMapper;
    }

    public String resolveTemplateType(String projectType)
    {
        if ("1".equals(projectType)) return TALENT;
        if ("2".equals(projectType) || "3".equals(projectType)) return RENTAL;
        return null;
    }

    public Map<Long, HzFacilityItem> validate(String projectType, Collection<Long> facilityItemIds)
    {
        String templateType = resolveTemplateType(projectType);
        if (templateType == null)
        {
            throw new IllegalArgumentException("项目类型无效");
        }
        Set<Long> requestedIds = requestedIds(facilityItemIds);
        if (requestedIds.isEmpty()) return Collections.emptyMap();

        List<HzFacilityTemplateItem> mappings = selectMappings(templateType, requestedIds);
        if (mappings.size() != requestedIds.size())
        {
            throw new IllegalArgumentException("所选设施不属于当前房源点验单，请重新选择");
        }
        if (RENTAL.equals(templateType) && mappings.stream()
                .anyMatch(mapping -> StringUtils.isEmpty(mapping.getEsignComponentKey())))
        {
            throw new IllegalArgumentException("点验单设施缺少合同控件映射，请联系管理员");
        }

        Map<Long, HzFacilityItem> items = facilityItemMapper.selectList(
                new LambdaQueryWrapper<HzFacilityItem>()
                        .in(HzFacilityItem::getFacilityItemId, requestedIds)
                        .eq(HzFacilityItem::getStatus, "0")
                        .eq(HzFacilityItem::getDelFlag, "0"))
                .stream().collect(Collectors.toMap(HzFacilityItem::getFacilityItemId, item -> item));
        if (items.size() != requestedIds.size())
        {
            throw new IllegalArgumentException("所选设施已停用，请重新选择");
        }
        return items;
    }

    public Map<Long, String> componentKeys(String templateType, Collection<Long> facilityItemIds)
    {
        if (!RENTAL.equals(templateType))
        {
            throw new IllegalArgumentException("合同点验单模板类型无效");
        }
        if (facilityItemIds == null || facilityItemIds.isEmpty())
        {
            return Collections.emptyMap();
        }
        Set<Long> requestedIds = requestedIds(facilityItemIds);
        validate("2", requestedIds);
        List<HzFacilityTemplateItem> mappings = selectMappings(templateType, requestedIds);
        Map<Long, String> result = new LinkedHashMap<>();
        for (HzFacilityTemplateItem mapping : mappings)
        {
            if (StringUtils.isEmpty(mapping.getEsignComponentKey()))
            {
                throw new IllegalArgumentException("点验单设施缺少合同控件映射，请联系管理员");
            }
            result.put(mapping.getFacilityItemId(), mapping.getEsignComponentKey());
        }
        if (result.size() != requestedIds.size())
        {
            throw new IllegalArgumentException("所选设施不属于当前房源点验单，请重新选择");
        }
        return result;
    }

    private Set<Long> requestedIds(Collection<Long> facilityItemIds)
    {
        if (facilityItemIds == null || facilityItemIds.isEmpty()) return Collections.emptySet();
        Set<Long> ids = facilityItemIds.stream().filter(java.util.Objects::nonNull).collect(Collectors.toSet());
        if (ids.size() != facilityItemIds.size())
        {
            throw new IllegalArgumentException("设施数据不完整，请重新选择");
        }
        return ids;
    }

    private List<HzFacilityTemplateItem> selectMappings(String templateType, Set<Long> facilityItemIds)
    {
        return mappingMapper.selectList(new LambdaQueryWrapper<HzFacilityTemplateItem>()
                .eq(HzFacilityTemplateItem::getTemplateType, templateType)
                .in(HzFacilityTemplateItem::getFacilityItemId, facilityItemIds)
                .eq(HzFacilityTemplateItem::getStatus, "0")
                .eq(HzFacilityTemplateItem::getDelFlag, "0")
                .orderByAsc(HzFacilityTemplateItem::getSortOrder));
    }
}
