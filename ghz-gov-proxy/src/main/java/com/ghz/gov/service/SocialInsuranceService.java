package com.ghz.gov.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.ghz.gov.sdk.SdkSocialApp;
import com.iflytek.fsp.shield.java.sdk.model.ApiResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 社保缴费信息查询服务
 */
@Service
public class SocialInsuranceService {

    private static final Logger log = LoggerFactory.getLogger(SocialInsuranceService.class);

    @Autowired
    private TokenService tokenService;

    private final SdkSocialApp app = new SdkSocialApp();

    /**
     * 查询个人参保人员社会保险缴费信息
     * 接口：省人社参保人员社会保险缴费信息v7
     * @param idCard 身份证号
     * @param name   姓名
     */
    public Map<String, Object> query(String idCard, String name) {
        String token = tokenService.getAccessToken();

        JSONObject reqBody = new JSONObject();
        // 根据《航空港区数据交换平台接口服务规范》：AAC002=身份证号、AAC003=姓名
        // STARTDATE/ENDDATE 文档写「选填」但后端实际必填，默认查近 12 个月，格式 YYYYMM
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyyMM");
        LocalDate today = LocalDate.now();
        reqBody.put("AAC002", idCard);
        reqBody.put("AAC003", name);
        reqBody.put("STARTDATE", today.minusMonths(12).format(fmt));
        reqBody.put("ENDDATE", today.format(fmt));

        return doQuery(token, reqBody.toJSONString().getBytes(StandardCharsets.UTF_8), idCard);
    }

    private static final int MAX_RETRY = 3;

    private Map<String, Object> doQuery(String token, byte[] body, String tag) {
        RuntimeException lastError = null;
        for (int attempt = 1; attempt <= MAX_RETRY; attempt++) {
            try {
                return doQueryOnce(token, body, tag);
            } catch (RuntimeException e) {
                lastError = e;
                if (isRetryable(e) && attempt < MAX_RETRY) {
                    log.warn("社保接口SHD-1004超时，第{}次失败，即将重试", attempt);
                    continue;
                }
                throw e;
            }
        }
        throw lastError;
    }

    private Map<String, Object> doQueryOnce(String token, byte[] body, String tag) {
        try {
            ApiResponse response = app.query(body, token);

            if (response.getStatusCode() != 200) {
                String errBody = response.getBody() != null
                        ? new String(response.getBody(), StandardCharsets.UTF_8) : "";
                log.error("社保接口调用失败, statusCode={}, body={}", response.getStatusCode(), errBody);
                throw new RuntimeException("社保接口调用失败: " + errBody);
            }

            String respJson = new String(response.getBody(), StandardCharsets.UTF_8);
            log.debug("社保接口响应: {}", respJson);

            JSONObject json = JSON.parseObject(respJson);
            Map<String, Object> result = new LinkedHashMap<>();

            List<?> records = json.get("data") instanceof List ? json.getObject("data", List.class) : null;
            boolean hasRecord = (records != null && !records.isEmpty()) || json.getIntValue("total") > 0;
            result.put("hasRecord", hasRecord);
            if (records != null) {
                result.put("records", records);
            }
            result.put("raw", json);
            return result;

        } catch (Exception e) {
            log.error("社保查询异常 tag={}", tag, e);
            throw new RuntimeException("社保查询失败: " + e.getMessage(), e);
        }
    }

    private static boolean isRetryable(Throwable e) {
        String msg = e.getMessage();
        return msg != null && (msg.contains("SHD-1004") || msg.contains("网关连接后端服务超时"));
    }
}
