package com.ruoyi.web.service;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ruoyi.common.core.redis.RedisCache;
import com.ruoyi.web.config.WechatMiniappConfig;

/**
 * 微信小程序服务
 * 负责调用微信API：jscode2session获取openid/unionid、getuserphonenumber获取手机号、access_token缓存管理
 *
 * @author ruoyi
 */
@Service
public class WechatMiniappService
{
    private static final Logger log = LoggerFactory.getLogger(WechatMiniappService.class);

    /** 微信jscode2session接口 */
    private static final String JSCODE2SESSION_URL = "https://api.weixin.qq.com/sns/jscode2session?appid=%s&secret=%s&js_code=%s&grant_type=authorization_code";

    /** 微信获取手机号接口 */
    private static final String GET_PHONE_URL = "https://api.weixin.qq.com/wxa/business/getuserphonenumber?access_token=%s";

    /** 微信获取access_token接口 */
    private static final String ACCESS_TOKEN_URL = "https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid=%s&secret=%s";

    /** Redis缓存key：access_token */
    private static final String CACHE_KEY_ACCESS_TOKEN = "wechat:miniapp:access_token";

    /** access_token缓存时间（秒），微信默认7200秒，提前200秒刷新 */
    private static final int ACCESS_TOKEN_CACHE_SECONDS = 7000;

    /** 微信错误码：access_token过期 */
    private static final int ERRCODE_TOKEN_EXPIRED = 40001;

    @Autowired
    private RedisCache redisCache;

    @Autowired
    private WechatMiniappConfig wechatConfig;

    // 不抛4xx/5xx异常，让业务代码自行处理响应体中的errcode
    private final RestTemplate restTemplate = new RestTemplate();
    {
        restTemplate.setErrorHandler(new org.springframework.web.client.DefaultResponseErrorHandler() {
            @Override
            public boolean hasError(@org.springframework.lang.NonNull org.springframework.http.client.ClientHttpResponse response) {
                return false;
            }
        });
    }

    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * 通过微信登录code获取openid和unionid
     *
     * @param code 微信登录凭证code
     * @return String数组 [openid, unionid]，unionid可能为null
     */
    public String[] getOpenidAndUnionid(String code)
    {
        String url = String.format(JSCODE2SESSION_URL, wechatConfig.getAppid(), wechatConfig.getSecret(), code);
        log.info("调用微信jscode2session接口, code={}", code);

        try
        {
            ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
            String body = response.getBody();
            if (body == null || body.isEmpty()) {
                throw new RuntimeException("微信登录失败: jscode2session返回空响应");
            }
            JsonNode jsonNode = objectMapper.readTree(body);

            // 检查错误码
            if (jsonNode.has("errcode") && jsonNode.get("errcode").asInt() != 0)
            {
                String errMsg = jsonNode.has("errmsg") ? jsonNode.get("errmsg").asText() : "未知错误";
                int errCode = jsonNode.get("errcode").asInt();
                log.error("微信jscode2session失败, errcode={}, errmsg={}", errCode, errMsg);
                throw new RuntimeException("微信登录失败: " + errMsg);
            }

            String openid = jsonNode.has("openid") ? jsonNode.get("openid").asText() : null;
            String unionid = jsonNode.has("unionid") ? jsonNode.get("unionid").asText() : null;

            if (openid == null)
            {
                log.error("微信jscode2session返回数据缺少openid");
                throw new RuntimeException("微信登录失败: 未获取到openid");
            }

            // 安全日志：只打印openid前8位
            String maskedOpenid = openid.length() > 8 ? openid.substring(0, 8) + "***" : openid;
            log.info("微信jscode2session成功, openid={}, unionid={}", maskedOpenid, unionid != null ? "有" : "无");

            return new String[] { openid, unionid };
        }
        catch (RuntimeException e)
        {
            throw e;
        }
        catch (Exception e)
        {
            log.error("调用微信jscode2session接口异常", e);
            throw new RuntimeException("微信登录失败: 网络异常", e);
        }
    }

    /**
     * 通过手机号code获取用户手机号
     *
     * @param phoneCode 手机号获取凭证code
     * @return 手机号
     */
    public String getPhoneNumber(String phoneCode)
    {
        try
        {
            return getPhoneNumberInternal(phoneCode);
        }
        catch (RuntimeException e)
        {
            // 如果是token过期错误，清除缓存重试一次
            if (e.getMessage() != null && e.getMessage().contains("40001"))
            {
                log.warn("access_token过期，清除缓存后重试");
                return getPhoneNumberRetry(phoneCode);
            }
            throw e;
        }
    }

