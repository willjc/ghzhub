# WeChat Mini Program Login Implementation Plan

> **For agentic workers:** REQUIRED: Use superpowers:subagent-driven-development (if subagents available) or superpowers:executing-plans to implement this plan. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Replace the current H5 test login with WeChat Mini Program native login (wx.login + getPhoneNumber), add a dedicated profile completion page, and adapt e-Sign real-name auth for mini program webview.

**Architecture:** Backend adds a `WechatMiniappService` to call WeChat APIs (jscode2session, getuserphonenumber), a new `/app/auth/wxLogin` endpoint, and a `loginOrRegisterByWechat` user service method. Frontend rewrites the login page with conditional compilation (`#ifdef MP-WEIXIN` / `#ifdef H5`), creates a standalone profile completion page replacing the homepage modal, and wraps e-Sign auth in a webview page for mini program.

**Tech Stack:** Spring Boot 3.5.4, MyBatis-Plus, Redis (via RedisCache), uni-app (Vue 2), WeChat Mini Program APIs

**Spec:** `docs/superpowers/specs/2026-04-09-wechat-miniapp-login-design.md`

---

## Chunk 1: Backend — WeChat Mini Program Login

### Task 1: Fix YAML duplicate `wechat:` key

The current `application.yml` has two `wechat:` root keys (line 179 and line 186). The second silently overrides the first. Merge them.

**Files:**
- Modify: `ruoyi-admin/src/main/resources/application.yml:179-196`

- [ ] **Step 1: Merge the two `wechat:` blocks into one**

Replace lines 179-196 with:

```yaml
# 微信配置
wechat:
  miniapp:
    appid: "wx74efa287343d0fa2"
    secret: "5bbac44092d17077e1924883929afcff"
  # 微信支付配置
  # 开发/测试环境不配置证书时设 enabled: false，生产环境填好证书后改为 true
  pay:
    enabled: false
    appid: "wx_your_appid"
    mch-id: "your_mch_id"
    api-v3-key: "your_api_v3_key_32chars"
    private-key-path: "classpath:certs/apiclient_key.pem"
    cert-serial-no: "your_cert_serial_no"
    notify-domain: "https://your-domain.com"
    h5-notify-url: "${wechat.pay.notify-domain}/h5/pay/wechat/notify"
```

- [ ] **Step 2: Update WechatPayConfig @Value paths**

In `ruoyi-admin/src/main/java/com/ruoyi/web/config/WechatPayConfig.java`, verify that `@Value` annotations reference `${wechat.pay.xxx}` (they should already, but confirm after the YAML merge).

- [ ] **Step 3: Commit**

```bash
git add ruoyi-admin/src/main/resources/application.yml
git commit -m "fix: merge duplicate wechat YAML keys to prevent miniapp config override"
```

---

### Task 2: Create WechatMiniappConfig

**Files:**
- Create: `ruoyi-admin/src/main/java/com/ruoyi/web/config/WechatMiniappConfig.java`

- [ ] **Step 1: Create the config class**

```java
package com.ruoyi.web.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * 微信小程序配置
 */
@Configuration
@ConfigurationProperties(prefix = "wechat.miniapp")
public class WechatMiniappConfig {

    private String appid;
    private String secret;

    public String getAppid() {
        return appid;
    }

    public void setAppid(String appid) {
        this.appid = appid;
    }

    public String getSecret() {
        return secret;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }
}
```

- [ ] **Step 2: Commit**

```bash
git add ruoyi-admin/src/main/java/com/ruoyi/web/config/WechatMiniappConfig.java
git commit -m "feat: add WechatMiniappConfig for miniapp appid/secret"
```

---

### Task 3: Create WechatMiniappService

This service calls WeChat APIs: jscode2session (get openid), getuserphonenumber (get phone), and manages access_token caching in Redis.

**Files:**
- Create: `ruoyi-admin/src/main/java/com/ruoyi/web/service/WechatMiniappService.java`

**Dependencies available:**
- `RedisCache` from `com.ruoyi.common.core.redis.RedisCache` — use `setCacheObject(key, value, timeout, unit)` and `getCacheObject(key)`
- No RestTemplate bean exists — create a local instance or use `new RestTemplate()`

- [ ] **Step 1: Create the service**

