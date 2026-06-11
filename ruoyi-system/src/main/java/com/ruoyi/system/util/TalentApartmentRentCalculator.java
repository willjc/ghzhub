package com.ruoyi.system.util;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * 人才公寓 7 折分档租金计算工具
 *
 * 业务规则（来自需求确认）：
 * 1. 仅 hz_project.project_type = "1"（人才公寓）适用本规则
 * 2. 房源 hz_house.rent_price 已是 7 折优惠后的"整套月租"
 * 3. 学历 -> 面积上限：
 *      博士(7) -> 90 ㎡
 *      大专(4) / 本科(5) / 硕士(6) -> 70 ㎡
 *      其它（含 null / 1 小学 / 2 初中）-> 不适用（依靠资格申诉前置拦截）
 * 4. 超出面积上限部分按"标准价"计算（即 rent_price / 0.7 反推得到的市场价）
 * 5. 押金不参与 7 折逻辑（由调用方自行处理）
 */
public final class TalentApartmentRentCalculator {

    /** 7 折系数 */
    public static final BigDecimal DISCOUNT_RATE = new BigDecimal("0.7");

    /** 人才公寓 project_type */
    public static final String PROJECT_TYPE_TALENT = "1";

    private TalentApartmentRentCalculator() {}

    /**
     * 根据学历返回该学历对应的人才公寓面积上限
     *
     * @param education 字典 hz_education_type 的值
     * @return 面积上限（㎡），不适用时返回 null
     */
    public static BigDecimal areaLimitOf(String education) {
        if (education == null) return null;
        switch (education.trim()) {
            case "7": // 博士
                return new BigDecimal("90");
            case "3": // 高中（按本科标准分档）
            case "4": // 大专
            case "5": // 本科
            case "6": // 硕士
                return new BigDecimal("70");
            default:
                return null; // 1/2 小学/初中 不适用
        }
    }

    /**
     * 计算实际月租与分档明细
     *
     * @param projectType 项目类型（hz_project.project_type）
     * @param education   用户学历（hz_user.education）
     * @param area        房源建筑面积
     * @param rentPrice   房源月租金（已是 7 折后价）
     * @return 计算结果；当不适用时 applicable=false 且 actualMonthlyRent 直接返回原 rentPrice
     */
    public static Result calculate(String projectType, String education,
                                   BigDecimal area, BigDecimal rentPrice) {
        Result r = new Result();
        r.setOriginalRent(rentPrice);
        r.setActualMonthlyRent(rentPrice);

        // 仅人才公寓适用
        if (!PROJECT_TYPE_TALENT.equals(projectType)) {
            r.setApplicable(false);
            r.setRemark(null);
            return r;
        }
        // 价格、面积非法 -> 不适用
        if (area == null || area.compareTo(BigDecimal.ZERO) <= 0
                || rentPrice == null || rentPrice.compareTo(BigDecimal.ZERO) <= 0) {
            r.setApplicable(false);
            return r;
        }
        // 学历无映射（小初高、未填）-> 不适用
        BigDecimal areaLimit = areaLimitOf(education);
        if (areaLimit == null) {
            r.setApplicable(false);
            return r;
        }

        r.setApplicable(true);
        r.setAreaLimit(areaLimit);

        // 标准价（市场价整套月租）= 7 折价 / 0.7
        BigDecimal standardPrice = rentPrice.divide(DISCOUNT_RATE, 2, RoundingMode.HALF_UP);
        r.setStandardPrice(standardPrice);

        // 不超出 -> 实际月租 = 房源原价
        if (area.compareTo(areaLimit) <= 0) {
            r.setOverflowArea(BigDecimal.ZERO);
            r.setActualMonthlyRent(rentPrice);
            r.setRemark(buildRemark(area, areaLimit, rentPrice, standardPrice, BigDecimal.ZERO,
                    rentPrice, BigDecimal.ZERO, rentPrice));
            return r;
        }

        // 超出 -> 上限内按 7 折单价，超出部分按标准价单价
        BigDecimal overflow = area.subtract(areaLimit);
        r.setOverflowArea(overflow);

        // 折后单价 = rentPrice / area；上限内租金 = 折后单价 × areaLimit = rentPrice × areaLimit / area
        BigDecimal withinLimitRent = rentPrice.multiply(areaLimit)
                .divide(area, 2, RoundingMode.HALF_UP);
        // 标准单价 = standardPrice / area；超出部分租金 = 标准单价 × overflow = standardPrice × overflow / area
        BigDecimal overflowRent = standardPrice.multiply(overflow)
                .divide(area, 2, RoundingMode.HALF_UP);
        BigDecimal actual = withinLimitRent.add(overflowRent).setScale(2, RoundingMode.HALF_UP);
        r.setActualMonthlyRent(actual);
        r.setWithinLimitRent(withinLimitRent);
        r.setOverflowRent(overflowRent);
        r.setRemark(buildRemark(area, areaLimit, rentPrice, standardPrice, overflow,
                withinLimitRent, overflowRent, actual));
        return r;
    }

