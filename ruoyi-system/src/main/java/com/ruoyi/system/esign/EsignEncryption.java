package com.ruoyi.system.esign;

import org.apache.commons.codec.binary.Base64;
import org.apache.http.message.BasicNameValuePair;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.Collator;
import java.text.MessageFormat;
import java.util.*;

/**
 * 请求数据通用处理类
 */
public class EsignEncryption {

    private EsignEncryption() {}

    public static String appendSignDataString(String httpMethod, String contentMd5, String accept,
            String contentType, String headers, String date, String url) throws EsignDemoException {
        StringBuffer sb = new StringBuffer();
        sb.append(httpMethod).append("\n").append(accept).append("\n").append(contentMd5).append("\n")
                .append(contentType).append("\n");
        if ("".equals(date) || date == null) {
            sb.append("\n");
        } else {
            sb.append(date).append("\n");
        }
        if ("".equals(headers) || headers == null) {
            sb.append(url);
        } else {
            sb.append(headers).append("\n").append(url);
        }
        return new String(sb);
    }

    public static String doContentMD5(String str) throws EsignDemoException {
        byte[] md5Bytes = null;
        MessageDigest md5 = null;
        String contentMD5 = null;
        try {
            md5 = MessageDigest.getInstance("MD5");
            md5.update(str.getBytes("UTF-8"));
            md5Bytes = md5.digest();
            contentMD5 = Base64.encodeBase64String(md5Bytes);
        } catch (NoSuchAlgorithmException e) {
            EsignDemoException ex = new EsignDemoException("不支持此算法", e);
            ex.initCause(e);
            throw ex;
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return contentMD5;
    }

    public static String doSignatureBase64(String message, String secret) throws EsignDemoException {
        String algorithm = "HmacSHA256";
        Mac hmacSha256;
        String digestBase64 = null;
        try {
            hmacSha256 = Mac.getInstance(algorithm);
            byte[] keyBytes = secret.getBytes("UTF-8");
            byte[] messageBytes = message.getBytes("UTF-8");
            hmacSha256.init(new SecretKeySpec(keyBytes, 0, keyBytes.length, algorithm));
            byte[] digestBytes = hmacSha256.doFinal(messageBytes);
            digestBase64 = Base64.encodeBase64String(digestBytes);
        } catch (NoSuchAlgorithmException e) {
            EsignDemoException ex = new EsignDemoException("不支持此算法", e);
            ex.initCause(e);
            throw ex;
        } catch (InvalidKeyException e) {
            EsignDemoException ex = new EsignDemoException("无效的密钥规范", e);
            ex.initCause(e);
            throw ex;
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return digestBase64;
    }

    public static String timeStamp() {
        long timeStamp = System.currentTimeMillis();
        return String.valueOf(timeStamp);
    }

    public static String byteArrayToHexString(byte[] b) {
        StringBuilder hs = new StringBuilder();
        String stmp;
        for (int n = 0; b != null && n < b.length; n++) {
            stmp = Integer.toHexString(b[n] & 0XFF);
            if (stmp.length() == 1) hs.append('0');
            hs.append(stmp);
        }
        return hs.toString().toLowerCase();
    }

    public static String Hmac_SHA256(String message, String key) throws EsignDemoException {
        byte[] rawHmac = null;
        try {
            SecretKeySpec sk = new SecretKeySpec(key.getBytes(), "HmacSHA256");
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(sk);
            rawHmac = mac.doFinal(message.getBytes());
        } catch (InvalidKeyException e) {
            EsignDemoException ex = new EsignDemoException("无效的密钥规范", e);
            ex.initCause(e);
            throw ex;
        } catch (NoSuchAlgorithmException e) {
            EsignDemoException ex = new EsignDemoException("不支持此算法", e);
            ex.initCause(e);
            throw ex;
        } catch (Exception e) {
            EsignDemoException ex = new EsignDemoException("hash散列加密算法报错", e);
            ex.initCause(e);
            throw ex;
        } finally {
            return byteArrayToHexString(rawHmac);
        }
    }

    public static String MD5Digest(String text) throws EsignDemoException {
        byte[] digest = null;
        try {
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            md5.update(text.getBytes());
            digest = md5.digest();
        } catch (NoSuchAlgorithmException e) {
            EsignDemoException ex = new EsignDemoException("不支持此算法", e);
            ex.initCause(e);
            throw ex;
        } finally {
            return byteArrayToHexString(digest);
        }
    }

    public static void formDataSort(List<BasicNameValuePair> param) {
        Collections.sort(param, new Comparator<BasicNameValuePair>() {
            @Override
            public int compare(BasicNameValuePair o1, BasicNameValuePair o2) {
                Comparator<Object> com = Collator.getInstance(Locale.CHINA);
                return com.compare(o1.getName(), o2.getName());
            }
        });
    }

    public static boolean isBlank(String str) {
        if (null == str || 0 == str.length()) return true;
        int strLen = str.length();
        for (int i = 0; i < strLen; i++) {
            if (!Character.isWhitespace(str.charAt(i))) return false;
        }
        return true;
    }

    public static String sortApiUrl(String apiUrl) throws EsignDemoException {
        if (!apiUrl.contains("?")) return apiUrl;
        int queryIndex = apiUrl.indexOf("?");
        String apiUrlPath = apiUrl.substring(0, queryIndex + 1);
        String apiUrlQuery = apiUrl.substring(queryIndex + 1);
        if (isBlank(apiUrlQuery)) {
            return apiUrl.substring(0, apiUrl.length() - 1);
        }
        Map<Object, Object> queryParamsMap = new HashMap<>();
        String[] params = apiUrlQuery.split("&");
        for (String str : params) {
            int index = str.indexOf("=");
            String key = str.substring(0, index);
            String value = str.substring(index + 1);
            if (queryParamsMap.containsKey(key)) {
                String msg = MessageFormat.format("请求URL中的Query参数的{0}重复", key);
                throw new EsignDemoException(msg);
            }
            queryParamsMap.put(key, value);
        }
        ArrayList<String> queryMapKeys = new ArrayList<>();
        for (Map.Entry<Object, Object> entry : queryParamsMap.entrySet()) {
            queryMapKeys.add((String) entry.getKey());
        }
        Collections.sort(queryMapKeys, new Comparator<String>() {
            @Override
            public int compare(String o1, String o2) {
                return (o1.compareToIgnoreCase(o2) == 0 ? -o1.compareTo(o2) : o1.compareToIgnoreCase(o2));
            }
        });
        StringBuffer queryString = new StringBuffer();
        for (int i = 0; i < queryMapKeys.size(); i++) {
            String key = queryMapKeys.get(i);
            String value = (String) queryParamsMap.get(key);
            queryString.append(key).append("=").append(value).append("&");
        }
        if (queryString.length() > 0) {
            queryString = queryString.deleteCharAt(queryString.length() - 1);
        }
        StringBuffer sortApiUrl = new StringBuffer();
        sortApiUrl.append(apiUrlPath).append(queryString.toString());
        return sortApiUrl.toString();
    }

    public static boolean callBackCheck(String timestamp, String requestQuery, String body,
                                        String key, String signature) {
        String algorithm = "HmacSHA256";
        String encoding = "UTF-8";
        Mac mac = null;
        try {
            String data = timestamp + requestQuery + body;
            mac = Mac.getInstance(algorithm);
            SecretKeySpec secretKey = new SecretKeySpec(key.getBytes(encoding), algorithm);
            mac.init(secretKey);
            mac.update(data.getBytes(encoding));
        } catch (NoSuchAlgorithmException | InvalidKeyException | UnsupportedEncodingException e) {
            e.printStackTrace();
            return false;
        }
        return byte2hex(mac.doFinal()).equalsIgnoreCase(signature);
    }

    public static String byte2hex(byte[] data) {
        StringBuilder hash = new StringBuilder();
        String stmp;
        for (int n = 0; data != null && n < data.length; n++) {
            stmp = Integer.toHexString(data[n] & 0XFF);
            if (stmp.length() == 1) hash.append('0');
            hash.append(stmp);
        }
        return hash.toString();
    }
}