```java
package com.ruoyi.web.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ruoyi.common.core.redis.RedisCache;
import com.ruoyi.web.config.WechatMiniappConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.concurrent.TimeUnit;

/**
 * 微信小程序服务
 * 封装微信API调用：jscode2session、getuserphonenumber、access_token管理
 */
@Service
public class WechatMiniappService {

    private static final Logger log = LoggerFactory.getLogger(WechatMiniappService.class);

    private static final String JSCODE2SESSION_URL = "https://api.weixin.qq.com/sns/jscode2session?appid=%s&secret=%s&js_code=%s&grant_type=authorization_code";
    private static final String GET_ACCESS_TOKEN_URL = "https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid=%s&secret=%s";
    private static final String GET_PHONE_URL = "https://api.weixin.qq.com/wxa/business/getuserphonenumber?access_token=%s";

    private static final String REDIS_ACCESS_TOKEN_KEY = "wechat:miniapp:access_token";

    @Autowired
    private WechatMiniappConfig config;

    @Autowired
    private RedisCache redisCache;

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * 通过wx.login的code获取openid和unionid
     * 注意：session_key不存储、不返回给前端
     *
     * @param code wx.login()返回的临时码
     * @return [openid, unionid] — unionid可能为null
     */
    public String[] getOpenidAndUnionid(String code) {
        String url = String.format(JSCODE2SESSION_URL, config.getAppid(), config.getSecret(), code);
        try {
            String response = restTemplate.getForObject(url, String.class);
            JsonNode json = objectMapper.readTree(response);

            if (json.has("errcode") && json.get("errcode").asInt() != 0) {
                String errmsg = json.has("errmsg") ? json.get("errmsg").asText() : "unknown error";
                log.error("jscode2session失败: errcode={}, errmsg={}", json.get("errcode").asInt(), errmsg);
                throw new RuntimeException("微信登录失败: " + errmsg);
            }

            String openid = json.get("openid").asText();
            String unionid = json.has("unionid") ? json.get("unionid").asText() : null;
            log.info("获取openid成功: {}", openid.substring(0, Math.min(8, openid.length())) + "***");
            return new String[] { openid, unionid };
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            log.error("调用jscode2session异常", e);
            throw new RuntimeException("微信登录失败，请重试");
        }
    }

    /**
     * 通过getPhoneNumber的code获取手机号
     * API: POST https://api.weixin.qq.com/wxa/business/getuserphonenumber?access_token=XXX
     * Body: {"code": phoneCode}
     *
     * @param phoneCode getPhoneNumber事件返回的code
     * @return 手机号（如 13800138000）
     */
    public String getPhoneNumber(String phoneCode) {
        String accessToken = getAccessToken();
        String url = String.format(GET_PHONE_URL, accessToken);

        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            String body = "{\"code\":\"" + phoneCode + "\"}";
            HttpEntity<String> entity = new HttpEntity<>(body, headers);

            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, entity, String.class);
            JsonNode json = objectMapper.readTree(response.getBody());

            if (json.has("errcode") && json.get("errcode").asInt() != 0) {
                String errmsg = json.has("errmsg") ? json.get("errmsg").asText() : "unknown error";
                log.error("getuserphonenumber失败: errcode={}, errmsg={}", json.get("errcode").asInt(), errmsg);

                // 如果是access_token过期，清缓存重试一次
                if (json.get("errcode").asInt() == 40001) {
                    log.info("access_token过期，清缓存重试");
                    redisCache.deleteObject(REDIS_ACCESS_TOKEN_KEY);
                    return getPhoneNumberRetry(phoneCode);
                }
                throw new RuntimeException("获取手机号失败: " + errmsg);
            }

            JsonNode phoneInfo = json.get("phone_info");
            String phoneNumber = phoneInfo.get("phoneNumber").asText();
            log.info("获取手机号成功: {}****{}", phoneNumber.substring(0, 3), phoneNumber.substring(7));
            return phoneNumber;
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            log.error("调用getuserphonenumber异常", e);
            throw new RuntimeException("获取手机号失败，请重试");
        }
    }

    /**
     * 重试一次获取手机号（access_token过期时调用）
     */
    private String getPhoneNumberRetry(String phoneCode) {
        String accessToken = getAccessToken();
        String url = String.format(GET_PHONE_URL, accessToken);

        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            String body = "{\"code\":\"" + phoneCode + "\"}";
            HttpEntity<String> entity = new HttpEntity<>(body, headers);

            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, entity, String.class);
            JsonNode json = objectMapper.readTree(response.getBody());

            if (json.has("errcode") && json.get("errcode").asInt() != 0) {
                String errmsg = json.has("errmsg") ? json.get("errmsg").asText() : "unknown error";
                throw new RuntimeException("获取手机号失败: " + errmsg);
            }

            return json.get("phone_info").get("phoneNumber").asText();
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            log.error("重试getuserphonenumber异常", e);
            throw new RuntimeException("获取手机号失败，请重试");
        }
    }

    /**
     * 获取微信接口调用凭证（access_token）
     * 优先从Redis缓存获取，过期则从微信API刷新
     * 缓存7000秒（微信默认7200秒有效期，提前200秒刷新）
     */
    public String getAccessToken() {
        // 先从缓存获取
        String cached = redisCache.getCacheObject(REDIS_ACCESS_TOKEN_KEY);
        if (cached != null) {
            return cached;
        }

        // 缓存没有，从微信API获取
        String url = String.format(GET_ACCESS_TOKEN_URL, config.getAppid(), config.getSecret());
        try {
            String response = restTemplate.getForObject(url, String.class);
            JsonNode json = objectMapper.readTree(response);

            if (json.has("errcode") && json.get("errcode").asInt() != 0) {
                String errmsg = json.has("errmsg") ? json.get("errmsg").asText() : "unknown error";
                log.error("获取access_token失败: errcode={}, errmsg={}", json.get("errcode").asInt(), errmsg);
                throw new RuntimeException("获取微信access_token失败: " + errmsg);
            }

            String accessToken = json.get("access_token").asText();
            // 缓存7000秒
            redisCache.setCacheObject(REDIS_ACCESS_TOKEN_KEY, accessToken, 7000, TimeUnit.SECONDS);
            log.info("获取并缓存access_token成功");
            return accessToken;
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            log.error("调用token接口异常", e);
            throw new RuntimeException("获取微信access_token失败，请重试");
        }
    }
}
```

- [ ] **Step 2: Commit**

```bash
git add ruoyi-admin/src/main/java/com/ruoyi/web/service/WechatMiniappService.java
git commit -m "feat: add WechatMiniappService for WeChat API calls (openid, phone, access_token)"
```

---

### Task 4: Add `loginOrRegisterByWechat` to user service

**Files:**
- Modify: `ruoyi-system/src/main/java/com/ruoyi/system/service/IHzUserService.java` — add method signature
- Modify: `ruoyi-system/src/main/java/com/ruoyi/system/service/impl/HzUserServiceImpl.java` — add implementation