    private static String buildRemark(BigDecimal area, BigDecimal areaLimit,
                                      BigDecimal rentPrice, BigDecimal standardPrice,
                                      BigDecimal overflow,
                                      BigDecimal withinLimitRent, BigDecimal overflowRent,
                                      BigDecimal actual) {
        StringBuilder sb = new StringBuilder();
        sb.append("人才公寓7折分档：建筑面积").append(strip(area)).append("㎡，学历对应上限")
          .append(strip(areaLimit)).append("㎡");
        if (overflow.compareTo(BigDecimal.ZERO) > 0) {
            sb.append("；上限内 ").append(strip(areaLimit)).append("㎡×7折价=¥").append(strip(withinLimitRent))
              .append("/月，超出 ").append(strip(overflow)).append("㎡×标准价=¥").append(strip(overflowRent))
              .append("/月，合计 ¥").append(strip(actual)).append("/月");
        } else {
            sb.append("；未超上限，按 7 折价 ¥").append(strip(rentPrice)).append("/月计算");
        }
        return sb.toString();
    }

    private static String strip(BigDecimal v) {
        if (v == null) return "0";
        return v.stripTrailingZeros().toPlainString();
    }

    /** 计算结果 */
    public static class Result {
        /** 是否适用 7 折分档（false 时 actualMonthlyRent = originalRent） */
        private boolean applicable;
        /** 房源原价（7 折后） */
        private BigDecimal originalRent;
        /** 学历对应面积上限 */
        private BigDecimal areaLimit;
        /** 反推的标准价（整套月租） */
        private BigDecimal standardPrice;
        /** 超出面积 */
        private BigDecimal overflowArea;
        /** 上限内部分月租 */
        private BigDecimal withinLimitRent;
        /** 超出部分月租 */
        private BigDecimal overflowRent;
        /** 实际应付月租 */
        private BigDecimal actualMonthlyRent;
        /** 备注（人话明细） */
        private String remark;

        public boolean isApplicable() { return applicable; }
        public void setApplicable(boolean applicable) { this.applicable = applicable; }
        public BigDecimal getOriginalRent() { return originalRent; }
        public void setOriginalRent(BigDecimal originalRent) { this.originalRent = originalRent; }
        public BigDecimal getAreaLimit() { return areaLimit; }
        public void setAreaLimit(BigDecimal areaLimit) { this.areaLimit = areaLimit; }
        public BigDecimal getStandardPrice() { return standardPrice; }
        public void setStandardPrice(BigDecimal standardPrice) { this.standardPrice = standardPrice; }
        public BigDecimal getOverflowArea() { return overflowArea; }
        public void setOverflowArea(BigDecimal overflowArea) { this.overflowArea = overflowArea; }
        public BigDecimal getWithinLimitRent() { return withinLimitRent; }
        public void setWithinLimitRent(BigDecimal withinLimitRent) { this.withinLimitRent = withinLimitRent; }
        public BigDecimal getOverflowRent() { return overflowRent; }
        public void setOverflowRent(BigDecimal overflowRent) { this.overflowRent = overflowRent; }
        public BigDecimal getActualMonthlyRent() { return actualMonthlyRent; }
        public void setActualMonthlyRent(BigDecimal actualMonthlyRent) { this.actualMonthlyRent = actualMonthlyRent; }
        public String getRemark() { return remark; }
        public void setRemark(String remark) { this.remark = remark; }
    }
}
