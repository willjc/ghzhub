package com.ruoyi.system.service;

import java.util.Map;

/**
 * 更换手机号Service接口
 *
 * @author ruoyi
 */
public interface IHzPhoneChangeService {

    /**
     * 预览更换手机号操作：查询两个账号信息，判断是否可执行
     */
    Map<String, Object> previewChange(String oldPhone, String newPhone);

    /**
     * 执行更换手机号：合并账号或直接更新手机号
     */
    Map<String, Object> executeChange(String oldPhone, String newPhone);
}
