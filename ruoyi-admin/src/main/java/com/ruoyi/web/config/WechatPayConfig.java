package com.ruoyi.web.config;

import com.wechat.pay.java.core.RSAAutoCertificateConfig;
import com.wechat.pay.java.service.payments.h5.H5Service;
import com.wechat.pay.java.service.payments.jsapi.JsapiService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ResourceLoader;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

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

    private final ResourceLoader resourceLoader;

    public WechatPayConfig(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }

    @Bean
    public RSAAutoCertificateConfig rsaAutoCertificateConfig() throws IOException {
        InputStream keyStream = resourceLoader.getResource(privateKeyPath).getInputStream();
        String privateKey = new String(keyStream.readAllBytes(), StandardCharsets.UTF_8);
        return new RSAAutoCertificateConfig.Builder()
                .merchantId(mchId)
                .privateKey(privateKey)
                .merchantSerialNumber(certSerialNo)
                .apiV3Key(apiV3Key)
                .build();
    }

    @Bean
    public JsapiService jsapiService(RSAAutoCertificateConfig config) {
        return new JsapiService.Builder().config(config).build();
    }

    @Bean
    public H5Service h5Service(RSAAutoCertificateConfig config) {
        return new H5Service.Builder().config(config).build();
    }
}
