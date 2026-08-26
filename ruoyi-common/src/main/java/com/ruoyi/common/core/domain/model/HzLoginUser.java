package com.ruoyi.common.core.domain.model;

import java.io.Serializable;

/**
 * 微信小程序用户登录会话。
 *
 * 与管理后台的 LoginUser 分开，避免 hz_user 与 sys_user 身份混用。
 */
public class HzLoginUser implements Serializable
{
    private static final long serialVersionUID = 1L;

    private Long userId;

    private String token;

    private Long loginTime;

    private Long expireTime;

    /** 当前会话是否允许使用客户要求保留的测试身份切换能力 */
    private boolean debugSwitchAllowed;

    public HzLoginUser()
    {
    }

    public HzLoginUser(Long userId)
    {
        this.userId = userId;
    }

    public Long getUserId()
    {
        return userId;
    }

    public void setUserId(Long userId)
    {
        this.userId = userId;
    }

    public String getToken()
    {
        return token;
    }

    public void setToken(String token)
    {
        this.token = token;
    }

    public Long getLoginTime()
    {
        return loginTime;
    }

    public void setLoginTime(Long loginTime)
    {
        this.loginTime = loginTime;
    }

    public Long getExpireTime()
    {
        return expireTime;
    }

    public void setExpireTime(Long expireTime)
    {
        this.expireTime = expireTime;
    }

    public boolean isDebugSwitchAllowed()
    {
        return debugSwitchAllowed;
    }

    public void setDebugSwitchAllowed(boolean debugSwitchAllowed)
    {
        this.debugSwitchAllowed = debugSwitchAllowed;
    }
}
