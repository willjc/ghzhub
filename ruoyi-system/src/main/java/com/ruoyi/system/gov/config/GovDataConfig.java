package com.ruoyi.system.gov.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 政务数据代理服务配置
 */
@Component
@ConfigurationProperties(prefix = "gov.proxy")
public class GovDataConfig {

    /** 代理服务地址，如 http://政务外网IP:9001 */
    private String baseUrl = "http://127.0.0.1:9001";

    /** API Key */
    private String apiKey = "ghz-gov-proxy-key-2026";

    /** 连接超时（毫秒） */
    private int connectTimeout = 10000;

    /** 读超时（毫秒） */
    private int readTimeout = 30000;

    // ========== getter/setter ==========

    public String getBaseUrl() { return baseUrl; }
    public void setBaseUrl(String baseUrl) { this.baseUrl = baseUrl; }
    public String getApiKey() { return apiKey; }
    public void setApiKey(String apiKey) { this.apiKey = apiKey; }
    public int getConnectTimeout() { return connectTimeout; }
    public void setConnectTimeout(int connectTimeout) { this.connectTimeout = connectTimeout; }
    public int getReadTimeout() { return readTimeout; }
    public void setReadTimeout(int readTimeout) { this.readTimeout = readTimeout; }
}
