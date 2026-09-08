package com.ruoyi.web.controller.system;

import java.math.BigDecimal;
import java.util.Objects;

/** 仅在获准环境编译后运行 main，无测试框架依赖。 */
public class DashboardStatisticsCheck {
    public static void main(String[] args) {
        check("0", "0", null);
        check("100", "0", 0);
        check("100", "80", 80);
        check("100", "150", 100);
        check("100", "-1", 0);
        check("3", "2", 67);
    }

    private static void check(String due, String paid, Integer expected) {
        Integer actual = HzStatisticsController.ledgerCollectionRate(new BigDecimal(due), new BigDecimal(paid));
        if (!Objects.equals(expected, actual)) {
            throw new AssertionError("collection rate: expected " + expected + ", actual " + actual);
        }
    }
}