- [ ] **Step 1: Add method to IHzUserService interface**

Add at the end of the interface (after `loginOrRegisterByZhb`):

```java
/**
 * 微信小程序用户登录或注册
 *
 * @param openid 微信openid
 * @param unionid 微信unionid（可选，跨应用用户匹配用）
 * @param phone 手机号（从微信获取）
 * @return 用户信息
 */
HzUser loginOrRegisterByWechat(String openid, String unionid, String phone);
```

- [ ] **Step 2: Add implementation to HzUserServiceImpl**

Add at the end of the class (before the closing `}`):

```java
/**
 * 微信小程序用户登录或注册
 *
 * @param openid 微信openid
 * @param unionid 微信unionid（可选）
 * @param phone 手机号（从微信获取）
 * @return 用户信息
 */
@Override
public HzUser loginOrRegisterByWechat(String openid, String unionid, String phone) {
    // 1. 先按openid查找用户
    LambdaQueryWrapper<HzUser> wrapper = new LambdaQueryWrapper<>();
    wrapper.eq(HzUser::getWechatOpenid, openid);
    HzUser user = this.getOne(wrapper);

    if (user != null) {
        // 老用户，更新登录时间
        user.setLastLoginTime(new Date());
        // 补充unionid（可能之前没有）
        if (com.ruoyi.common.utils.StringUtils.isNotEmpty(unionid)
                && com.ruoyi.common.utils.StringUtils.isEmpty(user.getWechatUnionid())) {
            user.setWechatUnionid(unionid);
        }
        this.updateById(user);
        return user;
    }

    // 2. openid不存在，按手机号查找
    LambdaQueryWrapper<HzUser> phoneWrapper = new LambdaQueryWrapper<>();
    phoneWrapper.eq(HzUser::getPhone, phone);
    HzUser phoneUser = this.getOne(phoneWrapper);

    if (phoneUser != null) {
        // 手机号已存在
        if (com.ruoyi.common.utils.StringUtils.isNotEmpty(phoneUser.getWechatOpenid())
                && !phoneUser.getWechatOpenid().equals(openid)) {
            // 手机号已绑定其他微信账号，拒绝
            throw new RuntimeException("该手机号已被其他账号绑定");
        }
        // 手机号存在但没绑openid（如从郑好办注册的），绑定openid
        phoneUser.setWechatOpenid(openid);
        if (com.ruoyi.common.utils.StringUtils.isNotEmpty(unionid)) {
            phoneUser.setWechatUnionid(unionid);
        }
        phoneUser.setLastLoginTime(new Date());
        this.updateById(phoneUser);
        return phoneUser;
    }

    // 3. 全新用户，创建
    HzUser newUser = new HzUser();
    newUser.setPhone(phone);
    newUser.setWechatOpenid(openid);
    if (com.ruoyi.common.utils.StringUtils.isNotEmpty(unionid)) {
        newUser.setWechatUnionid(unionid);
    }
    newUser.setSourceType("1"); // 微信小程序
    newUser.setLoginType("wechat");
    newUser.setNickname("微信用户");
    newUser.setStatus("0");
    newUser.setIsInfoCompleted("0");
    newUser.setAuthStatus("0");
    newUser.setDelFlag("0");
    newUser.setCreateTime(new Date());
    newUser.setLastLoginTime(new Date());
    this.save(newUser);

    return newUser;
}
```

- [ ] **Step 3: Commit**

```bash
git add ruoyi-system/src/main/java/com/ruoyi/system/service/IHzUserService.java \
       ruoyi-system/src/main/java/com/ruoyi/system/service/impl/HzUserServiceImpl.java
git commit -m "feat: add loginOrRegisterByWechat with phone collision guard"
```

---

### Task 5: Add `/app/auth/wxLogin` endpoint

**Files:**
- Modify: `ruoyi-admin/src/main/java/com/ruoyi/web/controller/system/HzAuthController.java` — add wxLogin method and inject WechatMiniappService

- [ ] **Step 1: Add import and field injection**

Add import at the top of the file:

```java
import com.ruoyi.web.service.WechatMiniappService;
```

Add field injection alongside the existing `@Autowired` fields:

```java
@Autowired
private WechatMiniappService wechatMiniappService;
```

- [ ] **Step 2: Add the wxLogin endpoint method**

Add after the existing `zhbLogin` method:

```java
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
```

- [ ] **Step 3: Commit**

Note: `/app/**` is already whitelisted in `SecurityConfig.java` (line 105), so `/app/auth/wxLogin` is automatically accessible without authentication.

```bash
git add ruoyi-admin/src/main/java/com/ruoyi/web/controller/system/HzAuthController.java
git commit -m "feat: add /app/auth/wxLogin endpoint for WeChat Mini Program login"
```

---

## Chunk 2: Frontend — Login, Profile Completion, and Cleanup

### Task 6: Update manifest.json with WeChat appid

**Files:**
- Modify: `uniapp-h5/manifest.json:52-58`

- [ ] **Step 1: Fill in the appid**

Replace the `mp-weixin` section:

```json
"mp-weixin" : {
    "appid" : "wx74efa287343d0fa2",
    "setting" : {
        "urlCheck" : false
    },
    "usingComponents" : true
},
```

- [ ] **Step 2: Commit**

```bash
git add uniapp-h5/manifest.json
git commit -m "feat: add WeChat Mini Program appid to manifest.json"
```

---

### Task 7: Add `wxLogin` API function

**Files:**
- Modify: `uniapp-h5/api/auth.js` — add wxLogin function

- [ ] **Step 1: Add the function**

