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

    /**
     * 主动向微信查单（按商户订单号），用于回调未到达时的兜底同步
     * 返回 trade_state（SUCCESS/NOTPAY/CLOSED 等）和 transaction_id
     */
    Map<String, Object> queryByOutTradeNo(String outTradeNo) throws Exception;

    /**
     * 申请微信退款（原路退款）
     *
     * @param transactionId 原支付微信交易号
     * @param outRefundNo   商户退款单号（唯一）
     * @param refundFen     退款金额（分）
     * @param totalFen      原订单金额（分）
     * @param reason        退款原因
     * @return 退款结果，含 refund_id、status、out_refund_no
     */
    Map<String, Object> wechatRefund(String transactionId, String outRefundNo,
                                     int refundFen, int totalFen, String reason);
}
