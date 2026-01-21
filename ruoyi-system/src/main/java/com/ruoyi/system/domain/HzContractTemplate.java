package com.ruoyi.system.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.ruoyi.common.core.domain.BaseEntity;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.math.BigDecimal;

/**
 * 合同模版对象 hz_contract_template
 *
 * @author ruoyi
 */
@TableName("hz_contract_template")
public class HzContractTemplate extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /** 模版ID */
    @TableId(type = IdType.AUTO)
    private Long templateId;

    /** 模版名称 */
    @TableField("template_name")
    private String templateName;

    /** 模版编码 */
    @TableField("template_code")
    private String templateCode;

    /** 合同类型(1:人才公寓 2:保租房 3:市场租赁) */
    @TableField("contract_type")
    private String contractType;

    /** 模版内容 */
    @TableField("template_content")
    private String templateContent;

    /** 付款周期 */
    @TableField("payment_cycle")
    private String paymentCycle;

    /** 合同期限（月） */
    @TableField("contract_duration")
    private Integer contractDuration;

    /** 押金月数 */
    @TableField("deposit_months")
    private BigDecimal depositMonths;

    /** 固定押金金额（元） */
    @TableField("deposit_amount")
    private BigDecimal depositAmount;

    /** 违约金比例（%） */
    @TableField("penalty_rate")
    private BigDecimal penaltyRate;

    /** 固定违约金金额（元） */
    @TableField("penalty_amount")
    private BigDecimal penaltyAmount;

    /** 版本号 */
    @TableField("version")
    private String version;

    /** 是否默认(0:否 1:是) */
    @TableField("is_default")
    private String isDefault;

    /** 状态(0:正常 1:停用) */
    @TableField("status")
    private String status;

    /** 删除标志(0:存在 2:删除) */
    @TableField("del_flag")
    private String delFlag;

    public void setTemplateId(Long templateId) {
        this.templateId = templateId;
    }

    public Long getTemplateId() {
        return templateId;
    }

    public void setTemplateName(String templateName) {
        this.templateName = templateName;
    }

    public String getTemplateName() {
        return templateName;
    }

    public void setTemplateCode(String templateCode) {
        this.templateCode = templateCode;
    }

    public String getTemplateCode() {
        return templateCode;
    }

    public void setContractType(String contractType) {
        this.contractType = contractType;
    }

    public String getContractType() {
        return contractType;
    }

    public void setTemplateContent(String templateContent) {
        this.templateContent = templateContent;
    }

    public String getTemplateContent() {
        return templateContent;
    }

    public void setPaymentCycle(String paymentCycle) {
        this.paymentCycle = paymentCycle;
    }

    public String getPaymentCycle() {
        return paymentCycle;
    }

    public void setContractDuration(Integer contractDuration) {
        this.contractDuration = contractDuration;
    }

    public Integer getContractDuration() {
        return contractDuration;
    }

    public void setDepositMonths(BigDecimal depositMonths) {
        this.depositMonths = depositMonths;
    }

    public BigDecimal getDepositMonths() {
        return depositMonths;
    }

    public void setDepositAmount(BigDecimal depositAmount) {
        this.depositAmount = depositAmount;
    }

    public BigDecimal getDepositAmount() {
        return depositAmount;
    }

    public void setPenaltyRate(BigDecimal penaltyRate) {
        this.penaltyRate = penaltyRate;
    }

    public BigDecimal getPenaltyRate() {
        return penaltyRate;
    }

    public void setPenaltyAmount(BigDecimal penaltyAmount) {
        this.penaltyAmount = penaltyAmount;
    }

    public BigDecimal getPenaltyAmount() {
        return penaltyAmount;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getVersion() {
        return version;
    }

    public void setIsDefault(String isDefault) {
        this.isDefault = isDefault;
    }

    public String getIsDefault() {
        return isDefault;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }

    public void setDelFlag(String delFlag) {
        this.delFlag = delFlag;
    }

    public String getDelFlag() {
        return delFlag;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
            .append("templateId", getTemplateId())
            .append("templateName", getTemplateName())
            .append("templateCode", getTemplateCode())
            .append("contractType", getContractType())
            .append("templateContent", getTemplateContent())
            .append("paymentCycle", getPaymentCycle())
            .append("contractDuration", getContractDuration())
            .append("depositMonths", getDepositMonths())
            .append("depositAmount", getDepositAmount())
            .append("penaltyRate", getPenaltyRate())
            .append("penaltyAmount", getPenaltyAmount())
            .append("version", getVersion())
            .append("isDefault", getIsDefault())
            .append("status", getStatus())
            .append("delFlag", getDelFlag())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("updateBy", getUpdateBy())
            .append("updateTime", getUpdateTime())
            .append("remark", getRemark())
            .toString();
    }
}
