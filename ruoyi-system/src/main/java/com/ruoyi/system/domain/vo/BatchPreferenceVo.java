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

    /** 入驻开始日期 */
    private String entryStartDate;

    /** 入驻结束日期 */
    private String entryEndDate;

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

    public String getEntryStartDate() {
        return entryStartDate;
    }

    public void setEntryStartDate(String entryStartDate) {
        this.entryStartDate = entryStartDate;
    }

    public String getEntryEndDate() {
        return entryEndDate;
    }

    public void setEntryEndDate(String entryEndDate) {
        this.entryEndDate = entryEndDate;
    }

    @Override
    public String toString() {
        return "BatchPreferenceVo{" +
                "batchId=" + batchId +
                ", preferentialType='" + preferentialType + '\'' +
                ", freeRentPeriods=" + freeRentPeriods +
                ", entryStartDate='" + entryStartDate + '\'' +
                ", entryEndDate='" + entryEndDate + '\'' +
                '}';
    }
}
