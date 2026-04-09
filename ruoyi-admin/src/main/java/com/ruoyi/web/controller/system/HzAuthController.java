package com.ruoyi.web.controller.system;

import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.system.domain.HzUser;
import com.ruoyi.system.domain.HzUserMessage;
import com.ruoyi.system.domain.ZhbUserInfo;
import com.ruoyi.system.service.IHzUserService;
import com.ruoyi.system.service.IHzUserMessageService;
import com.ruoyi.web.service.WechatMiniappService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 用户端认证Controller
 *
 * @author ruoyi
 * @date 2026-01-13
 */
@RestController
@RequestMapping("/app/auth")
public class HzAuthController extends BaseController {

    @Autowired
    private IHzUserService userService;

    @Autowired
    private IHzUserMessageService userMessageService;

    @Autowired
    private IZhengHaobanService zhengHaobanService;

    @Autowired
    private WechatMiniappService wechatMiniappService;

    /**
     * 用户登录（微信/郑好办）
     */
    @PostMapping("/login")
    public AjaxResult login(@RequestBody Map<String, String> loginData) {
        String loginType = loginData.get("loginType");
        String phone = loginData.get("phone");
        String openId = loginData.get("openId");
        String nickname = loginData.get("nickname");

        // 参数验证
        if (loginType == null || phone == null || openId == null) {
            return error("登录参数不完整");
        }

        try {
            // 根据手机号和openId查询或创建用户（在hz_user表）
            HzUser user = userService.loginOrRegister(loginType, phone, openId, nickname);

            // 发送登录消息
            sendLoginMessage(user);

            // 生成简化Token（基于hz_user）
            String token = "hz_token_" + user.getUserId() + "_" + System.currentTimeMillis();

            // 构造返回数据
            Map<String, Object> data = new HashMap<>();
            data.put("token", token);

            Map<String, Object> userInfo = new HashMap<>();
            userInfo.put("userId", user.getUserId());
            userInfo.put("phone", user.getPhone());
            userInfo.put("nickname", user.getNickname());
            userInfo.put("realName", user.getRealName());
            userInfo.put("idCard", user.getIdCard());
            userInfo.put("loginType", user.getLoginType());
            userInfo.put("isInfoCompleted", user.getIsInfoCompleted());

            data.put("userInfo", userInfo);

            return success(data);
        } catch (Exception e) {
            logger.error("登录失败:", e);
            return error("登录失败: " + e.getMessage());
        }
    }

    /**
     * 郑好办授权登录
     *
     * @param params 参数，包含authCode授权码
     * @return 登录结果
     */
    @PostMapping("/zhbLogin")
    public AjaxResult zhbLogin(@RequestBody Map<String, String> params) {
        String authCode = params.get("authCode");

        // 参数验证
        if (authCode == null || authCode.trim().isEmpty()) {
            return error("授权码不能为空");
        }

        try {
            // 1. 通过authCode获取郑好办用户信息
            ZhbUserInfo zhbUserInfo = zhengHaobanService.loginByAuthCode(authCode);
            if (zhbUserInfo == null) {
                return error("获取郑好办用户信息失败，请重新授权");
            }

            // 2. 登录或注册用户
            HzUser user = userService.loginOrRegisterByZhb(
                    zhbUserInfo.getZid(),
                    zhbUserInfo.getPhone(),
                    zhbUserInfo.getDisplayName(),
                    zhbUserInfo.getRealName(),
                    zhbUserInfo.getIdCode(),
                    zhbUserInfo.getAvatarUrl()
            );

            // 3. 发送登录消息
            sendLoginMessage(user);

            // 4. 生成简化Token
            String token = "hz_token_" + user.getUserId() + "_" + System.currentTimeMillis();

            // 5. 构造返回数据
            Map<String, Object> data = new HashMap<>();
            data.put("token", token);

            Map<String, Object> userInfo = new HashMap<>();
            userInfo.put("userId", user.getUserId());
            userInfo.put("phone", user.getPhone());
            userInfo.put("nickname", user.getNickname());
            userInfo.put("realName", user.getRealName());
            userInfo.put("idCard", user.getIdCard());
            userInfo.put("loginType", user.getLoginType());
            userInfo.put("isInfoCompleted", user.getIsInfoCompleted());
            if (user.getRealName() != null) {
                userInfo.put("realName", user.getRealName());
            }
            if (user.getIdCard() != null) {
                userInfo.put("hasIdCard", true);
            }

            data.put("userInfo", userInfo);

            return success(data);
        } catch (Exception e) {
            logger.error("郑好办登录失败:", e);
            return error("登录失败: " + e.getMessage());
        }
    }

