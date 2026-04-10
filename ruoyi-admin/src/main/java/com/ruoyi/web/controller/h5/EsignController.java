package com.ruoyi.web.controller.h5;

import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.system.domain.HzContract;
import com.ruoyi.system.domain.HzUser;
import com.ruoyi.system.mapper.HzContractMapper;
import com.ruoyi.system.mapper.HzUserMapper;
import com.ruoyi.system.service.EsignService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/h5/esign")
public class EsignController extends BaseController {

    @Value("${esign.redirect-url}") private String redirectUrl;
    @Autowired private EsignService esignService;
    @Autowired private HzContractMapper contractMapper;
    @Autowired private HzUserMapper userMapper;

    // ==================== 实名认证 ====================

    /**
     * 获取实名认证URL
     * @param realName   真实姓名（前端表单填写，优先使用）
     * @param idCard     身份证号（前端表单填写，优先使用）
     * @param redirectUrl 认证完成后跳转地址（小程序传 wechat://back）
     */
    @GetMapping("/auth-url/{userId}")
    public AjaxResult getAuthUrl(@PathVariable Long userId,
                                 @RequestParam(required = false) String realName,
                                 @RequestParam(required = false) String idCard,
                                 @RequestParam(required = false) String redirectUrl) {
        HzUser user = userMapper.selectById(userId);
        if (user == null) return error("用户信息不存在，请先完善个人信息");
        if (user.getEsignPsnId() != null && !user.getEsignPsnId().isEmpty()) {
            Map<String, Object> r = new HashMap<>();
            r.put("needAuth", false);
            return success(r);
        }
        // 如果传入了 realName/idCard，保存到用户表（后续模板填充等流程使用）
        if (realName != null && !realName.isBlank()) {
            userMapper.update(null, new com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper<HzUser>()
                    .eq(HzUser::getUserId, userId).set(HzUser::getRealName, realName));
        }
        if (idCard != null && !idCard.isBlank()) {
            userMapper.update(null, new com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper<HzUser>()
                    .eq(HzUser::getUserId, userId).set(HzUser::getIdCard, idCard));
        }
        String callbackUrl = (redirectUrl != null && !redirectUrl.isBlank()) ? redirectUrl : this.redirectUrl;
        String authUrl = esignService.getPsnAuthUrl(userId, user.getPhone(), realName, idCard, callbackUrl);
        Map<String, Object> r = new HashMap<>();
        r.put("needAuth", true);
        r.put("authUrl", authUrl);
        return success(r);
    }

    /**
     * 查询实名认证状态
     */
    @GetMapping("/auth-status/{userId}")
    public AjaxResult getAuthStatus(@PathVariable Long userId) {
        HzUser user = userMapper.selectById(userId);
        if (user == null) return error("用户信息不存在");
        if (user.getEsignPsnId() != null && !user.getEsignPsnId().isEmpty()) {
            Map<String, Object> r = new HashMap<>();
            r.put("authenticated", true);
            r.put("psnId", user.getEsignPsnId());
            return success(r);
        }
        String psnId = esignService.queryAndSavePsnId(user.getUserId(), user.getPhone());
        Map<String, Object> r = new HashMap<>();
        r.put("authenticated", psnId != null);
        r.put("psnId", psnId);
        return success(r);
    }

    // ==================== 签署流程 ====================

