package com.ruoyi.system.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ruoyi.system.domain.HzFacilityItem;
import com.ruoyi.system.domain.HzFacilityTemplateItem;

@Mapper
public interface HzFacilityTemplateItemMapper extends BaseMapper<HzFacilityTemplateItem>
{
    @Select({
            "<script>",
            "SELECT fi.facility_item_id, fi.facility_name, fi.facility_category,",
            "       m.sort_order, fi.status, fi.del_flag,",
            "       m.template_type, m.esign_component_key",
            "FROM hz_facility_template_item m",
            "JOIN hz_facility_item fi ON fi.facility_item_id = m.facility_item_id",
            "WHERE m.template_type = #{templateType}",
            "  AND m.status = '0' AND m.del_flag = '0'",
            "  AND fi.status = '0' AND fi.del_flag = '0'",
            "<if test='facilityName != null and facilityName != &quot;&quot;'>",
            "  AND fi.facility_name LIKE CONCAT('%', #{facilityName}, '%')",
            "</if>",
            "<if test='facilityCategory != null and facilityCategory != &quot;&quot;'>",
            "  AND fi.facility_category = #{facilityCategory}",
            "</if>",
            "ORDER BY m.sort_order, fi.facility_item_id",
            "</script>"
    })
    List<HzFacilityItem> selectFacilityItemsByTemplate(HzFacilityItem query);
}
