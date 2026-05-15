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
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.system.domain.HzBill;
import com.ruoyi.system.domain.HzCheckIn;
import com.ruoyi.system.domain.HzContract;
import com.ruoyi.system.mapper.HzBillMapper;
import com.ruoyi.system.mapper.HzContractMapper;
import com.ruoyi.system.service.IHzCheckInService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletResponse;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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

    @Autowired
    private HzContractMapper contractMapper;

    @Autowired
    private HzBillMapper billMapper;

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

        // 仅"通过"分支需要二次校验业务条件；"拒绝"放行
        if ("2".equals(checkIn.getStatus())) {
            String denyMsg = validateCanApprove(existCheckIn);
            if (denyMsg != null) {
                return error(denyMsg);
            }
        }

        // 更新审核信息
        existCheckIn.setStatus(checkIn.getStatus());  // 2=已入住(通过), 3=已拒绝
        existCheckIn.setAuditBy(getUsername());
        existCheckIn.setAuditTime(com.ruoyi.common.utils.DateUtils.getTime());
        existCheckIn.setAuditRemark(checkIn.getAuditRemark());

        return toAjax(checkInService.updateCheckIn(existCheckIn));
    }

    /**
     * 通过审核前的业务条件校验：
     * 1) 合同必须存在且状态为 已签署(2)/履行中(3)
     * 2) 押金账单（如有）必须全部已付清
     * 3) 首期租金账单（按 due_date 升序第一条）必须已付清
     * 4) 实际入住日期需落在 [sign_time, sign_time+3 天] 区间
     * 任一不满足返回拒绝原因；通过返回 null。
     */
    private String validateCanApprove(HzCheckIn existCheckIn) {
        Long contractId = existCheckIn.getContractId();
        if (contractId == null) {
            return "入住单未关联合同，无法审核通过";
        }
        HzContract contract = contractMapper.selectById(contractId);
        if (contract == null || !"0".equals(contract.getDelFlag())) {
            return "合同不存在或已删除，无法审核通过";
        }
        String cs = contract.getContractStatus();
        if (!"2".equals(cs) && !"3".equals(cs)) {
            return "合同当前状态(" + cs + ")已非「已签署/履行中」，无法办理入住";
        }

        // 押金：若存在押金账单，则必须全部 bill_status='1'
        List<HzBill> deposits = billMapper.selectList(new LambdaQueryWrapper<HzBill>()
                .eq(HzBill::getContractId, contractId)
                .eq(HzBill::getBillType, "1")
                .eq(HzBill::getDelFlag, "0"));
        for (HzBill b : deposits) {
            if (!"1".equals(b.getBillStatus())) {
                return "押金尚未收齐，无法办理入住";
            }
        }

        // 首期租金（按 due_date 升序的第一条）必须已付清
        HzBill firstRent = billMapper.selectOne(new LambdaQueryWrapper<HzBill>()
                .eq(HzBill::getContractId, contractId)
                .eq(HzBill::getBillType, "2")
                .eq(HzBill::getDelFlag, "0")
                .orderByAsc(HzBill::getDueDate)
                .last("LIMIT 1"));
        if (firstRent != null && !"1".equals(firstRent.getBillStatus())) {
            return "首期租金尚未收齐，无法办理入住";
        }

        // 入住日期必须在 [sign_time, sign_time+3 天]
        String dateMsg = validateCheckinDateRange(existCheckIn.getActualCheckinDate(), contract.getSignTime());
        if (dateMsg != null) {
            return dateMsg;
        }
        return null;
    }

    /**
     * 校验实际入住日期是否落在 [sign_time, sign_time+3 天] 区间。
     * 不通过返回提示文字；通过返回 null。
     */
    private String validateCheckinDateRange(String actualCheckinDate, String signTime) {
        if (actualCheckinDate == null || actualCheckinDate.isEmpty()) {
            return "实际入住日期不能为空";
        }
        if (signTime == null || signTime.isEmpty()) {
            // 老数据迁移合同可能无 sign_time，放行不卡
            return null;
        }
        try {
            LocalDate ci = LocalDate.parse(actualCheckinDate.length() >= 10
                    ? actualCheckinDate.substring(0, 10)
                    : actualCheckinDate, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            LocalDateTime st = LocalDateTime.parse(signTime.replace("T", " ").trim(),
                    DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            LocalDate signDay = st.toLocalDate();
            LocalDate maxDay = signDay.plusDays(3);
            if (ci.isBefore(signDay) || ci.isAfter(maxDay)) {
                return "入住日期需在签订合同当日至签订日后 3 日内（" + signDay + " 至 " + maxDay + "）";
            }
        } catch (Exception e) {
            // 解析失败不阻断主流程
            return null;
        }
        return null;
    }
}
