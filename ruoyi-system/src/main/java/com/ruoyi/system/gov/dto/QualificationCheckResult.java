package com.ruoyi.system.gov.dto;

import java.util.ArrayList;
import java.util.List;

/**
 * 资格校验结果 DTO
 * <p>
 * 6 个检测项：婚姻信息、社保缴纳、本人不动产、本人公租房、配偶不动产、配偶公租房
 * passed 为 true 的充要条件：5 个判定项全过（婚姻不是判定项，只是前置）
 */
public class QualificationCheckResult {

    /** 是否通过全部资格校验 */
    private boolean passed;

    /** 6 项检测明细 */
    private List<CheckItem> items = new ArrayList<>();

    /** 失败原因文案（模糊化，不展示房产地址等隐私信息） */
    private List<String> failReasons = new ArrayList<>();

    /** 资格记录ID（落库后返回） */
    private Long qualificationId;

    /** 最近一次校验时间（yyyy-MM-dd HH:mm:ss） */
    private String lastCheckTime;

    /** 是否已校验过（用于 status 接口） */
    private boolean checked;

    public static class CheckItem {
        /** 项目编码：marriage / social / selfEstate / selfHousing / spouseEstate / spouseHousing */
        private String code;
        /** 展示名称 */
        private String label;
        /** 状态：passed / failed / skipped / error */
        private String status;
        /** 结果文案（模糊） */
        private String message;

        public CheckItem() {}

        public CheckItem(String code, String label, String status, String message) {
            this.code = code;
            this.label = label;
            this.status = status;
            this.message = message;
        }

        public String getCode() { return code; }
        public void setCode(String code) { this.code = code; }
        public String getLabel() { return label; }
        public void setLabel(String label) { this.label = label; }
        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
    }

    public boolean isPassed() { return passed; }
    public void setPassed(boolean passed) { this.passed = passed; }
    public List<CheckItem> getItems() { return items; }
    public void setItems(List<CheckItem> items) { this.items = items; }
    public List<String> getFailReasons() { return failReasons; }
    public void setFailReasons(List<String> failReasons) { this.failReasons = failReasons; }
    public Long getQualificationId() { return qualificationId; }
    public void setQualificationId(Long qualificationId) { this.qualificationId = qualificationId; }
    public String getLastCheckTime() { return lastCheckTime; }
    public void setLastCheckTime(String lastCheckTime) { this.lastCheckTime = lastCheckTime; }
    public boolean isChecked() { return checked; }
    public void setChecked(boolean checked) { this.checked = checked; }
}
