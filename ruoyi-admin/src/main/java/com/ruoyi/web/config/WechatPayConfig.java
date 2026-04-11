package com.ruoyi.web.config;

import com.wechat.pay.java.core.RSAPublicKeyConfig;
import com.wechat.pay.java.service.payments.h5.H5Service;
import com.wechat.pay.java.service.payments.jsapi.JsapiServiceExtension;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ResourceLoader;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

/**
 * 微信支付配置（使用微信支付公钥模式，适用于2024年后新注册商户）
 */
@Configuration
@ConditionalOnProperty(prefix = "wechat.pay", name = "enabled", havingValue = "true")
public class WechatPayConfig {

    @Value("${wechat.pay.appid}")
    private String appid;

    @Value("${wechat.pay.mch-id}")
    private String mchId;

    @Value("${wechat.pay.api-v3-key}")
    private String apiV3Key;

    @Value("${wechat.pay.private-key-path}")
    private String privateKeyPath;

    @Value("${wechat.pay.cert-serial-no}")
    private String certSerialNo;

    /** 微信支付公钥ID，格式：PUB_KEY_ID_01... */
    @Value("${wechat.pay.public-key-id}")
    private String publicKeyId;

    /** 微信支付公钥文件路径（从商户平台下载的 .pem 文件） */
    @Value("${wechat.pay.public-key-path}")
    private String publicKeyPath;

    private final ResourceLoader resourceLoader;

    public WechatPayConfig(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }

    @Bean
    public RSAPublicKeyConfig rsaPublicKeyConfig() throws IOException {
        InputStream privateKeyStream = resourceLoader.getResource(privateKeyPath).getInputStream();
        String privateKey = new String(privateKeyStream.readAllBytes(), StandardCharsets.UTF_8);

        InputStream publicKeyStream = resourceLoader.getResource(publicKeyPath).getInputStream();
        String publicKey = new String(publicKeyStream.readAllBytes(), StandardCharsets.UTF_8);

        return new RSAPublicKeyConfig.Builder()
                .merchantId(mchId)
                .privateKey(privateKey)
                .merchantSerialNumber(certSerialNo)
                .apiV3Key(apiV3Key)
                .publicKeyId(publicKeyId)
                .publicKey(publicKey)
                .build();
    }

    @Bean
    public JsapiServiceExtension jsapiService(RSAPublicKeyConfig config) {
        return new JsapiServiceExtension.Builder().config(config).build();
    }

    @Bean
    public H5Service h5Service(RSAPublicKeyConfig config) {
        return new H5Service.Builder().config(config).build();
    }
}
