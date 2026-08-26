package com.ruoyi.web.controller.system;

import com.ruoyi.common.annotation.Anonymous;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.system.domain.HzUser;
import com.ruoyi.system.domain.HzUserMessage;
import com.ruoyi.system.service.IHzUserService;
import com.ruoyi.system.service.IHzUserMessageService;
import com.ruoyi.web.service.WechatMiniappService;
import com.ruoyi.framework.web.service.HzUserTokenService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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
    private WechatMiniappService wechatMiniappService;

    @Autowired
    private HzUserTokenService hzUserTokenService;

    @Value("${ghz.debug-switch.enabled:true}")
    private boolean debugSwitchEnabled;

    @Value("${ghz.debug-switch.operator-phone:18539279011}")
    private String debugSwitchOperatorPhone;

    /**
     * 微信小程序登录
     * 前端传入wx.login()的code和getPhoneNumber的phoneCode
     */
    @Anonymous
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

            // 5. 生成服务端可撤销、可过期的随机Token
            boolean debugSwitchAllowed = debugSwitchEnabled
                    && debugSwitchOperatorPhone != null
                    && debugSwitchOperatorPhone.equals(user.getPhone());
            String token = hzUserTokenService.createToken(user.getUserId(), debugSwitchAllowed);

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
        Long userId = SecurityUtils.getHzUserId();
        HzUser user = userService.getById(userId);
        return user == null ? error("用户不存在") : success(buildUserInfo(user));
    }

    /**
     * 更新用户信息
     * 使用Map接参避免HzUser中Date字段的Jackson反序列化问题
     */
    @PutMapping("/updateInfo")
    public AjaxResult updateUserInfo(@RequestBody Map<String, Object> params) {
        Long userId = SecurityUtils.getHzUserId();

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
    public AjaxResult logout(HttpServletRequest request) {
        hzUserTokenService.deleteLoginUser(request);
        return success("退出成功");
    }

    /**
     * 客户要求保留的测试身份切换能力。
     *
     * 权限跟随最初由指定测试账号通过微信登录创建的服务端会话，不能通过修改
     * userId、手机号或伪造 Token 获得；切换到目标用户后仍可继续切回测试账号。
     */
    @PostMapping("/debugSwitch")
    public AjaxResult debugSwitch(@RequestBody Map<String, String> params) {
        if (!debugSwitchEnabled || !SecurityUtils.getHzLoginUser().isDebugSwitchAllowed()) {
            return error("无权限");
        }

        String targetPhone = params.get("phone");
        if (targetPhone == null || targetPhone.trim().isEmpty()) {
            return error("目标手机号不能为空");
        }

        HzUser targetUser = userService.getUserByPhone(targetPhone.trim());
        if (targetUser == null) {
            return error("目标用户不存在");
        }

        String token = hzUserTokenService.createToken(targetUser.getUserId(), true);
        Map<String, Object> data = new HashMap<>();
        data.put("token", token);
        data.put("userInfo", buildUserInfo(targetUser));
        logger.info("测试会话切换身份: targetUserId={}", targetUser.getUserId());
        return success(data);
    }

    private Map<String, Object> buildUserInfo(HzUser user) {
        Map<String, Object> userInfo = new HashMap<>();
        userInfo.put("userId", user.getUserId());
        userInfo.put("phone", user.getPhone());
        userInfo.put("nickname", user.getNickname());
        userInfo.put("realName", user.getRealName());
        userInfo.put("idCard", user.getIdCard());
        userInfo.put("loginType", user.getLoginType());
        userInfo.put("isInfoCompleted", user.getIsInfoCompleted());
        userInfo.put("authStatus", user.getAuthStatus() != null ? user.getAuthStatus() : "0");
        userInfo.put("wechatOpenid", user.getWechatOpenid());
        return userInfo;
    }
}
