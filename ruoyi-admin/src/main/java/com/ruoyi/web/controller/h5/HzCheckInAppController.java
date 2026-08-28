package com.ruoyi.web.controller.h5;

import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.SecurityUtils;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.ruoyi.system.domain.HzBill;
import com.ruoyi.system.domain.HzCheckIn;
import com.ruoyi.system.domain.HzContract;
import com.ruoyi.system.domain.HzHouse;
import com.ruoyi.system.mapper.HzBillMapper;
import com.ruoyi.system.mapper.HzCheckInMapper;
import com.ruoyi.system.service.IHzCheckInService;
import com.ruoyi.system.service.IHzHouseService;
import com.ruoyi.system.mapper.HzUserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.ruoyi.system.domain.HzCoTenant;
import com.ruoyi.system.service.IHzCoTenantService;
import com.ruoyi.system.service.IHzContractService;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * H5用户端 - 入住办理API
 */
@RestController
@RequestMapping("/h5/app/checkin")
public class HzCheckInAppController extends BaseController {

    @Autowired
    private IHzCheckInService checkInService;

    @Autowired
    private IHzHouseService houseService;

    @Autowired
    private HzUserMapper userMapper;

    @Autowired
    private HzCheckInMapper checkInMapper;

    @Autowired
    private IHzCoTenantService coTenantService;

    @Autowired
    private IHzContractService contractService;

    @Autowired
    private HzBillMapper billMapper;

    @Autowired
    private com.ruoyi.system.service.IHzProjectService projectService;

    @Autowired
    private com.ruoyi.system.service.ISysConfigService sysConfigService;

    /**
     * 获取用户的房源列表（用于用户端"我的房源"页面）
     *
     * @param tenantId 租户ID
     * @return 房源列表
     */
    @GetMapping("/mylistings/{tenantId}")
    public AjaxResult getMyListings(@PathVariable Long tenantId) {
        try {
            SecurityUtils.requireCurrentHzUser(tenantId);
            List<Map<String, Object>> listings = checkInMapper.selectMyListingsByTenantId(tenantId);

            // 组装前端需要的数据格式
            List<Map<String, Object>> result = new java.util.ArrayList<>();
            for (Map<String, Object> item : listings) {
                Map<String, Object> listing = new HashMap<>();

                // 基本信息
                listing.put("id", item.get("record_id"));
                listing.put("recordId", item.get("record_id"));
                listing.put("houseId", item.get("house_id"));
                listing.put("projectId", item.get("project_id"));

                // 项目名称
                listing.put("title", item.get("project_name"));

                // 标签（项目配套设施）
                String facilities = (String) item.get("facilities");
                if (facilities != null && !facilities.isEmpty()) {
                    listing.put("tags", facilities.split(","));
                } else {
                    listing.put("tags", new String[]{});
                }

                // 详情：朝向-面积-楼栋-房号
                StringBuilder detail = new StringBuilder();
                String orientation = (String) item.get("orientation");
                if (orientation != null && !orientation.isEmpty()) {
                    detail.append(orientation).append("-");
                }

                BigDecimal area = (BigDecimal) item.get("area");
                if (area != null) {
                    detail.append(area.intValue()).append("㎡-");
                }

                Integer floor = (Integer) item.get("floor");
                String houseNo = (String) item.get("house_no");
                if (floor != null && houseNo != null) {
                    detail.append(floor).append("号楼").append(houseNo);
                }

                listing.put("detail", detail.toString());

                // 状态判断（基于合同状态）
                Integer isCheckedOut = ((Number) item.get("is_checked_out")).intValue();
                String contractStatus = (String) item.get("checkin_status");

                String status;
                String statusText;
                if (isCheckedOut == 1) {
                    // 有退租记录
                    status = "checked_out";
                    statusText = "已退租";
                } else {
                    // 根据合同状态判断
                    switch (contractStatus) {
                        case "3":
                            status = "renting";
                            statusText = "在租中";
                            break;
                        case "2":
                            status = "pending";
                            statusText = "待生效";
                            break;
                        case "4":
                            status = "confirming";
                            statusText = "即将到期";
                            break;
                        case "5":
                            status = "checked_out";
                            statusText = "已解约";
                            break;
                        default:
                            status = "unknown";
                            statusText = "未知";
                            break;
                    }
                }

                listing.put("status", status);
                listing.put("statusText", statusText);

                // 日期信息
                listing.put("checkinDate", item.get("checkin_date"));
                listing.put("startDate", item.get("start_date"));
                listing.put("endDate", item.get("end_date"));

                // 图片
                listing.put("image", item.get("cover_image"));

                result.add(listing);
            }

            return success(result);
        } catch (Exception e) {
            logger.error("获取我的房源列表失败", e);
            return error("获取失败：" + e.getMessage());
        }
    }

