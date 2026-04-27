package com.ghz.gov.controller;

import com.ghz.gov.config.GovProxyConfig;
import com.ghz.gov.dto.ApiResponse;
import com.ghz.gov.service.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * 政务数据接口代理Controller
 * 对外暴露RESTful接口，供业务系统调用
 */
@RestController
@RequestMapping("/api/v1/gov")
public class GovApiController {

    private static final Logger log = LoggerFactory.getLogger(GovApiController.class);

    @Autowired
    private TokenService tokenService;
    @Autowired
    private MarriageService marriageService;
    @Autowired
    private SocialInsuranceService socialInsuranceService;
    @Autowired
    private PublicHousingService publicHousingService;
    @Autowired
    private RealEstateService realEstateService;
    @Autowired
    private GovProxyConfig config;

    // ==================== 婚姻信息查询 ====================
    @PostMapping("/marriage/query")
    public ApiResponse<?> queryMarriage(@RequestBody Map<String, String> params, HttpServletRequest request) {
        if (!checkApiKey(request)) return authError();
        long start = System.currentTimeMillis();
        try {
            String idCard = params.get("idCard");
            String name = params.get("name");
            if (idCard == null || name == null) {
                return ApiResponse.error(400, "缺少必填参数: idCard, name");
            }
            Map<String, Object> result = marriageService.query(idCard, name);
            log.info("婚姻查询完成, 耗时{}ms, hasRecord={}", System.currentTimeMillis() - start,
                    result.get("hasRecord"));
            return ApiResponse.success(result);
        } catch (Exception e) {
            log.error("婚姻查询接口异常, 耗时{}ms", System.currentTimeMillis() - start, e);
            return ApiResponse.error(500, "查询失败: " + e.getMessage());
        }
    }

    // ==================== 社保缴费查询 ====================
    @PostMapping("/social/query")
    public ApiResponse<?> querySocial(@RequestBody Map<String, String> params, HttpServletRequest request) {
        if (!checkApiKey(request)) return authError();
        long start = System.currentTimeMillis();
        try {
            String companyName = params.get("companyName");
            String creditCode = params.get("creditCode");
            if (companyName == null || creditCode == null) {
                return ApiResponse.error(400, "缺少必填参数: companyName, creditCode");
            }
            Map<String, Object> result = socialInsuranceService.query(companyName, creditCode);
            log.info("社保查询完成, 耗时{}ms, hasRecord={}", System.currentTimeMillis() - start,
                    result.get("hasRecord"));
            return ApiResponse.success(result);
        } catch (Exception e) {
            log.error("社保查询接口异常, 耗时{}ms", System.currentTimeMillis() - start, e);
            return ApiResponse.error(500, "查询失败: " + e.getMessage());
        }
    }

    // ==================== 公租房查询 ====================
    @PostMapping("/housing/query")
    public ApiResponse<?> queryHousing(@RequestBody Map<String, String> params, HttpServletRequest request) {
        if (!checkApiKey(request)) return authError();
        long start = System.currentTimeMillis();
        try {
            String idCard = params.get("idCard");
            String name = params.get("name");
            if (idCard == null || name == null) {
                return ApiResponse.error(400, "缺少必填参数: idCard, name");
            }
            Map<String, Object> result = publicHousingService.query(idCard, name);
            log.info("公租房查询完成, 耗时{}ms, hasRecord={}", System.currentTimeMillis() - start,
                    result.get("hasRecord"));
            return ApiResponse.success(result);
        } catch (Exception e) {
            log.error("公租房查询接口异常, 耗时{}ms", System.currentTimeMillis() - start, e);
            return ApiResponse.error(500, "查询失败: " + e.getMessage());
        }
    }

    // ==================== 不动产查询 ====================
    @PostMapping("/estate/query")
    public ApiResponse<?> queryEstate(@RequestBody Map<String, String> params, HttpServletRequest request) {
        if (!checkApiKey(request)) return authError();
        long start = System.currentTimeMillis();
        try {
            String idCard = params.get("idCard");
            String name = params.get("name");
            if (idCard == null || name == null) {
                return ApiResponse.error(400, "缺少必填参数: idCard, name");
            }
            Map<String, Object> result = realEstateService.query(idCard, name);
            log.info("不动产查询完成, 耗时{}ms, hasRecord={}", System.currentTimeMillis() - start,
                    result.get("hasRecord"));
            return ApiResponse.success(result);
        } catch (Exception e) {
            log.error("不动产查询接口异常, 耗时{}ms", System.currentTimeMillis() - start, e);
            return ApiResponse.error(500, "查询失败: " + e.getMessage());
        }
    }

    // ==================== Token状态 ====================
    @GetMapping("/token/status")
    public ApiResponse<?> tokenStatus(HttpServletRequest request) {
        if (!checkApiKey(request)) return authError();
        TokenService.TokenStatus status = tokenService.getStatus();
        return ApiResponse.success(status);
    }

    // ==================== API Key校验 ====================
    private boolean checkApiKey(HttpServletRequest request) {
        String apiKey = request.getHeader("X-Api-Key");
        return config.getApiKey().equals(apiKey);
    }

    private ApiResponse<?> authError() {
        return ApiResponse.error(401, "认证失败：无效的 API Key");
    }
}
