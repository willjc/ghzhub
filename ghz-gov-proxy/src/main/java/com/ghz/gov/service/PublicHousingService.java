package com.ghz.gov.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.ghz.gov.sdk.SdkHousingApp;
import com.iflytek.fsp.shield.java.sdk.model.ApiResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 公租房申请人信息查询服务
 */
@Service
public class PublicHousingService {

    private static final Logger log = LoggerFactory.getLogger(PublicHousingService.class);

    @Autowired
    private TokenService tokenService;

    private final SdkHousingApp app = new SdkHousingApp();

    /**
     * 查询公租房信息
     * @param idCard 身份证号
     * @param name   姓名
     */
    public Map<String, Object> query(String idCard, String name) {
        String token = tokenService.getAccessToken();

        JSONObject reqBody = new JSONObject();
        reqBody.put("card_num", idCard);
        reqBody.put("name", name);

        return doQuery(token, reqBody.toJSONString().getBytes(StandardCharsets.UTF_8));
    }

    private Map<String, Object> doQuery(String token, byte[] body) {
        try {
            ApiResponse response = app.query(body, token);

            if (response.getStatusCode() != 200) {
                String errBody = response.getBody() != null
                        ? new String(response.getBody(), StandardCharsets.UTF_8) : "";
                log.error("公租房接口调用失败, statusCode={}, body={}", response.getStatusCode(), errBody);
                throw new RuntimeException("公租房接口调用失败: " + errBody);
            }

            String respJson = new String(response.getBody(), StandardCharsets.UTF_8);
            log.debug("公租房接口响应: {}", respJson);

            JSONObject json = JSON.parseObject(respJson);
            Map<String, Object> result = new LinkedHashMap<>();
            // 接口返回字段：address(配租地址)、create_time(申请时间)、ssq(所属区)
            result.put("hasRecord", json.containsKey("address") || json.containsKey("ssq"));
            result.put("raw", json);
            return result;

        } catch (Exception e) {
            log.error("公租房查询异常", e);
            throw new RuntimeException("公租房查询失败: " + e.getMessage(), e);
        }
    }
}