    /**
     * 获取用户的入住单列表
     *
     * @param tenantId 租户ID
     * @param projectType 项目类型（可选，1:人才公寓 2:保租房 3:市场租赁），为空则不过滤
     * @return 入住单列表
     */
    @GetMapping("/list/{tenantId}")
    public AjaxResult getCheckInList(@PathVariable Long tenantId,
                                     @RequestParam(required = false) String projectType) {
        SecurityUtils.requireCurrentHzUser(tenantId);
        List<HzCheckIn> list = checkInService.selectCheckInListByTenantId(tenantId, projectType);

        return success(list);
    }

    /**
     * 获取用户的已入住确认的入住单列表 (用于退租/续租页面)
     * 兼容老数据迁移场景：无入住记录时从合同表兜底查询
     *
     * @param tenantId 租户ID
     * @param type     页面类型：renew=续租(仅履行中), checkout=退租(已签署/履行中，已到期不允许退租)
     * @return 已入住确认的入住单/合同列表
     */
    @GetMapping("/confirmed/{tenantId}")
    public AjaxResult getConfirmedCheckInList(@PathVariable Long tenantId,
                                              @RequestParam(required = false) String type) {
        SecurityUtils.requireCurrentHzUser(tenantId);
        // 用合同ID去重，避免重复
        java.util.Set<Long> addedContractIds = new java.util.HashSet<>();
        List<Map<String, Object>> result = new java.util.ArrayList<>();

        // 方式1：查询该用户所有已入住确认的入住单 (status='4' 且 del_flag='0')
        List<HzCheckIn> list = checkInService.selectConfirmedCheckInListByTenantId(tenantId);

        for (HzCheckIn checkIn : list) {
            if (checkIn.getContractId() == null || addedContractIds.contains(checkIn.getContractId())) {
                continue;
            }

            // 获取合同信息
            com.ruoyi.system.domain.HzContract contract = null;
            if (checkIn.getContractId() != null) {
                contract = contractService.selectContractById(checkIn.getContractId());
            }

            // 续租场景：只保留履行中的合同
            if ("renew".equals(type) && (contract == null || !"3".equals(contract.getContractStatus()))) {
                continue;
            }

            Map<String, Object> item = new HashMap<>();
            item.put("recordId", checkIn.getRecordId());
            item.put("contractId", checkIn.getContractId());
            item.put("contractNo", contract != null ? contract.getContractNo() : "");
            item.put("isRenewed", contract != null ? contract.getIsRenewed() : "0");

            // 优先从合同/项目/房源表获取可读信息
            fillReadableInfo(item, contract, checkIn.getRemark());

            addedContractIds.add(checkIn.getContractId());
            result.add(item);
        }

        // 方式2：通过合同表补充（兼容老数据迁移场景，无入住记录但有已签署合同）
        List<com.ruoyi.system.domain.HzContract> allContracts = contractService.selectContractListByTenantId(tenantId);
        List<com.ruoyi.system.domain.HzContract> activeContracts = allContracts.stream()
                .filter(c -> {
                    String status = c.getContractStatus();
                    if ("renew".equals(type)) {
                        // 续租：只保留履行中的合同
                        return "3".equals(status);
                    }
                    // 退租/默认：保留已签署、履行中，排除已到期(4)、已解约(5)、超时失效(6)
                    return "2".equals(status) || "3".equals(status);
                })
                .toList();

        for (com.ruoyi.system.domain.HzContract contract : activeContracts) {
            if (addedContractIds.contains(contract.getContractId())) {
                continue; // 已通过入住记录添加，跳过
            }

            Map<String, Object> item = new HashMap<>();
            item.put("contractId", contract.getContractId());
            item.put("contractNo", contract.getContractNo());
            item.put("isRenewed", contract.getIsRenewed() != null ? contract.getIsRenewed() : "0");

            // 兼容老数据：查找合同对应的入住记录，补充 recordId
            if (contract.getContractId() != null) {
                LambdaQueryWrapper<HzCheckIn> wrapper = new LambdaQueryWrapper<>();
                wrapper.eq(HzCheckIn::getContractId, contract.getContractId());
                wrapper.eq(HzCheckIn::getDelFlag, "0");
                wrapper.orderByDesc(HzCheckIn::getCreateTime);
                wrapper.last("LIMIT 1");
                List<HzCheckIn> checkIns = checkInMapper.selectList(wrapper);
                if (!checkIns.isEmpty()) {
                    item.put("recordId", checkIns.get(0).getRecordId());
                }
            }

            // 从合同/项目/房源表获取可读信息
            fillReadableInfo(item, contract, null);

            addedContractIds.add(contract.getContractId());
            result.add(item);
        }

        return success(result);
    }

