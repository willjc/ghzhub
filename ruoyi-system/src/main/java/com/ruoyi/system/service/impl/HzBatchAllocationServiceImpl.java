package com.ruoyi.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.system.domain.HzBatchAllocation;
import com.ruoyi.system.domain.HzBatchHouse;
import com.ruoyi.system.domain.HzBatchTenant;
import com.ruoyi.system.domain.HzHouse;
import com.ruoyi.system.domain.HzProject;
import com.ruoyi.system.domain.HzBuilding;
import com.ruoyi.system.domain.HzUnit;
import com.ruoyi.system.mapper.HzBatchAllocationMapper;
import com.ruoyi.system.mapper.HzBatchHouseMapper;
import com.ruoyi.system.mapper.HzBatchTenantMapper;
import com.ruoyi.system.mapper.HzHouseMapper;
import com.ruoyi.system.mapper.HzProjectMapper;
import com.ruoyi.system.mapper.HzBuildingMapper;
import com.ruoyi.system.mapper.HzUnitMapper;
import com.ruoyi.system.service.IHzBatchAllocationService;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 配租批次Service业务层处理
 *
 * @author ruoyi
 */
@Service
public class HzBatchAllocationServiceImpl extends ServiceImpl<HzBatchAllocationMapper, HzBatchAllocation> implements IHzBatchAllocationService {

    @Autowired
    private HzBatchAllocationMapper batchAllocationMapper;

    @Autowired
    private HzBatchHouseMapper batchHouseMapper;

    @Autowired
    private HzBatchTenantMapper batchTenantMapper;

    @Autowired
    private HzHouseMapper houseMapper;

    @Autowired
    private HzProjectMapper projectMapper;

    @Autowired
    private HzBuildingMapper buildingMapper;

    @Autowired
    private HzUnitMapper unitMapper;

    @Override
    public List<HzBatchAllocation> selectBatchAllocationList(HzBatchAllocation batch) {
        return batchAllocationMapper.selectBatchAllocationList(batch);
    }

    @Override
    public HzBatchAllocation selectBatchAllocationById(Long batchId) {
        return this.getById(batchId);
    }

    @Override
    @Transactional
    public int insertBatchAllocation(HzBatchAllocation batch) {
        // 生成批次编号
        if (StringUtils.isEmpty(batch.getBatchNo())) {
            batch.setBatchNo("BATCH" + System.currentTimeMillis());
        }
        batch.setDelFlag("0");
        batch.setApplyTime(new Date());

        if (batch.getApproveStatus() == null) {
            batch.setApproveStatus("0"); // 待审批
        }
        if (batch.getBatchStatus() == null) {
            batch.setBatchStatus("0"); // 进行中
        }
        if (batch.getHouseCount() == null) {
            batch.setHouseCount(0);
        }
        if (batch.getAllocatedCount() == null) {
            batch.setAllocatedCount(0);
        }

        return this.save(batch) ? 1 : 0;
    }

    @Override
    @Transactional
    public int updateBatchAllocation(HzBatchAllocation batch) {
        // 只有待审批和已拒绝的批次可以修改
        HzBatchAllocation old = this.getById(batch.getBatchId());
        if (old != null && ("0".equals(old.getApproveStatus()) || "2".equals(old.getApproveStatus()))) {
            return this.updateById(batch) ? 1 : 0;
        }
        return 0;
    }

    @Override
    @Transactional
    public int deleteBatchAllocationById(Long batchId) {
        // 使用 LambdaUpdateWrapper 逻辑删除批次
        LambdaUpdateWrapper<HzBatchAllocation> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(HzBatchAllocation::getBatchId, batchId)
                     .set(HzBatchAllocation::getDelFlag, "2");

        if (!this.update(updateWrapper)) {
            return 0;
        }

        // 同时逻辑删除关联的房源分配记录
        LambdaUpdateWrapper<HzBatchHouse> houseUpdateWrapper = new LambdaUpdateWrapper<>();
        houseUpdateWrapper.eq(HzBatchHouse::getBatchId, batchId)
                          .eq(HzBatchHouse::getDelFlag, "0")
                          .set(HzBatchHouse::getDelFlag, "2");
        batchHouseMapper.update(null, houseUpdateWrapper);

        // 同时逻辑删除关联的人员记录
        LambdaUpdateWrapper<HzBatchTenant> tenantUpdateWrapper = new LambdaUpdateWrapper<>();
        tenantUpdateWrapper.eq(HzBatchTenant::getBatchId, batchId)
                           .eq(HzBatchTenant::getDelFlag, "0")
                           .set(HzBatchTenant::getDelFlag, "2");
        batchTenantMapper.update(null, tenantUpdateWrapper);

        return 1;
    }

