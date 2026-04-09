# WeChat Mini Program Login Integration Design

**Date:** 2026-04-09
**Status:** Approved

## Overview

Replace the current H5 login page (test data + ZhengHaoBan) with WeChat Mini Program native login. Users authorize their phone number via WeChat, then complete a profile form on a dedicated page. After profile completion, they are prompted to proceed with e-Sign (e签宝) real-name verification.

## Configuration Context

- **AppID:** `wx74efa287343d0fa2`
- **AppSecret:** configured in `application.yml` under `wechat.miniapp`
- **Server domain whitelist:** `https://api.caigon.cn` (request/upload/download)
- **e-Sign sandbox domain:** `https://hnj7439105666.smlh5.esign.cn` (webview whitelist for real-name auth)
- **Production config:** `config/index.js` production env already points to `https://api.caigon.cn`

### YAML Fix Required

**`application.yml` has a duplicate `wechat:` key** — the `wechat.pay` block (line 186) silently overrides `wechat.miniapp` (line 179). Before implementation, merge them under a single `wechat:` root:

```yaml
wechat:
  miniapp:
    appid: "wx74efa287343d0fa2"
    secret: "5bbac44092d17077e1924883929afcff"
  pay:
    enabled: false
    # ... rest of pay config
```

## User Flow

```
User opens Mini Program
    |
    v
Login page shows: Logo + "微信授权登录" button (open-type="getPhoneNumber")
    |
    v
User taps button -> WeChat phone number authorization popup
    |
    v  (on success)
Frontend calls wx.login() to get code
    |
    v
Frontend sends { code, phoneCode } -> POST /app/auth/wxLogin
    |
    v
Backend:
  1. code -> WeChat jscode2session API -> openid (+ optional unionid)
  2. phoneCode -> WeChat getuserphonenumber API -> phone number
  3. Lookup hz_user by wechatOpenid:
     - Exists: update lastLoginTime, return user
     - Not exists: create new user (phone, wechatOpenid, sourceType='1', loginType='wechat')
  4. Generate token, return { token, userInfo }
    |
    v
Frontend stores token + userId + userInfo
    |
    v
Check userInfo.isInfoCompleted:
    |--- '1' -> reLaunch('/pages/index/index')
    |--- '0' -> redirectTo('/pages/my/complete-info')
               |
               v
         Complete Info Page:
           - 身份类型 (在职人员/应届毕业生) [required]
           - 真实姓名 [required]
           - 身份证号 [required]
           - 联系电话 [prefilled from WeChat phone, editable] [required]
           - 工作单位 [required]
           - 单位联系电话 [required]
           - 配偶 [optional]
               |
               v
         Submit -> PUT /app/auth/updateInfo
               |
               v
         Success -> Modal: "是否立即进行实名认证？"
           |--- "去认证" -> navigateTo('/pages/auth/verify')
           |--- "稍后再说" -> reLaunch('/pages/index/index')
```

## Backend Changes

### 1. New: WechatMiniappService

**Location:** `ruoyi-admin/src/main/java/com/ruoyi/web/service/WechatMiniappService.java`

Responsibilities:
- `getOpenid(String code)` — calls WeChat `https://api.weixin.qq.com/sns/jscode2session` with appid, secret, code; returns openid (session_key is received but must NOT be stored in DB or returned to frontend)
- `getPhoneNumber(String phoneCode)` — POST to `https://api.weixin.qq.com/wxa/business/getuserphonenumber?access_token=XXX` with JSON body `{"code": phoneCode}`; returns phone number
- `getAccessToken()` — calls WeChat `https://api.weixin.qq.com/cgi-bin/token` to get access_token; cache in Redis with TTL (7200s default, refresh at 7000s); on failure retry once, then throw exception

**Config source:** reads `wechat.miniapp.appid` and `wechat.miniapp.secret` from application.yml (already configured).

### 2. New: /app/auth/wxLogin endpoint

**Location:** add to existing `HzAuthController.java`

```java
@PostMapping("/wxLogin")
public AjaxResult wxLogin(@RequestBody Map<String, String> params) {
    String code = params.get("code");         // from wx.login()
    String phoneCode = params.get("phoneCode"); // from getPhoneNumber
    
    // 1. code -> openid
    String openid = wechatMiniappService.getOpenid(code);
    
    // 2. phoneCode -> phone
    String phone = wechatMiniappService.getPhoneNumber(phoneCode);
    
    // 3. Find or create user
    HzUser user = userService.loginOrRegisterByWechat(openid, phone);
    
    // 4. Generate token
    String token = "hz_token_" + user.getUserId() + "_" + System.currentTimeMillis();
    
    // 5. Return token + userInfo (must include all fields the existing login returns:
    //    userId, phone, nickname, realName, idCard, loginType, isInfoCompleted, authStatus)
}
```

### 3. New: IHzUserService.loginOrRegisterByWechat

