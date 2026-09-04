package com.ruoyi.system.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import com.ruoyi.common.core.domain.BaseEntity;

/** 点验单模板与设施、e签宝控件的映射。 */
@TableName("hz_facility_template_item")
public class HzFacilityTemplateItem extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @TableField("template_type")
    private String templateType;

    @TableField("facility_item_id")
    private Long facilityItemId;

    @TableField("esign_component_key")
    private String esignComponentKey;

    @TableField("sort_order")
    private Integer sortOrder;

    @TableField("status")
    private String status;

    @TableField("del_flag")
    @TableLogic
    private String delFlag;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getTemplateType() { return templateType; }
    public void setTemplateType(String templateType) { this.templateType = templateType; }
    public Long getFacilityItemId() { return facilityItemId; }
    public void setFacilityItemId(Long facilityItemId) { this.facilityItemId = facilityItemId; }
    public String getEsignComponentKey() { return esignComponentKey; }
    public void setEsignComponentKey(String esignComponentKey) { this.esignComponentKey = esignComponentKey; }
    public Integer getSortOrder() { return sortOrder; }
    public void setSortOrder(Integer sortOrder) { this.sortOrder = sortOrder; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getDelFlag() { return delFlag; }
    public void setDelFlag(String delFlag) { this.delFlag = delFlag; }
}