    /**
     * 填充可读的房源信息（小区名、房间号、租期、租金、押金）
     * 优先从项目表/房源表获取真实数据，remark解析作为兜底
     *
     * @param item 要填充的Map
     * @param contract 合同对象（可能为null）
     * @param remark 入住记录备注（可能为null）
     */
    private void fillReadableInfo(Map<String, Object> item, com.ruoyi.system.domain.HzContract contract, String remark) {
        String community = "";
        String room = "";
        String rentPeriod = "";
        String rent = "";
        String deposit = "";

        // 先从remark中尝试解析
        if (remark != null && !remark.isEmpty()) {
            community = extractInfo(remark, "项目：");
            room = extractInfo(remark, "房间：");
            rentPeriod = extractInfo(remark, "租期：");
            rent = extractInfo(remark, "月租金：");
            deposit = extractInfo(remark, "押金：");
        }

        // 从合同/项目/房源表补充缺失的信息
        if (contract != null) {
            // 小区名：优先remark，其次从项目表查询
            if ((community == null || community.isEmpty()) && contract.getProjectId() != null) {
                com.ruoyi.system.domain.HzProject project = projectService.selectProjectById(contract.getProjectId());
                if (project != null && project.getProjectName() != null) {
                    community = project.getProjectName();
                }
            }

            // 房间号：优先remark，其次从房源表查询
            if ((room == null || room.isEmpty()) && contract.getHouseId() != null) {
                HzHouse house = houseService.selectHouseById(contract.getHouseId());
                if (house != null) {
                    // 拼接楼栋-房号
                    StringBuilder roomBuilder = new StringBuilder();
                    if (house.getBuildingName() != null && !house.getBuildingName().isEmpty()) {
                        roomBuilder.append(house.getBuildingName());
                    }
                    if (house.getHouseNo() != null && !house.getHouseNo().isEmpty()) {
                        roomBuilder.append(house.getHouseNo());
                    }
                    if (roomBuilder.length() > 0) {
                        room = roomBuilder.toString();
                    }
                }
            }

            // 租期：优先remark，其次从合同起止日期拼接
            if ((rentPeriod == null || rentPeriod.isEmpty()) && contract.getStartDate() != null && contract.getEndDate() != null) {
                rentPeriod = contract.getStartDate() + " 至 " + contract.getEndDate();
            }

            // 租金：优先remark，其次从合同表
            if ((rent == null || rent.isEmpty()) && contract.getRentPrice() != null) {
                rent = contract.getRentPrice().toString() + "元/月";
            }

            // 押金：优先remark，其次从合同表
            if ((deposit == null || deposit.isEmpty()) && contract.getDeposit() != null) {
                deposit = contract.getDeposit().toString() + "元";
            }
        }

        item.put("community", community);
        item.put("room", room);
        item.put("rentPeriod", rentPeriod);
        item.put("rent", rent);
        item.put("deposit", deposit);
    }

    /**
     * 获取入住单详情
     *
     * @param recordId 入住记录ID
     * @return 入住单详情
     */
    @GetMapping("/{recordId}")
    public AjaxResult getCheckInDetail(@PathVariable Long recordId) {
        HzCheckIn checkIn = checkInService.selectCheckInById(recordId);

        if (checkIn == null) {
            return error("入住单不存在");
        }
        if (!SecurityUtils.getHzUserId().equals(checkIn.getTenantId())) {
            return error("无权查看");
        }

        // 组装返回数据
        Map<String, Object> result = new HashMap<>();
        result.put("recordId", checkIn.getRecordId());
        result.put("checkinNo", checkIn.getCheckinNo());
        result.put("contractId", checkIn.getContractId());
        result.put("tenantId", checkIn.getTenantId());
        result.put("houseId", checkIn.getHouseId());
        result.put("checkinDate", checkIn.getCheckinDate());
        result.put("actualCheckinDate", checkIn.getActualCheckinDate());
        result.put("roommateInfo", checkIn.getRoommateInfo());
        result.put("emergencyContactName", checkIn.getEmergencyContactName());
        result.put("emergencyContactRelation", checkIn.getEmergencyContactRelation());
        result.put("emergencyContactPhone", checkIn.getEmergencyContactPhone());
        result.put("status", checkIn.getStatus());
        result.put("statusText", getStatusText(checkIn.getStatus()));

        // 兼容老数据：remark为空时，从合同/房源表构造remark
        String remark = checkIn.getRemark();
        if (remark == null || remark.isEmpty()) {
            if (checkIn.getContractId() != null) {
                com.ruoyi.system.domain.HzContract contract = contractService.selectContractById(checkIn.getContractId());
                Map<String, Object> infoMap = new HashMap<>();
                fillReadableInfo(infoMap, contract, null);
                StringBuilder sb = new StringBuilder();
                sb.append("项目：").append(infoMap.getOrDefault("community", "")).append("|");
                sb.append("房间：").append(infoMap.getOrDefault("room", "")).append("|");
                sb.append("租期：").append(infoMap.getOrDefault("rentPeriod", "")).append("|");
                sb.append("月租金：").append(infoMap.getOrDefault("rent", "")).append("|");
                sb.append("押金：").append(infoMap.getOrDefault("deposit", ""));
                remark = sb.toString();
            }
        }
        result.put("remark", remark);

        result.put("auditBy", checkIn.getAuditBy());
        result.put("auditTime", checkIn.getAuditTime());
        result.put("auditRemark", checkIn.getAuditRemark());
        // 添加时间字段
        result.put("createTime", checkIn.getCreateTime());
        result.put("checkinTime", checkIn.getCheckinTime());

        // 查询用户信息
        if (checkIn.getTenantId() != null) {
            com.ruoyi.system.domain.HzUser user = userMapper.selectById(checkIn.getTenantId());
            if (user != null) {
                result.put("userName", user.getRealName());
                result.put("userPhone", user.getPhone());
            }
        }

        // 补充合同的 signTime 与 startDate（小程序日期选择器需要据此限定可选范围）
        if (checkIn.getContractId() != null) {
            HzContract contract = contractService.selectContractById(checkIn.getContractId());
            if (contract != null) {
                result.put("signTime", contract.getSignTime());
                result.put("startDate", contract.getStartDate());
                result.put("endDate", contract.getEndDate());
                result.put("contractStatus", contract.getContractStatus());
            }
        }

        return success(result);
    }

