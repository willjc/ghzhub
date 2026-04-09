package com.ruoyi.web.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * 微信小程序配置
 */
@Configuration
@ConfigurationProperties(prefix = "wechat.miniapp")
public class WechatMiniappConfig {

    private String appid;
    private String secret;

    public String getAppid() {
        return appid;
    }

    public void setAppid(String appid) {
        this.appid = appid;
    }

    public String getSecret() {
        return secret;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }
}
