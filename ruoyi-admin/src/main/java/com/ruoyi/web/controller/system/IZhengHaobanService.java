package com.ruoyi.web.controller.system;

import com.ruoyi.system.domain.ZhbUserInfo;

/**
 * 郑好办对接服务接口
 *
 * @author ruoyi
 */
public interface IZhengHaobanService {

    /**
     * 用授权码换取访问令牌
     *
     * @param authCode 授权码（从郑好办JSAPI获取）
     * @return 访问令牌 oauthToken
     */
    String getOAuthToken(String authCode);

    /**
     * 获取用户详细信息
     *
     * @param oauthToken 访问令牌
     * @return 用户详细信息
     */
    ZhbUserInfo getUserDetail(String oauthToken);

    /**
     * 完整���录流程 - 通过授权码获取用户信息
     *
     * @param authCode 授权码（从郑好办JSAPI获取）
     * @return 用户详细信息
     */
    ZhbUserInfo loginByAuthCode(String authCode);
}