    /**
     * 一体化签署接口：模板填充→创建签署流→返回签署URL
     * @param params.platform "mp" = 小程序（使用 wechat://back），其他 = H5
     */
    @PostMapping("/init-sign")
    public AjaxResult initSign(@RequestBody Map<String, Object> params) {
        Long contractId = params.containsKey("contractId")
                ? Long.valueOf(params.get("contractId").toString()) : null;
        Long userId = params.containsKey("userId")
                ? Long.valueOf(params.get("userId").toString()) : null;
        String platform = params.containsKey("platform") ? params.get("platform").toString() : "h5";

        if (contractId == null || userId == null) return error("参数不完整");

        HzUser user = userMapper.selectById(userId);
        if (user == null) return error("用户信息不存在");

        // 小程序使用 wechat://back，H5 使用配置的 redirectUrl
        String signRedirectUrl = "mp".equals(platform) ? "wechat://back" : redirectUrl;

        // 1. 检查实名认证
        String psnId = user.getEsignPsnId();
        if (psnId == null || psnId.isEmpty()) {
            // 未认证 → 返回认证链接
            String authRedirectUrl = "mp".equals(platform) ? "wechat://back" : redirectUrl;
            String authUrl = esignService.getPsnAuthUrl(
                    user.getUserId(), user.getPhone(), null, null, authRedirectUrl);
            Map<String, Object> r = new HashMap<>();
            r.put("needAuth", true);
            r.put("authUrl", authUrl);
            return success(r);
        }

        // 2. 一体化：模板填充→创建签署流→返回签署URL
        try {
            String signUrl = esignService.initSign(contractId, psnId, signRedirectUrl);
            Map<String, Object> r = new HashMap<>();
            r.put("needAuth", false);
            r.put("signUrl", signUrl);
            return success(r);
        } catch (Exception e) {
            logger.error("initSign 失败，contractId={}", contractId, e);
            return error("发起签署失败：" + e.getMessage());
        }
    }

    /**
     * 获取合同签署URL（兼容旧入口，H5 端使用）
     */
    @GetMapping("/sign-url/{contractId}")
    public AjaxResult getSignUrl(@PathVariable Long contractId, @RequestParam Long userId) {
        HzContract contract = contractMapper.selectById(contractId);
        if (contract == null) return error("合同不存在");
        if ("2".equals(contract.getContractStatus())) return error("合同已签署");

        HzUser user = userMapper.selectById(userId);
        if (user == null) return error("用户信息不存在");

        String psnId = user.getEsignPsnId();
        if (psnId == null || psnId.isEmpty()) return error("请先完成实名认证");

        try {
            String signFlowId = contract.getEsignFlowId();
            if (signFlowId != null && !signFlowId.isEmpty()) {
                String signUrl = esignService.getSignUrl(signFlowId, user.getPhone(), redirectUrl);
                Map<String, String> r = new HashMap<>();
                r.put("signUrl", signUrl);
                return success(r);
            }
            String signUrl = esignService.initSign(contractId, psnId, redirectUrl);
            Map<String, String> r = new HashMap<>();
            r.put("signUrl", signUrl);
            return success(r);
        } catch (Exception e) {
            logger.error("获取签署 URL 失败 contractId={}", contractId, e);
            return error("获取签署链接失败：" + e.getMessage());
        }
    }

    // ==================== 回调 ====================

    /**
     * e签宝回调通知
     */
    @PostMapping("/notify")
    public ResponseEntity<Map<String, Object>> esignNotify(HttpServletRequest request, @RequestBody String body) {
        Map<String, Object> resp = new HashMap<>();
        try {
            String timestamp = request.getHeader("X-Tsign-Open-Timestamp");
            if (timestamp == null) timestamp = request.getHeader("X-Tsign-Open-Sign-Timestamp");
            String signature = request.getHeader("X-Tsign-Open-Sign");
            String requestQuery = request.getQueryString() != null ? request.getQueryString() : "";
            esignService.handleSignCallback(timestamp, requestQuery, body, signature);
            resp.put("code", "0");
            return ResponseEntity.ok(resp);
        } catch (Exception e) {
            logger.error("e签宝回调处理失败", e);
            resp.put("code", "-1");
            resp.put("message", e.getMessage());
            return ResponseEntity.status(500).body(resp);
        }
    }

    // ==================== 调试接口 ====================

    @GetMapping("/template-detail")
    public AjaxResult queryTemplateDetail() {
        try {
            String result = esignService.queryTemplateDetail();
            return success(result);
        } catch (Exception e) {
            logger.error("查询模板详情失败", e);
            return error("查询模板详情失败：" + e.getMessage());
        }
    }
}
