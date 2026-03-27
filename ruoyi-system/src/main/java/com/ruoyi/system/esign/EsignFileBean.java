package com.ruoyi.system.esign;

import org.apache.commons.codec.binary.Base64;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.security.MessageDigest;

public class EsignFileBean {

    private String filePath;
    private byte[] fileBytes;

    public EsignFileBean(String filePath) throws RuntimeException {
        this.filePath = filePath;
        try {
            this.fileBytes = Files.readAllBytes(new File(filePath).toPath());
        } catch (IOException e) {
            throw new RuntimeException("文件读取失败: " + filePath, e);
        }
    }

    /** 直接用字节数组构造（用于从内存生成 PDF 的场景） */
    public EsignFileBean(byte[] fileBytes, String fileName) {
        this.fileBytes = fileBytes;
        this.filePath = fileName;
    }

    public String getFileName() {
        return new File(filePath).getName();
    }

    public long getFileSize() {
        return fileBytes.length;
    }

    public byte[] getFileBytes() {
        return fileBytes;
    }

    /** 文件内容 MD5 → Base64 */
    public String getFileContentMD5() {
        try {
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            byte[] digest = md5.digest(fileBytes);
            return Base64.encodeBase64String(digest);
        } catch (Exception e) {
            throw new RuntimeException("MD5 计算失败", e);
        }
    }
}
