package com.ruoyi.web.controller.system;

import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.system.domain.HzUser;
import com.ruoyi.system.domain.HzUserMessage;
import com.ruoyi.system.domain.ZhbUserInfo;
import com.ruoyi.system.service.IHzUserService;
import com.ruoyi.system.service.IHzUserMessageService;
import com.ruoyi.web.service.WechatMiniappService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

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
            userInfo.put("wechatOpenid", user.getWechatOpenid()); // JSAPI 支付需要

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
            if (params.containsKey("idCard")) {
                String idCard = (String) params.get("idCard");
                existUser.setIdCard(idCard);
                // 根据身份证自动回填性别（未知/空且身份证合法时）
                existUser.setGender(com.ruoyi.common.utils.IdCardUtils.backfillGender(existUser.getGender(), idCard));
                // 身份证账号自动合并：如果该身份证已存在于其他旧账号（迁移数据），自动迁移业务数据
                if (idCard != null && !idCard.isBlank()) {
                    userService.mergeUserByIdCard(userId, idCard);
                }
            }
            if (params.containsKey("contactPhone"))   existUser.setContactPhone((String) params.get("contactPhone"));
            if (params.containsKey("workUnit"))       existUser.setWorkUnit((String) params.get("workUnit"));
            if (params.containsKey("unitContact"))    existUser.setUnitContact((String) params.get("unitContact"));
            if (params.containsKey("unitNature"))     existUser.setUnitNature((String) params.get("unitNature"));
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

    /**
     * 调试用：超级测试账号切换身份
     * 仅限手机号 18539279011 调用，可切换到任意用户
     */
    @PostMapping("/debugSwitch")
    public AjaxResult debugSwitch(@RequestBody Map<String, String> params) {
        // 1. 从token解析当前调用者userId
        Long currentUserId = getHzUserIdFromToken();
        if (currentUserId == null) {
            return error("请先登录");
        }

        // 2. 查找目标用户
        String targetPhone = params.get("phone");
        if (targetPhone == null || targetPhone.trim().isEmpty()) {
            return error("目标手机号不能为空");
        }
        targetPhone = targetPhone.trim();

        // 3. 权限校验：切回测试账号(18539279011)不限制，切到其他用户必须是测试账号
        HzUser currentUser = userService.getById(currentUserId);
        boolean isSwitchBack = "18539279011".equals(targetPhone);
        if (!isSwitchBack) {
            if (currentUser == null || !"18539279011".equals(currentUser.getPhone())) {
                return error("无权限");
            }
        }

        HzUser targetUser = userService.getUserByPhone(targetPhone);
        if (targetUser == null) {
            return error("目标用户不存在: " + targetPhone);
        }

        // 4. 生成目标用户的token
        String token = "hz_token_" + targetUser.getUserId() + "_" + System.currentTimeMillis();

        // 5. 构造返回数据（与正常登录返回格式一致）
        Map<String, Object> data = new HashMap<>();
        data.put("token", token);

        Map<String, Object> userInfo = new HashMap<>();
        userInfo.put("userId", targetUser.getUserId());
        userInfo.put("phone", targetUser.getPhone());
        userInfo.put("nickname", targetUser.getNickname());
        userInfo.put("realName", targetUser.getRealName());
        userInfo.put("idCard", targetUser.getIdCard());
        userInfo.put("loginType", targetUser.getLoginType());
        userInfo.put("isInfoCompleted", targetUser.getIsInfoCompleted());
        userInfo.put("authStatus", targetUser.getAuthStatus() != null ? targetUser.getAuthStatus() : "0");
        userInfo.put("wechatOpenid", targetUser.getWechatOpenid());

        data.put("userInfo", userInfo);

        logger.info("调试切换身份: {}(userId={}) -> {}(userId={})",
                currentUser.getPhone(), currentUserId, targetPhone, targetUser.getUserId());

        return success(data);
    }

    /**
     * 从请求头中的Token解析出hz_user的ID
     * Token格式：hz_token_{userId}_{timestamp}
     */
    private Long getHzUserIdFromToken() {
        try {
            HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
            String token = request.getHeader("Authorization");

            if (token == null || token.isEmpty()) {
                return null;
            }

            // 移除 "Bearer " 前缀（如果有）
            if (token.startsWith("Bearer ")) {
                token = token.substring(7);
            }

            // 解析token: hz_token_{userId}_{timestamp}
            if (token.startsWith("hz_token_")) {
                String[] parts = token.split("_");
                if (parts.length >= 3) {
                    return Long.parseLong(parts[2]);
                }
            }

            return null;
        } catch (Exception e) {
            logger.error("解析token失败", e);
            return null;
        }
    }
}