```java
HzUser loginOrRegisterByWechat(String openid, String phone);
```

Logic:
1. Query `hz_user` by `wechat_openid = openid`
2. If found: update `last_login_time`, return user
3. If not found: query by `phone` to check existing phone user
   - If phone user exists with a **different** openid already set: **reject login** with error "该手机号已被其他账号绑定" (prevents silent account takeover)
   - If phone user exists with no openid: bind openid to existing user, update `last_login_time`
   - If no user at all: create new `hz_user` with `phone`, `wechat_openid=openid`, `source_type='1'`, `login_type='wechat'`, `is_info_completed='0'`, `auth_status='0'`
4. Return user

### 4. WeChat Configuration Class

**Location:** `ruoyi-admin/src/main/java/com/ruoyi/web/config/WechatMiniappConfig.java`

```java
@Configuration
@ConfigurationProperties(prefix = "wechat.miniapp")
public class WechatMiniappConfig {
    private String appid;
    private String secret;
    // getters/setters
}
```

### 5. Security Filter Update

Ensure `/app/auth/wxLogin` is added to the anonymous access whitelist in Spring Security config (same as existing `/app/auth/login`).

## Frontend Changes

### 1. Rewrite: pages/login/index.vue

**Mini Program version** (`#ifdef MP-WEIXIN`):
- Logo section (same visual style)
- Single button: `<button open-type="getPhoneNumber" @getphonenumber="onGetPhoneNumber">微信授权登录</button>`
- On phone number success:
  1. Call `wx.login()` to get code
  2. Send `{ code, phoneCode: e.detail.code }` to `/app/auth/wxLogin`
  3. Store token, userId, userInfo
  4. Route based on `isInfoCompleted`
- User agreement text at bottom

**H5 version** (`#ifdef H5`):
- Keep current test login for development
- Remove ZhengHaoBan login button (no longer needed)

### 2. New: pages/my/complete-info.vue

A full-page form (not a modal) with the same fields as the current homepage popup:

| Field | Type | Required | Notes |
|-------|------|----------|-------|
| 身份类型 | Radio (在职人员/应届毕业生) | Yes | |
| 真实姓名 | Text input | Yes | |
| 身份证号 | Text input (maxlength=18) | Yes | Format validation |
| 联系电话 | Text input (number, maxlength=11) | Yes | Prefilled from WeChat phone |
| 工作单位 | Text input | Yes | |
| 单位联系电话 | Text input | Yes | |
| 配偶 | Text input | No | |

**Submit flow:**
1. Validate all required fields
2. Call `PUT /app/auth/updateInfo` with userId + form data
3. Update local `userInfo` in storage (`isInfoCompleted = '1'`)
4. Show modal: "信息提交成功，是否立即进行实名认证？"
   - "去认证" → `uni.navigateTo({ url: '/pages/auth/verify' })`
   - "稍后再说" → `uni.reLaunch({ url: '/pages/index/index' })`

**Page registration:** Add to `pages.json`:
```json
{ "path": "pages/my/complete-info", "style": { "navigationBarTitleText": "完善个人信息" } }
```

**Navigation restriction:** This page should not have a back button (use custom nav bar or `navigationStyle: custom`), forcing user to either submit or stay.

### 3. Modify: pages/index/index.vue

**Remove:**
- The entire `modal-overlay` template block (lines 3-88 of current template)
- `showModal`, `formData`, `identityTypes`, `selectedIdentityIndex` from `data()`
- `closeModal()`, `handleConfirm()`, `onIdentityChange()`, `getIdentityLabel()` methods
- `updateUserInfo` import (no longer used on this page)

**Modify `onShow`:**
```javascript
onShow() {
    const token = uni.getStorageSync('token')
    if (!token) {
        uni.reLaunch({ url: '/pages/login/index' })
        return
    }
    const userInfo = uni.getStorageSync('userInfo')
    if (userInfo && userInfo.isInfoCompleted !== '1') {
        uni.redirectTo({ url: '/pages/my/complete-info' })
        return
    }
    // ... rest of existing logic (loadProjectList, loadBanners, etc.)
}
```

### 4. Delete: components/UserInfoDialog.vue

This component is imported nowhere and is dead code. Remove it.

### 5. Modify: manifest.json

Fill in the WeChat Mini Program appid:
```json
"mp-weixin": {
    "appid": "wx74efa287343d0fa2",
    ...
}
```

### 6. Modify: pages/auth/verify.vue

The current `window.location.href = res.data.authUrl` won't work in Mini Program.

**Mini Program adaptation:**
- Create a new page `pages/auth/esign-webview.vue` that wraps `<web-view :src="url">` for the e-Sign auth flow
- The e-Sign sandbox domain `https://hnj7439105666.smlh5.esign.cn` is already in the webview whitelist
- In `verify.vue`, for Mini Program: instead of `window.location.href`, use `uni.navigateTo({ url: '/pages/auth/esign-webview?url=' + encodeURIComponent(authUrl) })`

