package com.ruoyi.web.controller.system;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.constant.HttpStatus;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.PageDomain;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.core.page.TableSupport;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.system.domain.HzBlacklist;
import com.ruoyi.system.domain.HzUser;
import com.ruoyi.system.mapper.HzUserMapper;
import com.ruoyi.system.service.IHzBlacklistService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 黑名单管理Controller（管理端）
 *
 * @author ruoyi
 */
@RestController("adminBlacklistController")
@RequestMapping("/gangzhu/blacklist")
public class HzBlacklistController extends BaseController {

    @Autowired
    private IHzBlacklistService blacklistService;

    @Autowired
    private HzUserMapper userMapper;

    /**
     * 查询黑名单列表
     */
    @PreAuthorize("@ss.hasPermi('gangzhu:blacklist:list')")
    @GetMapping("/list")
    public TableDataInfo list(HzBlacklist query) {
        PageDomain pageDomain = TableSupport.buildPageRequest();
        Integer pageNum = pageDomain.getPageNum();
        Integer pageSize = pageDomain.getPageSize();
        if (pageNum == null || pageNum <= 0) {
            pageNum = 1;
        }
        if (pageSize == null || pageSize <= 0) {
            pageSize = 10;
        }
        IPage<HzBlacklist> page = blacklistService.selectBlacklistPage(query, pageNum, pageSize);
        TableDataInfo rspData = new TableDataInfo();
        rspData.setCode(HttpStatus.SUCCESS);
        rspData.setMsg("查询成功");
        rspData.setRows(page.getRecords());
        rspData.setTotal(page.getTotal());
        return rspData;
    }

    /**
     * 导出黑名单
     */
    @PreAuthorize("@ss.hasPermi('gangzhu:blacklist:export')")
    @Log(title = "黑名单管理", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, HzBlacklist query) {
        List<HzBlacklist> list = blacklistService.selectBlacklistList(query);
        ExcelUtil<HzBlacklist> util = new ExcelUtil<>(HzBlacklist.class);
        util.exportExcel(response, list, "黑名单数据");
    }

    /**
     * 新增黑名单（支持两种方式：传 tenantId 自动带出用户信息；或手工填写姓名/身份证）
     */
    @PreAuthorize("@ss.hasPermi('gangzhu:blacklist:add')")
    @Log(title = "黑名单管理", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody HzBlacklist blacklist) {
        if (StringUtils.isEmpty(blacklist.getReason())) {
            return error("加入原因不能为空");
        }
        // 传了用户ID：从 hz_user 带出姓名和身份证
        if (blacklist.getTenantId() != null) {
            HzUser user = userMapper.selectById(blacklist.getTenantId());
            if (user == null) {
                return error("用户不存在");
            }
            blacklist.setTenantName(user.getRealName() != null && !user.getRealName().isEmpty()
                    ? user.getRealName() : user.getNickname());
            blacklist.setIdCard(user.getIdCard());
            // 已在黑名单中（生效中）则拒绝重复加入
            HzBlacklist exist = blacklistService.selectBlacklistByTenantId(blacklist.getTenantId());
            if (exist != null) {
                return error("该用户已在黑名单中");
            }
        } else if (StringUtils.isEmpty(blacklist.getIdCard())) {
            return error("身份证号不能为空");
        } else {
            // 手工录入：按身份证查是否已在黑名单
            HzBlacklist exist = blacklistService.selectBlacklistByIdCard(blacklist.getIdCard());
            if (exist != null) {
                return error("该身份证号已在黑名单中");
            }
        }
        return toAjax(blacklistService.insertBlacklist(blacklist));
    }

    /**
     * 解除黑名单
     */
    @PreAuthorize("@ss.hasPermi('gangzhu:blacklist:remove')")
    @Log(title = "黑名单管理", businessType = BusinessType.UPDATE)
    @PutMapping("/remove/{blacklistId}")
    public AjaxResult remove(@PathVariable Long blacklistId, @RequestBody(required = false) Map<String, String> params) {
        HzBlacklist exist = blacklistService.selectBlacklistById(blacklistId);
        if (exist == null) {
            return error("黑名单记录不存在");
        }
        if (!"1".equals(exist.getStatus())) {
            return error("该记录已解除，无需重复操作");
        }
        String removeReason = params != null ? params.get("removeReason") : null;
        return toAjax(blacklistService.removeBlacklist(blacklistId, removeReason));
    }

    /**
     * 查询指定用户的黑名单状态（用户管理页用）
     */
    @PreAuthorize("@ss.hasPermi('gangzhu:blacklist:list')")
    @GetMapping("/check/{userId}")
    public AjaxResult check(@PathVariable Long userId) {
        HzBlacklist record = blacklistService.selectBlacklistByTenantId(userId);
        Map<String, Object> data = new HashMap<>();
        data.put("inBlacklist", record != null);
        data.put("reason", record != null ? record.getReason() : null);
        return success(data);
    }
}
