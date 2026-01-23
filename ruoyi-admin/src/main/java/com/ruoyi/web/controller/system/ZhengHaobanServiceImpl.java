package com.ruoyi.web.controller.system;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.digital.szzz.gateway.sdk.api.GatewaySender;
import com.digital.szzz.gateway.sdk.bean.GatewayResponse;
import com.digital.szzz.gateway.sdk.util.AesUtil;
import com.digital.szzz.gateway.sdk.util.SignUtil;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.system.domain.ZhbUserInfo;
import com.ruoyi.web.core.config.ZhengHaobanConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;

/**
 * 郑好办对接服务实现
 *
 * @author ruoyi
 */
@Service
public class ZhengHaobanServiceImpl implements IZhengHaobanService {

    private static final Logger log = LoggerFactory.getLogger(ZhengHaobanServiceImpl.class);

    @Autowired
    private ZhengHaobanConfig zhengHaobanConfig;

    /**
     * 用授权码换取访问令牌
     *
     * @param authCode 授权码（从郑好办JSAPI获取）
     * @return 访问令牌 oauthToken
     */
    @Override
    public String getOAuthToken(String authCode) {
        try {
            // 通过网关调用的method名称
            String method = "szzz.uc.api.oauth.token";

            // 业务参数
            JSONObject json = new JSONObject();
            json.put("moduleId", zhengHaobanConfig.getApp().getModuleId());
            json.put("grantType", "authorization_code");
            json.put("authCode", authCode);
            String body = json.toJSONString();

            // 调用网关接口
            GatewayResponse response = callGateway(method, body);

            if (response == null) {
                log.error("获取OAuthToken失败：网关响应为空");
                return null;
            }

            // 解析返回结果
            JSONObject result = parseResponse(response);
            if (result == null) {
                return null;
            }

            if (result.getIntValue("code") == 0) {
                JSONObject data = result.getJSONObject("data");
                String oauthToken = data.getString("oauthToken");
                log.info("获取OAuthToken成功: {}", oauthToken);
                return oauthToken;
            } else {
                log.error("获取OAuthToken失败：{} - {}", result.getIntValue("code"), result.getString("message"));
                return null;
            }
        } catch (Exception e) {
            log.error("获取OAuthToken异常", e);
            return null;
        }
    }

    /**
     * 获取用户详细信息
     *
     * @param oauthToken 访问令牌
     * @return 用户详细信息
     */
    @Override
    public ZhbUserInfo getUserDetail(String oauthToken) {
        try {
            // 通过网关调用的method名称 - 获取用户详细信息
            String method = "szzz.uc.api.userdetail.get";

            // 业务参数
            JSONObject json = new JSONObject();
            json.put("moduleId", zhengHaobanConfig.getApp().getModuleId());
            json.put("oauthToken", oauthToken);
            String body = json.toJSONString();

            // 调用网关接口
            GatewayResponse response = callGateway(method, body);

            if (response == null) {
                log.error("获取用户详细信息失败：网关响应为空");
                return null;
            }

            // 解析返回结果
            JSONObject result = parseResponse(response);
            if (result == null) {
                return null;
            }

            if (result.getIntValue("code") == 0) {
                JSONObject data = result.getJSONObject("data");
                ZhbUserInfo userInfo = new ZhbUserInfo();
                userInfo.setZid(data.getString("zid"));
                userInfo.setPhone(data.getString("phone"));
                userInfo.setDisplayName(data.getString("displayName"));
                userInfo.setRealName(data.getString("realName"));
                userInfo.setIdCode(data.getString("idCode"));
                userInfo.setAvatarUrl(data.getString("avatarUrl"));
                userInfo.setGender(data.getInteger("gender"));
                userInfo.setUid(data.getString("uid"));
                log.info("获取用户详细信息成功：{}", userInfo);
                return userInfo;
            } else {
                log.error("获取用户详细信息失败：{} - {}", result.getIntValue("code"), result.getString("message"));
                return null;
            }
        } catch (Exception e) {
            log.error("获取用户详细信息异常", e);
            return null;
        }
    }

    /**
     * 完整登录流程 - 通过授权码获取用户信息
     *
     * @param authCode 授权码（从郑好办JSAPI获取）
     * @return 用户详细信息
     */
    @Override
    public ZhbUserInfo loginByAuthCode(String authCode) {
        // 1. 用授权码换取访问令牌
        String oauthToken = getOAuthToken(authCode);
        if (StringUtils.isEmpty(oauthToken)) {
            log.error("通过授权码获取OAuthToken失败");
            return null;
        }

        // 2. 用访问令牌获取用户信息
        ZhbUserInfo userInfo = getUserDetail(oauthToken);
        if (userInfo == null) {
            log.error("通过OAuthToken获取用户信息失败");
            return null;
        }

        return userInfo;
    }

    /**
     * 调用数郑网关
     *
     * @param method 接口方法名
     * @param body   业务参数JSON字符串
     * @return 网关响应
     */
    private GatewayResponse callGateway(String method, String body) {
        ZhengHaobanConfig.GatewayConfig gateway = zhengHaobanConfig.getGateway();

        try {
            GatewayResponse response = GatewaySender.send(
                    gateway.getAppId(),
                    method,
                    gateway.getIvStr(),
                    gateway.getKeyStr(),
                    gateway.getSecretKey(),
                    body,
                    5000,
                    10000
            );

            // code不为200时，请求异常（此状态码为网络状态码，非接口出参code）
            if (response.getCode() != 200) {
                log.error("网关请求异常：{}", response);
                return null;
            }

            // 验证接口出参未被恶意拦截篡改
            String expectSign = response.getSign();
            String factSign = SignUtil.signResponse(response, gateway.getSecretKey());
            if (!expectSign.equals(factSign)) {
                log.error("网关响应签名验证失败，可能被篡改");
                return null;
            }

            return response;
        } catch (Exception e) {
            log.error("调用网关异常", e);
            return null;
        }
    }

    /**
     * 解析网关响应
     *
     * @param response 网关响应
     * @return 解析后的JSON对象
     */
    private JSONObject parseResponse(GatewayResponse response) {
        try {
            ZhengHaobanConfig.GatewayConfig gateway = zhengHaobanConfig.getGateway();

            byte[] data = AesUtil.base64ToByte(response.getData());
            byte[] plainData = AesUtil.decrypt(
                    AesUtil.base64ToByte(gateway.getIvStr()),
                    AesUtil.base64ToByte(gateway.getKeyStr()),
                    data
            );
            String plainText = new String(plainData, StandardCharsets.UTF_8);
            log.info("网关响应明文：{}", plainText);
            return JSON.parseObject(plainText);
        } catch (Exception e) {
            log.error("解析网关响应异常", e);
            return null;
        }
    }
}