Add after the existing `zhbLogin` function:

```javascript
/**
 * 微信小程序登录
 * @param {Object} data - 登录数据
 * @param {String} data.code - wx.login()返回的code
 * @param {String} data.phoneCode - getPhoneNumber返回的code
 */
export function wxLogin(data) {
	return request.post('/app/auth/wxLogin', data)
}
```

- [ ] **Step 2: Commit**

```bash
git add uniapp-h5/api/auth.js
git commit -m "feat: add wxLogin API function for mini program auth"
```

---

### Task 8: Rewrite login page with conditional compilation

**Files:**
- Rewrite: `uniapp-h5/pages/login/index.vue`

The login page uses conditional compilation: `#ifdef MP-WEIXIN` for mini program (wxLogin), `#ifdef H5` for H5 (simplified test login, no ZhengHaoBan).

- [ ] **Step 1: Rewrite the login page**

```vue
<template>
	<view class="login-page">
		<!-- 背景装饰 -->
		<view class="bg-decoration"></view>

		<!-- Logo区域 -->
		<view class="logo-section">
			<image class="logo" src="/static/logo.png" mode="aspectFit"></image>
			<text class="app-name">港好住</text>
			<text class="app-desc">港区保租房·人才公寓服务平台</text>
		</view>

		<!-- 登录卡片 -->
		<view class="login-card">
			<text class="card-title">欢迎登录</text>

			<!-- #ifdef MP-WEIXIN -->
			<!-- 微信小程序：授权手机号登录 -->
			<button class="login-btn wechat-btn" open-type="getPhoneNumber" @getphonenumber="onGetPhoneNumber" :disabled="loading">
				<text class="btn-text">{{ loading ? '登录中...' : '微信授权登录' }}</text>
			</button>
			<!-- #endif -->

			<!-- #ifdef H5 -->
			<!-- H5环境：测试登录 -->
			<view class="login-btn wechat-btn" @click="handleTestLogin">
				<text class="btn-text">{{ loading ? '登录中...' : '测试登录' }}</text>
			</view>
			<!-- #endif -->

			<text class="tips">登录即表示同意《用户协议》和《隐私政策》</text>
		</view>
	</view>
</template>

<script>
	// #ifdef MP-WEIXIN
	import { wxLogin } from '@/api/auth'
	// #endif

	// #ifdef H5
	import { login } from '@/api/auth'
	// #endif

	export default {
		data() {
			return {
				loading: false
			}
		},
		methods: {
			// #ifdef MP-WEIXIN
			/**
			 * 微信小程序：获取手机号回调
			 */
			async onGetPhoneNumber(e) {
				if (this.loading) return

				// 用户拒绝授权
				if (e.detail.errMsg !== 'getPhoneNumber:ok') {
					uni.showToast({
						title: '需要授权手机号才能登录',
						icon: 'none'
					})
					return
				}

				this.loading = true

				try {
					// 1. 调用wx.login获取code
					const loginRes = await new Promise((resolve, reject) => {
						wx.login({
							success: resolve,
							fail: reject
						})
					})

					// 2. 发送code和phoneCode到后端
					const response = await wxLogin({
						code: loginRes.code,
						phoneCode: e.detail.code
					})

					// 3. 存储登录信息
					uni.setStorageSync('token', response.data.token)
					uni.setStorageSync('userId', response.data.userInfo.userId)
					uni.setStorageSync('userInfo', response.data.userInfo)

					uni.showToast({
						title: '登录成功',
						icon: 'success'
					})

					// 4. 根据isInfoCompleted决定跳转
					setTimeout(() => {
						if (response.data.userInfo.isInfoCompleted === '1') {
							uni.reLaunch({ url: '/pages/index/index' })
						} else {
							uni.redirectTo({ url: '/pages/my/complete-info' })
						}
					}, 1000)

				} catch (error) {
					console.error('微信登录失败:', error)
					uni.showToast({
						title: error.message || error.msg || '登录失败',
						icon: 'none'
					})
				} finally {
					this.loading = false
				}
			},
			// #endif

			// #ifdef H5
			/**
			 * H5环境：测试登录
			 */
			async handleTestLogin() {
				if (this.loading) return
				this.loading = true

				try {
					const testData = {
						loginType: 'wechat',
						phone: '13800138001',
						openId: 'wx_test_001',
						nickname: '测试用户'
					}

					const response = await login(testData)

					uni.setStorageSync('token', response.data.token)
					uni.setStorageSync('userId', response.data.userInfo.userId)
					uni.setStorageSync('userInfo', response.data.userInfo)

					uni.showToast({ title: '登录成功', icon: 'success' })

					setTimeout(() => {
						if (response.data.userInfo.isInfoCompleted === '1') {
							uni.reLaunch({ url: '/pages/index/index' })
						} else {
							uni.redirectTo({ url: '/pages/my/complete-info' })
						}
					}, 1000)

				} catch (error) {
					console.error('登录失败:', error)
					uni.showToast({
						title: error.message || '登录失败',
						icon: 'none'
					})
				} finally {
					this.loading = false
				}
			}
			// #endif
		}
	}
</script>

<style scoped>
	.login-page {
		min-height: 100vh;
		background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
		display: flex;
		flex-direction: column;
		align-items: center;
		justify-content: center;
		padding: 40rpx;
		position: relative;
		overflow: hidden;
	}

	.bg-decoration {
		position: absolute;
		width: 200%;
		height: 200%;
		background: radial-gradient(circle, rgba(255,255,255,0.1) 1px, transparent 1px);
		background-size: 50rpx 50rpx;
		animation: float 20s linear infinite;
	}

	@keyframes float {
		from { transform: translate(0, 0); }
		to { transform: translate(-50rpx, -50rpx); }
	}

	.logo-section {
		display: flex;
		flex-direction: column;
		align-items: center;
		margin-bottom: 80rpx;
		position: relative;
		z-index: 1;
	}

	.logo {
		width: 160rpx;
		height: 160rpx;
		margin-bottom: 30rpx;
		background: #ffffff;
		border-radius: 80rpx;
		padding: 20rpx;
	}

	.app-name {
		font-size: 48rpx;
		font-weight: bold;
		color: #ffffff;
		margin-bottom: 16rpx;
		text-shadow: 0 2rpx 10rpx rgba(0,0,0,0.2);
	}

	.app-desc {
		font-size: 26rpx;
		color: rgba(255,255,255,0.9);
	}

	.login-card {
		width: 100%;
		max-width: 600rpx;
		background: #ffffff;
		border-radius: 32rpx;
		padding: 60rpx 40rpx;
		box-shadow: 0 20rpx 60rpx rgba(0,0,0,0.2);
		position: relative;
		z-index: 1;
	}

	.card-title {
		font-size: 36rpx;
		font-weight: 500;
		color: #333333;
		text-align: center;
		display: block;
		margin-bottom: 60rpx;
	}

	.login-btn {
		width: 100%;
		height: 96rpx;
		border-radius: 48rpx;
		display: flex;
		align-items: center;
		justify-content: center;
		margin-bottom: 30rpx;
		transition: all 0.3s;
		border: none;
		padding: 0;
		line-height: 96rpx;
	}

	.login-btn::after {
		border: none;
	}

	.login-btn:active {
		transform: scale(0.98);
		opacity: 0.8;
	}

	.wechat-btn {
		background: linear-gradient(135deg, #09bb07 0%, #06a906 100%);
		box-shadow: 0 8rpx 20rpx rgba(9,187,7,0.3);
	}

	.btn-text {
		font-size: 32rpx;
		font-weight: 500;
		color: #ffffff;
	}

	.tips {
		font-size: 24rpx;
		color: #999999;
		text-align: center;
		display: block;
		margin-top: 40rpx;
		line-height: 36rpx;
	}
</style>
```

