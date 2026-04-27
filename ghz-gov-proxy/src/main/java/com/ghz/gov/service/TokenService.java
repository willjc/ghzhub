package com.ghz.gov.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.ghz.gov.sdk.SdkTokenApp;
import com.iflytek.fsp.shield.java.sdk.model.ApiResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.nio.charset.StandardCharsets;

/**
 * Token管理服务
 * - 懒加载获取Token
 * - 定时刷新（每25分钟，Token有效期30分钟，留5分钟余量）
 * - 并发锁保护，防止同时多次刷新
 */
@Service
public class TokenService {

    private static final Logger log = LoggerFactory.getLogger(TokenService.class);

    /** Token缓存 */
    private volatile String accessToken;
    /** Token过期时间戳（毫秒） */
    private volatile long expireTime = 0;

    private final SdkTokenApp tokenApp = new SdkTokenApp();

    /** 刷新锁 */
    private final Object refreshLock = new Object();

    /** 提前刷新时间（Token有效期30分钟=1800秒，提前5分钟=300秒刷新） */
    private static final long REFRESH_BEFORE_MS = 300_000L;

    /**
     * 获取有效的 access_token
     */
    public String getAccessToken() {
        if (isTokenValid()) {
            return accessToken;
        }
        synchronized (refreshLock) {
            if (isTokenValid()) {
                return accessToken;
            }
            refreshToken();
            return accessToken;
        }
    }

    /**
     * 检查Token是否有效
     */
    private boolean isTokenValid() {
        return accessToken != null && System.currentTimeMillis() < expireTime - REFRESH_BEFORE_MS;
    }

    /**
     * 强制刷新Token
     */
    private void refreshToken() {
        log.info("开始获取 access_token ...");
        try {
            // grant_type=client_credentials, client_id和client_secret使用Token App的AppId/AppSecret
            ApiResponse response = tokenApp.getToken(
                    "client_credentials",
                    "b2619ca04a2446dea85f741328ddc238",
                    "763F673675DE32D1DFA9D2F2F11A2916"
            );

            if (response.getStatusCode() != 200) {
                String errBody = response.getBody() != null
                        ? new String(response.getBody(), StandardCharsets.UTF_8) : "";
                log.error("获取Token失败, statusCode={}, body={}", response.getStatusCode(), errBody);
                throw new RuntimeException("获取Token失败: " + errBody);
            }

            String body = new String(response.getBody(), StandardCharsets.UTF_8);
            log.debug("Token响应: {}", body);

            JSONObject json = JSON.parseObject(body);
            // 响应格式: {"controls":[],"custom":{"access_token":"xxx","expires_in":"1800"},"status":{"code":"1"}}
            JSONObject custom = json.getJSONObject("custom");
            if (custom == null) {
                throw new RuntimeException("Token响应格式异常，缺少custom字段: " + body);
            }

            this.accessToken = custom.getString("access_token");
            Integer expiresIn = custom.getInteger("expires_in");
            if (expiresIn == null) {
                expiresIn = 1800; // 默认30分钟
            }
            this.expireTime = System.currentTimeMillis() + expiresIn * 1000L;

            log.info("Token获取成功, 有效期{}秒, 过期时间={}", expiresIn,
                    new java.text.SimpleDateFormat("HH:mm:ss").format(new java.util.Date(expireTime)));

        } catch (Exception e) {
            log.error("获取Token异常", e);
            throw new RuntimeException("获取Token失败: " + e.getMessage(), e);
        }
    }

    /**
     * 定时刷新Token（每25分钟执行）
     */
    @Scheduled(fixedDelay = 25 * 60 * 1000)
    public void scheduledRefresh() {
        log.info("定时刷新Token任务触发");
        try {
            synchronized (refreshLock) {
                refreshToken();
            }
        } catch (Exception e) {
            log.error("定时刷新Token失败", e);
        }
    }

    /**
     * 获取Token状态（供健康检查接口使用）
     */
    public TokenStatus getStatus() {
        return new TokenStatus(
                accessToken != null,
                isTokenValid(),
                expireTime,
                System.currentTimeMillis()
        );
    }

    public static class TokenStatus {
        private final boolean hasToken;
        private final boolean isValid;
        private final long expireTime;
        private final long currentTime;
        private final long remainSeconds;

        public TokenStatus(boolean hasToken, boolean isValid, long expireTime, long currentTime) {
            this.hasToken = hasToken;
            this.isValid = isValid;
            this.expireTime = expireTime;
            this.currentTime = currentTime;
            this.remainSeconds = (expireTime - currentTime) / 1000;
        }

        public boolean isHasToken() { return hasToken; }
        public boolean isValid() { return isValid; }
        public long getExpireTime() { return expireTime; }
        public long getCurrentTime() { return currentTime; }
        public long getRemainSeconds() { return remainSeconds; }
    }
}
