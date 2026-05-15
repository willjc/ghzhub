package com.ruoyi.web.controller.system;

import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.system.domain.HzBill;
import com.ruoyi.system.domain.HzContract;
import com.ruoyi.system.mapper.HzBillMapper;
import com.ruoyi.system.mapper.HzContractMapper;
import com.ruoyi.system.mapper.HzProjectMapper;
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

        // 查询所有项目
        List<HzProject> projects = projectMapper.selectList(
            new LambdaQueryWrapper<HzProject>().eq(HzProject::getDelFlag, "0")
        );

        // 查询时间范围内的所有账单（排除押金 bill_type=1）
        LambdaQueryWrapper<HzBill> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(HzBill::getDelFlag, "0")
               .ne(HzBill::getBillType, "1") // 排除押金
               .ge(HzBill::getBillDate, startDate.toString())
               .le(HzBill::getBillDate, endDate.toString());
        if (projectId != null) {
            // 需要通过contract获取对应projectId的bill，这里用子查询
            wrapper.inSql(HzBill::getContractId,
                "SELECT contract_id FROM hz_contract WHERE project_id = " + projectId + " AND del_flag = '0'");
        }
        List<HzBill> bills = billMapper.selectList(wrapper);

        // 获取 contract_id -> project_id 映射
        Map<Long, Long> contractProjectMap = getContractProjectMap();

        // 按项目分组统计
        Map<Long, List<HzBill>> groupByProject = bills.stream()
            .collect(Collectors.groupingBy(b -> contractProjectMap.getOrDefault(b.getContractId(), 0L)));

        List<Map<String, Object>> resultList = new ArrayList<>();
        BigDecimal totalReceivable = BigDecimal.ZERO;
        BigDecimal totalReceived = BigDecimal.ZERO;
        BigDecimal totalOverdue = BigDecimal.ZERO;

        for (HzProject project : projects) {
            if (projectId != null && !project.getProjectId().equals(projectId)) continue;

            List<HzBill> projectBills = groupByProject.getOrDefault(project.getProjectId(), Collections.emptyList());
            if (projectBills.isEmpty()) continue;

            BigDecimal receivable = projectBills.stream()
                .map(HzBill::getBillAmount)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

            BigDecimal received = projectBills.stream()
                .filter(b -> "1".equals(b.getBillStatus()))
                .map(b -> b.getPaidAmount() != null ? b.getPaidAmount() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

            BigDecimal overdue = projectBills.stream()
                .filter(b -> "0".equals(b.getBillStatus()) || "3".equals(b.getBillStatus()))
                .filter(b -> b.getDueDate() != null && LocalDate.parse(b.getDueDate()).isBefore(LocalDate.now()))
                .map(b -> b.getBillAmount().subtract(b.getPaidAmount() != null ? b.getPaidAmount() : BigDecimal.ZERO))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

            int collectionRate = receivable.compareTo(BigDecimal.ZERO) > 0
                ? received.multiply(BigDecimal.valueOf(100)).divide(receivable, 0, RoundingMode.HALF_UP).intValue()
                : 0;

            Map<String, Object> item = new LinkedHashMap<>();
            item.put("projectId", project.getProjectId());
            item.put("projectName", project.getProjectName());
            item.put("receivableAmount", receivable);
            item.put("receivedAmount", received);
            item.put("overdueAmount", overdue);
            item.put("collectionRate", collectionRate);
            item.put("status", collectionRate >= 90 ? "normal" : collectionRate >= 80 ? "warning" : "error");
            resultList.add(item);

            totalReceivable = totalReceivable.add(receivable);
            totalReceived = totalReceived.add(received);
            totalOverdue = totalOverdue.add(overdue);
        }

        int totalRate = totalReceivable.compareTo(BigDecimal.ZERO) > 0
            ? totalReceived.multiply(BigDecimal.valueOf(100)).divide(totalReceivable, 0, RoundingMode.HALF_UP).intValue()
            : 0;

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("summary", Map.of(
            "totalReceivable", totalReceivable,
            "totalReceived", totalReceived,
            "totalOverdue", totalOverdue,
            "collectionRate", totalRate
        ));
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

        // 补充项目名称
        Map<Long, Long> contractProjectMap = getContractProjectMap();
        Map<Long, String> projectNameMap = getProjectNameMap();

        List<Map<String, Object>> detailList = bills.stream().map(b -> {
            Map<String, Object> item = new LinkedHashMap<>();
            item.put("billId", b.getBillId());
            item.put("billNo", b.getBillNo());
            item.put("tenantName", b.getTenantName());
            item.put("houseCode", b.getHouseCode());
            Long pId = contractProjectMap.getOrDefault(b.getContractId(), 0L);
            item.put("projectName", projectNameMap.getOrDefault(pId, "-"));
            item.put("billType", b.getBillType());
            item.put("billTypeText", billTypeText(b.getBillType()));
            item.put("billAmount", b.getBillAmount());
            item.put("paidAmount", b.getPaidAmount());
            item.put("payTime", b.getPayTime());
            item.put("payMethod", b.getPayMethod());
            item.put("transactionNo", b.getTransactionNo());
            item.put("billPeriod", b.getBillPeriod());
            return item;
        }).collect(Collectors.toList());

        return getDataTable(detailList, bills);
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

        // 查询账单
        LambdaQueryWrapper<HzBill> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(HzBill::getDelFlag, "0")
               .ne(HzBill::getBillType, "1")
               .ge(HzBill::getBillDate, start.toString())
               .le(HzBill::getBillDate, end.toString());

        if (projectIds != null && !projectIds.isEmpty()) {
            String inSql = projectIds.stream().map(String::valueOf).collect(Collectors.joining(","));
            wrapper.inSql(HzBill::getContractId,
                "SELECT contract_id FROM hz_contract WHERE project_id IN (" + inSql + ") AND del_flag = '0'");
        }

        List<HzBill> bills = billMapper.selectList(wrapper);

        // 获取映射
        Map<Long, Long> contractProjectMap = getContractProjectMap();
        Map<Long, String> projectNameMap = getProjectNameMap();

        // 按维度分组
        Map<String, List<HzBill>> grouped = bills.stream().collect(Collectors.groupingBy(b -> {
            StringBuilder key = new StringBuilder();
            for (String dim : dimensions) {
                if (key.length() > 0) key.append("|");
                switch (dim) {
                    case "projectName":
                        Long pId = contractProjectMap.getOrDefault(b.getContractId(), 0L);
                        key.append(projectNameMap.getOrDefault(pId, "未知"));
                        break;
                    case "month":
                        key.append(b.getBillDate() != null ? b.getBillDate().substring(0, 7) : "未知");
                        break;
                    case "billType":
                        key.append(billTypeText(b.getBillType()));
                        break;
                    default:
                        key.append("-");
                }
            }
            return key.toString();
        }));

        // 聚合
        List<Map<String, Object>> resultList = new ArrayList<>();
        for (Map.Entry<String, List<HzBill>> entry : grouped.entrySet()) {
            String[] keys = entry.getKey().split("\\|", -1);
            List<HzBill> group = entry.getValue();

            Map<String, Object> row = new LinkedHashMap<>();
            for (int i = 0; i < dimensions.size() && i < keys.length; i++) {
                row.put(dimensions.get(i), keys[i]);
            }

            // 计算指标
            for (String metric : metrics) {
                switch (metric) {
                    case "receivableAmount":
                        row.put(metric, group.stream()
                            .map(HzBill::getBillAmount).filter(Objects::nonNull)
                            .reduce(BigDecimal.ZERO, BigDecimal::add));
                        break;
                    case "receivedAmount":
                        row.put(metric, group.stream()
                            .filter(b -> "1".equals(b.getBillStatus()))
                            .map(b -> b.getPaidAmount() != null ? b.getPaidAmount() : BigDecimal.ZERO)
                            .reduce(BigDecimal.ZERO, BigDecimal::add));
                        break;
                    case "overdueAmount":
                        row.put(metric, group.stream()
                            .filter(b -> "0".equals(b.getBillStatus()) || "3".equals(b.getBillStatus()))
                            .filter(b -> b.getDueDate() != null && LocalDate.parse(b.getDueDate()).isBefore(LocalDate.now()))
                            .map(b -> b.getBillAmount().subtract(b.getPaidAmount() != null ? b.getPaidAmount() : BigDecimal.ZERO))
                            .reduce(BigDecimal.ZERO, BigDecimal::add));
                        break;
                    case "billCount":
                        row.put(metric, group.size());
                        break;
                    case "paidCount":
                        row.put(metric, group.stream().filter(b -> "1".equals(b.getBillStatus())).count());
                        break;
                    case "collectionRate":
                        BigDecimal recv = group.stream()
                            .map(HzBill::getBillAmount).filter(Objects::nonNull)
                            .reduce(BigDecimal.ZERO, BigDecimal::add);
                        BigDecimal paid = group.stream()
                            .filter(b -> "1".equals(b.getBillStatus()))
                            .map(b -> b.getPaidAmount() != null ? b.getPaidAmount() : BigDecimal.ZERO)
                            .reduce(BigDecimal.ZERO, BigDecimal::add);
                        int rate = recv.compareTo(BigDecimal.ZERO) > 0
                            ? paid.multiply(BigDecimal.valueOf(100)).divide(recv, 0, RoundingMode.HALF_UP).intValue() : 0;
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
