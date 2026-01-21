package com.ruoyi.system.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.ruoyi.common.core.domain.BaseEntity;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * 合同签名对象 hz_contract_signature
 *
 * @author ruoyi
 */
@TableName("hz_contract_signature")
public class HzContractSignature extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /** 签名ID */
    @TableId(type = IdType.AUTO)
    private Long signatureId;

    /** 合同ID */
    @TableField("contract_id")
    private Long contractId;

    /** 签名人ID */
    @TableField("signer_id")
    private Long signerId;

    /** 签名人姓名 */
    @TableField("signer_name")
    private String signerName;

    /** 签名人类型(1:租户 2:房东 3:管理员) */
    @TableField("signer_type")
    private String signerType;

    /** 签名图片 */
    @TableField("signature_image")
    private String signatureImage;

    /** 签名时间 */
    @TableField("sign_time")
    private String signTime;

    /** IP地址 */
    @TableField("ip_address")
    private String ipAddress;

    /** 设备信息 */
    @TableField("device_info")
    private String deviceInfo;

    /** 签名状态(0:未签署 1:已签署) */
    @TableField("sign_status")
    private String signStatus;

    /** 删除标志(0:存在 2:删除) */
    @TableField("del_flag")
    private String delFlag;

    public void setSignatureId(Long signatureId) {
        this.signatureId = signatureId;
    }

    public Long getSignatureId() {
        return signatureId;
    }

    public void setContractId(Long contractId) {
        this.contractId = contractId;
    }

    public Long getContractId() {
        return contractId;
    }

    public void setSignerId(Long signerId) {
        this.signerId = signerId;
    }

    public Long getSignerId() {
        return signerId;
    }

    public void setSignerName(String signerName) {
        this.signerName = signerName;
    }

    public String getSignerName() {
        return signerName;
    }

    public void setSignerType(String signerType) {
        this.signerType = signerType;
    }

    public String getSignerType() {
        return signerType;
    }

    public void setSignatureImage(String signatureImage) {
        this.signatureImage = signatureImage;
    }

    public String getSignatureImage() {
        return signatureImage;
    }

    public void setSignTime(String signTime) {
        this.signTime = signTime;
    }

    public String getSignTime() {
        return signTime;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setDeviceInfo(String deviceInfo) {
        this.deviceInfo = deviceInfo;
    }

    public String getDeviceInfo() {
        return deviceInfo;
    }

    public void setSignStatus(String signStatus) {
        this.signStatus = signStatus;
    }

    public String getSignStatus() {
        return signStatus;
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
            .append("signatureId", getSignatureId())
            .append("contractId", getContractId())
            .append("signerId", getSignerId())
            .append("signerName", getSignerName())
            .append("signerType", getSignerType())
            .append("signatureImage", getSignatureImage())
            .append("signTime", getSignTime())
            .append("ipAddress", getIpAddress())
            .append("deviceInfo", getDeviceInfo())
            .append("signStatus", getSignStatus())
            .append("delFlag", getDelFlag())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("updateBy", getUpdateBy())
            .append("updateTime", getUpdateTime())
            .append("remark", getRemark())
            .toString();
    }
}
