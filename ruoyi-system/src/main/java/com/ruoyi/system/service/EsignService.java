package com.ruoyi.system.service;
public interface EsignService {
    String getPsnAuthUrl(Long tenantId, String mobile, String redirectUrl);
    String queryAndSavePsnId(Long tenantId, String mobile);
    String uploadContractPdf(byte[] pdfBytes, String fileName) throws Exception;
    String createSignFlow(Long contractId, String psnId, String fileId) throws Exception;
    String getSignUrl(String signFlowId, String mobile, String redirectUrl) throws Exception;
    void handleSignCallback(String timestamp, String requestQuery, String body, String signature) throws Exception;

    // ========== 流程模板模式 ==========

    /** 查询模板详情（含 components 列表） */
    String queryTemplateDetail() throws Exception;

    /** 用模板+合同数据生成文件，返回 fileId */
    String createFileByTemplate(Long contractId) throws Exception;

    /** 一体化：模板填充→等待文件就绪→创建签署流→返回签署URL */
    String initSign(Long contractId, String psnId) throws Exception;
}
