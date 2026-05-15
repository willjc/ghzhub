package com.ruoyi.web.controller.system;

import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.system.domain.HzBill;
import com.ruoyi.system.domain.HzBuilding;
import com.ruoyi.system.domain.HzContract;
import com.ruoyi.system.domain.HzHouse;
import com.ruoyi.system.domain.HzUnit;
import com.ruoyi.system.mapper.HzBillMapper;
import com.ruoyi.system.mapper.HzBuildingMapper;
import com.ruoyi.system.mapper.HzContractMapper;
import com.ruoyi.system.mapper.HzHouseMapper;
import com.ruoyi.system.mapper.HzProjectMapper;
import com.ruoyi.system.mapper.HzUnitMapper;
import com.ruoyi.system.domain.HzProject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletResponse;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.WeekFields;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 报表管理Controller
 */
@RestController
@RequestMapping("/system/report")
public class HzReportController extends BaseController {

    @Autowired
    private HzBillMapper billMapper;

    @Autowired
    private HzContractMapper contractMapper;

    @Autowired
    private HzProjectMapper projectMapper;

    @Autowired
    private HzHouseMapper houseMapper;

    @Autowired
    private HzBuildingMapper buildingMapper;

    @Autowired
    private HzUnitMapper unitMapper;

    /**
     * 收款台账汇总 - 按项目分组
     * periodType: day/week/month/year
     * periodValue: 2026-05-13 / 2026-W20 / 2026-05 / 2026
     */
    @PreAuthorize("@ss.hasPermi('gangzhu:report:receipt')")
    @GetMapping("/receipt/summary")
    public AjaxResult receiptSummary(
            @RequestParam(defaultValue = "month") String periodType,
            @RequestParam(required = false) String periodValue,
            @RequestParam(required = false) Long projectId) {

        // 计算时间范围
        LocalDate startDate = getStartDate(periodType, periodValue);
        LocalDate endDate = getEndDate(periodType, periodValue);
        String startStr = startDate.toString();
        String endStr = endDate.toString();
        String startDt = startStr + " 00:00:00";
        String endDt = endStr + " 23:59:59";
        String today = LocalDate.now().toString();

        // 查询所有项目
        List<HzProject> projects = projectMapper.selectList(
            new LambdaQueryWrapper<HzProject>().eq(HzProject::getDelFlag, "0")
        );

        // 获取 contract_id -> project_id 映射（用于按项目归类账单）
        Map<Long, Long> contractProjectMap = getContractProjectMap();

        // 项目过滤限定的合同ID集合
        Set<Long> filterContractIds = null;
        if (projectId != null) {
            filterContractIds = contractProjectMap.entrySet().stream()
                .filter(e -> projectId.equals(e.getValue()))
                .map(Map.Entry::getKey)
                .collect(Collectors.toSet());
        }

        // 1) 应收：due_date 落在时段内的所有未关闭账单
        LambdaQueryWrapper<HzBill> receivableWrapper = new LambdaQueryWrapper<>();
        receivableWrapper.eq(HzBill::getDelFlag, "0")
                         .ne(HzBill::getBillStatus, "4") // 排除已关闭
                         .ge(HzBill::getDueDate, startStr)
                         .le(HzBill::getDueDate, endStr);
        if (filterContractIds != null) {
            if (filterContractIds.isEmpty()) {
                receivableWrapper.eq(HzBill::getContractId, -1L);
            } else {
                receivableWrapper.in(HzBill::getContractId, filterContractIds);
            }
        }
        List<HzBill> receivableBills = billMapper.selectList(receivableWrapper);

        // 2) 实收：pay_time 落在时段内的已支付账单
        LambdaQueryWrapper<HzBill> receivedWrapper = new LambdaQueryWrapper<>();
        receivedWrapper.eq(HzBill::getDelFlag, "0")
                       .eq(HzBill::getBillStatus, "1")
                       .ge(HzBill::getPayTime, startDt)
                       .le(HzBill::getPayTime, endDt);
        if (filterContractIds != null) {
            if (filterContractIds.isEmpty()) {
                receivedWrapper.eq(HzBill::getContractId, -1L);
            } else {
                receivedWrapper.in(HzBill::getContractId, filterContractIds);
            }
        }
        List<HzBill> receivedBills = billMapper.selectList(receivedWrapper);

        // 3) 逾期：due_date 在时段结束之前 且未付清（截至endDate所在时点的存量逾期）
        LambdaQueryWrapper<HzBill> overdueWrapper = new LambdaQueryWrapper<>();
        overdueWrapper.eq(HzBill::getDelFlag, "0")
                      .in(HzBill::getBillStatus, "0", "2", "3")
                      .lt(HzBill::getDueDate, today)
                      .le(HzBill::getDueDate, endStr);
        if (filterContractIds != null) {
            if (filterContractIds.isEmpty()) {
                overdueWrapper.eq(HzBill::getContractId, -1L);
            } else {
                overdueWrapper.in(HzBill::getContractId, filterContractIds);
            }
        }
        List<HzBill> overdueBills = billMapper.selectList(overdueWrapper);

        // 按项目分组
        Map<Long, List<HzBill>> recvGroup = receivableBills.stream()
            .collect(Collectors.groupingBy(b -> contractProjectMap.getOrDefault(b.getContractId(), 0L)));
        Map<Long, List<HzBill>> paidGroup = receivedBills.stream()
            .collect(Collectors.groupingBy(b -> contractProjectMap.getOrDefault(b.getContractId(), 0L)));
        Map<Long, List<HzBill>> overdueGroup = overdueBills.stream()
            .collect(Collectors.groupingBy(b -> contractProjectMap.getOrDefault(b.getContractId(), 0L)));

        List<Map<String, Object>> resultList = new ArrayList<>();
        BigDecimal totalReceivable = BigDecimal.ZERO;
        BigDecimal totalReceived = BigDecimal.ZERO;
        BigDecimal totalOverdue = BigDecimal.ZERO;
        int totalBillCount = 0;
        int totalPaidCount = 0;

        for (HzProject project : projects) {
            if (projectId != null && !project.getProjectId().equals(projectId)) continue;

            List<HzBill> recvList = recvGroup.getOrDefault(project.getProjectId(), Collections.emptyList());
            List<HzBill> paidList = paidGroup.getOrDefault(project.getProjectId(), Collections.emptyList());
            List<HzBill> overList = overdueGroup.getOrDefault(project.getProjectId(), Collections.emptyList());

            if (recvList.isEmpty() && paidList.isEmpty() && overList.isEmpty()) continue;

            BigDecimal receivable = recvList.stream()
                .map(HzBill::getBillAmount).filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

            BigDecimal received = paidList.stream()
                .map(b -> b.getPaidAmount() != null ? b.getPaidAmount() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

            BigDecimal overdue = overList.stream()
                .map(b -> {
                    BigDecimal amt = b.getBillAmount() != null ? b.getBillAmount() : BigDecimal.ZERO;
                    BigDecimal paid = b.getPaidAmount() != null ? b.getPaidAmount() : BigDecimal.ZERO;
                    return amt.subtract(paid);
                })
                .reduce(BigDecimal.ZERO, BigDecimal::add);

            int collectionRate = receivable.compareTo(BigDecimal.ZERO) > 0
                ? received.multiply(BigDecimal.valueOf(100)).divide(receivable, 0, RoundingMode.HALF_UP).intValue()
                : (received.compareTo(BigDecimal.ZERO) > 0 ? 100 : 0);

            Map<String, Object> item = new LinkedHashMap<>();
            item.put("projectId", project.getProjectId());
            item.put("projectName", project.getProjectName());
            item.put("receivableAmount", receivable);
            item.put("receivedAmount", received);
            item.put("overdueAmount", overdue);
            item.put("billCount", recvList.size());
            item.put("paidCount", paidList.size());
            item.put("overdueCount", overList.size());
            item.put("collectionRate", collectionRate);
            item.put("status", collectionRate >= 90 ? "normal" : collectionRate >= 80 ? "warning" : "error");
            resultList.add(item);

            totalReceivable = totalReceivable.add(receivable);
            totalReceived = totalReceived.add(received);
            totalOverdue = totalOverdue.add(overdue);
            totalBillCount += recvList.size();
            totalPaidCount += paidList.size();
        }

        int totalRate = totalReceivable.compareTo(BigDecimal.ZERO) > 0
            ? totalReceived.multiply(BigDecimal.valueOf(100)).divide(totalReceivable, 0, RoundingMode.HALF_UP).intValue()
            : (totalReceived.compareTo(BigDecimal.ZERO) > 0 ? 100 : 0);

        Map<String, Object> summary = new LinkedHashMap<>();
        summary.put("totalReceivable", totalReceivable);
        summary.put("totalReceived", totalReceived);
        summary.put("totalOverdue", totalOverdue);
        summary.put("collectionRate", totalRate);
        summary.put("totalBillCount", totalBillCount);
        summary.put("totalPaidCount", totalPaidCount);

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("summary", summary);
        result.put("projects", resultList);
        result.put("periodType", periodType);
        result.put("periodValue", periodValue);
        return AjaxResult.success(result);
    }

    /**
     * 收款明细列表（分页）
     */
    @PreAuthorize("@ss.hasPermi('gangzhu:report:receipt')")
    @GetMapping("/receipt/detail")
    public TableDataInfo receiptDetail(
            @RequestParam(defaultValue = "month") String periodType,
            @RequestParam(required = false) String periodValue,
            @RequestParam(required = false) Long projectId,
            @RequestParam(required = false) String billType) {

        startPage();
        LocalDate startDate = getStartDate(periodType, periodValue);
        LocalDate endDate = getEndDate(periodType, periodValue);

        LambdaQueryWrapper<HzBill> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(HzBill::getDelFlag, "0")
               .eq(HzBill::getBillStatus, "1") // 只查已支付
               .ge(HzBill::getPayTime, startDate.toString() + " 00:00:00")
               .le(HzBill::getPayTime, endDate.toString() + " 23:59:59");

        if (billType != null && !billType.isEmpty()) {
            wrapper.eq(HzBill::getBillType, billType);
        }
        if (projectId != null) {
            wrapper.inSql(HzBill::getContractId,
                "SELECT contract_id FROM hz_contract WHERE project_id = " + projectId + " AND del_flag = '0'");
        }
        wrapper.orderByDesc(HzBill::getPayTime);

        List<HzBill> bills = billMapper.selectList(wrapper);

        // 补充项目名称 + 房源完整地址
        Map<Long, Long> contractProjectMap = getContractProjectMap();
        Map<Long, String> projectNameMap = getProjectNameMap();
        Map<Long, String> houseAddressMap = getHouseAddressMap(bills);

        List<Map<String, Object>> detailList = bills.stream().map(b -> {
            Map<String, Object> item = new LinkedHashMap<>();
            item.put("billId", b.getBillId());
            item.put("billNo", b.getBillNo());
            item.put("tenantName", b.getTenantName());
            item.put("houseCode", b.getHouseCode());
            item.put("houseAddress", houseAddressMap.getOrDefault(b.getHouseId(), b.getHouseCode() != null ? b.getHouseCode() : "-"));
            Long pId = contractProjectMap.getOrDefault(b.getContractId(), 0L);
            item.put("projectName", projectNameMap.getOrDefault(pId, "-"));
            item.put("billType", b.getBillType());
            item.put("billTypeText", billTypeText(b.getBillType()));
            item.put("billAmount", b.getBillAmount());
            item.put("paidAmount", b.getPaidAmount());
            item.put("payTime", b.getPayTime());
            item.put("payMethod", b.getPayMethod());
            item.put("payMethodText", payMethodText(b.getPayMethod()));
            item.put("transactionNo", b.getTransactionNo());
            // 账期兜底：billPeriod 为空则用 billDate 的 yyyy-MM
            String period = b.getBillPeriod();
            if (period == null || period.isEmpty()) {
                if (b.getBillDate() != null && b.getBillDate().length() >= 7) {
                    period = b.getBillDate().substring(0, 7);
                } else {
                    period = "-";
                }
            }
            item.put("billPeriod", period);
            return item;
        }).collect(Collectors.toList());

        return getDataTable(detailList, bills);
    }

    /**
     * 收款明细汇总（按当前筛选条件统计明细总数/金额，用于页面顶部展示）
     */
    @PreAuthorize("@ss.hasPermi('gangzhu:report:receipt')")
    @GetMapping("/receipt/detail/summary")
    public AjaxResult receiptDetailSummary(
            @RequestParam(defaultValue = "month") String periodType,
            @RequestParam(required = false) String periodValue,
            @RequestParam(required = false) Long projectId,
            @RequestParam(required = false) String billType) {

        LocalDate startDate = getStartDate(periodType, periodValue);
        LocalDate endDate = getEndDate(periodType, periodValue);

        LambdaQueryWrapper<HzBill> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(HzBill::getDelFlag, "0")
               .eq(HzBill::getBillStatus, "1")
               .ge(HzBill::getPayTime, startDate.toString() + " 00:00:00")
               .le(HzBill::getPayTime, endDate.toString() + " 23:59:59");
        if (billType != null && !billType.isEmpty()) {
            wrapper.eq(HzBill::getBillType, billType);
        }
        if (projectId != null) {
            wrapper.inSql(HzBill::getContractId,
                "SELECT contract_id FROM hz_contract WHERE project_id = " + projectId + " AND del_flag = '0'");
        }

        List<HzBill> bills = billMapper.selectList(wrapper);

        BigDecimal totalAmount = bills.stream()
            .map(b -> b.getPaidAmount() != null ? b.getPaidAmount() : BigDecimal.ZERO)
            .reduce(BigDecimal.ZERO, BigDecimal::add);

        // 按账单类型分类统计
        Map<String, BigDecimal> typeAmount = new LinkedHashMap<>();
        Map<String, Integer> typeCount = new LinkedHashMap<>();
        for (HzBill b : bills) {
            String t = billTypeText(b.getBillType());
            BigDecimal pa = b.getPaidAmount() != null ? b.getPaidAmount() : BigDecimal.ZERO;
            typeAmount.merge(t, pa, BigDecimal::add);
            typeCount.merge(t, 1, Integer::sum);
        }
        List<Map<String, Object>> typeStats = new ArrayList<>();
        for (Map.Entry<String, BigDecimal> e : typeAmount.entrySet()) {
            Map<String, Object> tm = new LinkedHashMap<>();
            tm.put("name", e.getKey());
            tm.put("amount", e.getValue());
            tm.put("count", typeCount.getOrDefault(e.getKey(), 0));
            typeStats.add(tm);
        }

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("totalCount", bills.size());
        result.put("totalAmount", totalAmount);
        result.put("typeStats", typeStats);
        return AjaxResult.success(result);
    }

    /**
     * 收款明细导出
     */
    @PreAuthorize("@ss.hasPermi('gangzhu:report:receipt')")
    @PostMapping("/receipt/export")
    public void receiptExport(HttpServletResponse response,
            @RequestParam(defaultValue = "month") String periodType,
            @RequestParam(required = false) String periodValue,
            @RequestParam(required = false) Long projectId,
            @RequestParam(required = false) String billType) {

        LocalDate startDate = getStartDate(periodType, periodValue);
        LocalDate endDate = getEndDate(periodType, periodValue);

        LambdaQueryWrapper<HzBill> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(HzBill::getDelFlag, "0")
               .eq(HzBill::getBillStatus, "1")
               .ge(HzBill::getPayTime, startDate.toString() + " 00:00:00")
               .le(HzBill::getPayTime, endDate.toString() + " 23:59:59");

        if (billType != null && !billType.isEmpty()) {
            wrapper.eq(HzBill::getBillType, billType);
        }
        if (projectId != null) {
            wrapper.inSql(HzBill::getContractId,
                "SELECT contract_id FROM hz_contract WHERE project_id = " + projectId + " AND del_flag = '0'");
        }
        wrapper.orderByDesc(HzBill::getPayTime);

        List<HzBill> bills = billMapper.selectList(wrapper);

        // 在导出前对账期与支付方式做兜底/映射，并填充房源地址（写入 remark 临时承载或直接保留 houseCode）
        Map<Long, String> houseAddressMap = getHouseAddressMap(bills);
        for (HzBill b : bills) {
            // 账期兜底
            if ((b.getBillPeriod() == null || b.getBillPeriod().isEmpty())
                    && b.getBillDate() != null && b.getBillDate().length() >= 7) {
                b.setBillPeriod(b.getBillDate().substring(0, 7));
            }
            // 支付方式中文化
            b.setPayMethod(payMethodText(b.getPayMethod()));
            // 房源编号替换为完整地址
            String addr = houseAddressMap.get(b.getHouseId());
            if (addr != null && !addr.isEmpty()) {
                b.setHouseCode(addr);
            }
        }

        ExcelUtil<HzBill> util = new ExcelUtil<>(HzBill.class);
        util.exportExcel(response, bills, "收款明细");
    }

    /**
     * 自定义报表 - 生成数据
     */
    @PreAuthorize("@ss.hasPermi('gangzhu:report:custom')")
    @PostMapping("/custom/generate")
    public AjaxResult customGenerate(@RequestBody Map<String, Object> params) {
        String startMonth = (String) params.get("startMonth");
        String endMonth = (String) params.get("endMonth");
        List<String> dimensions = (List<String>) params.get("dimensions");
        List<String> metrics = (List<String>) params.get("metrics");
        List<Integer> projectIds = params.get("projectIds") != null
            ? ((List<?>) params.get("projectIds")).stream().map(o -> Integer.parseInt(o.toString())).collect(Collectors.toList())
            : null;

        if (dimensions == null || dimensions.isEmpty()) {
            return error("请至少选择一个数据维度");
        }
        if (metrics == null || metrics.isEmpty()) {
            return error("请至少选择一个统计指标");
        }

        // 时间范围
        LocalDate start = LocalDate.parse(startMonth + "-01");
        LocalDate end = LocalDate.parse(endMonth + "-01").plusMonths(1).minusDays(1);
        String startStr = start.toString();
        String endStr = end.toString();
        String startDt = startStr + " 00:00:00";
        String endDt = endStr + " 23:59:59";
        String today = LocalDate.now().toString();

        // 项目过滤
        Map<Long, Long> contractProjectMap = getContractProjectMap();
        Map<Long, String> projectNameMap = getProjectNameMap();
        Set<Long> filterContractIds = null;
        if (projectIds != null && !projectIds.isEmpty()) {
            Set<Long> wantedPids = projectIds.stream().map(Integer::longValue).collect(Collectors.toSet());
            filterContractIds = contractProjectMap.entrySet().stream()
                .filter(e -> wantedPids.contains(e.getValue()))
                .map(Map.Entry::getKey)
                .collect(Collectors.toSet());
        }

        // 1) 应收账单：due_date 落在范围内
        LambdaQueryWrapper<HzBill> recvW = new LambdaQueryWrapper<>();
        recvW.eq(HzBill::getDelFlag, "0").ne(HzBill::getBillStatus, "4")
             .ge(HzBill::getDueDate, startStr).le(HzBill::getDueDate, endStr);
        if (filterContractIds != null) {
            if (filterContractIds.isEmpty()) recvW.eq(HzBill::getContractId, -1L);
            else recvW.in(HzBill::getContractId, filterContractIds);
        }
        List<HzBill> receivableBills = billMapper.selectList(recvW);

        // 2) 实收账单：pay_time 落在范围内 且已支付
        LambdaQueryWrapper<HzBill> paidW = new LambdaQueryWrapper<>();
        paidW.eq(HzBill::getDelFlag, "0").eq(HzBill::getBillStatus, "1")
             .ge(HzBill::getPayTime, startDt).le(HzBill::getPayTime, endDt);
        if (filterContractIds != null) {
            if (filterContractIds.isEmpty()) paidW.eq(HzBill::getContractId, -1L);
            else paidW.in(HzBill::getContractId, filterContractIds);
        }
        List<HzBill> paidBills = billMapper.selectList(paidW);

        // 3) 逾期账单：截至endDate已逾期未付清
        LambdaQueryWrapper<HzBill> overW = new LambdaQueryWrapper<>();
        overW.eq(HzBill::getDelFlag, "0").in(HzBill::getBillStatus, "0", "2", "3")
             .lt(HzBill::getDueDate, today).le(HzBill::getDueDate, endStr);
        if (filterContractIds != null) {
            if (filterContractIds.isEmpty()) overW.eq(HzBill::getContractId, -1L);
            else overW.in(HzBill::getContractId, filterContractIds);
        }
        List<HzBill> overBills = billMapper.selectList(overW);

        // 维度分组键生成器：分别对应收/实收/逾期账单做分组
        java.util.function.Function<HzBill, String> keyFn = b -> {
            StringBuilder key = new StringBuilder();
            for (String dim : dimensions) {
                if (key.length() > 0) key.append("|");
                switch (dim) {
                    case "projectName":
                        Long pId = contractProjectMap.getOrDefault(b.getContractId(), 0L);
                        key.append(projectNameMap.getOrDefault(pId, "未知"));
                        break;
                    case "month":
                        // 按 due_date 月份(应收口径)；若空则用 bill_date
                        String d = b.getDueDate() != null ? b.getDueDate() : b.getBillDate();
                        key.append(d != null && d.length() >= 7 ? d.substring(0, 7) : "未知");
                        break;
                    case "billType":
                        key.append(billTypeText(b.getBillType()));
                        break;
                    default:
                        key.append("-");
                }
            }
            return key.toString();
        };
        java.util.function.Function<HzBill, String> keyFnPaid = b -> {
            // 实收按 pay_time 月份分组
            StringBuilder key = new StringBuilder();
            for (String dim : dimensions) {
                if (key.length() > 0) key.append("|");
                switch (dim) {
                    case "projectName":
                        Long pId = contractProjectMap.getOrDefault(b.getContractId(), 0L);
                        key.append(projectNameMap.getOrDefault(pId, "未知"));
                        break;
                    case "month":
                        String pt = b.getPayTime();
                        key.append(pt != null && pt.length() >= 7 ? pt.substring(0, 7) : "未知");
                        break;
                    case "billType":
                        key.append(billTypeText(b.getBillType()));
                        break;
                    default:
                        key.append("-");
                }
            }
            return key.toString();
        };

        Map<String, List<HzBill>> recvGroup = receivableBills.stream().collect(Collectors.groupingBy(keyFn));
        Map<String, List<HzBill>> paidGroup = paidBills.stream().collect(Collectors.groupingBy(keyFnPaid));
        Map<String, List<HzBill>> overGroup = overBills.stream().collect(Collectors.groupingBy(keyFn));

        // 合并所有分组键
        Set<String> allKeys = new java.util.LinkedHashSet<>();
        allKeys.addAll(recvGroup.keySet());
        allKeys.addAll(paidGroup.keySet());
        allKeys.addAll(overGroup.keySet());

        // 聚合
        List<Map<String, Object>> resultList = new ArrayList<>();
        for (String groupKey : allKeys) {
            String[] keys = groupKey.split("\\|", -1);
            List<HzBill> recvList = recvGroup.getOrDefault(groupKey, Collections.emptyList());
            List<HzBill> paidList = paidGroup.getOrDefault(groupKey, Collections.emptyList());
            List<HzBill> overList = overGroup.getOrDefault(groupKey, Collections.emptyList());

            Map<String, Object> row = new LinkedHashMap<>();
            for (int i = 0; i < dimensions.size() && i < keys.length; i++) {
                row.put(dimensions.get(i), keys[i]);
            }

            for (String metric : metrics) {
                switch (metric) {
                    case "receivableAmount":
                        row.put(metric, recvList.stream()
                            .map(HzBill::getBillAmount).filter(Objects::nonNull)
                            .reduce(BigDecimal.ZERO, BigDecimal::add));
                        break;
                    case "receivedAmount":
                        row.put(metric, paidList.stream()
                            .map(b -> b.getPaidAmount() != null ? b.getPaidAmount() : BigDecimal.ZERO)
                            .reduce(BigDecimal.ZERO, BigDecimal::add));
                        break;
                    case "overdueAmount":
                        row.put(metric, overList.stream()
                            .map(b -> {
                                BigDecimal amt = b.getBillAmount() != null ? b.getBillAmount() : BigDecimal.ZERO;
                                BigDecimal pa = b.getPaidAmount() != null ? b.getPaidAmount() : BigDecimal.ZERO;
                                return amt.subtract(pa);
                            })
                            .reduce(BigDecimal.ZERO, BigDecimal::add));
                        break;
                    case "billCount":
                        row.put(metric, recvList.size());
                        break;
                    case "paidCount":
                        row.put(metric, paidList.size());
                        break;
                    case "collectionRate":
                        BigDecimal recv = recvList.stream()
                            .map(HzBill::getBillAmount).filter(Objects::nonNull)
                            .reduce(BigDecimal.ZERO, BigDecimal::add);
                        BigDecimal paid = paidList.stream()
                            .map(b -> b.getPaidAmount() != null ? b.getPaidAmount() : BigDecimal.ZERO)
                            .reduce(BigDecimal.ZERO, BigDecimal::add);
                        int rate = recv.compareTo(BigDecimal.ZERO) > 0
                            ? paid.multiply(BigDecimal.valueOf(100)).divide(recv, 0, RoundingMode.HALF_UP).intValue()
                            : (paid.compareTo(BigDecimal.ZERO) > 0 ? 100 : 0);
                        row.put(metric, rate);
                        break;
                }
            }
            resultList.add(row);
        }

        // 排序（按第一个维度）
        if (!dimensions.isEmpty()) {
            resultList.sort(Comparator.comparing(m -> String.valueOf(m.get(dimensions.get(0)))));
        }

        return AjaxResult.success(resultList);
    }

    /**
     * 自定义报表导出
     */
    @PreAuthorize("@ss.hasPermi('gangzhu:report:custom')")
    @PostMapping("/custom/export")
    public void customExport(HttpServletResponse response, @RequestBody Map<String, Object> params) {
        // 复用 generate 逻辑获取数据
        AjaxResult result = customGenerate(params);
        List<Map<String, Object>> data = (List<Map<String, Object>>) result.get("data");
        if (data == null || data.isEmpty()) {
            return;
        }

        // 动态导出
        List<String> dimensions = (List<String>) params.get("dimensions");
        List<String> metrics = (List<String>) params.get("metrics");

        // 构建表头映射
        Map<String, String> headerMap = new LinkedHashMap<>();
        for (String dim : dimensions) {
            headerMap.put(dim, getDimensionLabel(dim));
        }
        for (String metric : metrics) {
            headerMap.put(metric, getMetricLabel(metric));
        }

        // 使用简单CSV方式导出
        try {
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            response.setHeader("Content-Disposition", "attachment;filename=custom_report.xlsx");

            // 使用 poi 写入
            org.apache.poi.xssf.usermodel.XSSFWorkbook workbook = new org.apache.poi.xssf.usermodel.XSSFWorkbook();
            org.apache.poi.xssf.usermodel.XSSFSheet sheet = workbook.createSheet("自定义报表");

            // 表头
            org.apache.poi.xssf.usermodel.XSSFRow headerRow = sheet.createRow(0);
            List<String> headers = new ArrayList<>(headerMap.values());
            List<String> fields = new ArrayList<>(headerMap.keySet());
            for (int i = 0; i < headers.size(); i++) {
                headerRow.createCell(i).setCellValue(headers.get(i));
            }

            // 数据行
            for (int r = 0; r < data.size(); r++) {
                org.apache.poi.xssf.usermodel.XSSFRow row = sheet.createRow(r + 1);
                Map<String, Object> rowData = data.get(r);
                for (int c = 0; c < fields.size(); c++) {
                    Object val = rowData.get(fields.get(c));
                    if (val instanceof BigDecimal) {
                        row.createCell(c).setCellValue(((BigDecimal) val).doubleValue());
                    } else if (val instanceof Number) {
                        row.createCell(c).setCellValue(((Number) val).doubleValue());
                    } else {
                        row.createCell(c).setCellValue(val != null ? val.toString() : "");
                    }
                }
            }

            workbook.write(response.getOutputStream());
            workbook.close();
        } catch (Exception e) {
            logger.error("自定义报表导出失败", e);
        }
    }

    // ===== 工具方法 =====

    private Map<Long, Long> getContractProjectMap() {
        List<HzContract> contracts = contractMapper.selectList(
            new LambdaQueryWrapper<HzContract>()
                .select(HzContract::getContractId, HzContract::getProjectId)
                .eq(HzContract::getDelFlag, "0")
                .isNotNull(HzContract::getProjectId)
        );
        Map<Long, Long> map = new HashMap<>();
        for (HzContract c : contracts) {
            if (c.getContractId() != null && c.getProjectId() != null) {
                map.put(c.getContractId(), c.getProjectId());
            }
        }
        return map;
    }

    private Map<Long, String> getProjectNameMap() {
        List<HzProject> projects = projectMapper.selectList(
            new LambdaQueryWrapper<HzProject>().eq(HzProject::getDelFlag, "0")
        );
        return projects.stream().collect(Collectors.toMap(HzProject::getProjectId, HzProject::getProjectName, (a, b) -> a));
    }

    private TableDataInfo getDataTable(List<?> list, List<?> originalList) {
        TableDataInfo rspData = new TableDataInfo();
        rspData.setCode(200);
        rspData.setMsg("查询成功");
        rspData.setRows(list);
        rspData.setTotal(list.size());
        return rspData;
    }

    private LocalDate getStartDate(String periodType, String periodValue) {
        if (periodValue == null || periodValue.isEmpty()) {
            periodValue = getDefaultPeriodValue(periodType);
        }
        switch (periodType) {
            case "day":
                return LocalDate.parse(periodValue);
            case "week":
                // 格式: 2026-W20
                String[] wp = periodValue.split("-W");
                int year = Integer.parseInt(wp[0]);
                int week = Integer.parseInt(wp[1]);
                return LocalDate.of(year, 1, 1)
                    .with(WeekFields.ISO.weekOfYear(), week)
                    .with(DayOfWeek.MONDAY);
            case "month":
                return LocalDate.parse(periodValue + "-01");
            case "year":
                return LocalDate.parse(periodValue + "-01-01");
            default:
                return LocalDate.now().withDayOfMonth(1);
        }
    }

    private LocalDate getEndDate(String periodType, String periodValue) {
        if (periodValue == null || periodValue.isEmpty()) {
            periodValue = getDefaultPeriodValue(periodType);
        }
        switch (periodType) {
            case "day":
                return LocalDate.parse(periodValue);
            case "week":
                String[] wp = periodValue.split("-W");
                int year = Integer.parseInt(wp[0]);
                int week = Integer.parseInt(wp[1]);
                return LocalDate.of(year, 1, 1)
                    .with(WeekFields.ISO.weekOfYear(), week)
                    .with(DayOfWeek.SUNDAY);
            case "month":
                LocalDate first = LocalDate.parse(periodValue + "-01");
                return first.plusMonths(1).minusDays(1);
            case "year":
                return LocalDate.parse(periodValue + "-12-31");
            default:
                return LocalDate.now();
        }
    }

    private String getDefaultPeriodValue(String periodType) {
        LocalDate now = LocalDate.now();
        switch (periodType) {
            case "day": return now.toString();
            case "week": return now.getYear() + "-W" + now.get(WeekFields.ISO.weekOfYear());
            case "month": return now.format(DateTimeFormatter.ofPattern("yyyy-MM"));
            case "year": return String.valueOf(now.getYear());
            default: return now.format(DateTimeFormatter.ofPattern("yyyy-MM"));
        }
    }

    private String billTypeText(String type) {
        if (type == null) return "其他";
        switch (type) {
            case "1": return "押金";
            case "2": return "租金";
            case "3": return "水费";
            case "4": return "电费";
            case "5": return "燃气费";
            case "6": return "物业费";
            case "7": return "其他";
            default: return "其他";
        }
    }

    /** 支付方式中文化映射 */
    private String payMethodText(String method) {
        if (method == null || method.isEmpty()) return "-";
        // 历史导入数据用代码值，新数据用英文标识
        switch (method) {
            case "wechat":
            case "WECHAT":
            case "wxpay":
            case "2":
                return "微信支付";
            case "alipay":
            case "ALIPAY":
            case "1":
                return "支付宝";
            case "cash":
            case "3":
                return "现金";
            case "bank":
            case "transfer":
            case "4":
                return "银行转账";
            case "offline":
            case "5":
                return "线下支付";
            default:
                return method;
        }
    }

    /** 根据账单的 houseId 批量查询房源完整地址（项目-楼栋-单元-楼层-房号） */
    private Map<Long, String> getHouseAddressMap(List<HzBill> bills) {
        Map<Long, String> result = new HashMap<>();
        if (bills == null || bills.isEmpty()) return result;
        Set<Long> houseIds = bills.stream()
            .map(HzBill::getHouseId)
            .filter(Objects::nonNull)
            .collect(Collectors.toSet());
        if (houseIds.isEmpty()) return result;

        List<HzHouse> houses = houseMapper.selectList(
            new LambdaQueryWrapper<HzHouse>().in(HzHouse::getHouseId, houseIds)
        );
        if (houses.isEmpty()) return result;

        Set<Long> buildingIds = houses.stream().map(HzHouse::getBuildingId).filter(Objects::nonNull).collect(Collectors.toSet());
        Set<Long> unitIds = houses.stream().map(HzHouse::getUnitId).filter(Objects::nonNull).collect(Collectors.toSet());

        Map<Long, String> buildingNameMap = new HashMap<>();
        if (!buildingIds.isEmpty()) {
            List<HzBuilding> buildings = buildingMapper.selectList(
                new LambdaQueryWrapper<HzBuilding>().in(HzBuilding::getBuildingId, buildingIds)
            );
            for (HzBuilding b : buildings) {
                buildingNameMap.put(b.getBuildingId(), b.getBuildingName());
            }
        }
        Map<Long, String> unitNameMap = new HashMap<>();
        if (!unitIds.isEmpty()) {
            List<HzUnit> units = unitMapper.selectList(
                new LambdaQueryWrapper<HzUnit>().in(HzUnit::getUnitId, unitIds)
            );
            for (HzUnit u : units) {
                unitNameMap.put(u.getUnitId(), u.getUnitName());
            }
        }

        for (HzHouse h : houses) {
            StringBuilder sb = new StringBuilder();
            String bn = buildingNameMap.get(h.getBuildingId());
            String un = unitNameMap.get(h.getUnitId());
            if (bn != null && !bn.isEmpty()) sb.append(bn);
            if (un != null && !un.isEmpty()) {
                if (sb.length() > 0) sb.append("-");
                sb.append(un);
            }
            if (h.getFloor() != null) {
                if (sb.length() > 0) sb.append("-");
                sb.append(h.getFloor()).append("层");
            }
            if (h.getHouseNo() != null && !h.getHouseNo().isEmpty()) {
                if (sb.length() > 0) sb.append("-");
                sb.append(h.getHouseNo());
            }
            result.put(h.getHouseId(), sb.length() > 0 ? sb.toString() : (h.getHouseCode() != null ? h.getHouseCode() : "-"));
        }
        return result;
    }

    private String getDimensionLabel(String dim) {
        switch (dim) {
            case "projectName": return "项目名称";
            case "month": return "月份";
            case "billType": return "账单类型";
            default: return dim;
        }
    }

    private String getMetricLabel(String metric) {
        switch (metric) {
            case "receivableAmount": return "应收金额";
            case "receivedAmount": return "实收金额";
            case "overdueAmount": return "逾期金额";
            case "billCount": return "账单数";
            case "paidCount": return "已付数";
            case "collectionRate": return "收款率(%)";
            default: return metric;
        }
    }
}
