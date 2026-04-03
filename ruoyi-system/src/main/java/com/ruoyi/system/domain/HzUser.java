package com.ruoyi.system.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.util.Date;

/**
 * 用户信息对象 hz_user
 *
 * @author ruoyi
 */
@TableName("hz_user")
public class HzUser extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /** 用户ID */
    @TableId(type = IdType.AUTO)
    private Long userId;

    /** 手机号 */
    @Excel(name = "手机号（登录账号）")
    private String phone;

    /** 联系电话 */
    @Excel(name = "联系电话")
    private String contactPhone;

    /** 来源类型（1=微信小程序 2=郑好办） */
    @Excel(name = "来源类型", readConverterExp = "1=微信小程序,2=郑好办")
    private String sourceType;

    /** 微信OpenID */
    private String wechatOpenid;

    /** 微信UnionID */
    private String wechatUnionid;

    /** 郑好办用户ID */
    private String zhaohaoUserId;

    /** 昵称 */
    @Excel(name = "昵称")
    private String nickname;

    /** 头像URL */
    private String avatar;

    /** 真实姓名 */
    @Excel(name = "真实姓名")
    private String realName;

    /** 性别（0=未知 1=男 2=女） */
    @Excel(name = "性别", readConverterExp = "0=未知,1=男,2=女")
    private String gender;

    /** 身份证号 */
    @Excel(name = "身份证号")
    private String idCard;

    /** 学历 */
    @Excel(name = "学历", dictType = "hz_education_type")
    private String education;

    /** 身份类型（1=在职人员 2=应届毕业生） */
    @Excel(name = "身份类型", dictType = "hz_identity_type")
    private String identityType;

    /** 工作单位 */
    @Excel(name = "工作单位")
    private String workUnit;

    /** 单位联系方式 */
    @Excel(name = "单位联系方式")
    private String unitContact;

    /** 配偶姓名 */
    @Excel(name = "配偶姓名")
    private String spouseName;

    /** 工作证明附件 */
    @Excel(name = "工作证明附件")
    private String workProofAttachment;

    /** 状态（0=正常 1=停用） */
    @Excel(name = "状态", readConverterExp = "0=正常,1=停用")
    private String status;

    /** 最后登录时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date lastLoginTime;

    /** 最后登录IP */
    private String lastLoginIp;

    /** 删除标志 */
    private String delFlag;

    /** 登录类型（wechat=微信 zhenghaoban=郑好办） */
    private String loginType;

    /** 是否完善个人信息（0=否 1=是） */
    private String isInfoCompleted;

    /** e签宝个人账号ID */
    private String esignPsnId;

    /** 认证状态（0=未认证 1=已填写资料 2=已实名认证） */
    private String authStatus;

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getSourceType() {
        return sourceType;
    }

    public String getContactPhone() {
        return contactPhone;
    }

    public void setContactPhone(String contactPhone) {
        this.contactPhone = contactPhone;
    }

    public void setSourceType(String sourceType) {
        this.sourceType = sourceType;
    }

    public String getWechatOpenid() {
        return wechatOpenid;
    }

    public void setWechatOpenid(String wechatOpenid) {
        this.wechatOpenid = wechatOpenid;
    }

    public String getWechatUnionid() {
        return wechatUnionid;
    }

    public void setWechatUnionid(String wechatUnionid) {
        this.wechatUnionid = wechatUnionid;
    }

    public String getZhaohaoUserId() {
        return zhaohaoUserId;
    }

    public void setZhaohaoUserId(String zhaohaoUserId) {
        this.zhaohaoUserId = zhaohaoUserId;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getRealName() {
        return realName;
    }

    public void setRealName(String realName) {
        this.realName = realName;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getIdCard() {
        return idCard;
    }

    public void setIdCard(String idCard) {
        this.idCard = idCard;
    }

    public String getEducation() {
        return education;
    }

    public void setEducation(String education) {
        this.education = education;
    }

    public String getIdentityType() {
        return identityType;
    }

    public void setIdentityType(String identityType) {
        this.identityType = identityType;
    }

    public String getWorkUnit() {
        return workUnit;
    }

    public void setWorkUnit(String workUnit) {
        this.workUnit = workUnit;
    }

    public String getUnitContact() {
        return unitContact;
    }

    public void setUnitContact(String unitContact) {
        this.unitContact = unitContact;
    }

    public String getSpouseName() {
        return spouseName;
    }

    public void setSpouseName(String spouseName) {
        this.spouseName = spouseName;
    }

    public String getWorkProofAttachment() {
        return workProofAttachment;
    }

    public void setWorkProofAttachment(String workProofAttachment) {
        this.workProofAttachment = workProofAttachment;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Date getLastLoginTime() {
        return lastLoginTime;
    }

    public void setLastLoginTime(Date lastLoginTime) {
        this.lastLoginTime = lastLoginTime;
    }

    public String getLastLoginIp() {
        return lastLoginIp;
    }

    public void setLastLoginIp(String lastLoginIp) {
        this.lastLoginIp = lastLoginIp;
    }

    public String getDelFlag() {
        return delFlag;
    }

    public void setDelFlag(String delFlag) {
        this.delFlag = delFlag;
    }

    public String getLoginType() {
        return loginType;
    }

    public void setLoginType(String loginType) {
        this.loginType = loginType;
    }

    public String getIsInfoCompleted() {
        return isInfoCompleted;
    }

    public void setIsInfoCompleted(String isInfoCompleted) {
        this.isInfoCompleted = isInfoCompleted;
    }

    public String getEsignPsnId() {
        return esignPsnId;
    }

    public void setEsignPsnId(String esignPsnId) {
        this.esignPsnId = esignPsnId;
    }

    public String getAuthStatus() {
        return authStatus;
    }

    public void setAuthStatus(String authStatus) {
        this.authStatus = authStatus;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
                .append("userId", getUserId())
                .append("phone", getPhone())
                .append("sourceType", getSourceType())
                .append("wechatOpenid", getWechatOpenid())
                .append("wechatUnionid", getWechatUnionid())
                .append("zhaohaoUserId", getZhaohaoUserId())
                .append("nickname", getNickname())
                .append("avatar", getAvatar())
                .append("realName", getRealName())
                .append("gender", getGender())
                .append("idCard", getIdCard())
                .append("education", getEducation())
                .append("status", getStatus())
                .append("lastLoginTime", getLastLoginTime())
                .append("lastLoginIp", getLastLoginIp())
                .append("delFlag", getDelFlag())
                .append("createBy", getCreateBy())
                .append("createTime", getCreateTime())
                .append("updateBy", getUpdateBy())
                .append("updateTime", getUpdateTime())
                .append("remark", getRemark())
                .toString();
    }
}
