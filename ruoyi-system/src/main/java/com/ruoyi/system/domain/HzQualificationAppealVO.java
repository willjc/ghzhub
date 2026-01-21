package com.ruoyi.system.domain;

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
    private String realName;

    /** 用户手机号 */
    private String phone;

    /** 用户联系电话 */
    private String contactPhone;

    /** 用户身份证号 */
    private String idCard;

    /** 用户当前学历 */
    private String currentEducation;

    /** 申述的新学历 */
    private String newEducation;

    /** 处理人姓名 */
    private String handlerName;

    /** 申请类型文本（人才公寓/保租房） */
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
