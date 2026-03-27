package com.ruoyi.system.esign;

/**
 * 网络请求的response类
 */
public class EsignHttpResponse {
    private int status;
    private String body;

    public int getStatus() { return status; }
    public void setStatus(int status) { this.status = status; }
    public String getBody() { return body; }
    public void setBody(String body) { this.body = body; }
}
