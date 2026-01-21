package com.ruoyi.system.domain;

/**
 * 承诺书记录VO对象（用于列表展示）
 *
 * @author ruoyi
 */
public class HzCommitmentVO extends HzCommitment {
    private static final long serialVersionUID = 1L;

    /** 用户昵称 */
    private String userNickname;

    /** 用户账号 */
    private String userAccount;

    /** 项目名称 */
    private String projectName;

    /** 项目编码 */
    private String projectCode;

    public String getUserNickname() {
        return userNickname;
    }

    public void setUserNickname(String userNickname) {
        this.userNickname = userNickname;
    }

    public String getUserAccount() {
        return userAccount;
    }

    public void setUserAccount(String userAccount) {
        this.userAccount = userAccount;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public String getProjectCode() {
        return projectCode;
    }

    public void setProjectCode(String projectCode) {
        this.projectCode = projectCode;
    }
}
