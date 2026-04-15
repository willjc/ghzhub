package com.ruoyi.web.controller.system;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.common.utils.PageUtils;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.system.domain.HzUser;
import com.ruoyi.system.service.IHzUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * 用户信息Controller
 *
 * @author ruoyi
 */
@RestController
@RequestMapping("/gangzhu/user")
public class HzUserController extends BaseController {

    @Autowired
    private IHzUserService hzUserService;

    /**
     * 查询用户列表（真正走数据库分页，避免全表扫描导致前端 10s 超时）
     */
    @PreAuthorize("@ss.hasPermi('gangzhu:user:list')")
    @GetMapping("/list")
    public TableDataInfo list(HzUser hzUser) {
        Page<HzUser> page = PageUtils.getPage();
        IPage<HzUser> pageResult = hzUserService.selectHzUserPage(
                hzUser, (int) page.getCurrent(), (int) page.getSize());

        TableDataInfo rspData = new TableDataInfo();
        rspData.setCode(200);
        rspData.setMsg("查询成功");
        rspData.setRows(pageResult.getRecords());
        rspData.setTotal(pageResult.getTotal());
        return rspData;
    }

    /**
     * 导出用户列表
     */
    @PreAuthorize("@ss.hasPermi('gangzhu:user:export')")
    @Log(title = "用户信息", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, HzUser hzUser) {
        List<HzUser> list = hzUserService.selectHzUserList(hzUser);
        ExcelUtil<HzUser> util = new ExcelUtil<>(HzUser.class);
        util.exportExcel(response, list, "用户数据");
    }

    /**
     * 获取用户详细信息
     */
    @PreAuthorize("@ss.hasPermi('gangzhu:user:query')")
    @GetMapping("/{userId}")
    public AjaxResult getInfo(@PathVariable("userId") Long userId) {
        return success(hzUserService.selectHzUserById(userId));
    }

    /**
     * 修改用户状态
     */
    @PreAuthorize("@ss.hasPermi('gangzhu:user:edit')")
    @Log(title = "用户信息", businessType = BusinessType.UPDATE)
    @PutMapping("/changeStatus")
    public AjaxResult changeStatus(@RequestBody HzUser hzUser) {
        return toAjax(hzUserService.updateHzUserStatus(hzUser));
    }

    /**
     * 删除用户
     */
    @PreAuthorize("@ss.hasPermi('gangzhu:user:remove')")
    @Log(title = "用户信息", businessType = BusinessType.DELETE)
    @DeleteMapping("/{userIds}")
    public AjaxResult remove(@PathVariable Long[] userIds) {
        return toAjax(hzUserService.deleteHzUserByIds(userIds));
    }

}
