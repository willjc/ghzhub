package com.ruoyi.web.controller.h5;

import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.config.RuoYiConfig;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.common.utils.file.FileUploadUtils;
import com.ruoyi.system.domain.HzUser;
import com.ruoyi.system.service.IHzUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import jakarta.servlet.http.HttpServletRequest;

/**
 * 用户端 - 用户信息Controller
 *
 * @author ruoyi
 */
@RestController
@RequestMapping("/h5/user")
public class HzUserAppController extends BaseController {

    @Autowired
    private IHzUserService hzUserService;

    @Autowired
    private HttpServletRequest request;

    /**
     * 获取当前登录用户信息
     */
    @GetMapping("/info")
    public AjaxResult getInfo() {
        // TODO: 从token或session中获取当前用户ID
        // 临时方案：从请求参数获取
        Long userId = getCurrentUserId();
        if (userId == null) {
            return error("用户未登录");
        }

        HzUser user = hzUserService.selectHzUserById(userId);
        if (user == null) {
            return error("用户不存在");
        }

        return success(user);
    }

    /**
     * 上传工作证明附件
     */
    @Log(title = "上传工作证明", businessType = BusinessType.UPDATE)
    @PostMapping("/uploadWorkProof")
    public AjaxResult uploadWorkProof(@RequestParam("file") MultipartFile file) {
        try {
            // TODO: 从token或session中获取当前用户ID
            Long userId = getCurrentUserId();
            if (userId == null) {
                return error("用户未登录");
            }

            HzUser user = hzUserService.selectHzUserById(userId);
            if (user == null) {
                return error("用户不存在");
            }

            // 上传文件
            String filePath = RuoYiConfig.getUploadPath();
            String fileName = FileUploadUtils.upload(filePath, file);

            // 更新用户的工作证明附件字段
            user.setWorkProofAttachment(fileName);
            hzUserService.updateById(user);

            AjaxResult ajax = AjaxResult.success();
            ajax.put("fileName", fileName);
            ajax.put("message", "工作证明上传成功");
            return ajax;
        } catch (Exception e) {
            return error("上传失败：" + e.getMessage());
        }
    }

    /**
     * 更新用户基本信息
     */
    @Log(title = "更新用户信息", businessType = BusinessType.UPDATE)
    @PutMapping("/update")
    public AjaxResult update(@RequestBody HzUser hzUser) {
        // TODO: 从token或session中获取当前用户ID
        Long userId = getCurrentUserId();
        if (userId == null) {
            return error("用户未登录");
        }

        // 确保只能修改自己的信息
        hzUser.setUserId(userId);

        return toAjax(hzUserService.updateById(hzUser));
    }

    /**
     * 上传用户头像
     */
    @Log(title = "上传头像", businessType = BusinessType.UPDATE)
    @PostMapping("/uploadAvatar")
    public AjaxResult uploadAvatar(@RequestParam("file") MultipartFile file) {
        try {
            Long userId = getCurrentUserId();
            if (userId == null) {
                return error("用户未登录");
            }

            HzUser user = hzUserService.selectHzUserById(userId);
            if (user == null) {
                return error("用户不存在");
            }

            // 上传文件
            String filePath = RuoYiConfig.getUploadPath();
            String fileName = FileUploadUtils.upload(filePath, file);

            // 更新用户头像
            user.setAvatar(fileName);
            hzUserService.updateById(user);

            AjaxResult ajax = AjaxResult.success();
            ajax.put("fileName", fileName);
            ajax.put("url", fileName);
            return ajax;
        } catch (Exception e) {
            return error("上传失败：" + e.getMessage());
        }
    }

    /**
     * 临时方法：获取当前用户ID
     * TODO: 实现JWT认证后，从token中获取
     */
    protected Long getCurrentUserId() {
        // 临时方案：从请求头或参数获取
        String userIdStr = request.getParameter("userId");
        if (StringUtils.isNotEmpty(userIdStr)) {
            try {
                return Long.parseLong(userIdStr);
            } catch (NumberFormatException e) {
                return null;
            }
        }
        return null;
    }
}
