package com.ruoyi.system.esign;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * Http请求辅助类
 */
public class EsignHttpHelper {
    private static final Logger LOGGER = LoggerFactory.getLogger(EsignHttpHelper.class);

    private EsignHttpHelper() {}

    public static EsignHttpResponse doCommHttp(String host, String url, EsignRequestType reqType,
            Object paramStr, Map<String, String> httpHeader, boolean debug) throws EsignDemoException {
        return EsignHttpCfgHelper.sendHttp(reqType, host + url, httpHeader, paramStr, debug);
    }

    public static EsignHttpResponse doUploadHttp(String uploadUrl, EsignRequestType reqType,
            byte[] param, String fileContentMd5, String contentType, boolean debug) throws EsignDemoException {
        Map<String, String> uploadHeader = buildUploadHeader(fileContentMd5, contentType);
        if (debug) {
            LOGGER.info("----------------------------start------------------------");
            LOGGER.info("fileContentMd5:{}", fileContentMd5);
            LOGGER.info("contentType:{}", contentType);
        }
        return EsignHttpCfgHelper.sendHttp(reqType, uploadUrl, uploadHeader, param, debug);
    }

    public static Map<String, String> buildSignAndJsonHeader(String projectId, String contentMD5,
            String accept, String contentType, String authMode) {
        Map<String, String> header = new HashMap<>();
        header.put("X-Tsign-Open-App-Id", projectId);
        header.put("X-Tsign-Open-Version-Sdk", EsignCoreSdkInfo.getSdkVersion());
        header.put("X-Tsign-Open-Ca-Timestamp", EsignEncryption.timeStamp());
        header.put("Accept", accept);
        header.put("Content-MD5", contentMD5);
        header.put("Content-Type", contentType);
        header.put("X-Tsign-Open-Auth-Mode", authMode);
        return header;
    }

    public static Map<String, String> signAndBuildSignAndJsonHeader(String projectId, String secret,
            String paramStr, String httpMethod, String url, boolean debug) throws EsignDemoException {
        String contentMD5 = "";
        httpMethod = httpMethod.toUpperCase();
        if ("GET".equals(httpMethod) || "DELETE".equals(httpMethod)) {
            paramStr = null;
            contentMD5 = "";
        } else if ("PUT".equals(httpMethod) || "POST".equals(httpMethod)) {
            contentMD5 = EsignEncryption.doContentMD5(paramStr);
        } else {
            throw new EsignDemoException(String.format("不支持的请求方法%s", httpMethod));
        }
        Map<String, String> esignHeaderMap = buildSignAndJsonHeader(projectId, contentMD5,
                EsignHeaderConstant.ACCEPT.VALUE(), EsignHeaderConstant.CONTENTTYPE_JSON.VALUE(),
                EsignHeaderConstant.AUTHMODE.VALUE());
        url = EsignEncryption.sortApiUrl(url);
        String message = EsignEncryption.appendSignDataString(httpMethod,
                esignHeaderMap.get("Content-MD5"), esignHeaderMap.get("Accept"),
                esignHeaderMap.get("Content-Type"), esignHeaderMap.get("Headers"),
                esignHeaderMap.get("Date"), url);
        String reqSignature = EsignEncryption.doSignatureBase64(message, secret);
        esignHeaderMap.put("X-Tsign-Open-Ca-Signature", reqSignature);
        if (debug) {
            LOGGER.info("----------------------------start------------------------");
            LOGGER.info("待计算body值:{}", paramStr + "\n");
            LOGGER.info("MD5值:{}", contentMD5 + "\n");
            LOGGER.info("待签名字符串:{}", message + "\n");
            LOGGER.info("签名值:{}", reqSignature + "\n");
        }
        return esignHeaderMap;
    }

    public static Map<String, String> buildTokenAndJsonHeader(String appid, String token) {
        Map<String, String> esignHeader = new HashMap<>();
        esignHeader.put("X-Tsign-Open-Version-Sdk", EsignCoreSdkInfo.getSdkVersion());
        esignHeader.put("Content-Type", EsignHeaderConstant.CONTENTTYPE_JSON.VALUE());
        esignHeader.put("X-Tsign-Open-App-Id", appid);
        esignHeader.put("X-Tsign-Open-Token", token);
        return esignHeader;
    }

    public static Map<String, String> buildFormDataHeader(String appid) {
        Map<String, String> esignHeader = new HashMap<>();
        esignHeader.put("X-Tsign-Open-Version-Sdk", EsignCoreSdkInfo.getSdkVersion());
        esignHeader.put("X-Tsign-Open-Authorization-Version", "v2");
        esignHeader.put("Content-Type", EsignHeaderConstant.CONTENTTYPE_FORMDATA.VALUE());
        esignHeader.put("X-Tsign-Open-App-Id", appid);
        return esignHeader;
    }

    public static Map<String, String> buildUploadHeader(String fileContentMd5, String contentType) {
        Map<String, String> header = new HashMap<>();
        header.put("Content-MD5", fileContentMd5);
        header.put("Content-Type", contentType);
        return header;
    }
}
