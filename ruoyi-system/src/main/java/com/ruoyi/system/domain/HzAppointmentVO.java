package com.ruoyi.system.domain;

import com.baomidou.mybatisplus.annotation.TableField;

/**
 * 预约看房VO对象
 *
 * @author ruoyi
 */
public class HzAppointmentVO extends HzAppointment {
    private static final long serialVersionUID = 1L;

    /** 用户昵称（用于显示租户信息） */
    @TableField(exist = false)
    private String userName;

    /** 房源编码 */
    @TableField(exist = false)
    private String houseCode;

    /** 房源名称 */
    @TableField(exist = false)
    private String houseName;

    /** 项目名称 */
    @TableField(exist = false)
    private String projectName;

    /** 项目地址 */
    @TableField(exist = false)
    private String projectAddress;

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getHouseCode() {
        return houseCode;
    }

    public void setHouseCode(String houseCode) {
        this.houseCode = houseCode;
    }

    public String getHouseName() {
        return houseName;
    }

    public void setHouseName(String houseName) {
        this.houseName = houseName;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public String getProjectAddress() {
        return projectAddress;
    }

    public void setProjectAddress(String projectAddress) {
        this.projectAddress = projectAddress;
    }
}
