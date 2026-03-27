package com.ruoyi.system.service;
public interface EsignService {
    String getPsnAuthUrl(Long tenantId, String mobile, String redirectUrl);
    String queryAndSavePsnId(Long tenantId, String mobile);
    String uploadContractPdf(byte[] pdfBytes, String fileName) throws Exception;
    String createSignFlow(Long contractId, String psnId, String fileId) throws Exception;
    String getSignUrl(String signFlowId, String mobile, String redirectUrl) throws Exception;
    void handleSignCallback(String timestamp, String requestQuery, String body, String signature) throws Exception;
}
