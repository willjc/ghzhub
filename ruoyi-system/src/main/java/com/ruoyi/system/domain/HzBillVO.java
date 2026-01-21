package com.ruoyi.system.domain;

import com.baomidou.mybatisplus.annotation.TableField;

/**
 * 账单VO对象（扩展账单信息，包含关联数据）
 *
 * @author ruoyi
 */
public class HzBillVO extends HzBill {
    private static final long serialVersionUID = 1L;

    /** 合同编号 */
    @TableField(exist = false)
    private String contractNo;

    /** 项目名称（小区名） */
    @TableField(exist = false)
    private String projectName;

    /** 楼栋名称 */
    @TableField(exist = false)
    private String buildingName;

    /** 单元名称 */
    @TableField(exist = false)
    private String unitName;

    /** 房源编号 */
    @TableField(exist = false)
    private String houseNo;

    public String getContractNo() {
        return contractNo;
    }

    public void setContractNo(String contractNo) {
        this.contractNo = contractNo;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public String getBuildingName() {
        return buildingName;
    }

    public void setBuildingName(String buildingName) {
        this.buildingName = buildingName;
    }

    public String getUnitName() {
        return unitName;
    }

    public void setUnitName(String unitName) {
        this.unitName = unitName;
    }

    public String getHouseNo() {
        return houseNo;
    }

    public void setHouseNo(String houseNo) {
        this.houseNo = houseNo;
    }
}
