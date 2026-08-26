package com.ruoyi.web.controller.h5;

import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.config.RuoYiConfig;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.common.utils.file.FileUploadUtils;
import com.ruoyi.system.domain.HzUser;
import com.ruoyi.system.service.IHzUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;


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

    /**
     * 获取当前登录用户信息
     */
    @GetMapping("/info")
    public AjaxResult getInfo() {
        Long userId = getCurrentUserId();

        HzUser user = hzUserService.selectHzUserById(userId);
        if (user == null) {
            return error("用户不存在");
        }

        // 性别编码转换：库内存储为 1=男/2=女/0=未知，
        // 而用户端(小程序/H5)使用若依标准编码 0=男/1=女/2=未知，
        // 返回前统一转换为用户端编码，避免前端显示错乱。
        user.setGender(genderStoredToApp(user.getGender()));

        return success(user);
    }

    /**
     * 获取当前用户认证状态
     */
    @GetMapping("/auth-status")
    public AjaxResult getUserAuthStatus() {
        Long userId = getCurrentUserId();
        HzUser user = hzUserService.selectHzUserById(userId);
        if (user == null) {
            return error("用户不存在");
        }
        Map<String, Object> result = new HashMap<>();
        result.put("authStatus", user.getAuthStatus() != null ? user.getAuthStatus() : "0");
        result.put("realName", user.getRealName());
        result.put("idCard", user.getIdCard());
        result.put("esignPsnId", user.getEsignPsnId());
        return success(result);
    }

    /**
     * 上传工作证明附件
     */
    @Log(title = "上传工作证明", businessType = BusinessType.UPDATE)
    @PostMapping("/uploadWorkProof")
    public AjaxResult uploadWorkProof(@RequestParam("file") MultipartFile file) {
        try {
            Long userId = getCurrentUserId();

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
        Long userId = getCurrentUserId();

        HzUser existUser = hzUserService.selectHzUserById(userId);
        if (existUser == null) {
            return error("用户不存在");
        }

        // 用户资料页只允许修改展示字段，认证状态、手机号、openid等敏感字段不能批量覆盖。
        if (hzUser.getNickname() != null) {
            existUser.setNickname(hzUser.getNickname());
        }
        if (hzUser.getRealName() != null) {
            existUser.setRealName(hzUser.getRealName());
        }

        // 性别编码转换：用户端(小程序/H5)传入若依标准编码 0=男/1=女/2=未知，
        // 落库前转换回库内存储编码 1=男/2=女/0=未知。
        // 仅当本次请求携带 gender 时才转换，避免误改其它字段的部分更新。
        if (StringUtils.isNotEmpty(hzUser.getGender())) {
            existUser.setGender(genderAppToStored(hzUser.getGender()));
        }

        return toAjax(hzUserService.updateById(existUser));
    }

    /**
     * 上传用户头像
     */
    @Log(title = "上传头像", businessType = BusinessType.UPDATE)
    @PostMapping("/uploadAvatar")
    public AjaxResult uploadAvatar(@RequestParam("file") MultipartFile file) {
        try {
            Long userId = getCurrentUserId();
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

    protected Long getCurrentUserId() {
        return SecurityUtils.getHzUserId();
    }

    /**
     * 性别编码：库内存储 -> 用户端展示
     * 库内：1=男 2=女 0=未知；用户端：0=男 1=女 2=未知
     */
    private String genderStoredToApp(String stored) {
        if (stored == null) {
            return null;
        }
        switch (stored) {
            case "1":
                return "0"; // 男
            case "2":
                return "1"; // 女
            case "0":
                return "2"; // 未知
            default:
                return stored;
        }
    }

    /**
     * 性别编码：用户端提交 -> 库内存储
     * 用户端：0=男 1=女 2=未知；库内：1=男 2=女 0=未知
     */
    private String genderAppToStored(String app) {
        if (app == null) {
            return null;
        }
        switch (app) {
            case "0":
                return "1"; // 男
            case "1":
                return "2"; // 女
            case "2":
                return "0"; // 未知
            default:
                return app;
        }
    }
}
