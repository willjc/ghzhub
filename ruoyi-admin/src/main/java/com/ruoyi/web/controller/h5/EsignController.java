package com.ruoyi.web.controller.h5;

import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.system.domain.HzContract;
import com.ruoyi.system.domain.HzTenant;
import com.ruoyi.system.mapper.HzContractMapper;
import com.ruoyi.system.service.EsignService;
import com.ruoyi.system.service.IHzTenantService;
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
    @Autowired private IHzTenantService tenantService;

    // ==================== 实名认证 ====================

    /**
     * 获取实名认证URL
     */
    @GetMapping("/auth-url/{userId}")
    public AjaxResult getAuthUrl(@PathVariable Long userId,
                                 @RequestParam(required = false) String redirectUrl) {
        HzTenant tenant = tenantService.selectTenantByUserId(userId);
        if (tenant == null) return error("租户信息不存在，请先完善个人信息");
        if (tenant.getEsignPsnId() != null && !tenant.getEsignPsnId().isEmpty()) {
            Map<String, Object> r = new HashMap<>();
            r.put("needAuth", false);
            return success(r);
        }
        String callbackUrl = redirectUrl != null ? redirectUrl : this.redirectUrl;
        String authUrl = esignService.getPsnAuthUrl(tenant.getTenantId(), tenant.getPhone(), callbackUrl);
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
        HzTenant tenant = tenantService.selectTenantByUserId(userId);
        if (tenant == null) return error("租户信息不存在");
        if (tenant.getEsignPsnId() != null && !tenant.getEsignPsnId().isEmpty()) {
            Map<String, Object> r = new HashMap<>();
            r.put("authenticated", true);
            r.put("psnId", tenant.getEsignPsnId());
            return success(r);
        }
        String psnId = esignService.queryAndSavePsnId(tenant.getTenantId(), tenant.getPhone());
        Map<String, Object> r = new HashMap<>();
        r.put("authenticated", psnId != null);
        r.put("psnId", psnId);
        return success(r);
    }

    // ==================== 签署流程 ====================

    /**
     * 一体化签署接口：模板填充→创建签署流→返回签署URL
     * POST /h5/esign/init-sign
     *
     * 请求体：{ "contractId": 123, "userId": 456 }
     * 响应：
     *   需认证 → { "code": 200, "data": { "needAuth": true, "authUrl": "https://..." } }
     *   签署链接 → { "code": 200, "data": { "needAuth": false, "signUrl": "https://..." } }
     */
    @PostMapping("/init-sign")
    public AjaxResult initSign(@RequestBody Map<String, Long> params) {
        Long contractId = params.get("contractId");
        Long userId = params.get("userId");
        if (contractId == null || userId == null) return error("参数不完整");

        HzTenant tenant = tenantService.selectTenantByUserId(userId);
        if (tenant == null) return error("租户信息不存在");

        // 1. 检查实名认证
        String psnId = tenant.getEsignPsnId();
        if (psnId == null || psnId.isEmpty()) {
            // 未认证 → 返回认证链接
            String authUrl = esignService.getPsnAuthUrl(tenant.getTenantId(), tenant.getPhone(), redirectUrl);
            Map<String, Object> r = new HashMap<>();
            r.put("needAuth", true);
            r.put("authUrl", authUrl);
            return success(r);
        }

        // 2. 一体化：模板填充→创建签署流→返回签署URL
        try {
            String signUrl = esignService.initSign(contractId, psnId);
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
     * 获取合同签署URL（兼容旧入口，内部改为调用模板模式）
     */
    @GetMapping("/sign-url/{contractId}")
    public AjaxResult getSignUrl(@PathVariable Long contractId, @RequestParam Long userId) {
        HzContract contract = contractMapper.selectById(contractId);
        if (contract == null) return error("合同不存在");
        if ("2".equals(contract.getContractStatus())) return error("合同已签署");

        HzTenant tenant = tenantService.selectTenantByUserId(userId);
        if (tenant == null) return error("租户信息不存在");

        String psnId = tenant.getEsignPsnId();
        if (psnId == null || psnId.isEmpty()) return error("请先完成实名认证");

        try {
            // 如果已有签署流，直接获取签署链接
            String signFlowId = contract.getEsignFlowId();
            if (signFlowId != null && !signFlowId.isEmpty()) {
                String signUrl = esignService.getSignUrl(signFlowId, tenant.getPhone(), redirectUrl);
                Map<String, String> r = new HashMap<>();
                r.put("signUrl", signUrl);
                return success(r);
            }
            // 否则走一体化流程
            String signUrl = esignService.initSign(contractId, psnId);
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
     * e签宝回调通知（签署完成等事件）
     * 此接口由e签宝服务器调用，无需JWT认证
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

    /**
     * 查询模板详情（调试用）
     */
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
