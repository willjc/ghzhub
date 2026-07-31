package com.ruoyi.web.controller.system;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.system.domain.HzBill;
import com.ruoyi.system.domain.HzContract;
import com.ruoyi.system.domain.HzProject;
import com.ruoyi.system.domain.HzRefundApply;
import com.ruoyi.system.mapper.HzBillMapper;
import com.ruoyi.system.mapper.HzContractMapper;
import com.ruoyi.system.mapper.HzHouseMapper;
import com.ruoyi.system.mapper.HzProjectMapper;
import com.ruoyi.system.mapper.HzRefundApplyMapper;
import com.ruoyi.system.mapper.HzUserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * 首页驾驶舱统计Controller
 *
 * 口径与「报表管理 - 收款台账」(HzReportController) 完全一致，保证驾驶舱与报表数字对得上。
 * 该接口为所有登录用户首页所用，故不加 @PreAuthorize（仅需登录认证）。
 *
 * build-marker: statistics-v2 (redeploy verify)
 */
@RestController
@RequestMapping("/gangzhu/statistics")
public class HzStatisticsController extends BaseController {

    @Autowired
    private HzBillMapper billMapper;

    @Autowired
    private HzContractMapper contractMapper;

    @Autowired
    private HzProjectMapper projectMapper;

    @Autowired
    private HzHouseMapper houseMapper;

    @Autowired
    private HzUserMapper userMapper;

    @Autowired
    private HzRefundApplyMapper refundApplyMapper;