    /**
     * 提交入住办理信息
     *
     * @param params 入住办理信息
     * @return 提交结果
     */
    @PostMapping("/submit")
    public AjaxResult submitCheckIn(@RequestBody Map<String, Object> params) {
        try {
            Long recordId = Long.parseLong(params.get("recordId").toString());

            // 查询入住单
            HzCheckIn checkIn = checkInService.selectCheckInById(recordId);
            if (checkIn == null) {
                return error("入住单不存在");
            }
            if (!SecurityUtils.getHzUserId().equals(checkIn.getTenantId())) {
                return error("无权操作");
            }

            // 检查状态
            if (!"0".equals(checkIn.getStatus())) {
                return error("该入住单已办理，无法重复提交");
            }

            // 二次校验：合同状态 + 押金/首期租金已收清 + 入住日期范围
            String denyMsg = validateCheckinSubmit(checkIn, params.get("actualCheckinDate"));
            if (denyMsg != null) {
                return error(denyMsg);
            }

            // 更新入住办理信息
            // 转换中文日期格式为标准日期格式
            String actualCheckinDate = params.get("actualCheckinDate") != null
                ? params.get("actualCheckinDate").toString()
                : null;
            if (actualCheckinDate != null && !actualCheckinDate.isEmpty()) {
                actualCheckinDate = convertChineseDateToDate(actualCheckinDate);
            }

            checkIn.setActualCheckinDate(actualCheckinDate);
            checkIn.setRoommateInfo(params.get("roommateInfo") != null ? params.get("roommateInfo").toString() : null);
            checkIn.setEmergencyContactName(params.get("emergencyContactName").toString());
            checkIn.setEmergencyContactRelation(params.get("emergencyContactRelation").toString());
            checkIn.setEmergencyContactPhone(params.get("emergencyContactPhone").toString());

            // 更新状态为待审核
            checkIn.setStatus("1");  // 1=待审核
            checkIn.setUpdateTime(DateUtils.getNowDate());

            int result = checkInService.updateCheckIn(checkIn);

            if (result > 0) {
                // 同步写入 hz_co_tenant 表（若有合住人信息）
                String roommateJson = checkIn.getRoommateInfo();
                if (roommateJson != null && !roommateJson.isEmpty()) {
                    try {
                        Gson gson = new Gson();
                        java.lang.reflect.Type listType = new TypeToken<List<Map<String, Object>>>(){}.getType();
                        List<Map<String, Object>> roommates = gson.fromJson(roommateJson, listType);
                        if (roommates != null) {
                            for (Map<String, Object> rm : roommates) {
                                String name = rm.get("name") != null ? rm.get("name").toString() : "";
                                if (name.isEmpty()) continue; // 没有姓名则跳过
                                HzCoTenant coTenant = new HzCoTenant();
                                coTenant.setContractId(checkIn.getContractId());
                                coTenant.setTenantName(name);
                                coTenant.setRelationship(rm.get("relation") != null ? rm.get("relation").toString() : "");
                                coTenant.setStatus("0"); // 待审核
                                coTenant.setCreateBy(checkIn.getTenantNickname() != null ? checkIn.getTenantNickname() : "");
                                coTenant.setCreateTime(DateUtils.getNowDate());
                                coTenantService.insertCoTenant(coTenant);
                            }
                        }
                    } catch (Exception ex) {
                        logger.warn("同步合住人到hz_co_tenant失败（不影响主流程）: {}", ex.getMessage());
                    }
                }
                return success("入住办理提交成功，等待管理员审核");
            } else {
                return error("提交失败");
            }

        } catch (Exception e) {
            logger.error("提交入住办理失败", e);
            return error("提交失败：" + e.getMessage());
        }
    }