- [ ] **Step 2: Commit**

```bash
git add uniapp-h5/pages/login/index.vue
git commit -m "feat: rewrite login page with WeChat Mini Program auth and H5 conditional compilation"
```

---

### Task 9: Create profile completion page

**Files:**
- Create: `uniapp-h5/pages/my/complete-info.vue`
- Modify: `uniapp-h5/pages.json` — register the new page

This page replaces the homepage modal. Same form fields, but as a full standalone page. After submit, prompts user to go to e-Sign real-name auth.

- [ ] **Step 1: Register the page in pages.json**

Add after the `pages/my/about` entry (line 57 in pages.json):

```json
{
    "path": "pages/my/complete-info",
    "style": {
        "navigationBarTitleText": "完善个人信息",
        "navigationStyle": "custom"
    }
},
```

Also register the auth pages that are missing (verify.vue exists but is not registered):

```json
{
    "path": "pages/auth/verify",
    "style": {
        "navigationBarTitleText": "实名认证"
    }
},
{
    "path": "pages/auth/esign-webview",
    "style": {
        "navigationBarTitleText": "实名认证",
        "navigationStyle": "custom"
    }
},
```

- [ ] **Step 2: Create complete-info.vue**

```vue
<template>
	<view class="page">
		<!-- 自定义导航栏（无返回按钮） -->
		<view class="custom-nav">
			<view class="nav-status-bar"></view>
			<view class="nav-title-bar">
				<text class="nav-title">完善个人信息</text>
			</view>
		</view>

		<view class="content">
		<view class="header-card">
			<text class="header-title">完善个人信息</text>
			<text class="header-desc">请填写以下信息，以便使用平台服务</text>
		</view>

		<view class="form-card">
			<!-- 身份类型 -->
			<view class="form-group required">
				<text class="form-label">身份类型</text>
				<view class="radio-group">
					<view
						v-for="item in identityTypes"
						:key="item.value"
						class="radio-item"
						:class="{ active: form.identityType === item.value }"
						@click="form.identityType = item.value"
					>
						<text>{{ item.label }}</text>
					</view>
				</view>
			</view>

			<!-- 真实姓名 -->
			<view class="form-group required">
				<text class="form-label">真实姓名</text>
				<input class="form-input" v-model="form.realName" placeholder="请输入真实姓名" />
			</view>

			<!-- 身份证号 -->
			<view class="form-group required">
				<text class="form-label">身份证号</text>
				<input class="form-input" v-model="form.idCard" placeholder="请输入身份证号" maxlength="18" />
			</view>

			<!-- 联系电话 -->
			<view class="form-group required">
				<text class="form-label">联系电话</text>
				<input class="form-input" v-model="form.contactPhone" placeholder="请输入联系电话" type="number" maxlength="11" />
			</view>

			<!-- 工作单位 -->
			<view class="form-group required">
				<text class="form-label">工作单位</text>
				<input class="form-input" v-model="form.workUnit" placeholder="请输入工作单位" />
			</view>

			<!-- 单位联系电话 -->
			<view class="form-group required">
				<text class="form-label">单位联系电话</text>
				<input class="form-input" v-model="form.unitContact" placeholder="请输入单位联系电话" />
			</view>

			<!-- 配偶 -->
			<view class="form-group">
				<text class="form-label">配偶姓名</text>
				<input class="form-input" v-model="form.spouseName" placeholder="请输入配偶姓名（选填）" />
			</view>
		</view>

		<!-- 提交按钮 -->
		<view class="submit-btn" :class="{ disabled: submitting }" @click="handleSubmit">
			<text class="submit-btn-text">{{ submitting ? '提交中...' : '提交' }}</text>
		</view>
		</view>
	</view>
</template>

<script>
	import { updateUserInfo } from '@/api/auth'

	export default {
		data() {
			return {
				userId: null,
				submitting: false,
				identityTypes: [
					{ label: '在职人员', value: '1' },
					{ label: '应届毕业生', value: '2' }
				],
				form: {
					identityType: '',
					realName: '',
					idCard: '',
					contactPhone: '',
					workUnit: '',
					unitContact: '',
					spouseName: ''
				}
			}
		},
		onLoad() {
			// 检查登录状态（不用authCheck混入，因为这个页面本身就是登录后的下一步）
			const userInfo = uni.getStorageSync('userInfo')
			const token = uni.getStorageSync('token')
			if (!userInfo || !userInfo.userId || !token) {
				uni.reLaunch({ url: '/pages/login/index' })
				return
			}
			this.userId = userInfo.userId
			// 预填手机号
			if (userInfo.phone) {
				this.form.contactPhone = userInfo.phone
			}
		},
		methods: {
			validate() {
				if (!this.form.identityType) {
					uni.showToast({ title: '请选择身份类型', icon: 'none' })
					return false
				}
				if (!this.form.realName.trim()) {
					uni.showToast({ title: '请输入真实姓名', icon: 'none' })
					return false
				}
				if (!this.form.idCard.trim()) {
					uni.showToast({ title: '请输入身份证号', icon: 'none' })
					return false
				}
				const idCardReg = /(^\d{15}$)|(^\d{18}$)|(^\d{17}(\d|X|x)$)/
				if (!idCardReg.test(this.form.idCard.trim())) {
					uni.showToast({ title: '身份证号格式不正确', icon: 'none' })
					return false
				}
				if (!this.form.contactPhone.trim()) {
					uni.showToast({ title: '请输入联系电话', icon: 'none' })
					return false
				}
				if (!this.form.workUnit.trim()) {
					uni.showToast({ title: '请输入工作单位', icon: 'none' })
					return false
				}
				if (!this.form.unitContact.trim()) {
					uni.showToast({ title: '请输入单位联系电话', icon: 'none' })
					return false
				}
				return true
			},

			async handleSubmit() {
				if (this.submitting) return
				if (!this.validate()) return

				this.submitting = true
				try {
					await updateUserInfo({
						userId: this.userId,
						identityType: this.form.identityType,
						realName: this.form.realName.trim(),
						idCard: this.form.idCard.trim(),
						contactPhone: this.form.contactPhone.trim(),
						workUnit: this.form.workUnit.trim(),
						unitContact: this.form.unitContact.trim(),
						spouseName: this.form.spouseName.trim()
					})

					// 更新本地存储
					const userInfo = uni.getStorageSync('userInfo') || {}
					userInfo.isInfoCompleted = '1'
					userInfo.identityType = this.form.identityType
					userInfo.realName = this.form.realName.trim()
					userInfo.idCard = this.form.idCard.trim()
					userInfo.contactPhone = this.form.contactPhone.trim()
					userInfo.workUnit = this.form.workUnit.trim()
					userInfo.unitContact = this.form.unitContact.trim()
					userInfo.spouseName = this.form.spouseName.trim()
					uni.setStorageSync('userInfo', userInfo)

					// 提交成功，引导实名认证
					uni.showModal({
						title: '信息提交成功',
						content: '是否立即进行实名认证？完成实名认证后可使用更多平台功能。',
						confirmText: '去认证',
						cancelText: '稍后再说',
						success: (res) => {
							if (res.confirm) {
								uni.navigateTo({ url: '/pages/auth/verify' })
							} else {
								uni.reLaunch({ url: '/pages/index/index' })
							}
						}
					})

				} catch (error) {
					console.error('提交失败:', error)
					uni.showToast({
						title: error.msg || '提交失败',
						icon: 'none'
					})
				} finally {
					this.submitting = false
				}
			}
		}
	}
</script>

<style scoped>
	.page {
		width: 100%;
		min-height: 100vh;
		background: #f5f6fc;
		box-sizing: border-box;
	}

	.custom-nav {
		background: linear-gradient(270deg, #4fc7ff 0%, #0f73ff 100%);
	}

	.nav-status-bar {
		height: var(--status-bar-height, 44px);
	}

	.nav-title-bar {
		height: 88rpx;
		display: flex;
		align-items: center;
		justify-content: center;
	}

	.nav-title {
		font-size: 34rpx;
		font-weight: 500;
		color: #ffffff;
	}

	.content {
		padding: 24rpx;
	}

	.header-card {
		background: linear-gradient(270deg, #4fc7ff 0%, #0f73ff 100%);
		border-radius: 20rpx;
		padding: 40rpx 32rpx;
		margin-bottom: 24rpx;
	}

	.header-title {
		display: block;
		font-size: 40rpx;
		font-weight: 700;
		color: #ffffff;
		margin-bottom: 12rpx;
	}

	.header-desc {
		display: block;
		font-size: 26rpx;
		color: rgba(255, 255, 255, 0.85);
	}

	.form-card {
		background: #ffffff;
		border-radius: 20rpx;
		padding: 32rpx;
		margin-bottom: 40rpx;
	}

	.form-group {
		margin-bottom: 32rpx;
	}

	.form-group:last-child {
		margin-bottom: 0;
	}

	.form-group.required .form-label::before {
		content: '*';
		color: #e5252b;
		margin-right: 6rpx;
	}

	.form-label {
		display: block;
		font-size: 28rpx;
		color: #333333;
		font-weight: 500;
		margin-bottom: 16rpx;
	}

	.form-input {
		width: 100%;
		height: 88rpx;
		background: #f5f6fc;
		border-radius: 12rpx;
		padding: 0 24rpx;
		font-size: 28rpx;
		color: #333333;
		box-sizing: border-box;
	}

	.radio-group {
		display: flex;
		gap: 20rpx;
	}

	.radio-item {
		flex: 1;
		height: 88rpx;
		display: flex;
		align-items: center;
		justify-content: center;
		background: #f5f6fc;
		border-radius: 12rpx;
		font-size: 28rpx;
		color: #666666;
		border: 2rpx solid transparent;
	}

	.radio-item.active {
		background: #e6f0ff;
		color: #4A90E2;
		border-color: #4A90E2;
		font-weight: 500;
	}

	.submit-btn {
		width: 100%;
		height: 96rpx;
		background: linear-gradient(270deg, #4fc7ff 0%, #0f73ff 100%);
		border-radius: 48rpx;
		display: flex;
		align-items: center;
		justify-content: center;
	}

	.submit-btn.disabled {
		opacity: 0.6;
	}

	.submit-btn-text {
		font-size: 32rpx;
		font-weight: 500;
		color: #ffffff;
	}
</style>
```

