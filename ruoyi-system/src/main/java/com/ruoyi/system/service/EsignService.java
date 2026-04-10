package com.ruoyi.system.service;

public interface EsignService {

    // ==================== 实名认证 ====================

    /**
     * 获取个人实名认证页面链接
     * @param realName 真实姓名（优先使用，为空则从 DB 读取）
     * @param idCard   身份证号（优先使用，为空则从 DB 读取）
     */
    String getPsnAuthUrl(Long tenantId, String mobile, String realName, String idCard, String redirectUrl);

    /** 查询个人认证状态并保存 psnId */
    String queryAndSavePsnId(Long tenantId, String mobile);

    // ==================== 模板模式 ====================

    /** 查询模板详情（含 components 列表） */
    String queryTemplateDetail() throws Exception;

    /** 用模板+合同数据生成文件，返回 fileId */
    String createFileByTemplate(Long contractId) throws Exception;

    /**
     * 一体化：模板填充→等待文件就绪→创建签署流→返回签署URL
     * @param redirectUrl 签署完成后的跳转地址（小程序传 wechat://back）
     */
    String initSign(Long contractId, String psnId, String redirectUrl) throws Exception;

    // ==================== 签署流程 ====================

    /** 获取签署链接 */
    String getSignUrl(String signFlowId, String mobile, String redirectUrl) throws Exception;

    /** 获取已签合同PDF下载链接 */
    String getSignedPdfUrl(String flowId) throws Exception;

    // ==================== 回调 ====================

    /** 处理 e签宝 签署完成回调 */
    void handleSignCallback(String timestamp, String requestQuery, String body, String signature) throws Exception;
}