    /**
     * 根据合同ID获取入住单
     *
     * @param contractId 合同ID
     * @return 入住单信息
     */
    @GetMapping("/contract/{contractId}")
    public AjaxResult getCheckInByContractId(@PathVariable Long contractId) {
        HzCheckIn checkIn = checkInService.selectCheckInByContractId(contractId);

        if (checkIn == null) {
            return error("未找到该合同的入住单");
        }
        if (!SecurityUtils.getHzUserId().equals(checkIn.getTenantId())) {
            return error("无权查看");
        }

        // 组装返回数据
        Map<String, Object> result = new HashMap<>();
        result.put("recordId", checkIn.getRecordId());
        result.put("checkinNo", checkIn.getCheckinNo());
        result.put("contractId", checkIn.getContractId());
        result.put("status", checkIn.getStatus());
        result.put("statusText", getStatusText(checkIn.getStatus()));
        result.put("checkinDate", checkIn.getCheckinDate());
        result.put("remark", checkIn.getRemark());

        return success(result);
    }

    /**
     * 将状态代码转换为文本
     */
    private String getStatusText(String status) {
        switch (status) {
            case "0": return "待办理";
            case "1": return "待审核";
            case "2": return "待入住确认";
            case "3": return "已拒绝";
            case "4": return "已入住确认";
            default: return "未知状态";
        }
    }

    /**
     * 获取入住确认信息（包含房源设施）
     *
     * @param recordId 入住记录ID
     * @return 入住确认信息
     */
    @GetMapping("/confirm/{recordId}")
    public AjaxResult getCheckInConfirmInfo(@PathVariable Long recordId) {
        try {
            // 查询入住单
            HzCheckIn checkIn = checkInService.selectCheckInById(recordId);
            if (checkIn == null) {
                return error("入住单不存在");
            }
            if (!SecurityUtils.getHzUserId().equals(checkIn.getTenantId())) {
                return error("无权查看");
            }

            // 检查状态
            if (!"2".equals(checkIn.getStatus())) {
                return error("当前状态无法进行入住确认");
            }

            // 查询房源信息（获取房间设施）
            HzHouse house = null;
            if (checkIn.getHouseId() != null) {
                house = houseService.selectHouseById(checkIn.getHouseId());
            }

            // 组装返回数据
            Map<String, Object> result = new HashMap<>();
            result.put("recordId", checkIn.getRecordId());
            result.put("checkinNo", checkIn.getCheckinNo());
            result.put("checkinDate", checkIn.getActualCheckinDate());
            result.put("roommateInfo", checkIn.getRoommateInfo());
            result.put("emergencyContactName", checkIn.getEmergencyContactName());
            result.put("emergencyContactRelation", checkIn.getEmergencyContactRelation());
            result.put("emergencyContactPhone", checkIn.getEmergencyContactPhone());
            result.put("remark", checkIn.getRemark());
            result.put("tenantId", checkIn.getTenantId());

            // 查询用户信息
            if (checkIn.getTenantId() != null) {
                com.ruoyi.system.domain.HzUser user = userMapper.selectById(checkIn.getTenantId());
                if (user != null) {
                    result.put("userName", user.getRealName());
                    result.put("userPhone", user.getPhone());
                }
            }

            // 添加时间字段
            result.put("createTime", checkIn.getCreateTime());      // 申请时间（入住单创建时间）
            result.put("checkinTime", checkIn.getCheckinTime());    // 用户确认时间

            // 房源设施信息
            if (house != null) {
                result.put("houseId", house.getHouseId());
                result.put("houseCode", house.getHouseNo());
                result.put("facilities", house.getFacilities());
                // 解析备注中的项目信息
                String remark = checkIn.getRemark();
                result.put("projectName", extractInfo(remark, "项目："));
                result.put("roomAddress", extractInfo(remark, "房间："));
            }

            return success(result);
        } catch (Exception e) {
            logger.error("获取入住确认信息失败", e);
            return error("获取信息失败：" + e.getMessage());
        }
    }

