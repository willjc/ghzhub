package com.ruoyi.system.service;

public interface EsignService {

    // ==================== 实名认证 ====================

    /** 获取个人实名认证页面链接 */
    String getPsnAuthUrl(Long tenantId, String mobile, String redirectUrl);

    /** 查询个人认证状态并保存 psnId */
    String queryAndSavePsnId(Long tenantId, String mobile);

    // ==================== 模板模式（新） ====================

    /** 查询模板详情（含 components 列表） */
    String queryTemplateDetail() throws Exception;

    /** 用模板+合同数据生成文件，返回 fileId */
    String createFileByTemplate(Long contractId) throws Exception;

    /** 一体化：模板填充→等待文件就绪→创建签署流→返回签署URL */
    String initSign(Long contractId, String psnId) throws Exception;

    // ==================== 签署流程 ====================

    /** 获取签署链接 */
    String getSignUrl(String signFlowId, String mobile, String redirectUrl) throws Exception;

    /** 获取已签合同PDF下载链接 */
    String getSignedPdfUrl(String flowId) throws Exception;

    // ==================== 回调 ====================

    /** 处理 e签宝 签署完成回调 */
    void handleSignCallback(String timestamp, String requestQuery, String body, String signature) throws Exception;
}
