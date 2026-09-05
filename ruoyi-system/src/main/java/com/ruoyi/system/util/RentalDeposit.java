package com.ruoyi.system.util;

import java.math.BigDecimal;

/** 保租房、市场租赁的新签押金规则。 */
public final class RentalDeposit {
    private RentalDeposit() {
    }

    public static BigDecimal resolve(String projectType, BigDecimal originalDeposit) {
        return "2".equals(projectType) || "3".equals(projectType)
                ? new BigDecimal("1000.00") : originalDeposit;
    }
}