    @Override
    @Transactional
    public int deleteBatchAllocationByIds(Long[] batchIds) {
        int count = 0;
        for (Long batchId : batchIds) {
            count += deleteBatchAllocationById(batchId);
        }
        return count;
    }

    @Override
    @Transactional
    public int approveBatchAllocation(Long batchId, String approveStatus, String remark) {
        HzBatchAllocation batch = new HzBatchAllocation();
        batch.setBatchId(batchId);
        batch.setApproveStatus(approveStatus);
        batch.setApproveBy(SecurityUtils.getUsername());
        batch.setApproveTime(new Date());
        batch.setRemark(remark);

        // 如果审批通过,批次状态改为进行中，并将房源状态改为已预定
        if ("1".equals(approveStatus)) {
            batch.setBatchStatus("0");

            // 查询该批次的所有房源
            LambdaQueryWrapper<HzBatchHouse> houseWrapper = new LambdaQueryWrapper<>();
            houseWrapper.eq(HzBatchHouse::getBatchId, batchId);
            List<HzBatchHouse> batchHouses = batchHouseMapper.selectList(houseWrapper);

            // 更新房源状态为已预定(1)
            for (HzBatchHouse batchHouse : batchHouses) {
                HzHouse house = new HzHouse();
                house.setHouseId(batchHouse.getHouseId());
                house.setHouseStatus("1"); // 已预定
                houseMapper.updateById(house);
            }

            // 更新已分配数量 = 房源数量
            HzBatchAllocation oldBatch = this.getById(batchId);
            if (oldBatch != null) {
                batch.setAllocatedCount(oldBatch.getHouseCount());
            }
        }

        return this.updateById(batch) ? 1 : 0;
    }

    @Override
    @Transactional
    public int cancelBatchAllocation(Long batchId) {
        HzBatchAllocation batch = new HzBatchAllocation();
        batch.setBatchId(batchId);
        batch.setBatchStatus("2"); // 已作废
        return this.updateById(batch) ? 1 : 0;
    }

