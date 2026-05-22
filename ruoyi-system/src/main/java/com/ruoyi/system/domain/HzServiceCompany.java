package com.ruoyi.system.domain;

import com.baomidou.mybatisplus.annotation.*;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 服务公司对象 hz_service_company
 *
 * @author ruoyi
 */
@TableName("hz_service_company")
public class HzServiceCompany extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 服务公司ID */
    @TableId(type = IdType.AUTO)
    @Excel(name = "公司ID")
    private Long companyId;

    /** 公司名称 */
    @Excel(name = "公司名称")
    @TableField("company_name")
    private String companyName;

    /** 服务类型：1=保洁 2=搬家 3=综合 */
    @Excel(name = "服务类型", readConverterExp = "1=保洁,2=搬家,3=综合")
    @TableField("company_type")
    private String companyType;

    /** 联系人 */
    @Excel(name = "联系人")
    @TableField("contact_person")
    private String contactPerson;

    /** 联系电话 */
    @Excel(name = "联系电话")
    @TableField("contact_phone")
    private String contactPhone;

    /** 公司地址 */
    @Excel(name = "公司地址")
    @TableField("address")
    private String address;

    /** 服务区域 */
    @TableField("service_area")
    private String serviceArea;

    /** 公司简介 */
    @TableField("intro")
    private String intro;

    /** 排序值 */
    @TableField("sort_order")
    private Integer sortOrder;

    /** 状态：0=启用 1=停用 */
    @Excel(name = "状态", readConverterExp = "0=启用,1=停用")
    @TableField("status")
    private String status;

    /** 删除标志：0=存在 2=删除 */
    @TableField("del_flag")
    @TableLogic
    private String delFlag;

    public Long getCompanyId() { return companyId; }
    public void setCompanyId(Long companyId) { this.companyId = companyId; }

    public String getCompanyName() { return companyName; }
    public void setCompanyName(String companyName) { this.companyName = companyName; }

    public String getCompanyType() { return companyType; }
    public void setCompanyType(String companyType) { this.companyType = companyType; }

    public String getContactPerson() { return contactPerson; }
    public void setContactPerson(String contactPerson) { this.contactPerson = contactPerson; }

    public String getContactPhone() { return contactPhone; }
    public void setContactPhone(String contactPhone) { this.contactPhone = contactPhone; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public String getServiceArea() { return serviceArea; }
    public void setServiceArea(String serviceArea) { this.serviceArea = serviceArea; }

    public String getIntro() { return intro; }
    public void setIntro(String intro) { this.intro = intro; }

    public Integer getSortOrder() { return sortOrder; }
    public void setSortOrder(Integer sortOrder) { this.sortOrder = sortOrder; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getDelFlag() { return delFlag; }
    public void setDelFlag(String delFlag) { this.delFlag = delFlag; }
}
