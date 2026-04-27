package com.ghz.gov.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.ghz.gov.sdk.SdkMarriageApp;
import com.iflytek.fsp.shield.java.sdk.model.ApiResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 婚姻信息查询服务
 */
@Service
public class MarriageService {

    private static final Logger log = LoggerFactory.getLogger(MarriageService.class);

    @Autowired
    private TokenService tokenService;

    private final SdkMarriageApp app = new SdkMarriageApp();

    /**
     * 查询婚姻信息
     * @param idCard 身份证号
     * @param name   姓名
     * @return 婚姻信息Map，未婚时data中hasRecord=false
     */
    public Map<String, Object> query(String idCard, String name) {
        String token = tokenService.getAccessToken();

        JSONObject reqBody = new JSONObject();
        reqBody.put("sfzh", idCard);
        reqBody.put("xm", name);

        return doQuery(token, reqBody.toJSONString().getBytes(StandardCharsets.UTF_8));
    }

    private Map<String, Object> doQuery(String token, byte[] body) {
        try {
            ApiResponse response = app.query(body, token);

            if (response.getStatusCode() != 200) {
                String errBody = response.getBody() != null
                        ? new String(response.getBody(), StandardCharsets.UTF_8) : "";
                log.error("婚姻接口调用失败, statusCode={}, body={}", response.getStatusCode(), errBody);
                throw new RuntimeException("婚姻接口调用失败: " + errBody);
            }

            String respJson = new String(response.getBody(), StandardCharsets.UTF_8);
            log.debug("婚姻接口响应: {}", respJson);

            JSONObject json = JSON.parseObject(respJson);
            // 返回原始数据，由调用方解析
            Map<String, Object> result = new LinkedHashMap<>();
            result.put("hasRecord", json.containsKey("女方姓名") || json.containsKey("男方姓名"));
            result.put("raw", json);
            return result;

        } catch (Exception e) {
            log.error("婚姻查询异常 idCard={} name={}", idCardBody() != null ? "***" : "", nameBody() != null ? "***" : "", e);
            throw new RuntimeException("婚姻查询失败: " + e.getMessage(), e);
        }
    }

    // 仅用于日志脱敏
    private String idCardBody() { return ""; }
    private String nameBody() { return ""; }
}