    /**
     * 财务指标（按月）
     * 返回 { financial: { receivableAmount, receivedAmount, expectedAmount, overdueAmount, refundAmount } }
     */
    @GetMapping("/dashboard")
    public AjaxResult dashboard(@RequestParam(required = false) String month) {
        LocalDate first = parseMonth(month);
        String startStr = first.toString();
        String endStr = first.plusMonths(1).minusDays(1).toString();
        String startDt = startStr + " 00:00:00";
        String endDt = endStr + " 23:59:59";

        // 应收：due_date 落在月内的未关闭账单，取 bill_amount
        List<HzBill> receivableBills = billMapper.selectList(new LambdaQueryWrapper<HzBill>()
                .eq(HzBill::getDelFlag, "0")
                .ne(HzBill::getBillStatus, "4")
                .ge(HzBill::getDueDate, startStr)
                .le(HzBill::getDueDate, endStr));
        BigDecimal receivable = sumBillAmount(receivableBills);

        // 实收：pay_time 落在月内的已支付账单，取 paid_amount
        List<HzBill> receivedBills = billMapper.selectList(new LambdaQueryWrapper<HzBill>()
                .eq(HzBill::getDelFlag, "0")
                .eq(HzBill::getBillStatus, "1")
                .ge(HzBill::getPayTime, startDt)
                .le(HzBill::getPayTime, endDt));
        BigDecimal received = sumPaidAmount(receivedBills);

        // 逾期：月内应付但未付清（bill_amount - paid_amount）
        List<HzBill> overdueBills = billMapper.selectList(new LambdaQueryWrapper<HzBill>()
                .eq(HzBill::getDelFlag, "0")
                .in(HzBill::getBillStatus, "0", "2", "3")
                .ge(HzBill::getDueDate, startStr)
                .le(HzBill::getDueDate, endStr));
        BigDecimal overdue = overdueBills.stream()
                .map(b -> nz(b.getBillAmount()).subtract(nz(b.getPaidAmount())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // 预计应收：月内所有账单（含已关闭），取 bill_amount
        List<HzBill> expectedBills = billMapper.selectList(new LambdaQueryWrapper<HzBill>()
                .eq(HzBill::getDelFlag, "0")
                .ge(HzBill::getDueDate, startStr)
                .le(HzBill::getDueDate, endStr));
        BigDecimal expected = sumBillAmount(expectedBills);

        // 退款：hz_refund_apply 审核通过且审批时间落在月内
        List<HzRefundApply> refunds = refundApplyMapper.selectList(new LambdaQueryWrapper<HzRefundApply>()
                .eq(HzRefundApply::getDelFlag, "0")
                .eq(HzRefundApply::getApproveStatus, "1")
                .ge(HzRefundApply::getApproveTime, startDt)
                .le(HzRefundApply::getApproveTime, endDt));
        BigDecimal refundAmount = refunds.stream()
                .map(r -> nz(r.getRefundAmount()))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        Map<String, Object> financial = new LinkedHashMap<>();
        financial.put("receivableAmount", receivable);
        financial.put("receivedAmount", received);
        financial.put("expectedAmount", expected);
        financial.put("overdueAmount", overdue);
        financial.put("refundAmount", refundAmount);

        Map<String, Object> data = new LinkedHashMap<>();
        data.put("financial", financial);
        return AjaxResult.success(data);
    }

    /**
     * 房源管理概览
     * 返回 { totalRooms, rentedRooms, emptyRooms, maintenanceRooms, currentRentRate, cumulativeRentRate, userConversionRate }
     */
    @GetMapping("/house")
    public AjaxResult house() {
        long total = houseCount(null);
        long rented = houseCount("2");       // 已出租
        long empty = houseCount("0");         // 空置
        long maintenance = houseCount("3");   // 维修
        long reserved = houseCount("1");      // 已预订
        long offline = houseCount("4");       // 下架

        long denominator = total - offline;   // 分母排除下架房源
        int currentRate = denominator > 0
                ? BigDecimal.valueOf(rented * 100.0 / denominator).setScale(0, RoundingMode.HALF_UP).intValue() : 0;
        int cumulativeRate = denominator > 0
                ? BigDecimal.valueOf((rented + reserved) * 100.0 / denominator).setScale(0, RoundingMode.HALF_UP).intValue() : 0;

        // 用户转化率 = 签约租户数 / 注册用户数
        List<HzContract> signedContracts = contractMapper.selectList(new LambdaQueryWrapper<HzContract>()
                .select(HzContract::getTenantId)
                .eq(HzContract::getDelFlag, "0")
                .in(HzContract::getContractStatus, "2", "3", "4")
                .isNotNull(HzContract::getTenantId));
        long signedTenants = signedContracts.stream()
                .map(HzContract::getTenantId).filter(Objects::nonNull).distinct().count();
        long registeredUsers = userMapper.selectCount(new QueryWrapper<com.ruoyi.system.domain.HzUser>()
                .eq("del_flag", "0"));
        int conversionRate = registeredUsers > 0
                ? BigDecimal.valueOf(signedTenants * 100.0 / registeredUsers).setScale(0, RoundingMode.HALF_UP).intValue() : 0;

        Map<String, Object> data = new LinkedHashMap<>();
        data.put("totalRooms", total);
        data.put("rentedRooms", rented);
        data.put("emptyRooms", empty);
        data.put("maintenanceRooms", maintenance);
        data.put("currentRentRate", currentRate);
        data.put("cumulativeRentRate", cumulativeRate);
        data.put("userConversionRate", conversionRate);
        return AjaxResult.success(data);
    }

    /**
     * 租户画像 - 户籍来源分布（河南省=本地，身份证前2位 41）
     * 返回 { household: { local, nonlocal, unknown } }
     */
    @GetMapping("/tenant-profile")
    public AjaxResult tenantProfile() {
        long total = userMapper.selectCount(new QueryWrapper<com.ruoyi.system.domain.HzUser>()
                .eq("del_flag", "0"));
        // 有效身份证：前6位为数字
        long validTotal = userMapper.selectCount(new QueryWrapper<com.ruoyi.system.domain.HzUser>()
                .eq("del_flag", "0").apply("id_card REGEXP '^[0-9]{6}'"));
        // 本地：河南省 41 开头且前6位为数字
        long local = userMapper.selectCount(new QueryWrapper<com.ruoyi.system.domain.HzUser>()
                .eq("del_flag", "0").apply("id_card REGEXP '^41[0-9]{4}'"));
        long nonlocal = Math.max(0, validTotal - local);
        long unknown = Math.max(0, total - validTotal);

        Map<String, Object> household = new LinkedHashMap<>();
        household.put("local", local);
        household.put("nonlocal", nonlocal);
        household.put("unknown", unknown);

        Map<String, Object> data = new LinkedHashMap<>();
        data.put("household", household);
        return AjaxResult.success(data);
    }

    /**
     * 项目收款台账（按月）
     * 顶层返回 rows/total，每行含近6个月实收趋势 trendMonths/trendData。
     */
    @GetMapping("/project-ledger")
    public AjaxResult projectLedger(@RequestParam(required = false) String month) {
        LocalDate first = parseMonth(month);
        String selectedKey = first.format(DateTimeFormatter.ofPattern("yyyy-MM"));
        String monthEndDt = first.plusMonths(1).minusDays(1) + " 23:59:59";

        // 近6个月区间（含当月）
        LocalDate trendStart = first.minusMonths(5);
        // 本年累计起点
        LocalDate ytdStart = LocalDate.of(first.getYear(), 1, 1);
        // 取二者更早的作为查询下界
        LocalDate queryStart = trendStart.isBefore(ytdStart) ? trendStart : ytdStart;
        String queryStartDt = queryStart.toString() + " 00:00:00";

        // 近6个月标签与 key
        List<String> trendKeys = new ArrayList<>();
        List<String> trendLabels = new ArrayList<>();
        for (int i = 5; i >= 0; i--) {
            LocalDate m = first.minusMonths(i);
            trendKeys.add(m.format(DateTimeFormatter.ofPattern("yyyy-MM")));
            trendLabels.add(m.getMonthValue() + "月");
        }

        Map<Long, Long> contractProjectMap = getContractProjectMap();

        // 一次性取出查询区间内所有已支付账单
        List<HzBill> receivedBills = billMapper.selectList(new LambdaQueryWrapper<HzBill>()
                .eq(HzBill::getDelFlag, "0")
                .eq(HzBill::getBillStatus, "1")
                .ge(HzBill::getPayTime, queryStartDt)
                .le(HzBill::getPayTime, monthEndDt));

        // 应收（当月，用于计算收缴率与状态）
        List<HzBill> monthReceivableBills = billMapper.selectList(new LambdaQueryWrapper<HzBill>()
                .eq(HzBill::getDelFlag, "0")
                .ne(HzBill::getBillStatus, "4")
                .ge(HzBill::getDueDate, first.toString())
                .le(HzBill::getDueDate, first.plusMonths(1).minusDays(1).toString()));

        // projectId -> (monthKey -> 实收合计)
        Map<Long, Map<String, BigDecimal>> projectMonthReceived = new HashMap<>();
        for (HzBill b : receivedBills) {
            Long pid = contractProjectMap.getOrDefault(b.getContractId(), 0L);
            String payTime = b.getPayTime();
            if (payTime == null || payTime.length() < 7) continue;
            String key = payTime.substring(0, 7);
            projectMonthReceived
                    .computeIfAbsent(pid, k -> new HashMap<>())
                    .merge(key, nz(b.getPaidAmount()), BigDecimal::add);
        }

        // projectId -> 当月应收
        Map<Long, BigDecimal> projectMonthReceivable = new HashMap<>();
        for (HzBill b : monthReceivableBills) {
            Long pid = contractProjectMap.getOrDefault(b.getContractId(), 0L);
            projectMonthReceivable.merge(pid, nz(b.getBillAmount()), BigDecimal::add);
        }

        List<HzProject> projects = projectMapper.selectList(
                new LambdaQueryWrapper<HzProject>().eq(HzProject::getDelFlag, "0"));

        String updateTime = LocalDate.now() + " " + java.time.LocalTime.now().withNano(0);
        List<Map<String, Object>> rows = new ArrayList<>();

        for (HzProject project : projects) {
            Long pid = project.getProjectId();
            Map<String, BigDecimal> monthMap = projectMonthReceived.getOrDefault(pid, Collections.emptyMap());

            BigDecimal monthlyAmount = monthMap.getOrDefault(selectedKey, BigDecimal.ZERO);

            // 本年累计：monthKey 属于当年且 <= 当前选择月
            BigDecimal ytd = BigDecimal.ZERO;
            for (Map.Entry<String, BigDecimal> e : monthMap.entrySet()) {
                String k = e.getKey();
                if (k.startsWith(first.getYear() + "-") && k.compareTo(selectedKey) <= 0) {
                    ytd = ytd.add(e.getValue());
                }
            }

            // 近6个月趋势
            List<BigDecimal> trendData = new ArrayList<>();
            for (String key : trendKeys) {
                trendData.add(monthMap.getOrDefault(key, BigDecimal.ZERO));
            }

            BigDecimal receivable = projectMonthReceivable.getOrDefault(pid, BigDecimal.ZERO);
            int collectionRate = receivable.compareTo(BigDecimal.ZERO) > 0
                    ? monthlyAmount.multiply(BigDecimal.valueOf(100)).divide(receivable, 0, RoundingMode.HALF_UP).intValue()
                    : (monthlyAmount.compareTo(BigDecimal.ZERO) > 0 ? 100 : 0);

            // 无任何数据的项目不展示
            if (monthlyAmount.compareTo(BigDecimal.ZERO) == 0 && ytd.compareTo(BigDecimal.ZERO) == 0) {
                continue;
            }

            Map<String, Object> row = new LinkedHashMap<>();
            row.put("projectId", pid);
            row.put("projectName", project.getProjectName());
            row.put("projectCode", project.getProjectCode());
            row.put("monthlyAmount", monthlyAmount);
            row.put("yearToDateAmount", ytd);
            row.put("status", collectionRate >= 90 ? "normal" : collectionRate >= 80 ? "warning" : "error");
            row.put("updateTime", updateTime);
            row.put("trendMonths", trendLabels);
            row.put("trendData", trendData);
            rows.add(row);
        }

        // 按当月金额降序
        rows.sort((a, b) -> ((BigDecimal) b.get("monthlyAmount")).compareTo((BigDecimal) a.get("monthlyAmount")));

        AjaxResult ajax = AjaxResult.success();
        ajax.put("rows", rows);
        ajax.put("total", rows.size());
        return ajax;
    }

    // ================= 私有辅助 =================

    private long houseCount(String houseStatus) {
        QueryWrapper<com.ruoyi.system.domain.HzHouse> wrapper = new QueryWrapper<>();
        wrapper.eq("del_flag", "0");
        if (houseStatus != null) {
            wrapper.eq("house_status", houseStatus);
        }
        return houseMapper.selectCount(wrapper);
    }

    private Map<Long, Long> getContractProjectMap() {
        List<HzContract> contracts = contractMapper.selectList(new LambdaQueryWrapper<HzContract>()
                .select(HzContract::getContractId, HzContract::getProjectId)
                .eq(HzContract::getDelFlag, "0")
                .isNotNull(HzContract::getProjectId));
        Map<Long, Long> map = new HashMap<>();
        for (HzContract c : contracts) {
            if (c.getContractId() != null && c.getProjectId() != null) {
                map.put(c.getContractId(), c.getProjectId());
            }
        }
        return map;
    }

    private LocalDate parseMonth(String month) {
        if (month == null || month.isEmpty()) {
            return LocalDate.now().withDayOfMonth(1);
        }
        return LocalDate.parse(month + "-01");
    }

    private BigDecimal sumBillAmount(List<HzBill> bills) {
        return bills.stream().map(b -> nz(b.getBillAmount())).reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private BigDecimal sumPaidAmount(List<HzBill> bills) {
        return bills.stream().map(b -> nz(b.getPaidAmount())).reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private BigDecimal nz(BigDecimal v) {
        return v != null ? v : BigDecimal.ZERO;
    }
}