- [ ] **Step 3: Commit**

```bash
git add uniapp-h5/pages/my/complete-info.vue uniapp-h5/pages.json
git commit -m "feat: add profile completion page and register auth pages in pages.json"
```

---

### Task 10: Clean up homepage — remove modal popup

**Files:**
- Modify: `uniapp-h5/pages/index/index.vue`

This task removes: (1) the modal template, (2) modal-related data/methods, (3) modal CSS, (4) the global unscoped style block, and (5) updates the `onShow` logic.

- [ ] **Step 1: Remove the modal template block**

Delete the entire block from `<!-- 个人信息弹窗 -->` (line 3) to its closing `</view>` (line 89). The template should start directly with `<!-- 滚动内容区域 -->`.

- [ ] **Step 2: Remove the `updateUserInfo` import**

In the `<script>` imports section (around line 227), remove:

```javascript
import { updateUserInfo } from '@/api/auth'
```

- [ ] **Step 3: Remove modal-related data fields**

In `data()`, remove these fields:
- `showModal: false`
- `formData: { identity: '', name: '', idCard: '', phone: '', workUnit: '', workPhone: '', spouse: '' }`
- `identityTypes: [{ label: '在职人员', value: '1' }, { label: '应届毕业生', value: '2' }]`
- `selectedIdentityIndex: -1`

