package com.ruoyi.web.controller.system;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.PageUtils;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.system.domain.HzUser;
import com.ruoyi.system.domain.vo.HzTenantVO;
import com.ruoyi.system.mapper.HzUserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletResponse;

/**
 * 租户管理Controller
 *
 * @author ruoyi
 */
@RestController("adminTenantController")
@RequestMapping("/system/tenant")
public class HzTenantController extends BaseController
{
    private static final int MAX_EXPORT_ROWS = 10000;

    @Autowired
    private HzUserMapper userMapper;

    /**
     * 查询租户列表
     */
    @PreAuthorize("@ss.hasPermi('gangzhu:tenant:list')")
    @GetMapping("/list")
    public TableDataInfo list(HzTenantVO tenant)
    {
        Page<HzTenantVO> page = PageUtils.getPage();
        IPage<HzTenantVO> pageResult = userMapper.selectTenantPage(page, tenant);

        TableDataInfo rspData = new TableDataInfo();
        rspData.setCode(200);
        rspData.setMsg("查询成功");
        rspData.setRows(pageResult.getRecords());
        rspData.setTotal(pageResult.getTotal());
        return rspData;
    }

    /**
     * 导出租户列表
     */
    @PreAuthorize("@ss.hasPermi('gangzhu:tenant:export')")
    @Log(title = "租户管理", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, HzTenantVO tenant)
    {
        IPage<HzTenantVO> page = userMapper.selectTenantPage(new Page<>(1, MAX_EXPORT_ROWS), tenant);
        if (page.getTotal() > MAX_EXPORT_ROWS) {
            throw new ServiceException("导出数据超过10000条，请增加筛选条件后重试");
        }
        ExcelUtil<HzTenantVO> util = new ExcelUtil<>(HzTenantVO.class);
        util.exportExcel(response, page.getRecords(), "租户数据");
    }

    /**
     * 获取租户详细信息
     */
    @PreAuthorize("@ss.hasPermi('gangzhu:tenant:query')")
    @GetMapping(value = "/{userId}")
    public AjaxResult getInfo(@PathVariable("userId") Long userId)
    {
        return success(userMapper.selectTenantByUserId(userId));
    }

    /**
     * 修改租户
     */
    @PreAuthorize("@ss.hasPermi('gangzhu:tenant:edit')")
    @Log(title = "租户管理", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody HzUser tenant)
    {
        if (tenant == null || tenant.getUserId() == null) {
            return error("用户ID不能为空");
        }
        if (StringUtils.isEmpty(tenant.getPhone()) || !tenant.getPhone().matches("^1[3-9]\\d{9}$")) {
            return error("请输入正确的手机号码");
        }
        if (!"0".equals(tenant.getStatus()) && !"1".equals(tenant.getStatus())) {
            return error("用户状态无效");
        }
        HzUser existing = userMapper.selectById(tenant.getUserId());
        if (existing == null || !"0".equals(existing.getDelFlag())) {
            return error("用户不存在");
        }
        HzUser phoneOwner = userMapper.selectByPhoneIgnoreLogicDelete(tenant.getPhone());
        if (phoneOwner != null && !tenant.getUserId().equals(phoneOwner.getUserId())) {
            return error("手机号已存在，请更换后重试");
        }
        HzUser update = new HzUser();
        update.setUserId(tenant.getUserId());
        update.setPhone(tenant.getPhone());
        update.setStatus(tenant.getStatus());
        update.setRemark(tenant.getRemark());
        update.setUpdateTime(DateUtils.getNowDate());
        return toAjax(userMapper.updateById(update));
    }
}
