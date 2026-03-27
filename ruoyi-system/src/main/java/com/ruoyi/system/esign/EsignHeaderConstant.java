package com.ruoyi.system.esign;

/**
 * 头部信息常量
 */
public enum EsignHeaderConstant {
    ACCEPT("*/*"),
    DATE(""),
    HEADERS(""),
    CONTENTTYPE_FORMDATA("application/x-www-form-urlencoded"),
    CONTENTTYPE_JSON("application/json; charset=UTF-8"),
    CONTENTTYPE_PDF("application/pdf"),
    CONTENTTYPE_STREAM("application/octet-stream"),
    AUTHMODE("Signature");

    private String value;
    private EsignHeaderConstant(String value) { this.value = value; }
    public String VALUE() { return this.value; }
}
