package com.ruoyi.web.core.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 郑好办对接配置
 *
 * @author ruoyi
 */
@Component
@ConfigurationProperties(prefix = "zhenghaoban")
public class ZhengHaobanConfig {

    /**
     * 网关配置
     */
    private GatewayConfig gateway;

    /**
     * 应用配置
     */
    private AppConfig app;

    public GatewayConfig getGateway() {
        return gateway;
    }

    public void setGateway(GatewayConfig gateway) {
        this.gateway = gateway;
    }

    public AppConfig getApp() {
        return app;
    }

    public void setApp(AppConfig app) {
        this.app = app;
    }

    /**
     * 网关配置
     */
    public static class GatewayConfig {
        /**
         * 网关地址
         */
        private String url;

        /**
         * 网关应用ID
         */
        private String appId;

        /**
         * 网关加密参数IV
         */
        private String ivStr;

        /**
         * 网关加密参数Key
         */
        private String keyStr;

        /**
         * 网关签名密钥
         */
        private String secretKey;

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public String getAppId() {
            return appId;
        }

        public void setAppId(String appId) {
            this.appId = appId;
        }

        public String getIvStr() {
            return ivStr;
        }

        public void setIvStr(String ivStr) {
            this.ivStr = ivStr;
        }

        public String getKeyStr() {
            return keyStr;
        }

        public void setKeyStr(String keyStr) {
            this.keyStr = keyStr;
        }

        public String getSecretKey() {
            return secretKey;
        }

        public void setSecretKey(String secretKey) {
            this.secretKey = secretKey;
        }
    }

    /**
     * 应用配置
     */
    public static class AppConfig {
        /**
         * 模块ID（应用ID）
         */
        private String moduleId;

        public String getModuleId() {
            return moduleId;
        }

        public void setModuleId(String moduleId) {
            this.moduleId = moduleId;
        }
    }
}
