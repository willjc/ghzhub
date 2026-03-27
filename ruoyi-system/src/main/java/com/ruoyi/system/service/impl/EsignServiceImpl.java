package com.ruoyi.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.ruoyi.system.domain.HzContract;
import com.ruoyi.system.domain.HzHouseOrder;
import com.ruoyi.system.domain.HzTenant;
import com.ruoyi.system.esign.*;
import com.ruoyi.system.mapper.HzContractMapper;
import com.ruoyi.system.mapper.HzHouseOrderMapper;
import com.ruoyi.system.mapper.HzTenantMapper;
import com.ruoyi.system.service.EsignService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.concurrent.TimeUnit;

@Service
public class EsignServiceImpl implements EsignService {

    private static final Logger log = LoggerFactory.getLogger(EsignServiceImpl.class);
    private static final Gson gson = new Gson();

    @Value("${esign.app-id}") private String appId;
    @Value("${esign.app-secret}") private String appSecret;
    @Value("${esign.host}") private String host;
    @Value("${esign.notify-url}") private String notifyUrl;
    @Value("${esign.org-id}") private String orgId;

    private final HzTenantMapper tenantMapper;
    private final HzContractMapper contractMapper;
    private final HzHouseOrderMapper orderMapper;

    public EsignServiceImpl(HzTenantMapper tenantMapper, HzContractMapper contractMapper, HzHouseOrderMapper orderMapper) {
        this.tenantMapper = tenantMapper; this.contractMapper = contractMapper; this.orderMapper = orderMapper;
    }

    @Override
    public String getPsnAuthUrl(Long tenantId, String mobile, String redirectUrl) {
        HzTenant tenant = tenantMapper.selectById(tenantId);
        if (tenant != null && tenant.getEsignPsnId() != null && !tenant.getEsignPsnId().isEmpty()) return null;
        String jsonParm = "{\"psnAuthConfig\":{\"psnAccount\":\"" + mobile + "\",\"psnAuthPageConfig\":{\"psnEditableFields\":[\"name\",\"IDCardNum\",\"mobile\"]}},"
                + "\"clientType\":\"ALL\",\"redirectConfig\":{\"redirectDelayTime\":\"3\",\"redirectUrl\":\"" + redirectUrl + "\"},"
                + "\"authorizeConfig\":{\"authorizedScopes\":[\"get_psn_identity_info\",\"psn_initiate_sign\",\"manage_psn_resource\"]},"
                + "\"repeatableAuth\":true}";
        try {
            EsignHttpResponse resp = callApi("POST", "/v3/psn-auth-url", jsonParm);
            return gson.fromJson(resp.getBody(), JsonObject.class).getAsJsonObject("data").get("authUrl").getAsString();
        } catch (Exception e) { throw new RuntimeException("获取 e签宝 认证链接失败", e); }
    }

    @Override
    public String queryAndSavePsnId(Long tenantId, String mobile) {
        try {
            EsignHttpResponse resp = callApi("GET", "/v3/persons/identity-info?psnAccount=" + mobile, null);
            JsonObject data = gson.fromJson(resp.getBody(), JsonObject.class).getAsJsonObject("data");
            if (!"2".equals(data.get("realnameStatus").getAsString())) return null;
            String psnId = data.get("psnId").getAsString();
            tenantMapper.update(null, new LambdaUpdateWrapper<HzTenant>().eq(HzTenant::getTenantId, tenantId).set(HzTenant::getEsignPsnId, psnId));
            log.info("e签宝个人认证完成，tenantId={}, psnId={}", tenantId, psnId);
            return psnId;
        } catch (Exception e) { throw new RuntimeException("查询 e签宝 认证状态失败", e); }
    }

    @Override
    public String uploadContractPdf(byte[] pdfBytes, String fileName) throws Exception {
        EsignFileBean fileBean = new EsignFileBean(pdfBytes, fileName);
        String getUrlParm = "{\"contentMd5\":\"" + fileBean.getFileContentMD5() + "\",\"fileName\":\"" + fileName
                + "\",\"fileSize\":" + fileBean.getFileSize() + ",\"convertToPDF\":false,\"contentType\":\"application/octet-stream\"}";
        EsignHttpResponse getUrlResp = callApi("POST", "/v3/files/file-upload-url", getUrlParm);
        JsonObject getUrlData = gson.fromJson(getUrlResp.getBody(), JsonObject.class).getAsJsonObject("data");
        String fileId = getUrlData.get("fileId").getAsString();
        String uploadUrl = getUrlData.get("fileUploadUrl").getAsString();
        EsignHttpHelper.doUploadHttp(uploadUrl, EsignRequestType.PUT, fileBean.getFileBytes(), fileBean.getFileContentMD5(), "application/octet-stream", false);
        for (int i = 0; i < 5; i++) {
            TimeUnit.SECONDS.sleep(2);
            EsignHttpResponse sr = callApi("GET", "/v3/files/" + fileId, null);
            String fs = gson.fromJson(sr.getBody(), JsonObject.class).getAsJsonObject("data").get("fileStatus").getAsString();
            if ("2".equalsIgnoreCase(fs) || "5".equalsIgnoreCase(fs)) { log.info("e签宝文件已就绪 fileId={}", fileId); return fileId; }
        }
        throw new RuntimeException("e签宝文件长时间未就绪，fileId=" + fileId);
    }