    @Override
    public void downloadTenantTemplate(HttpServletResponse response) {
        try {
            // 创建工作簿
            Workbook workbook = new XSSFWorkbook();
            Sheet sheet = workbook.createSheet("人员信息");

            // 创建标题行样式
            CellStyle headerStyle = workbook.createCellStyle();
            Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerFont.setFontHeightInPoints((short) 12);
            headerStyle.setFont(headerFont);
            headerStyle.setAlignment(HorizontalAlignment.CENTER);
            headerStyle.setVerticalAlignment(VerticalAlignment.CENTER);

            // 创建标题行
            Row headerRow = sheet.createRow(0);
            String[] headers = {"姓名", "身份证号", "手机号"};
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
                sheet.setColumnWidth(i, 20 * 256);
            }

            // 添加示例数据
            Row exampleRow = sheet.createRow(1);
            exampleRow.createCell(0).setCellValue("张三");
            exampleRow.createCell(1).setCellValue("410123199001011234");
            exampleRow.createCell(2).setCellValue("13800138000");

            // 设置响应头
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            response.setCharacterEncoding("utf-8");
            String fileName = URLEncoder.encode("人员导入模板", "UTF-8").replaceAll("\\+", "%20");
            response.setHeader("Content-disposition", "attachment;filename*=utf-8''" + fileName + ".xlsx");

            // 写入输出流
            workbook.write(response.getOutputStream());
            workbook.close();
        } catch (IOException e) {
            throw new RuntimeException("下载模板失败", e);
        }
    }

    @Override
    public List<Map<String, Object>> getAvailableHousesByProjects(String projectIds) {
        if (StringUtils.isEmpty(projectIds)) {
            return new ArrayList<>();
        }

        // 将项目ID字符串转为List
        List<Long> projectIdList = Arrays.stream(projectIds.split(","))
                .map(Long::valueOf)
                .collect(Collectors.toList());

        // 查询状态为空置(0)的房源
        LambdaQueryWrapper<HzHouse> wrapper = new LambdaQueryWrapper<>();
        wrapper.in(HzHouse::getProjectId, projectIdList)
                .eq(HzHouse::getHouseStatus, "0") // 0=空置
                .eq(HzHouse::getStatus, "0") // 0=正常
                .orderByAsc(HzHouse::getProjectId, HzHouse::getHouseCode);

        List<HzHouse> houseList = houseMapper.selectList(wrapper);

        // 转换为Map格式
        return houseList.stream().map(house -> {
            Map<String, Object> map = new HashMap<>();
            map.put("houseId", house.getHouseId());
            map.put("houseCode", house.getHouseCode());
            map.put("projectId", house.getProjectId());
            map.put("houseNo", house.getHouseNo());
            map.put("floor", house.getFloor());
            map.put("houseTypeName", house.getHouseTypeName());
            map.put("area", house.getArea());
            map.put("rentPrice", house.getRentPrice());
            // 获取项目名称
            if (house.getProjectId() != null) {
                HzProject project = projectMapper.selectById(house.getProjectId());
                if (project != null) {
                    map.put("projectName", project.getProjectName());
                }
            }
            // 获取楼栋名称
            if (house.getBuildingId() != null) {
                HzBuilding building = buildingMapper.selectById(house.getBuildingId());
                if (building != null) {
                    map.put("buildingName", building.getBuildingName());
                }
            }
            // 获取单元名称
            if (house.getUnitId() != null) {
                HzUnit unit = unitMapper.selectById(house.getUnitId());
                if (unit != null) {
                    map.put("unitName", unit.getUnitName());
                }
            }
            return map;
        }).collect(Collectors.toList());
    }

    @Override
    public List<Map<String, Object>> importTenants(MultipartFile file) throws Exception {
        List<Map<String, Object>> tenantList = new ArrayList<>();

        try (InputStream inputStream = file.getInputStream();
             Workbook workbook = WorkbookFactory.create(inputStream)) {

            Sheet sheet = workbook.getSheetAt(0);
            int lastRowNum = sheet.getLastRowNum();

            // 从第2行开始读取(第1行是标题)
            for (int i = 1; i <= lastRowNum; i++) {
                Row row = sheet.getRow(i);
                if (row == null) {
                    continue;
                }

                // 获取单元格值
                String tenantName = getCellValue(row.getCell(0));
                String idCard = getCellValue(row.getCell(1));
                String phone = getCellValue(row.getCell(2));

                // 验证必填字段
                if (StringUtils.isEmpty(tenantName) || StringUtils.isEmpty(idCard) || StringUtils.isEmpty(phone)) {
                    continue;
                }

                // 验证身份证号格式
                if (!isValidIdCard(idCard)) {
                    throw new Exception("第" + (i + 1) + "行：身份证号格式不正确");
                }

                // 验证手机号格式
                if (!isValidPhone(phone)) {
                    throw new Exception("第" + (i + 1) + "行：手机号格式不正确");
                }

                Map<String, Object> tenant = new HashMap<>();
                tenant.put("tenantName", tenantName);
                tenant.put("idCard", idCard);
                tenant.put("phone", phone);
                tenantList.add(tenant);
            }
        }

        return tenantList;
    }

    @Override
    @Transactional
    public int saveBatchAllocation(Map<String, Object> params) {
        // 解析参数
        @SuppressWarnings("unchecked")
        Map<String, Object> batchInfo = (Map<String, Object>) params.get("batchInfo");
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> houseList = (List<Map<String, Object>>) params.get("houseList");
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> tenantList = (List<Map<String, Object>>) params.get("tenantList");

        // 验证数量一致性
        if (houseList.size() != tenantList.size()) {
            throw new RuntimeException("房源数量和人员数量必须一致");
        }

        // 获取批次ID，判断是新增还是修改
        Long batchId = null;
        if (batchInfo.get("batchId") != null) {
            batchId = Long.valueOf(batchInfo.get("batchId").toString());
        }
        boolean isUpdate = (batchId != null);

        // 1. 保存或更新批次信息
        HzBatchAllocation batch = new HzBatchAllocation();
        if (isUpdate) {
            // 修改模式：设置ID，保留原有的批次编号和申请时间
            batch.setBatchId(batchId);
            HzBatchAllocation oldBatch = this.getById(batchId);
            if (oldBatch != null) {
                batch.setBatchNo(oldBatch.getBatchNo());
                batch.setApplyTime(oldBatch.getApplyTime());
                batch.setApproveStatus(oldBatch.getApproveStatus());
                batch.setBatchStatus(oldBatch.getBatchStatus());
            }
        } else {
            // 新增模式：生成批次编号
            batch.setBatchNo("BATCH" + System.currentTimeMillis());
            batch.setApplyTime(new Date());
            batch.setApproveStatus("0"); // 待审批
            batch.setBatchStatus("0"); // 进行中
        }

        batch.setBatchName((String) batchInfo.get("batchName"));
        batch.setTalentType((String) batchInfo.get("talentType"));
        batch.setProjectIds((String) batchInfo.get("projectIds"));
        batch.setHouseCount(houseList.size());
        // 保存时房源和人员已一一匹配，所以已分配数量 = 房源数量
        batch.setAllocatedCount(houseList.size());
        batch.setRemark((String) batchInfo.get("remark"));

        // 处理入驻日期
        if (batchInfo.get("entryStartDate") != null) {
            batch.setEntryStartDate(new Date((Long) batchInfo.get("entryStartDate")));
        }
        if (batchInfo.get("entryEndDate") != null) {
            batch.setEntryEndDate(new Date((Long) batchInfo.get("entryEndDate")));
        }

        batch.setDelFlag("0");

        // 保存或更新批次
        if (isUpdate) {
            if (!this.updateById(batch)) {
                throw new RuntimeException("更新批次信息失败");
            }
            // 删除原有的房源和人员分配记录
            LambdaQueryWrapper<HzBatchHouse> houseWrapper = new LambdaQueryWrapper<>();
            houseWrapper.eq(HzBatchHouse::getBatchId, batchId);
            batchHouseMapper.delete(houseWrapper);

            LambdaQueryWrapper<HzBatchTenant> tenantWrapper = new LambdaQueryWrapper<>();
            tenantWrapper.eq(HzBatchTenant::getBatchId, batchId);
            batchTenantMapper.delete(tenantWrapper);
        } else {
            if (!this.save(batch)) {
                throw new RuntimeException("保存批次信息失败");
            }
            batchId = batch.getBatchId();
        }

        // 2. 保存租户信息
        List<HzBatchTenant> tenants = new ArrayList<>();
        for (Map<String, Object> tenantMap : tenantList) {
            HzBatchTenant tenant = new HzBatchTenant();
            tenant.setBatchId(batchId);

            // 去除前后空格，防止数据异常
            String tenantName = (String) tenantMap.get("tenantName");
            String idCard = (String) tenantMap.get("idCard");
            String phone = (String) tenantMap.get("phone");

            tenant.setTenantName(tenantName != null ? tenantName.trim() : "");
            tenant.setIdCard(idCard != null ? idCard.trim() : "");
            tenant.setPhone(phone != null ? phone.trim() : "");
            tenant.setAllocationStatus("0"); // 未分配
            tenant.setDelFlag("0");
            tenants.add(tenant);
        }

        // 批量插入租户
        for (HzBatchTenant tenant : tenants) {
            batchTenantMapper.insert(tenant);
        }

        // 3. 保存房源和匹配关系
        for (int i = 0; i < houseList.size(); i++) {
            Map<String, Object> houseMap = houseList.get(i);
            HzBatchTenant tenant = tenants.get(i);

            HzBatchHouse batchHouse = new HzBatchHouse();
            batchHouse.setBatchId(batchId);
            batchHouse.setHouseId(Long.valueOf(houseMap.get("houseId").toString()));
            batchHouse.setHouseCode((String) houseMap.get("houseCode"));
            batchHouse.setTenantId(tenant.getId()); // 关联租户ID
            batchHouse.setAllocationStatus("1"); // 已分配
            batchHouse.setAllocationTime(new Date());
            batchHouse.setDelFlag("0");

            if (batchHouseMapper.insert(batchHouse) != 1) {
                throw new RuntimeException("保存房源分配信息失败");
            }

            // 更新租户的房源ID
            tenant.setHouseId(batchHouse.getHouseId());
            tenant.setAllocationStatus("1"); // 已分配
            tenant.setAllocationTime(new Date());
            batchTenantMapper.updateById(tenant);
        }

        return 1;
    }

    @Override
    public List<Map<String, Object>> getBatchHouses(Long batchId) {
        // 查询批次的房源分配记录
        LambdaQueryWrapper<HzBatchHouse> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(HzBatchHouse::getBatchId, batchId)
                .eq(HzBatchHouse::getDelFlag, "0")
                .orderByAsc(HzBatchHouse::getId);

        List<HzBatchHouse> batchHouseList = batchHouseMapper.selectList(wrapper);

        // 转换为Map格式并关联房源信息和人员信息
        return batchHouseList.stream().map(batchHouse -> {
            Map<String, Object> map = new HashMap<>();

            // 获取房源详细信息
            HzHouse house = houseMapper.selectById(batchHouse.getHouseId());
            if (house != null) {
                map.put("houseId", house.getHouseId());
                map.put("houseCode", house.getHouseCode());
                map.put("houseNo", house.getHouseNo());
                map.put("floor", house.getFloor());
                map.put("houseTypeName", house.getHouseTypeName());
                map.put("area", house.getArea());
                map.put("rentPrice", house.getRentPrice());
                // 获取项目名称
                if (house.getProjectId() != null) {
                    HzProject project = projectMapper.selectById(house.getProjectId());
                    if (project != null) {
                        map.put("projectName", project.getProjectName());
                    }
                }
            }

            // 获取关联的租户信息
            if (batchHouse.getTenantId() != null) {
                HzBatchTenant tenant = batchTenantMapper.selectById(batchHouse.getTenantId());
                if (tenant != null) {
                    map.put("tenantName", tenant.getTenantName());
                }
            }

            map.put("allocationStatus", batchHouse.getAllocationStatus());
            map.put("allocationTime", batchHouse.getAllocationTime());

            return map;
        }).collect(Collectors.toList());
    }

    @Override
    public List<Map<String, Object>> getBatchTenants(Long batchId) {
        // 查询批次的人员记录
        LambdaQueryWrapper<HzBatchTenant> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(HzBatchTenant::getBatchId, batchId)
                .eq(HzBatchTenant::getDelFlag, "0")
                .orderByAsc(HzBatchTenant::getId);

        List<HzBatchTenant> tenantList = batchTenantMapper.selectList(wrapper);

        // 转换为Map格式并关联房源信息
        return tenantList.stream().map(tenant -> {
            Map<String, Object> map = new HashMap<>();
            map.put("id", tenant.getId());
            map.put("tenantName", tenant.getTenantName());
            map.put("idCard", tenant.getIdCard());
            map.put("phone", tenant.getPhone());
            map.put("workNo", tenant.getWorkNo());
            map.put("deptName", tenant.getDeptName());

            // 获取分配的房源信息
            if (tenant.getHouseId() != null) {
                HzHouse house = houseMapper.selectById(tenant.getHouseId());
                if (house != null) {
                    map.put("houseNo", house.getHouseNo());
                }
            }

            map.put("allocationStatus", tenant.getAllocationStatus());
            map.put("allocationTime", tenant.getAllocationTime());

            return map;
        }).collect(Collectors.toList());
    }

    /**
     * 获取单元格值
     */
    private String getCellValue(Cell cell) {
        if (cell == null) {
            return "";
        }

        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue().trim();
            case NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {
                    return cell.getDateCellValue().toString();
                } else {
                    return String.valueOf((long) cell.getNumericCellValue());
                }
            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());
            case FORMULA:
                return cell.getCellFormula();
            default:
                return "";
        }
    }

    /**
     * 验证身份证号
     */
    private boolean isValidIdCard(String idCard) {
        if (StringUtils.isEmpty(idCard)) {
            return false;
        }
        // 15位或18位
        return idCard.matches("^[1-9]\\d{5}(18|19|20)\\d{2}(0[1-9]|1[0-2])(0[1-9]|[12]\\d|3[01])\\d{3}[\\dXx]$")
                || idCard.matches("^[1-9]\\d{5}\\d{2}(0[1-9]|1[0-2])(0[1-9]|[12]\\d|3[01])\\d{3}$");
    }

    /**
     * 验证手机号
     */
    private boolean isValidPhone(String phone) {
        if (StringUtils.isEmpty(phone)) {
            return false;
        }
        return phone.matches("^1[3-9]\\d{9}$");
    }
}
