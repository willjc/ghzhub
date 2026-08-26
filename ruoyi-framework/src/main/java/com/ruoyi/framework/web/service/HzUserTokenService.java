package com.ruoyi.framework.web.service;

import java.util.concurrent.TimeUnit;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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
 * 令牌使用不可预测随机值，服务端 Redis 保存会话并校验有效期；旧版可伪造的
 * hz_token_{userId}_{timestamp} 令牌不会被接受。
 */
@Component
public class HzUserTokenService
{
    private static final String TOKEN_PREFIX = "hzu_";

    private static final long MILLIS_MINUTE = 60_000L;

    private static final long REFRESH_THRESHOLD = 20 * MILLIS_MINUTE;

    @Value("${hz-user-token.expire-time:1440}")
    private int expireTime;

    @Autowired
    private RedisCache redisCache;

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
        if (!isHzUserToken(token))
        {
            return null;
        }
        HzLoginUser loginUser = redisCache.getCacheObject(getTokenKey(token));
        if (loginUser != null && loginUser.getUserId() != null)
        {
            verifyToken(loginUser);
            return loginUser;
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
        return isHzUserToken(getToken(request));
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
        String token = request.getHeader(Constants.TOKEN);
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

    private String getTokenKey(String token)
    {
        return CacheConstants.HZ_USER_TOKEN_KEY + token;
    }
}