    @Override
    @Transactional
    public String createSignFlow(Long contractId, String psnId, String fileId) throws Exception {
        HzContract contract = contractMapper.selectById(contractId);
        if (contract == null) throw new RuntimeException("合同不存在: " + contractId);
        String jsonParm = "{\"docs\":[{\"fileId\":\"" + fileId + "\",\"fileName\":\"港好住租赁合同.pdf\"}],"
                + "\"signFlowConfig\":{\"signFlowTitle\":\"港好住租赁合同-" + contractId + "\",\"autoFinish\":true,"
                + "\"notifyUrl\":\"" + notifyUrl + "\",\"chargeConfig\":{\"chargeMode\":0}},"
                + "\"signers\":[{\"signerType\":1,\"orgSignerInfo\":{\"orgId\":\"" + orgId + "\"},"
                + "\"signConfig\":{\"signOrder\":1},\"signFields\":[{\"fileId\":\"" + fileId + "\","
                + "\"normalSignFieldConfig\":{\"autoSign\":true,\"freeMode\":false,"
                + "\"signFieldPosition\":{\"positionPage\":\"1\",\"positionX\":120,\"positionY\":460},"
                + "\"signFieldStyle\":1},\"signFieldType\":0}]},"
                + "{\"signerType\":0,\"psnSignerInfo\":{\"psnId\":\"" + psnId + "\"},"
                + "\"signConfig\":{\"signOrder\":2},\"signFields\":[{\"fileId\":\"" + fileId + "\","
                + "\"normalSignFieldConfig\":{\"autoSign\":false,\"freeMode\":false,"
                + "\"signFieldPosition\":{\"positionPage\":\"1\",\"positionX\":320,\"positionY\":460},"
                + "\"signFieldStyle\":1},\"signFieldType\":0}]}]}";
        EsignHttpResponse resp = callApi("POST", "/v3/sign-flow/create-by-file", jsonParm);
        String signFlowId = gson.fromJson(resp.getBody(), JsonObject.class).getAsJsonObject("data").get("signFlowId").getAsString();
        contractMapper.update(null, new LambdaUpdateWrapper<HzContract>().eq(HzContract::getContractId, contractId)
                .set(HzContract::getEsignFlowId, signFlowId).set(HzContract::getContractStatus, "1"));
        log.info("e签宝签署流程创建成功 contractId={} signFlowId={}", contractId, signFlowId);
        return signFlowId;
    }

    @Override
    public String getSignUrl(String signFlowId, String mobile, String redirectUrl) throws Exception {
        String jsonParm = "{\"clientType\":\"ALL\",\"needLogin\":true,\"operator\":{\"psnAccount\":\"" + mobile + "\"},"
                + "\"redirectUrl\":\"" + redirectUrl + "\",\"urlType\":1}";
        EsignHttpResponse resp = callApi("POST", "/v3/sign-flow/" + signFlowId + "/sign-url", jsonParm);
        return gson.fromJson(resp.getBody(), JsonObject.class).getAsJsonObject("data").get("url").getAsString();
    }

    @Override
    @Transactional
    public void handleSignCallback(String timestamp, String requestQuery, String body, String signature) throws Exception {
        if (!EsignEncryption.callBackCheck(timestamp, requestQuery, body, appSecret, signature))
            throw new RuntimeException("e签宝回调验签失败");
        JsonObject nb = gson.fromJson(body, JsonObject.class);
        String action = nb.get("action").getAsString();
        String flowId = nb.get("flowId").getAsString();
        int signResult = nb.has("signResult") ? nb.get("signResult").getAsInt() : -1;
        log.info("e签宝回调 action={} flowId={} signResult={}", action, flowId, signResult);
        if (!"SIGN_FLOW_UPDATE".equals(action) || signResult != 2) return;
        HzContract contract = contractMapper.selectOne(new LambdaQueryWrapper<HzContract>().eq(HzContract::getEsignFlowId, flowId).last("LIMIT 1"));
        if (contract == null) { log.warn("e签宝回调未找到对应合同 flowId={}", flowId); return; }
        if ("2".equals(contract.getContractStatus())) { log.info("合同已处理，忽略重复回调 flowId={}", flowId); return; }
        contractMapper.update(null, new LambdaUpdateWrapper<HzContract>().eq(HzContract::getContractId, contract.getContractId()).set(HzContract::getContractStatus, "2"));
        HzHouseOrder order = orderMapper.selectOne(new LambdaQueryWrapper<HzHouseOrder>().eq(HzHouseOrder::getContractId, contract.getContractId()).last("LIMIT 1"));
        if (order != null && "0".equals(order.getOrderStatus())) {
            orderMapper.update(null, new LambdaUpdateWrapper<HzHouseOrder>().eq(HzHouseOrder::getOrderNo, order.getOrderNo()).set(HzHouseOrder::getOrderStatus, "1"));
            log.info("预订单推进至待付押金 orderNo={}", order.getOrderNo());
        }
    }

    private EsignHttpResponse callApi(String method, String apiAddr, String jsonParm) throws EsignDemoException {
        Map<String, String> header = EsignHttpHelper.signAndBuildSignAndJsonHeader(appId, appSecret, jsonParm, method, apiAddr, false);
        return EsignHttpHelper.doCommHttp(host, apiAddr, EsignRequestType.valueOf(method.toUpperCase()), jsonParm, header, false);
    }
}