    /**
     * 提交入住确认（保存签字和设施）
     *
     * @param params 入住确认信息
     * @return 提交结果
     */
    @PostMapping("/confirm")
    public AjaxResult submitCheckInConfirm(@RequestBody Map<String, Object> params) {
        try {
            Long recordId = Long.parseLong(params.get("recordId").toString());

            // 查询入住单
            HzCheckIn checkIn = checkInService.selectCheckInById(recordId);
            if (checkIn == null) {
                return error("入住单不存在");
            }
            if (!SecurityUtils.getHzUserId().equals(checkIn.getTenantId())) {
                return error("无权操作");
            }

            // 检查状态
            if (!"2".equals(checkIn.getStatus())) {
                return error("当前状态无法进行入住确认");
            }

            // 处理签名图片 - 将 base64 转为图片并上传
            String signatureBase64 = params.get("signature") != null ? params.get("signature").toString() : null;
            String signatureUrl = "";
            if (signatureBase64 != null && signatureBase64.startsWith("data:image")) {
                try {
                    // 移除 data:image/png;base64, 前缀
                    String base64Data = signatureBase64.substring(signatureBase64.indexOf(",") + 1);
                    byte[] imageBytes = java.util.Base64.getDecoder().decode(base64Data);

                    // 生成文件名
                    String fileName = DateUtils.datePath() + "/" + com.ruoyi.common.utils.uuid.IdUtils.fastSimpleUUID() + ".png";

                    // 获取绝对路径
                    java.io.File file = com.ruoyi.common.utils.file.FileUploadUtils.getAbsoluteFile(
                        com.ruoyi.common.config.RuoYiConfig.getUploadPath(), fileName);

                    // 写入文件
                    java.nio.file.Files.write(file.toPath(), imageBytes);

                    // 返回相对路径
                    signatureUrl = com.ruoyi.common.utils.file.FileUploadUtils.getPathFileName(
                        com.ruoyi.common.config.RuoYiConfig.getUploadPath(), fileName);

                    logger.info("签字图片上传成功: {}", signatureUrl);
                } catch (Exception e) {
                    logger.error("上传签字图片失败", e);
                    return error("上传签字失败");
                }
            }

            // 更新入住确认信息
            checkIn.setTenantSignature(signatureUrl);
            checkIn.setCheckinTime(DateUtils.getTime());

            // 保存用户选择的配套设施（JSON格式）到独立字段
            if (params.get("selectedFacilities") != null) {
                String facilitiesJson = params.get("selectedFacilities").toString();
                checkIn.setConfirmedFacilities(facilitiesJson);
            }

            // 更新状态为已入住确认
            checkIn.setStatus("4");  // 4=已入住确认
            checkIn.setUpdateTime(DateUtils.getNowDate());

            int result = checkInService.updateCheckIn(checkIn);

            if (result > 0) {
                return success("入住确认成功");
            } else {
                return error("确认失败");
            }

        } catch (Exception e) {
            logger.error("提交入住确认失败", e);
            return error("提交失败：" + e.getMessage());
        }
    }

    /**
     * 取消入住申请
     *
     * @param recordId 入住记录ID
     * @return 取消结果
     */
    @PostMapping("/cancel/{recordId}")
    public AjaxResult cancelCheckIn(@PathVariable Long recordId) {
        try {
            // 查询入住单
            HzCheckIn checkIn = checkInService.selectCheckInById(recordId);
            if (checkIn == null) {
                return error("入住单不存在");
            }
            if (!SecurityUtils.getHzUserId().equals(checkIn.getTenantId())) {
                return error("无权操作");
            }

            // 检查状态 - 只有待审核状态(status='1')可以取消
            if (!"1".equals(checkIn.getStatus())) {
                return error("当前状态无法取消申请");
            }

            // 重置为待办理状态
            checkIn.setStatus("0");

            // 清空已填写的信息
            checkIn.setActualCheckinDate(null);
            checkIn.setRoommateInfo(null);
            checkIn.setEmergencyContactName(null);
            checkIn.setEmergencyContactRelation(null);
            checkIn.setEmergencyContactPhone(null);
            checkIn.setCheckinNo(null);

            // 更新时间
            checkIn.setUpdateTime(DateUtils.getNowDate());

            int result = checkInService.updateCheckIn(checkIn);

            if (result > 0) {
                return success("已成功取消申请");
            } else {
                return error("取消失败");
            }

        } catch (Exception e) {
            logger.error("取消入住申请失败", e);
            return error("取消失败：" + e.getMessage());
        }
    }

    /**
     * 从备注中提取信息
     */
    private String extractInfo(String remark, String key) {
        if (remark == null || key == null) {
            return "";
        }
        int index = remark.indexOf(key);
        if (index != -1) {
            int start = index + key.length();
            int end = remark.indexOf("|", start);
            if (end == -1) end = remark.length();
            return remark.substring(start, end).trim();
        }
        return "";
    }

