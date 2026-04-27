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
     * 查询单位社保缴费信息
     * @param companyName 单位名称
     * @param creditCode  统一社会信用代码
     */
    public Map<String, Object> query(String companyName, String creditCode) {
        String token = tokenService.getAccessToken();

        JSONObject reqBody = new JSONObject();
        reqBody.put("AAB004", companyName);       // 单位名称
        reqBody.put("AAB039", creditCode);         // 统一社会信用代码
        reqBody.put("STARTDATE", "");              // 查询开始时间（选填）
        reqBody.put("ENDDATE", "");                // 查询结束时间（选填）

        return doQuery(token, reqBody.toJSONString().getBytes(StandardCharsets.UTF_8), companyName);
    }

    private Map<String, Object> doQuery(String token, byte[] body, String companyName) {
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

            // 解析返回的缴费记录列表
            JSONObject json = JSON.parseObject(respJson);
            Map<String, Object> result = new LinkedHashMap<>();

            // 接口返回字段：AAB004(单位名称)、AAB039(信用代码)、AAB083(缴费人数)、
            // AAE041(缴费年月)、AAE140(险种类型)
            if (json.containsKey("data") && json.get("data") instanceof List) {
                List<?> records = json.getObject("data", List.class);
                result.put("hasRecord", !records.isEmpty());
                result.put("records", records);
            } else {
                // 单条返回或空
                result.put("hasRecord", json.containsKey("AAB004") || json.containsKey("AAB083"));
            }
            result.put("raw", json);
            return result;

        } catch (Exception e) {
            log.error("社保查询异常 company={}", companyName, e);
            throw new RuntimeException("社保查询失败: " + e.getMessage(), e);
        }
    }
}
