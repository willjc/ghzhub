package com.ruoyi.system.service.impl;

import com.ruoyi.system.domain.HzUser;

/** 无数据库、无网络的微信重绑回归入口；在允许测试的环境中运行 main。 */
public class WechatRebindCheck {
    public static void main(String[] args) {
        check("0", "0", true);
        check("1", "0", false);
        check("2", "0", false);
        check("0", "1", false);
        check(null, null, false);
    }

    private static void check(String authStatus, String isInfoCompleted, boolean expected) {
        HzUser user = new HzUser();
        user.setAuthStatus(authStatus);
        user.setIsInfoCompleted(isInfoCompleted);
        if (HzUserServiceImpl.canRebindWechatOpenid(user) != expected) {
            throw new AssertionError("微信重绑条件判断错误");
        }
    }
}
