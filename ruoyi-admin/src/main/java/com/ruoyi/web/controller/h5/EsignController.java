package com.ruoyi.web.controller.h5;

import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.system.domain.HzContract;
import com.ruoyi.system.domain.HzTenant;
import com.ruoyi.system.mapper.HzContractMapper;
import com.ruoyi.system.mapper.HzTenantMapper;
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
    @Autowired private HzTenantMapper tenantMapper;

    @GetMapping("/auth-url/{tenantId}")
    public AjaxResult getAuthUrl(@PathVariable Long tenantId,
                                 @RequestParam(required = false) String redirectUrl) {
        HzTenant tenant = tenantMapper.selectById(tenantId);
        if (tenant == null) return error("租户不存在");
        if (tenant.getEsignPsnId() != null && !tenant.getEsignPsnId().isEmpty()) {
            Map<String, Object> r = new HashMap<>(); r.put("needAuth", false); return success(r);
        }
        String callbackUrl = redirectUrl != null ? redirectUrl : this.redirectUrl;
        String authUrl = esignService.getPsnAuthUrl(tenantId, tenant.getPhone(), callbackUrl);
        Map<String, Object> r = new HashMap<>(); r.put("needAuth", true); r.put("authUrl", authUrl);
        return success(r);
    }

    @GetMapping("/auth-status/{tenantId}")
    public AjaxResult getAuthStatus(@PathVariable Long tenantId) {
        HzTenant tenant = tenantMapper.selectById(tenantId);
        if (tenant == null) return error("租户不存在");
        if (tenant.getEsignPsnId() != null && !tenant.getEsignPsnId().isEmpty()) {
            Map<String, Object> r = new HashMap<>(); r.put("authenticated", true); r.put("psnId", tenant.getEsignPsnId()); return success(r);
        }
        String psnId = esignService.queryAndSavePsnId(tenantId, tenant.getPhone());
        Map<String, Object> r = new HashMap<>(); r.put("authenticated", psnId != null); r.put("psnId", psnId);
        return success(r);
    }

    @GetMapping("/sign-url/{contractId}")
    public AjaxResult getSignUrl(@PathVariable Long contractId, @RequestParam Long tenantId) {
        HzContract contract = contractMapper.selectById(contractId);
        if (contract == null) return error("合同不存在");
        if ("2".equals(contract.getContractStatus())) return error("合同已签署");
        HzTenant tenant = tenantMapper.selectById(tenantId);
        if (tenant == null) return error("租户不存在");
        String psnId = tenant.getEsignPsnId();
        if (psnId == null || psnId.isEmpty()) return error("请先完成实名认证");
        try {
            String signFlowId = contract.getEsignFlowId();
            if (signFlowId == null || signFlowId.isEmpty()) {
                byte[] pdfBytes = generateContractPdf(contract);
                String fileId = esignService.uploadContractPdf(pdfBytes, "contract_" + contractId + ".pdf");
                signFlowId = esignService.createSignFlow(contractId, psnId, fileId);
            }
            String signUrl = esignService.getSignUrl(signFlowId, tenant.getPhone(), redirectUrl);
            Map<String, String> r = new HashMap<>(); r.put("signUrl", signUrl);
            return success(r);
        } catch (Exception e) {
            logger.error("获取签署 URL 失败 contractId={}", contractId, e);
            return error("获取签署链接失败：" + e.getMessage());
        }
    }

    @PostMapping("/notify")
    public ResponseEntity<Map<String, Object>> esignNotify(HttpServletRequest request, @RequestBody String body) {
        Map<String, Object> resp = new HashMap<>();
        try {
            String timestamp = request.getHeader("X-Tsign-Open-Sign-Timestamp");
            String signature = request.getHeader("X-Tsign-Open-Sign");
            String requestQuery = request.getQueryString() != null ? request.getQueryString() : "";
            esignService.handleSignCallback(timestamp, requestQuery, body, signature);
            resp.put("code", "0"); return ResponseEntity.ok(resp);
        } catch (Exception e) {
            logger.error("e签宝回调处理失败", e);
            resp.put("code", "-1"); resp.put("message", e.getMessage());
            return ResponseEntity.status(500).body(resp);
        }
    }

    private byte[] generateContractPdf(HzContract contract) {
        String content = "%PDF-1.4\n1 0 obj<</Type/Catalog/Pages 2 0 R>>endobj\n"
                + "2 0 obj<</Type/Pages/Kids[3 0 R]/Count 1>>endobj\n"
                + "3 0 obj<</Type/Page/Parent 2 0 R/MediaBox[0 0 612 792]/Contents 4 0 R>>endobj\n"
                + "4 0 obj<</Length 50>>stream\nBT /F1 12 Tf 72 720 Td (Contract No." + contract.getContractId() + ") Tj ET\n"
                + "endstream endobj\nxref\n0 5\n0000000000 65535 f\n%%EOF";
        return content.getBytes(java.nio.charset.StandardCharsets.ISO_8859_1);
    }
}
