package com.ruoyi.framework.web.service;

import java.time.OffsetDateTime;
import java.time.format.DateTimeParseException;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import com.ruoyi.common.constant.CacheConstants;
import com.ruoyi.common.constant.Constants;
import com.ruoyi.common.core.domain.model.HzLoginUser;
import com.ruoyi.common.core.redis.RedisCache;
import com.ruoyi.common.utils.uuid.IdUtils;
import com.ruoyi.common.utils.StringUtils;

/**
 * 微信小程序用户令牌服务。
 *
 * 新令牌使用不可预测随机值，服务端 Redis 保存会话并校验有效期。
 * 发布过渡期可通过配置临时接受旧版 hz_token_{userId}_{timestamp} 令牌；
 * 旧令牌没有签名，只用于兼容已发布客户端，并应在新版覆盖后关闭。
 */
@Component
public class HzUserTokenService
{
    private static final Logger log = LoggerFactory.getLogger(HzUserTokenService.class);

    private static final String TOKEN_PREFIX = "hzu_";

    private static final String LEGACY_TOKEN_PREFIX = "hz_token_";

    private static final long MILLIS_MINUTE = 60_000L;

    private static final long REFRESH_THRESHOLD = 20 * MILLIS_MINUTE;

    @Value("${hz-user-token.expire-time:1440}")
    private int expireTime;

    @Value("${token.header:Authorization}")
    private String tokenHeader;

    @Value("${ghz.legacy-auth.enabled:false}")
    private boolean legacyAuthEnabled;

    @Value("${ghz.legacy-auth.accept-until:2026-10-31T23:59:59+08:00}")
    private String legacyAcceptUntil;

    private long legacyAcceptUntilMillis;

    /** 每个旧版用户每次进程启动只记录一次，便于观察存量客户端且避免刷日志。 */
    private final Set<Long> loggedLegacyUsers = ConcurrentHashMap.newKeySet();

    @Autowired
    private RedisCache redisCache;

    @PostConstruct
    public void initLegacyCompatibility()
    {
        if (!legacyAuthEnabled)
        {
            return;
        }
        try
        {
            legacyAcceptUntilMillis = OffsetDateTime.parse(legacyAcceptUntil).toInstant().toEpochMilli();
            log.warn("旧版小程序鉴权兼容已开启，将于 {} 自动停止接受旧Token", legacyAcceptUntil);
        }
        catch (DateTimeParseException e)
        {
            legacyAcceptUntilMillis = 0L;
            log.error("旧版鉴权截止时间配置无效，兼容功能已自动关闭: {}", legacyAcceptUntil);
        }
    }

    public String createToken(Long userId)
    {
        return createToken(userId, false);
    }

    public String createToken(Long userId, boolean debugSwitchAllowed)
    {
        String token = TOKEN_PREFIX + IdUtils.fastUUID();
        HzLoginUser loginUser = new HzLoginUser(userId);
        loginUser.setToken(token);
        loginUser.setDebugSwitchAllowed(debugSwitchAllowed);
        refreshToken(loginUser);
        return token;
    }

    public HzLoginUser getLoginUser(HttpServletRequest request)
    {
        String token = getToken(request);
        if (isHzUserToken(token))
        {
            HzLoginUser loginUser = redisCache.getCacheObject(getTokenKey(token));
            if (loginUser != null && loginUser.getUserId() != null)
            {
                verifyToken(loginUser);
                return loginUser;
            }
            return null;
        }

        if (isLegacyToken(token) && isLegacyCompatibilityActive())
        {
            return parseLegacyLoginUser(token);
        }
        return null;
    }

    public void deleteLoginUser(HttpServletRequest request)
    {
        String token = getToken(request);
        if (isHzUserToken(token))
        {
            redisCache.deleteObject(getTokenKey(token));
        }
    }

    public boolean hasHzUserToken(HttpServletRequest request)
    {
        String token = getToken(request);
        return isHzUserToken(token) || isLegacyToken(token);
    }

    public boolean isLegacyCompatibilityActive()
    {
        return legacyAuthEnabled
                && legacyAcceptUntilMillis > 0L
                && System.currentTimeMillis() <= legacyAcceptUntilMillis;
    }

    private void verifyToken(HzLoginUser loginUser)
    {
        long currentTime = System.currentTimeMillis();
        if (loginUser.getExpireTime() == null || loginUser.getExpireTime() - currentTime <= REFRESH_THRESHOLD)
        {
            refreshToken(loginUser);
        }
    }

    private void refreshToken(HzLoginUser loginUser)
    {
        loginUser.setLoginTime(System.currentTimeMillis());
        loginUser.setExpireTime(loginUser.getLoginTime() + expireTime * MILLIS_MINUTE);
        redisCache.setCacheObject(getTokenKey(loginUser.getToken()), loginUser, expireTime, TimeUnit.MINUTES);
    }

    private String getToken(HttpServletRequest request)
    {
        // 小程序统一使用配置中的 Authorization；同时兼容历史上使用的 token 请求头。
        String token = request.getHeader(tokenHeader);
        if (StringUtils.isEmpty(token) && !Constants.TOKEN.equalsIgnoreCase(tokenHeader))
        {
            token = request.getHeader(Constants.TOKEN);
        }
        if (StringUtils.isNotEmpty(token) && token.startsWith(Constants.TOKEN_PREFIX))
        {
            token = token.substring(Constants.TOKEN_PREFIX.length());
        }
        return token;
    }

    private boolean isHzUserToken(String token)
    {
        return StringUtils.isNotEmpty(token) && token.startsWith(TOKEN_PREFIX);
    }

    private boolean isLegacyToken(String token)
    {
        return StringUtils.isNotEmpty(token) && token.startsWith(LEGACY_TOKEN_PREFIX);
    }

    private HzLoginUser parseLegacyLoginUser(String token)
    {
        int timestampSeparator = token.lastIndexOf('_');
        if (timestampSeparator <= LEGACY_TOKEN_PREFIX.length() || timestampSeparator >= token.length() - 1)
        {
            return null;
        }

        try
        {
            Long userId = Long.valueOf(token.substring(LEGACY_TOKEN_PREFIX.length(), timestampSeparator));
            long loginTime = Long.parseLong(token.substring(timestampSeparator + 1));
            long currentTime = System.currentTimeMillis();
            if (userId <= 0L || loginTime <= 0L || loginTime > currentTime + 5 * MILLIS_MINUTE)
            {
                return null;
            }

            HzLoginUser loginUser = new HzLoginUser(userId);
            loginUser.setToken(token);
            loginUser.setLoginTime(loginTime);
            loginUser.setExpireTime(legacyAcceptUntilMillis);
            if (loggedLegacyUsers.add(userId))
            {
                log.warn("接受旧版小程序Token，userId={}；请在新版覆盖后关闭 ghz.legacy-auth.enabled", userId);
            }
            return loginUser;
        }
        catch (NumberFormatException e)
        {
            return null;
        }
    }

    private String getTokenKey(String token)
    {
        return CacheConstants.HZ_USER_TOKEN_KEY + token;
    }
}
