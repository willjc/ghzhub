package com.ruoyi.web.controller.system;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.constant.HttpStatus;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.PageDomain;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.core.page.TableSupport;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.system.domain.HzCheckIn;
import com.ruoyi.system.service.IHzCheckInService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * 入住管理Controller
 *
 * @author ruoyi
 */
@RestController("adminCheckInController")
@RequestMapping("/system/checkin")
public class HzCheckInController extends BaseController
{
    @Autowired
    private IHzCheckInService checkInService;

    /**
     * 查询入住申请列表
     */
    @PreAuthorize("@ss.hasPermi('gangzhu:checkin:list')")
    @GetMapping("/list")
    public TableDataInfo list(HzCheckIn checkIn)
    {
        PageDomain pageDomain = TableSupport.buildPageRequest();
        Integer pageNum = pageDomain.getPageNum();
        Integer pageSize = pageDomain.getPageSize();

        // 使用默认值：第1页，每页10条
        if (pageNum == null || pageNum <= 0) {
            pageNum = 1;
        }
        if (pageSize == null || pageSize <= 0) {
            pageSize = 10;
        }

        IPage<HzCheckIn> page = checkInService.selectCheckInPage(checkIn, pageNum, pageSize);

        // 手动构建分页响应
        TableDataInfo rspData = new TableDataInfo();
        rspData.setCode(HttpStatus.SUCCESS);
        rspData.setMsg("查询成功");
        rspData.setRows(page.getRecords());
        rspData.setTotal(page.getTotal());
        return rspData;
    }

    /**
     * 导出入住申请列表
     */
    @PreAuthorize("@ss.hasPermi('gangzhu:checkin:export')")
    @Log(title = "入住管理", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, HzCheckIn checkIn)
    {
        List<HzCheckIn> list = checkInService.selectCheckInList(checkIn);
        ExcelUtil<HzCheckIn> util = new ExcelUtil<HzCheckIn>(HzCheckIn.class);
        util.exportExcel(response, list, "入住申请数据");
    }

    /**
     * 获取入住申请详细信息
     */
    @PreAuthorize("@ss.hasPermi('gangzhu:checkin:query')")
    @GetMapping(value = "/{recordId}")
    public AjaxResult getInfo(@PathVariable("recordId") Long recordId)
    {
        return success(checkInService.selectCheckInById(recordId));
    }

    /**
     * 新增入住申请
     */
    @PreAuthorize("@ss.hasPermi('gangzhu:checkin:add')")
    @Log(title = "入住管理", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody HzCheckIn checkIn)
    {
        return toAjax(checkInService.insertCheckIn(checkIn));
    }

    /**
     * 修改入住申请
     */
    @PreAuthorize("@ss.hasPermi('gangzhu:checkin:edit')")
    @Log(title = "入住管理", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody HzCheckIn checkIn)
    {
        return toAjax(checkInService.updateCheckIn(checkIn));
    }

    /**
     * 删除入住申请
     */
    @PreAuthorize("@ss.hasPermi('gangzhu:checkin:remove')")
    @Log(title = "入住管理", businessType = BusinessType.DELETE)
    @DeleteMapping("/{recordIds}")
    public AjaxResult remove(@PathVariable Long[] recordIds)
    {
        for (Long recordId : recordIds) {
            checkInService.deleteCheckInById(recordId);
        }
        return success();
    }

    /**
     * 审核入住申请
     */
    @PreAuthorize("@ss.hasPermi('gangzhu:checkin:audit')")
    @Log(title = "入住审核", businessType = BusinessType.UPDATE)
    @PutMapping("/audit")
    public AjaxResult audit(@RequestBody HzCheckIn checkIn)
    {
        // 验证审核信息
        if (checkIn.getRecordId() == null) {
            return error("入住记录ID不能为空");
        }
        if (checkIn.getStatus() == null) {
            return error("审核状态不能为空");
        }

        // 查询入住记录
        HzCheckIn existCheckIn = checkInService.selectCheckInById(checkIn.getRecordId());
        if (existCheckIn == null) {
            return error("入住记录不存在");
        }

        // 检查当前状态
        if (!"1".equals(existCheckIn.getStatus())) {
            return error("该入住单不是待审核状态，无法审核");
        }

        // 更新审核信息
        existCheckIn.setStatus(checkIn.getStatus());  // 2=已入住(通过), 3=已拒绝
        existCheckIn.setAuditBy(getUsername());
        existCheckIn.setAuditTime(com.ruoyi.common.utils.DateUtils.getTime());
        existCheckIn.setAuditRemark(checkIn.getAuditRemark());

        return toAjax(checkInService.updateCheckIn(existCheckIn));
    }
}