    /**
     * 获取手机号内部实现
     *
     * @param phoneCode 手机号获取凭证code
     * @return 手机号
     */
    private String getPhoneNumberInternal(String phoneCode)
    {
        String accessToken = getAccessToken();
        String url = String.format(GET_PHONE_URL, accessToken);
        log.info("调用微信获取手机号接口");

        try
        {
            // 构建请求体
            Map<String, String> requestBody = new HashMap<>();
            requestBody.put("code", phoneCode);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<Map<String, String>> httpEntity = new HttpEntity<>(requestBody, headers);

            ResponseEntity<String> response = restTemplate.postForEntity(url, httpEntity, String.class);
            String responseBody = response.getBody();
            log.info("微信getuserphonenumber响应: status={}, body={}", response.getStatusCode(), responseBody);
            if (responseBody == null || responseBody.isEmpty()) {
                log.error("微信getuserphonenumber返回空响应体, status={}", response.getStatusCode());
                throw new RuntimeException("微信获取手机号失败: 接口返回空响应(status=" + response.getStatusCode() + ")");
            }
            JsonNode jsonNode = objectMapper.readTree(responseBody);

            // 检查错误码
            if (jsonNode.has("errcode") && jsonNode.get("errcode").asInt() != 0)
            {
                int errCode = jsonNode.get("errcode").asInt();
                String errMsg = jsonNode.has("errmsg") ? jsonNode.get("errmsg").asText() : "未知错误";
                log.error("微信获取手机号失败, errcode={}, errmsg={}", errCode, errMsg);

                if (errCode == ERRCODE_TOKEN_EXPIRED)
                {
                    throw new RuntimeException("微信获取手机号失败[40001]: access_token过期");
                }
                throw new RuntimeException("微信获取手机号失败: " + errMsg);
            }

            // 解析phone_info.phoneNumber
            JsonNode phoneInfo = jsonNode.get("phone_info");
            if (phoneInfo == null || !phoneInfo.has("phoneNumber"))
            {
                log.error("微信获取手机号返回数据缺少phone_info.phoneNumber");
                throw new RuntimeException("微信获取手机号失败: 未获取到手机号");
            }

            String phoneNumber = phoneInfo.get("phoneNumber").asText();

            // 安全日志：手机号脱敏（前3 + **** + 后4）
            String maskedPhone = phoneNumber.length() >= 7
                    ? phoneNumber.substring(0, 3) + "****" + phoneNumber.substring(phoneNumber.length() - 4)
                    : phoneNumber;
            log.info("微信获取手机号成功, phone={}", maskedPhone);

            return phoneNumber;
        }
        catch (RuntimeException e)
        {
            throw e;
        }
        catch (Exception e)
        {
            log.error("调用微信获取手机号接口异常", e);
            throw new RuntimeException("微信获取手机号失败: 网络异常", e);
        }
    }

    /**
     * access_token过期后清除缓存并重试获取手机号
     *
     * @param phoneCode 手机号获取凭证code
     * @return 手机号
     */
    private String getPhoneNumberRetry(String phoneCode)
    {
        log.info("清除access_token缓存，重新获取手机号");
        redisCache.deleteObject(CACHE_KEY_ACCESS_TOKEN);
        return getPhoneNumberInternal(phoneCode);
    }

    /**
     * 获取微信小程序access_token（带Redis缓存）
     *
     * @return access_token
     */
    public String getAccessToken()
    {
        // 优先从Redis缓存获取
        String cachedToken = redisCache.getCacheObject(CACHE_KEY_ACCESS_TOKEN);
        if (cachedToken != null)
        {
            log.debug("从Redis缓存获取access_token");
            return cachedToken;
        }

        // 缓存未命中，调用微信API获取
        return fetchAndCacheAccessToken(true);
    }

    /**
     * 调用微信API获取access_token并缓存到Redis
     *
     * @param allowRetry 是否允许失败重试
     * @return access_token
     */
    private String fetchAndCacheAccessToken(boolean allowRetry)
    {
        String url = String.format(ACCESS_TOKEN_URL, wechatConfig.getAppid(), wechatConfig.getSecret());
        log.info("调用微信获取access_token接口");

        try
        {
            ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
            String tokenBody = response.getBody();
            if (tokenBody == null || tokenBody.isEmpty()) {
                throw new RuntimeException("微信获取access_token失败: 返回空响应");
            }
            JsonNode jsonNode = objectMapper.readTree(tokenBody);

            // 检查错误码
            if (jsonNode.has("errcode") && jsonNode.get("errcode").asInt() != 0)
            {
                String errMsg = jsonNode.has("errmsg") ? jsonNode.get("errmsg").asText() : "未知错误";
                int errCode = jsonNode.get("errcode").asInt();
                log.error("微信获取access_token失败, errcode={}, errmsg={}", errCode, errMsg);

                // 允许重试一次
                if (allowRetry)
                {
                    log.warn("获取access_token失败，重试一次");
                    return fetchAndCacheAccessToken(false);
                }
                throw new RuntimeException("微信获取access_token失败: " + errMsg);
            }

            String accessToken = jsonNode.get("access_token").asText();

            // 缓存到Redis，7000秒（微信默认7200秒，提前刷新）
            redisCache.setCacheObject(CACHE_KEY_ACCESS_TOKEN, accessToken, ACCESS_TOKEN_CACHE_SECONDS, TimeUnit.SECONDS);
            log.info("access_token已缓存到Redis, 有效期{}秒", ACCESS_TOKEN_CACHE_SECONDS);

            return accessToken;
        }
        catch (RuntimeException e)
        {
            if (allowRetry)
            {
                log.warn("获取access_token异常，重试一次", e);
                return fetchAndCacheAccessToken(false);
            }
            throw e;
        }
        catch (Exception e)
        {
            log.error("调用微信获取access_token接口异常", e);
            if (allowRetry)
            {
                log.warn("获取access_token异常，重试一次");
                return fetchAndCacheAccessToken(false);
            }
            throw new RuntimeException("微信获取access_token失败: 网络异常", e);
        }
    }
}