- [ ] **Step 4: Update onShow logic**

Replace the `isInfoCompleted` check block (around lines 290-299) with:

```javascript
// 检查用户是否已填写个人信息
const userInfo = uni.getStorageSync('userInfo')
if (userInfo && userInfo.isInfoCompleted !== '1') {
    uni.redirectTo({ url: '/pages/my/complete-info' })
    return
}
```

- [ ] **Step 5: Remove modal-related methods**

Remove these methods:
- `closeModal()`
- `onIdentityChange(e)`
- `getIdentityLabel()`
- `handleConfirm()`

- [ ] **Step 6: Remove modal CSS**

Delete all styles from `/* 弹窗样式 */` (line ~1382) through the end of the scoped style block. This includes `.modal-overlay`, `.modal-content`, `.modal-header`, `.modal-header-bg`, `.modal-title`, `.modal-body`, `.form-item`, `.form-label`, `.required-mark`, `.label-text`, `.form-input-wrapper`, `.picker-input`, `.form-input`, `.form-select`, `.input-icon-wrapper`, `.input-icon`, `.modal-footer`, `.confirm-btn`, `.confirm-btn-text`.

Also delete the entire unscoped `<style>` block at the bottom (lines ~1663-1689) that controls picker dropdown widths.

- [ ] **Step 7: Commit**

```bash
git add uniapp-h5/pages/index/index.vue
git commit -m "refactor: remove homepage modal popup, redirect to complete-info page instead"
```

---

### Task 11: Delete dead code — UserInfoDialog.vue

**Files:**
- Delete: `uniapp-h5/components/UserInfoDialog.vue`

- [ ] **Step 1: Delete the file**

```bash
rm uniapp-h5/components/UserInfoDialog.vue
```

- [ ] **Step 2: Commit**

```bash
git add -u uniapp-h5/components/UserInfoDialog.vue
git commit -m "cleanup: remove unused UserInfoDialog component"
```

---

## Chunk 3: e-Sign Webview Adaptation for Mini Program

### Task 12: Create e-Sign webview wrapper page

**Files:**
- Create: `uniapp-h5/pages/auth/esign-webview.vue`

This page wraps `<web-view>` to open e-Sign authentication URL in the mini program. After e-Sign redirects back, the backend auth-done page uses `wx.miniProgram.redirectTo` to navigate back to the native verify page.

- [ ] **Step 1: Create the webview page**

```vue
<template>
	<!-- #ifdef MP-WEIXIN -->
	<web-view :src="url" @message="onMessage"></web-view>
	<!-- #endif -->

	<!-- #ifdef H5 -->
	<view class="page">
		<text>此页面仅在微信小程序中使用</text>
	</view>
	<!-- #endif -->
</template>

<script>
	export default {
		data() {
			return {
				url: ''
			}
		},
		onLoad(options) {
			if (options.url) {
				this.url = decodeURIComponent(options.url)
			}
		},
		methods: {
			onMessage(e) {
				// web-view内页面通过postMessage传回的消息
				console.log('webview message:', e.detail)
			}
		}
	}
</script>

<style scoped>
	.page {
		display: flex;
		align-items: center;
		justify-content: center;
		min-height: 100vh;
	}
</style>
```