**e-Sign callback handling in Mini Program:**
- e-Sign redirects back to `auth-redirect-url` after completion. In Mini Program webview, when the `<web-view>` navigates to a URL matching our domain, the page needs to detect it.
- Update `application.yml` `esign.auth-redirect-url` to a **non-hash** URL that the backend serves: `https://api.caigon.cn/h5/esign/auth-done` (a backend endpoint that returns an HTML page with `<script>` calling `wx.miniProgram.navigateBack()` or `wx.miniProgram.redirectTo()`)
- The backend `auth-done` endpoint renders a simple HTML page that uses WeChat JS-SDK `wx.miniProgram.redirectTo({ url: '/pages/auth/verify?auth_done=1' })` to jump back to the native verify page
- `verify.vue` then queries auth status as it already does when `auth_done=1`

This approach works because `<web-view>` pages can communicate back to the Mini Program via `wx.miniProgram` API from within the webview.

### 7. API: New wxLogin function

**Location:** `uniapp-h5/api/auth.js`

```javascript
export function wxLogin(data) {
    return request.post('/app/auth/wxLogin', data)
}
```

### 8. Config: Ensure production env is active for Mini Program

In `config/index.js`, the Mini Program build should use the `production` config which points to `https://api.caigon.cn`. The current manual `env = 'production'` setting already handles this.

## Data Flow Summary

```
hz_user table fields involved:
  - user_id (auto)
  - phone (from WeChat getPhoneNumber)
  - wechat_openid (from wx.login code -> jscode2session)
  - wechat_unionid (optional, from jscode2session if available)
  - source_type = '1' (微信小程序)
  - login_type = 'wechat'
  - is_info_completed = '0' (initial) -> '1' (after complete-info submit)
  - auth_status = '0' (initial) -> '2' (after e-Sign verification)
  - real_name (from complete-info form)
  - id_card (from complete-info form)
  - identity_type (from complete-info form)
  - work_unit (from complete-info form)
  - unit_contact (from complete-info form)
  - contact_phone (from complete-info form, may differ from login phone)
  - spouse_name (from complete-info form, optional)
  - nickname (set to real_name initially, or "微信用户")
```

## Files Changed Summary

| Action | File |
|--------|------|
| **New** | `ruoyi-admin/.../service/WechatMiniappService.java` |
| **New** | `ruoyi-admin/.../config/WechatMiniappConfig.java` |
| **Modify** | `ruoyi-admin/.../controller/system/HzAuthController.java` — add wxLogin endpoint |
| **Modify** | `ruoyi-system/.../service/IHzUserService.java` — add loginOrRegisterByWechat |
| **Modify** | `ruoyi-system/.../service/impl/HzUserServiceImpl.java` — implement loginOrRegisterByWechat |
| **Modify** | `ruoyi-admin/.../config/SecurityConfig.java` — whitelist /app/auth/wxLogin |
| **Rewrite** | `uniapp-h5/pages/login/index.vue` — WeChat mini program login |
| **New** | `uniapp-h5/pages/my/complete-info.vue` — profile completion page |
| **New** | `uniapp-h5/pages/auth/esign-webview.vue` — webview wrapper for e-Sign |
| **Modify** | `uniapp-h5/pages/index/index.vue` — remove popup, add redirect logic |
| **Modify** | `uniapp-h5/pages/auth/verify.vue` — mini program webview adaptation |
| **Modify** | `uniapp-h5/api/auth.js` — add wxLogin function |
| **Modify** | `uniapp-h5/pages.json` — register new pages |
| **Modify** | `uniapp-h5/manifest.json` — fill appid |
| **Delete** | `uniapp-h5/components/UserInfoDialog.vue` — dead code |

## Error Handling

- **User denies phone authorization:** Show toast "需要授权手机号才能登录", stay on login page
- **wx.login fails:** Show toast "微信登录失败，请重试"
- **Backend wxLogin fails (network):** Show toast "网络连接失败"
- **Backend wxLogin fails (WeChat API error):** Return specific error message, show to user
- **complete-info validation fails:** Highlight missing fields, show toast
- **complete-info submit fails:** Show error toast, stay on page

## Out of Scope

- WeChat Pay integration (already exists separately)
- Push notifications via WeChat
- WeChat subscription messages
- Mini Program sharing / QR code

## Known Limitations (Pre-existing)

- **Token is not cryptographically signed.** The current `hz_token_userId_timestamp` pattern is inherited from the existing codebase. It is not validated server-side. A proper JWT or Redis-backed token system is recommended as a future improvement but is out of scope for this change.
- **`/app/auth/updateInfo` accepts userId from request body** without server-side token validation. This is a pre-existing pattern; fixing it requires the token system improvement above.