    /**
     * 提交入住办理前的二次校验：
     * 1) 合同存在且状态为 已签署(2)/履行中(3)
     * 2) 押金（如有）必须全部已付清
     * 3) 首期租金（按 due_date 升序第一条）必须已付清
     * 4) 实际入住日期需在 [sign_time, sign_time+3 天]
     * 任一不满足返回拒绝原因，通过返回 null。
     */
    private String validateCheckinSubmit(HzCheckIn checkIn, Object rawCheckinDate) {
        Long contractId = checkIn.getContractId();
        if (contractId == null) {
            return "入住单未关联合同，无法提交办理";
        }
        HzContract contract = contractService.selectContractById(contractId);
        if (contract == null || !"0".equals(contract.getDelFlag())) {
            return "合同不存在或已删除，无法办理入住";
        }
        String cs = contract.getContractStatus();
        if (!"2".equals(cs) && !"3".equals(cs)) {
            return "合同当前状态已非「已签署/履行中」，无法办理入住";
        }
        // 押金账单
        List<HzBill> deposits = billMapper.selectList(new LambdaQueryWrapper<HzBill>()
                .eq(HzBill::getContractId, contractId)
                .eq(HzBill::getBillType, "1")
                .eq(HzBill::getDelFlag, "0"));
        for (HzBill b : deposits) {
            if (!"1".equals(b.getBillStatus())) {
                return "押金尚未收齐，请先完成支付再办理入住";
            }
        }
        // 首期租金
        HzBill firstRent = billMapper.selectOne(new LambdaQueryWrapper<HzBill>()
                .eq(HzBill::getContractId, contractId)
                .eq(HzBill::getBillType, "2")
                .eq(HzBill::getDelFlag, "0")
                .orderByAsc(HzBill::getDueDate)
                .last("LIMIT 1"));
        if (firstRent != null && !"1".equals(firstRent.getBillStatus())) {
            return "首期租金尚未收齐，请先完成支付再办理入住";
        }
        // 入住日期范围 [sign_time, sign_time+3 天]
        if (rawCheckinDate == null || rawCheckinDate.toString().isEmpty()) {
            return "请选择实际入住日期";
        }
        String stdDate = convertChineseDateToDate(rawCheckinDate.toString());
        String signTime = contract.getSignTime();
        if (stdDate == null || stdDate.isEmpty()) {
            return "入住日期格式不正确";
        }
        if (signTime == null || signTime.isEmpty()) {
            // 老数据迁移合同可能无 sign_time，放行
            return null;
        }
        try {
            LocalDate ci = LocalDate.parse(stdDate.length() >= 10 ? stdDate.substring(0, 10) : stdDate,
                    DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            LocalDateTime st = LocalDateTime.parse(signTime.replace("T", " ").trim(),
                    DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            LocalDate signDay = st.toLocalDate();
            LocalDate maxDay = signDay.plusDays(3);
            if (ci.isBefore(signDay) || ci.isAfter(maxDay)) {
                return "入住日期需在签订合同当日至签订日后 3 日内（" + signDay + " 至 " + maxDay + "）";
            }
        } catch (Exception e) {
            // 解析异常不阻断主流程
            return null;
        }
        return null;
    }

    /**
     * 转换中文日期格式为标准日期格式
     * 例如: "2026年2月5日" → "2026-02-05"
     *
     * @param chineseDate 中文日期字符串
     * @return 标准日期字符串 (yyyy-MM-dd)
     */
    private String convertChineseDateToDate(String chineseDate) {
        if (chineseDate == null || chineseDate.isEmpty()) {
            return null;
        }

        // 如果已经是标准格式，直接返回
        if (chineseDate.matches("\\d{4}-\\d{1,2}-\\d{1,2}.*")) {
            return chineseDate;
        }

        try {
            // 使用正则表达式匹配中文日期格式: 2026年2月5日
            Pattern pattern = Pattern.compile("(\\d{4})年(\\d{1,2})月(\\d{1,2})日");
            Matcher matcher = pattern.matcher(chineseDate);

            if (matcher.find()) {
                String year = matcher.group(1);
                String month = String.format("%02d", Integer.parseInt(matcher.group(2)));
                String day = String.format("%02d", Integer.parseInt(matcher.group(3)));

                return year + "-" + month + "-" + day;
            }
        } catch (Exception e) {
            logger.error("日期格式转换失败: " + chineseDate, e);
        }

        // 如果转换失败，尝试使用其他格式
        try {
            SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy年M月d日");
            SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd");
            Date date = inputFormat.parse(chineseDate);
            return outputFormat.format(date);
        } catch (ParseException e) {
            logger.error("日期格式解析失败: " + chineseDate, e);
            return chineseDate; // 返回原值
        }
    }

    /**
     * 入住超时倒计时（小程序「入住办理」页面卡片显示）。
     * 返回字段：
     *   showCountdown - 是否显示倒计时（true=用户已付款且未提交入住申请且功能启用）
     *   remainingSeconds - 剩余秒数（≤0 表示已超时）
     *   totalSeconds  - 总倒计时秒数（如 72*3600）
     *   deadline      - 截止时间字符串
     *   timeoutHours  - 配置的超时小时数
     */
    @GetMapping("/countdown/{contractId}")
    public AjaxResult getCheckinCountdown(@PathVariable("contractId") Long contractId) {
        Map<String, Object> data = new HashMap<>();
        data.put("showCountdown", false);
        data.put("remainingSeconds", 0);
        data.put("totalSeconds", 0);
        data.put("deadline", null);
        data.put("timeoutHours", 72);

        if (contractId == null) {
            return AjaxResult.success(data);
        }
        com.ruoyi.system.domain.HzContract ownerContract = contractService.selectContractById(contractId);
        if (ownerContract == null || !SecurityUtils.getHzUserId().equals(ownerContract.getTenantId())) {
            return error("无权查看");
        }

        // 1. 配置开关 + 启用日 + 超时小时数
        String enabled = readCfg("auto.cancel.enabled", "false");
        if (!"true".equalsIgnoreCase(enabled)) {
            return AjaxResult.success(data);
        }
        String startDateStr = readCfg("auto.cancel.start-date", null);
        if (startDateStr == null || startDateStr.isEmpty()) {
            return AjaxResult.success(data);
        }
        int timeoutHours;
        try {
            timeoutHours = Integer.parseInt(readCfg("auto.cancel.timeout-hours", "72"));
        } catch (NumberFormatException e) {
            timeoutHours = 72;
        }
        data.put("timeoutHours", timeoutHours);
        data.put("totalSeconds", timeoutHours * 3600);

        // 2. 加载合同
        HzContract contract = contractService.selectContractById(contractId);
        if (contract == null || !"3".equals(contract.getContractStatus()) || "2".equals(contract.getDelFlag())) {
            return AjaxResult.success(data);
        }
        // 启用日之后创建的合同才纳入
        if (contract.getCreateTime() != null) {
            try {
                LocalDateTime startDate = LocalDateTime.parse(
                        startDateStr.replace("T", " ").trim(),
                        DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
                LocalDateTime contractCreate = contract.getCreateTime().toInstant()
                        .atZone(java.time.ZoneId.systemDefault()).toLocalDateTime();
                if (contractCreate.isBefore(startDate)) {
                    return AjaxResult.success(data);
                }
            } catch (Exception ignore) {
                return AjaxResult.success(data);
            }
        }

        // 3. 已提交过入住申请（status>=1）→ 永久豁免，不显示倒计时
        Long submittedCount = checkInMapper.selectCount(new LambdaQueryWrapper<HzCheckIn>()
                .eq(HzCheckIn::getContractId, contractId)
                .ge(HzCheckIn::getStatus, "1")
                .eq(HzCheckIn::getDelFlag, "0"));
        if (submittedCount != null && submittedCount > 0) {
            return AjaxResult.success(data);
        }

        // 4. 押金账单 pay_time（微信支付）作为起算点
        HzBill depositBill = billMapper.selectOne(new LambdaQueryWrapper<HzBill>()
                .eq(HzBill::getContractId, contractId)
                .eq(HzBill::getBillType, "1")
                .eq(HzBill::getBillStatus, "1")
                .eq(HzBill::getPayMethod, "wechat")
                .eq(HzBill::getDelFlag, "0")
                .last("LIMIT 1"));
        if (depositBill == null || depositBill.getPayTime() == null || depositBill.getPayTime().isEmpty()) {
            return AjaxResult.success(data);
        }
        // 5. 必须首期租金也已支付（否则不进倒计时阶段）
        Long paidRent = billMapper.selectCount(new LambdaQueryWrapper<HzBill>()
                .eq(HzBill::getContractId, contractId)
                .eq(HzBill::getBillType, "2")
                .eq(HzBill::getBillStatus, "1")
                .eq(HzBill::getPayMethod, "wechat")
                .eq(HzBill::getDelFlag, "0"));
        if (paidRent == null || paidRent == 0) {
            return AjaxResult.success(data);
        }

        // 6. 计算剩余秒数
        try {
            LocalDateTime payTime = LocalDateTime.parse(
                    depositBill.getPayTime().replace("T", " ").trim(),
                    DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            LocalDateTime deadline = payTime.plusHours(timeoutHours);
            long remaining = java.time.Duration.between(LocalDateTime.now(), deadline).getSeconds();
            data.put("showCountdown", true);
            data.put("remainingSeconds", Math.max(remaining, 0));
            data.put("deadline", deadline.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        } catch (Exception e) {
            logger.warn("计算入住倒计时失败 contractId={}: {}", contractId, e.getMessage());
        }
        return AjaxResult.success(data);
    }

    private String readCfg(String key, String defaultValue) {
        try {
            String v = sysConfigService.selectConfigByKey(key);
            return (v == null || v.isEmpty()) ? defaultValue : v;
        } catch (Exception e) {
            return defaultValue;
        }
    }
}