- [ ] **Step 2: Commit**

```bash
git add uniapp-h5/pages/auth/esign-webview.vue
git commit -m "feat: add e-Sign webview wrapper page for mini program"
```

---

### Task 13: Adapt verify.vue for mini program

**Files:**
- Modify: `uniapp-h5/pages/auth/verify.vue:147-148`

The current code uses `window.location.href` which doesn't work in mini program. Add conditional compilation.

- [ ] **Step 1: Replace the redirect logic in handleSubmit**

In `handleSubmit()`, replace the line:

```javascript
window.location.href = res.data.authUrl
```

With:

```javascript
// #ifdef MP-WEIXIN
uni.navigateTo({
    url: '/pages/auth/esign-webview?url=' + encodeURIComponent(res.data.authUrl)
})
// #endif
// #ifdef H5
window.location.href = res.data.authUrl
// #endif
```

- [ ] **Step 2: Commit**

```bash
git add uniapp-h5/pages/auth/verify.vue
git commit -m "feat: adapt e-Sign verify page for mini program webview"
```

---

### Task 14: Add backend e-Sign auth-done redirect page

The backend needs an endpoint that serves an HTML page using `wx.miniProgram.redirectTo()` to jump back from webview to native mini program page after e-Sign completion.

**Files:**
- Create: `ruoyi-admin/src/main/java/com/ruoyi/web/controller/h5/EsignAuthDoneController.java`

- [ ] **Step 1: Create the controller**

```java
package com.ruoyi.web.controller.h5;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * e签宝实名认证完成后的中间跳转页
 * 在微信小程序webview中，e签宝认证完成后会重定向到此页面
 * 此页面通过wx.miniProgram.redirectTo跳回小程序原生页面
 */
@Controller
@RequestMapping("/h5/esign")
public class EsignAuthDoneController {

    @GetMapping("/auth-done")
    public void authDone(HttpServletResponse response) throws IOException {
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter writer = response.getWriter();
        writer.write("<!DOCTYPE html>");
        writer.write("<html><head>");
        writer.write("<meta charset=\"UTF-8\">");
        writer.write("<meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">");
        writer.write("<title>认证完成</title>");
        writer.write("<script src=\"https://res.wx.qq.com/open/js/jweixin-1.6.0.js\"></script>");
        writer.write("</head><body>");
        writer.write("<div style=\"display:flex;align-items:center;justify-content:center;height:100vh;font-size:16px;color:#666;\">认证完成，正在返回...</div>");
        writer.write("<script>");
        writer.write("try { wx.miniProgram.redirectTo({ url: '/pages/auth/verify?auth_done=1' }); } catch(e) {}");
        writer.write("setTimeout(function() { window.location.href = 'https://app.caigon.cn/#/pages/auth/verify?auth_done=1'; }, 2000);");
        writer.write("</script>");
        writer.write("</body></html>");
        writer.flush();
    }
}
```

- [ ] **Step 2: Update e-Sign auth-redirect-url in application.yml**

In `application.yml`, update the `esign.auth-redirect-url`. Note: the current value uses ngrok for dev. For production deployment, change it to the auth-done endpoint:

```yaml
  # 实名认证完成后跳回（小程序webview中间页 + H5 fallback）
  # 开发环境使用ngrok时保持当前值，部署时改为:
  auth-redirect-url: "https://api.caigon.cn/h5/esign/auth-done"
```

For local development, you can keep using the ngrok URL but point it to the same path: `https://<ngrok-domain>/h5/esign/auth-done`

- [ ] **Step 3: Commit**

```bash
git add ruoyi-admin/src/main/java/com/ruoyi/web/controller/h5/EsignAuthDoneController.java \
       ruoyi-admin/src/main/resources/application.yml
git commit -m "feat: add e-Sign auth-done redirect page for mini program webview callback"
```

---

## Summary of All Files

| Task | Action | File |
|------|--------|------|
| 1 | Modify | `ruoyi-admin/src/main/resources/application.yml` |
| 2 | Create | `ruoyi-admin/.../config/WechatMiniappConfig.java` |
| 3 | Create | `ruoyi-admin/.../service/WechatMiniappService.java` |
| 4 | Modify | `ruoyi-system/.../service/IHzUserService.java` |
| 4 | Modify | `ruoyi-system/.../service/impl/HzUserServiceImpl.java` |
| 5 | Modify | `ruoyi-admin/.../controller/system/HzAuthController.java` |
| 6 | Modify | `uniapp-h5/manifest.json` |
| 7 | Modify | `uniapp-h5/api/auth.js` |
| 8 | Rewrite | `uniapp-h5/pages/login/index.vue` |
| 9 | Create | `uniapp-h5/pages/my/complete-info.vue` |
| 9 | Modify | `uniapp-h5/pages.json` |
| 10 | Modify | `uniapp-h5/pages/index/index.vue` |
| 11 | Delete | `uniapp-h5/components/UserInfoDialog.vue` |
| 12 | Create | `uniapp-h5/pages/auth/esign-webview.vue` |
| 13 | Modify | `uniapp-h5/pages/auth/verify.vue` |
| 14 | Create | `ruoyi-admin/.../controller/h5/EsignAuthDoneController.java` |
| 14 | Modify | `ruoyi-admin/src/main/resources/application.yml` |