    /**
     * 微信小程序登录
     * 前端传入wx.login()的code和getPhoneNumber的phoneCode
     */
    @PostMapping("/wxLogin")
    public AjaxResult wxLogin(@RequestBody Map<String, String> params) {
        String code = params.get("code");
        String phoneCode = params.get("phoneCode");

        // 参数验证
        if (code == null || code.trim().isEmpty()) {
            return error("微信登录code不能为空");
        }
        if (phoneCode == null || phoneCode.trim().isEmpty()) {
            return error("手机号授权code不能为空");
        }

        try {
            // 1. code换openid和unionid
            String[] ids = wechatMiniappService.getOpenidAndUnionid(code);
            String openid = ids[0];
            String unionid = ids[1];

            // 2. phoneCode换手机号
            String phone = wechatMiniappService.getPhoneNumber(phoneCode);

            // 3. 查找或创建用户
            HzUser user = userService.loginOrRegisterByWechat(openid, unionid, phone);

            // 4. 发送登录消息
            sendLoginMessage(user);

            // 5. 生成Token
            String token = "hz_token_" + user.getUserId() + "_" + System.currentTimeMillis();

            // 6. 构造返回数据
            Map<String, Object> data = new HashMap<>();
            data.put("token", token);

            Map<String, Object> userInfo = new HashMap<>();
            userInfo.put("userId", user.getUserId());
            userInfo.put("phone", user.getPhone());
            userInfo.put("nickname", user.getNickname());
            userInfo.put("realName", user.getRealName());
            userInfo.put("idCard", user.getIdCard());
            userInfo.put("loginType", user.getLoginType());
            userInfo.put("isInfoCompleted", user.getIsInfoCompleted());
            userInfo.put("authStatus", user.getAuthStatus() != null ? user.getAuthStatus() : "0");

            data.put("userInfo", userInfo);

            return success(data);
        } catch (Exception e) {
            logger.error("微信小程序登录失败:", e);
            return error("登录失败: " + e.getMessage());
        }
    }

    /**
     * 发送登录消息
     */
    private void sendLoginMessage(HzUser user) {
        try {
            HzUserMessage message = new HzUserMessage();
            message.setUserId(user.getUserId());
            message.setMessageType("login");
            message.setMessageTitle("登录提醒");
            message.setMessageContent("欢迎登录，亲爱的" + (user.getNickname() != null ? user.getNickname() : "用户"));
            message.setIsRead("0");
            userMessageService.insertMessage(message);
        } catch (Exception e) {
            logger.error("发送登录消息失败:", e);
            // 不影响登录流程，只记录日志
        }
    }

    /**
     * 获取用户信息
     */
    @GetMapping("/userInfo")
    public AjaxResult getUserInfo() {
        // TODO: 从Token中解析userId
        return success();
    }

    /**
     * 更新用户信息
     * 使用Map接参避免HzUser中Date字段的Jackson反序列化问题
     */
    @PutMapping("/updateInfo")
    public AjaxResult updateUserInfo(@RequestBody Map<String, Object> params) {
        Object userIdObj = params.get("userId");
        if (userIdObj == null) {
            return error("用户ID不能为空");
        }
        Long userId;
        try {
            userId = Long.valueOf(userIdObj.toString());
        } catch (NumberFormatException e) {
            return error("用户ID格式不正确");
        }

        try {
            HzUser existUser = userService.getById(userId);
            if (existUser == null) {
                return error("用户不存在");
            }

            // 只更新前端传入的业务字段，不触碰Date字段
            if (params.containsKey("identityType"))  existUser.setIdentityType((String) params.get("identityType"));
            if (params.containsKey("realName"))       existUser.setRealName((String) params.get("realName"));
            if (params.containsKey("idCard"))         existUser.setIdCard((String) params.get("idCard"));
            if (params.containsKey("contactPhone"))   existUser.setContactPhone((String) params.get("contactPhone"));
            if (params.containsKey("workUnit"))       existUser.setWorkUnit((String) params.get("workUnit"));
            if (params.containsKey("unitContact"))    existUser.setUnitContact((String) params.get("unitContact"));
            if (params.containsKey("spouseName"))     existUser.setSpouseName((String) params.get("spouseName"));
            existUser.setIsInfoCompleted("1");

            boolean result = userService.updateById(existUser);
            if (result) {
                return success("信息更新成功");
            } else {
                return error("信息更新失败");
            }
        } catch (Exception e) {
            logger.error("更新用户信息失败:", e);
            return error("更新失败: " + e.getMessage());
        }
    }

    /**
     * 退出登录
     */
    @PostMapping("/logout")
    public AjaxResult logout() {
        return success("退出成功");
    }
}
