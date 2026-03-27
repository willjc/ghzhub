package com.ruoyi.system.service;

import java.util.Map;

public interface WechatPayService {

    /**
     * JSAPI 预支付（微信内H5或小程序调用）
     */
    Map<String, String> prepayJsapi(String billNo, int totalFen, String desc,
                                    String openid, String notifyUrl);

    /**
     * H5 WAP 预支付（普通浏览器调用）
     */
    String prepayH5(String billNo, int totalFen, String desc,
                    String clientIp, String notifyUrl);

    /**
     * 处理微信支付回调 — 验签+解密，返回解密后的订单信息
     */
    Map<String, Object> parseNotify(byte[] requestBody, Map<String, String> headers) throws Exception;
}
