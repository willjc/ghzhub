package com.ruoyi.system.gov.client;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.ruoyi.system.gov.config.GovDataConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 政务数据代理服务HTTP调用客户端
 * <p>
 * 使用方式：在业务代码中直接注入 GovDataClient，调用对应方法
 * <pre>
 *   @Autowired
 *   private GovDataClient govDataClient;
 *
 *   Map&lt;String, Object&gt; result = govDataClient.queryMarriage("身份证号", "姓名");
 *   boolean hasRecord = (boolean) result.get("hasRecord");
 * </pre>
 */
@Component
public class GovDataClient {

    private static final Logger log = LoggerFactory.getLogger(GovDataClient.class);

    @Autowired
    private GovDataConfig config;

    private final RestTemplate restTemplate = new RestTemplate();

    {
        // 不抛4xx/5xx异常，由业务代码自行判断
        restTemplate.setErrorHandler(new org.springframework.web.client.DefaultResponseErrorHandler() {
            @Override
            public boolean hasError(ClientHttpResponse response) {
                return false;
            }
        });
    }

    // ==================== 婚姻查询 ====================

    /**
     * 查询婚姻信息
     * @param idCard 身份证号
     * @param name   姓名
     * @return 包含 hasRecord 和 raw 的Map
     */
    public Map<String, Object> queryMarriage(String idCard, String name) {
        Map<String, String> params = new LinkedHashMap<>();
        params.put("idCard", idCard);
        params.put("name", name);
        return doPost("/api/v1/gov/marriage/query", params);
    }

    // ==================== 社保查询 ====================

    /**
     * 查询单位社保缴费信息
     * @param companyName 单位名称
     * @param creditCode  统一社会信用代码
     */
    public Map<String, Object> querySocialInsurance(String companyName, String creditCode) {
        Map<String, String> params = new LinkedHashMap<>();
        params.put("companyName", companyName);
        params.put("creditCode", creditCode);
        return doPost("/api/v1/gov/social/query", params);
    }

    // ==================== 公租房查询 ====================

    /**
     * 查询公租房信息
     * @param idCard 身份证号
     * @param name   姓名
     */
    public Map<String, Object> queryPublicHousing(String idCard, String name) {
        Map<String, String> params = new LinkedHashMap<>();
        params.put("idCard", idCard);
        params.put("name", name);
        return doPost("/api/v1/gov/housing/query", params);
    }

    // ==================== 不动产查询 ====================

    /**
     * 查询不动产登记信息
     * @param idCard 身份证号
     * @param name   姓名
     */
    public Map<String, Object> queryRealEstate(String idCard, String name) {
        Map<String, String> params = new LinkedHashMap<>();
        params.put("idCard", idCard);
        params.put("name", name);
        return doPost("/api/v1/gov/estate/query", params);
    }

    // ==================== Token状态 ====================

    public Map<String, Object> tokenStatus() {
        return doGet("/api/v1/gov/token/status");
    }

    // ==================== 内部HTTP调用 ====================

    private Map<String, Object> doPost(String path, Map<String, String> body) {
        long start = System.currentTimeMillis();
        try {
            String url = config.getBaseUrl() + path;
            HttpHeaders headers = new HttpHeaders();
            headers.set("X-Api-Key", config.getApiKey());
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<Map<String, String>> request = new HttpEntity<>(body, headers);
            ResponseEntity<String> response = restTemplate.postForEntity(url, request, String.class);

            long cost = System.currentTimeMillis() - start;
            log.info("GovDataClient POST {} -> status={}, 耗时{}ms", path, response.getStatusCodeValue(), cost);

            if (response.getBody() == null) {
                Map<String, Object> err = new LinkedHashMap<>();
                err.put("success", false);
                err.put("error", "代理服务返回空响应");
                return err;
            }

            // 解析代理服务的统一响应格式 {"code":200, "data":{...}}
            JSONObject respJson = JSON.parseObject(response.getBody());
            int code = respJson.getIntValue("code");
            if (code != 200) {
                Map<String, Object> err = new LinkedHashMap<>();
                err.put("success", false);
                err.put("error", respJson.getString("message"));
                return err;
            }

            JSONObject data = respJson.getJSONObject("data");
            Map<String, Object> result = new LinkedHashMap<>();
            result.put("success", true);
            if (data != null) {
                result.putAll(data);
            }
            return result;

        } catch (Exception e) {
            long cost = System.currentTimeMillis() - start;
            log.error("GovDataClient POST {} 异常, 耗时{}ms", path, cost, e);
            Map<String, Object> err = new LinkedHashMap<>();
            err.put("success", false);
            err.put("error", "调用代理服务异常: " + e.getMessage());
            return err;
        }
    }

    private Map<String, Object> doGet(String path) {
        try {
            String url = config.getBaseUrl() + path;
            HttpHeaders headers = new HttpHeaders();
            headers.set("X-Api-Key", config.getApiKey());

            HttpEntity<Void> request = new HttpEntity<>(headers);
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, request, String.class);

            if (response.getBody() == null) {
                Map<String, Object> err = new LinkedHashMap<>();
                err.put("success", false);
                return err;
            }
            JSONObject respJson = JSON.parseObject(response.getBody());
            Map<String, Object> result = new LinkedHashMap<>();
            result.put("success", true);
            if (respJson.getJSONObject("data") != null) {
                result.putAll(respJson.getJSONObject("data"));
            }
            return result;

        } catch (Exception e) {
            log.error("GovDataClient GET {} 异常", path, e);
            Map<String, Object> err = new LinkedHashMap<>();
            err.put("success", false);
            return err;
        }
    }
}
