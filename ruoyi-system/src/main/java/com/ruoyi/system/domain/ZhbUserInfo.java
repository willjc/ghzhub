package com.ruoyi.system.domain;

import java.io.Serializable;

/**
 * 郑好办用户信息DTO
 *
 * @author ruoyi
 */
public class ZhbUserInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 郑好办用户ID (zid)
     */
    private String zid;

    /**
     * 手机号
     */
    private String phone;

    /**
     * 昵称 (displayName)
     */
    private String displayName;

    /**
     * 真实姓名 (realName)
     */
    private String realName;

    /**
     * 身份证号 (idCode)
     */
    private String idCode;

    /**
     * 头像URL (avatarUrl)
     */
    private String avatarUrl;

    /**
     * 性别 (0:男 1:女)
     */
    private Integer gender;

    /**
     * uid（用户唯一标识）
     */
    private String uid;

    public String getZid() {
        return zid;
    }

    public void setZid(String zid) {
        this.zid = zid;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getRealName() {
        return realName;
    }

    public void setRealName(String realName) {
        this.realName = realName;
    }

    public String getIdCode() {
        return idCode;
    }

    public void setIdCode(String idCode) {
        this.idCode = idCode;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public Integer getGender() {
        return gender;
    }

    public void setGender(Integer gender) {
        this.gender = gender;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    @Override
    public String toString() {
        return "ZhbUserInfo{" +
                "zid='" + zid + '\'' +
                ", phone='" + phone + '\'' +
                ", displayName='" + displayName + '\'' +
                ", realName='" + realName + '\'' +
                ", idCode='" + idCode + '\'' +
                ", avatarUrl='" + avatarUrl + '\'' +
                ", gender=" + gender +
                ", uid='" + uid + '\'' +
                '}';
    }
}
