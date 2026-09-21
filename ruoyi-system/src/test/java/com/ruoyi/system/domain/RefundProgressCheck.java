package com.ruoyi.system.domain;

/** 无数据库、无网络的退款状态回归入口；在允许测试的环境中运行 main。 */
public class RefundProgressCheck {
    public static void main(String[] args) {
        check("0", true, false, true, false);
        check("2", true, true, true, false);
        check("2", true, false, true, true);
        check("1", true, true, true, true);
        check("1", true, true, false, false);
        check("1", false, false, true, true);
    }

    private static void check(String expected, boolean depositRequired, boolean depositOk,
            boolean rentRequired, boolean rentOk) {
        String actual = HzCheckoutRecord.resolveRefundStatus(
                depositRequired, depositOk, rentRequired, rentOk);
        if (!expected.equals(actual)) {
            throw new AssertionError("refund status: expected " + expected + ", actual " + actual);
        }
    }
}
