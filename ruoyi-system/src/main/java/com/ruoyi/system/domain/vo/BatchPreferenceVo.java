package com.ruoyi.system.domain.vo;

/**
 * 批次优惠信息VO
 *
 * @author ruoyi
 */
public class BatchPreferenceVo {

    /** 批次ID */
    private Long batchId;

    /** 优惠类型(0:无优惠 1:免租期数) */
    private String preferentialType;

    /** 免租期数 */
    private Integer freeRentPeriods;

    public Long getBatchId() {
        return batchId;
    }

    public void setBatchId(Long batchId) {
        this.batchId = batchId;
    }

    public String getPreferentialType() {
        return preferentialType;
    }

    public void setPreferentialType(String preferentialType) {
        this.preferentialType = preferentialType;
    }

    public Integer getFreeRentPeriods() {
        return freeRentPeriods;
    }

    public void setFreeRentPeriods(Integer freeRentPeriods) {
        this.freeRentPeriods = freeRentPeriods;
    }

    @Override
    public String toString() {
        return "BatchPreferenceVo{" +
                "batchId=" + batchId +
                ", preferentialType='" + preferentialType + '\'' +
                ", freeRentPeriods=" + freeRentPeriods +
                '}';
    }
}
