package com.ruoyi.system.service.impl;

import com.ruoyi.system.service.WechatPayService;
import com.wechat.pay.java.core.RSAAutoCertificateConfig;
import com.wechat.pay.java.core.notification.NotificationParser;
import com.wechat.pay.java.core.notification.RequestParam;
import com.wechat.pay.java.service.payments.h5.H5Service;
import com.wechat.pay.java.service.payments.h5.model.PrepayRequest;
import com.wechat.pay.java.service.payments.h5.model.PrepayResponse;
import com.wechat.pay.java.service.payments.jsapi.JsapiService;
import com.wechat.pay.java.service.payments.jsapi.model.Amount;
import com.wechat.pay.java.service.payments.jsapi.model.Payer;
import com.wechat.pay.java.service.payments.jsapi.model.PrepayWithRequestPaymentResponse;
import com.wechat.pay.java.service.payments.model.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * 微信支付 Service 实现
 * 使用 wechatpay-java SDK 0.2.14
 */
@Service
public class WechatPayServiceImpl implements WechatPayService {

    private static final Logger log = LoggerFactory.getLogger(WechatPayServiceImpl.class);

    @Value("${wechat.pay.appid}")
    private String appid;

    @Value("${wechat.pay.mch-id}")
    private String mchId;

    private final JsapiService jsapiService;
    private final H5Service h5Service;
    private final RSAAutoCertificateConfig rsaConfig;

    public WechatPayServiceImpl(JsapiService jsapiService,
                                H5Service h5Service,
                                RSAAutoCertificateConfig rsaConfig) {
        this.jsapiService = jsapiService;
        this.h5Service = h5Service;
        this.rsaConfig = rsaConfig;
    }

    @Override
    public Map<String, String> prepayJsapi(String billNo, int totalFen, String desc,
                                           String openid, String notifyUrl) {
        com.wechat.pay.java.service.payments.jsapi.model.PrepayRequest request =
                new com.wechat.pay.java.service.payments.jsapi.model.PrepayRequest();
        request.setAppid(appid);
        request.setMchid(mchId);
        request.setDescription(desc);
        request.setOutTradeNo(billNo);
        request.setNotifyUrl(notifyUrl);

        Amount amount = new Amount();
        amount.setTotal(totalFen);
        amount.setCurrency("CNY");
        request.setAmount(amount);

        Payer payer = new Payer();
        payer.setOpenid(openid);
        request.setPayer(payer);

        PrepayWithRequestPaymentResponse response = jsapiService.prepayWithRequestPayment(request);

        Map<String, String> result = new HashMap<>();
        result.put("appId", response.getAppId());
        result.put("timeStamp", response.getTimeStamp());
        result.put("nonceStr", response.getNonceStr());
        result.put("package", response.getPackageVal());
        result.put("signType", response.getSignType());
        result.put("paySign", response.getPaySign());
        return result;
    }

    @Override
    public String prepayH5(String billNo, int totalFen, String desc,
                           String clientIp, String notifyUrl) {
        PrepayRequest request = new PrepayRequest();
        request.setAppid(appid);
        request.setMchid(mchId);
        request.setDescription(desc);
        request.setOutTradeNo(billNo);
        request.setNotifyUrl(notifyUrl);

        com.wechat.pay.java.service.payments.h5.model.Amount amount =
                new com.wechat.pay.java.service.payments.h5.model.Amount();
        amount.setTotal(totalFen);
        amount.setCurrency("CNY");
        request.setAmount(amount);

        com.wechat.pay.java.service.payments.h5.model.SceneInfo sceneInfo =
                new com.wechat.pay.java.service.payments.h5.model.SceneInfo();
        sceneInfo.setPayerClientIp(clientIp);

        com.wechat.pay.java.service.payments.h5.model.H5Info h5Info =
                new com.wechat.pay.java.service.payments.h5.model.H5Info();
        h5Info.setType("Wap");
        sceneInfo.setH5Info(h5Info);
        request.setSceneInfo(sceneInfo);

        PrepayResponse response = h5Service.prepay(request);
        return response.getH5Url();
    }

    /**
     * 解析微信支付回调通知
     * 使用 SDK 的 NotificationParser.parse(RequestParam, Transaction.class) 进行验签+解密
     * 将 Transaction 对象的关键字段提取为 Map 返回给 Controller
     */
    @Override
    public Map<String, Object> parseNotify(byte[] requestBody, Map<String, String> headers) throws Exception {
        // 构建 RequestParam，SDK 要求原始报文体 + 四个关键 Header
        RequestParam requestParam = new RequestParam.Builder()
                .serialNumber(headers.get("Wechatpay-Serial"))
                .nonce(headers.get("Wechatpay-Nonce"))
                .signature(headers.get("Wechatpay-Signature"))
                .timestamp(headers.get("Wechatpay-Timestamp"))
                .body(new String(requestBody, "UTF-8"))
                .build();

        // NotificationParser 构造传入 RSAAutoCertificateConfig，SDK 自动验签+解密
        NotificationParser parser = new NotificationParser(rsaConfig);

        // 解析为 Transaction 对象（SDK 0.2.14 的通用解析方法）
        // getTradeState() 返回 TradeStateEnum，需要转为字符串
        Transaction transaction = parser.parse(requestParam, Transaction.class);

        log.info("微信支付回调解析成功，outTradeNo={}, tradeState={}",
                transaction.getOutTradeNo(), transaction.getTradeState());

        // 提取关键字段到 Map，供 Controller 使用
        Map<String, Object> result = new HashMap<>();
        result.put("out_trade_no", transaction.getOutTradeNo());
        result.put("transaction_id", transaction.getTransactionId());
        // TradeStateEnum.name() 返回枚举名，如 "SUCCESS"、"NOTPAY" 等
        result.put("trade_state", transaction.getTradeState() != null
                ? transaction.getTradeState().name() : null);
        result.put("trade_state_desc", transaction.getTradeStateDesc());
        result.put("success_time", transaction.getSuccessTime());
        return result;
    }
}
