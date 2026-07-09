package com.ruoyi.web.controller.system;

import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.system.service.IHzPhoneChangeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 更换手机号Controller
 *
 * @author ruoyi
 */
@RestController
@RequestMapping("/system/phoneChange")
public class HzPhoneChangeController extends BaseController {

    @Autowired
    private IHzPhoneChangeService phoneChangeService;

    /**
     * 预览更换手机号
     */
    @PreAuthorize("@ss.hasPermi('tool:phoneChange:list')")
    @GetMapping("/preview")
    public AjaxResult preview(@RequestParam String oldPhone, @RequestParam String newPhone) {
        return success(phoneChangeService.previewChange(oldPhone, newPhone));
    }

    /**
     * 执行更换手机号
     */
    @PreAuthorize("@ss.hasPermi('tool:phoneChange:change')")
    @PostMapping("/execute")
    public AjaxResult execute(@RequestBody Map<String, String> params) {
        String oldPhone = params.get("oldPhone");
        String newPhone = params.get("newPhone");
        if (oldPhone == null || oldPhone.trim().isEmpty() || newPhone == null || newPhone.trim().isEmpty()) {
            return error("原手机号和新手机号不能为空");
        }
        return success(phoneChangeService.executeChange(oldPhone.trim(), newPhone.trim()));
    }
}
