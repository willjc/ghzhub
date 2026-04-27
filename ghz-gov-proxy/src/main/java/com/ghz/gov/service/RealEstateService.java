package com.ghz.gov.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.ghz.gov.sdk.SdkEstateApp;
import com.iflytek.fsp.shield.java.sdk.model.ApiResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 不动产登记信息查询服务
 */
@Service
public class RealEstateService {

    private static final Logger log = LoggerFactory.getLogger(RealEstateService.class);

    @Autowired
    private TokenService tokenService;

    private final SdkEstateApp app = new SdkEstateApp();

    /**
     * 查询不动产登记信息
     * @param idCard 身份证号
     * @param name   姓名
     */
    public Map<String, Object> query(String idCard, String name) {
        String token = tokenService.getAccessToken();

        JSONObject reqBody = new JSONObject();
        reqBody.put("personCertNo", idCard);
        reqBody.put("personName", name);

        return doQuery(token, reqBody.toJSONString().getBytes(StandardCharsets.UTF_8));
    }

    private Map<String, Object> doQuery(String token, byte[] body) {
        try {
            ApiResponse response = app.query(body, token);

            if (response.getStatusCode() != 200) {
                String errBody = response.getBody() != null
                        ? new String(response.getBody(), StandardCharsets.UTF_8) : "";
                log.error("不动产接口调用失败, statusCode={}, body={}", response.getStatusCode(), errBody);
                throw new RuntimeException("不动产接口调用失败: " + errBody);
            }

            String respJson = new String(response.getBody(), StandardCharsets.UTF_8);
            log.debug("不动产接口响应: {}", respJson);

            JSONObject json = JSON.parseObject(respJson);
            Map<String, Object> result = new LinkedHashMap<>();
            // 接口返回字段：DJSJ、FWZL、SUBSYSTEMID（具体含义调通后确认）
            result.put("hasRecord", json.containsKey("DJSJ") || json.containsKey("FWZL"));
            result.put("raw", json);
            return result;

        } catch (Exception e) {
            log.error("不动产查询异常", e);
            throw new RuntimeException("不动产查询失败: " + e.getMessage(), e);
        }
    }
}
