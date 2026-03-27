package com.ruoyi.system.esign;

import org.apache.http.client.methods.*;

/**
 * 请求类型
 */
public enum EsignRequestType {

    POST {
        @Override
        public HttpRequestBase getHttpType(String url) { return new HttpPost(url); }
    },
    GET {
        @Override
        public HttpRequestBase getHttpType(String url) { return new HttpGet(url); }
    },
    DELETE {
        @Override
        public HttpRequestBase getHttpType(String url) { return new HttpDelete(url); }
    },
    PUT {
        @Override
        public HttpRequestBase getHttpType(String url) { return new HttpPut(url); }
    };

    public abstract HttpRequestBase getHttpType(String url);
}
