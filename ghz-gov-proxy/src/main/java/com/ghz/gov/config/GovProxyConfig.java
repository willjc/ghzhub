package com.ghz.gov.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "gov.proxy")
public class GovProxyConfig {

    /** 业务系统调用时需传入的API Key */
    private String apiKey = "ghz-gov-proxy-key-2026";

    public String getApiKey() { return apiKey; }
    public void setApiKey(String apiKey) { this.apiKey = apiKey; }
}
