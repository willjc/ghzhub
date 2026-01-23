package org.springframework.util;

import java.nio.charset.StandardCharsets;

/**
 * Base64工具类
 * 兼容Spring Framework 5.x中的org.springframework.util.Base64Utils
 * 用于支持数郑网关SDK
 *
 * @author ruoyi
 */
public class Base64Utils {

    /**
     * Base64编码
     */
    public static byte[] encode(byte[] src) {
        return java.util.Base64.getEncoder().encode(src);
    }

    /**
     * Base64编码字符串
     */
    public static String encodeToString(byte[] src) {
        return java.util.Base64.getEncoder().encodeToString(src);
    }

    /**
     * Base64解码
     */
    public static byte[] decode(byte[] src) {
        return java.util.Base64.getDecoder().decode(src);
    }

    /**
     * Base64解码字符串
     */
    public static byte[] decodeFromString(String src) {
        return java.util.Base64.getDecoder().decode(src);
    }

    /**
     * Base64编码URL安全的字符串
     */
    public static String encodeUrlSafe(byte[] src) {
        return java.util.Base64.getUrlEncoder().encodeToString(src);
    }

    /**
     * Base64解码URL安全的字符串
     */
    public static byte[] decodeUrlSafe(String src) {
        return java.util.Base64.getUrlDecoder().decode(src);
    }

    /**
     * 字符串Base64编码
     */
    public static String encode(String src) {
        return encodeToString(src.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * 字符串Base64解码
     */
    public static String decodeToString(String src) {
        return new String(decodeFromString(src), StandardCharsets.UTF_8);
    }
}
