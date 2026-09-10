package com.ruoyi.system.domain;

import com.ruoyi.common.annotation.Excel;

/**
 * 资格申述VO对象（用于管理端查询，包含用户信息）
 *
 * @author ruoyi
 */
public class HzQualificationAppealVO extends HzQualificationAppeal {
    private static final long serialVersionUID = 1L;

    /** 用户昵称 */
    private String nickname;

    /** 用户真实姓名 */
    @Excel(name = "申请人姓名", sort = 1)
    private String realName;

    /** 用户手机号 */
    @Excel(name = "手机号", sort = 2)
    private String phone;

    /** 用户联系电话 */
    private String contactPhone;

    /** 用户身份证号 */
    @Excel(name = "身份证号", width = 25, sort = 3)
    private String idCard;

    /** 用户工作单位（公司名） */
    @Excel(name = "工作单位", sort = 4)
    private String workUnit;

    /** 单位联系电话（公司电话） */
    private String unitContact;

    /** 用户当前学历 */
    private String currentEducation;

    /** 申述的新学历 */
    private String newEducation;

    /** 处理人姓名 */
    @Excel(name = "处理人", sort = 9)
    private String handlerName;

    /** 申请类型文本（人才公寓/保租房） */
    @Excel(name = "申请类型", sort = 5)
    private String applyTypeText;

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getRealName() {
        return realName;
    }

    public void setRealName(String realName) {
        this.realName = realName;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getContactPhone() {
        return contactPhone;
    }

    public void setContactPhone(String contactPhone) {
        this.contactPhone = contactPhone;
    }

    public String getIdCard() {
        return idCard;
    }

    public void setIdCard(String idCard) {
        this.idCard = idCard;
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

    public String getCurrentEducation() {
        return currentEducation;
    }

    public void setCurrentEducation(String currentEducation) {
        this.currentEducation = currentEducation;
    }

    public String getNewEducation() {
        return newEducation;
    }

    public void setNewEducation(String newEducation) {
        this.newEducation = newEducation;
    }

    public String getHandlerName() {
        return handlerName;
    }

    public void setHandlerName(String handlerName) {
        this.handlerName = handlerName;
    }

    public String getApplyTypeText() {
        return applyTypeText;
    }

    public void setApplyTypeText(String applyTypeText) {
        this.applyTypeText = applyTypeText;
    }
}
