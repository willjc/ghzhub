package com.ruoyi.web.controller.h5;

import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.system.domain.HzCheckIn;
import com.ruoyi.system.domain.HzContract;
import com.ruoyi.system.domain.HzHouse;
import com.ruoyi.system.domain.HzHouseExchange;
import com.ruoyi.system.service.IHzCheckInService;
import com.ruoyi.system.service.IHzContractService;
import com.ruoyi.system.service.IHzHouseExchangeService;
import com.ruoyi.system.service.IHzHouseService;
import com.ruoyi.system.service.IHzProjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * H5用户端 - 调换房申请API
 */
@RestController
@RequestMapping("/h5/app/exchange")
public class HzExchangeAppController extends BaseController {

    @Autowired
    private IHzHouseExchangeService exchangeService;

    @Autowired
    private IHzCheckInService checkInService;

    @Autowired
    private IHzContractService contractService;

    @Autowired
    private IHzProjectService projectService;

    @Autowired
    private IHzHouseService houseService;

    /**
     * 获取用户的调换房申请列表
     *
     * @param tenantId 租户ID
     * @return 调换房申请列表
     */
    @GetMapping("/list/{tenantId}")
    public AjaxResult getExchangeList(@PathVariable Long tenantId) {
        List<HzHouseExchange> list = exchangeService.selectExchangeListByTenantId(tenantId);

        // 转换为前端需要的格式
        List<Map<String, Object>> result = list.stream().map(exchange -> {
            Map<String, Object> item = new HashMap<>();
            item.put("id", exchange.getExchangeId());
            item.put("status", exchange.getStatus());
            item.put("statusText", getStatusText(exchange.getStatus()));
            item.put("contractNo", exchange.getContractNo());
            item.put("applicant", exchange.getTenantName());
            item.put("idCard", exchange.getTenantIdCard());
            item.put("phone", exchange.getTenantPhone());
            item.put("roomAddress", exchange.getRoomAddress());
            item.put("applyTime", exchange.getApplyTime());
            item.put("reason", exchange.getExchangeReason());
            // 原房源完整地址
            item.put("oldFullAddress", exchange.getOldFullAddress());
            // 目标房源完整地址（换房完成后）
            item.put("newFullAddress", exchange.getNewFullAddress());
            // 合同期限
            item.put("contractStartDate", exchange.getContractStartDate());
            item.put("contractEndDate", exchange.getContractEndDate());
            // 换房时间
            item.put("exchangeTime", exchange.getExchangeTime());
            // 审核意见和时间
            item.put("approveOpinion", exchange.getApproveOpinion());
            item.put("approveTime", exchange.getApproveTime());
            return item;
        }).toList();

        return success(result);
    }

    /**
     * 获取调换房申请详情
     *
     * @param exchangeId 调换房ID
     * @return 调换房申请详情
     */
    @GetMapping("/{exchangeId}")
    public AjaxResult getExchangeDetail(@PathVariable Long exchangeId) {
        HzHouseExchange exchange = exchangeService.selectExchangeById(exchangeId);

        if (exchange == null) {
            return error("调换房申请不存在");
        }

        Map<String, Object> result = new HashMap<>();
        result.put("id", exchange.getExchangeId());
        result.put("status", exchange.getStatus());
        result.put("statusText", getStatusText(exchange.getStatus()));
        result.put("contractNo", exchange.getContractNo());
        result.put("applicant", exchange.getTenantName());
        result.put("idCard", exchange.getTenantIdCard());
        result.put("phone", exchange.getTenantPhone());
        result.put("roomAddress", exchange.getRoomAddress());
        result.put("applyTime", exchange.getApplyTime());
        result.put("reason", exchange.getExchangeReason());
        result.put("approveOpinion", exchange.getApproveOpinion());
        result.put("approveTime", exchange.getApproveTime());

        return success(result);
    }

    /**
     * 获取用户已确认的合同列表（可调换的房源）
     * 兼容老数据迁移场景：优先查入住记录，同时补充通过合同直接查询的方式
     *
     * @param tenantId 租户ID
     * @return 已确认的合同列表
     */
    @GetMapping("/confirmed/{tenantId}")
    public AjaxResult getConfirmedContractList(@PathVariable Long tenantId) {
        // 方式1：查询该用户所有已入住确认的入住单 (status='4')
        List<HzCheckIn> checkInList = checkInService.selectConfirmedCheckInListByTenantId(tenantId);

        // 用合同ID去重，避免重复
        java.util.Set<Long> addedContractIds = new java.util.HashSet<>();
        List<Map<String, Object>> result = new java.util.ArrayList<>();

        // 处理入住记录中的合同
        for (HzCheckIn checkIn : checkInList) {
            if (checkIn.getContractId() == null || addedContractIds.contains(checkIn.getContractId())) {
                continue;
            }

            HzContract contract = contractService.selectContractById(checkIn.getContractId());
            if (contract == null || contract.getContractNo() == null) {
                continue;
            }

            Map<String, Object> item = new HashMap<>();
            item.put("recordId", checkIn.getRecordId());
            item.put("contractId", checkIn.getContractId());
            item.put("contractNo", contract.getContractNo());
            item.put("houseAddress", contract.getHouseAddress());
            item.put("rentPrice", contract.getRentPrice());

            // 从备注中解析房源信息
            String remark = checkIn.getRemark();
            String community = extractInfo(remark, "项目：");
            String room = extractInfo(remark, "房间：");
            String rentPeriod = extractInfo(remark, "租期：");

            // 如果备注中没有房源信息，从项目表/房源表补充
            if ((community == null || community.isEmpty()) && contract.getProjectId() != null) {
                com.ruoyi.system.domain.HzProject project = projectService.selectProjectById(contract.getProjectId());
                if (project != null && project.getProjectName() != null) {
                    community = project.getProjectName();
                }
            }
            if ((community == null || community.isEmpty())) {
                community = contract.getHouseAddress() != null ? contract.getHouseAddress() : "";
            }

            if ((room == null || room.isEmpty()) && contract.getHouseId() != null) {
                HzHouse house = houseService.selectHouseById(contract.getHouseId());
                if (house != null) {
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

            item.put("community", community);
            item.put("room", room);
            item.put("rentPeriod", rentPeriod);

            addedContractIds.add(checkIn.getContractId());
            result.add(item);
        }

        // 方式2：通过合同表补充（兼容老数据迁移场景，无入住记录但有已签署合同）
        // 查询该用户所有合同，筛选状态为已签署(2)/生效中(3)/续签(4)/退租中(5)的合同
        List<HzContract> allContracts = contractService.selectContractListByTenantId(tenantId);
        List<HzContract> activeContracts = allContracts.stream()
                .filter(c -> "2".equals(c.getContractStatus()) || "3".equals(c.getContractStatus())
                        || "4".equals(c.getContractStatus()) || "5".equals(c.getContractStatus()))
                .toList();

        for (HzContract contract : activeContracts) {
            if (addedContractIds.contains(contract.getContractId())) {
                continue; // 已通过入住记录添加，跳过
            }

            Map<String, Object> item = new HashMap<>();
            item.put("contractId", contract.getContractId());
            item.put("contractNo", contract.getContractNo());
            item.put("houseAddress", contract.getHouseAddress());
            item.put("rentPrice", contract.getRentPrice());

            // 从项目表获取小区名
            String community = "";
            if (contract.getProjectId() != null) {
                com.ruoyi.system.domain.HzProject project = projectService.selectProjectById(contract.getProjectId());
                if (project != null && project.getProjectName() != null) {
                    community = project.getProjectName();
                }
            }
            if (community.isEmpty() && contract.getHouseAddress() != null) {
                community = contract.getHouseAddress();
            }
            item.put("community", community);

            // 从房源表获取房间号
            String room = "";
            if (contract.getHouseId() != null) {
                HzHouse house = houseService.selectHouseById(contract.getHouseId());
                if (house != null) {
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
            if (room.isEmpty() && contract.getHouseCode() != null) {
                room = contract.getHouseCode();
            }
            item.put("room", room);

            addedContractIds.add(contract.getContractId());
            result.add(item);
        }

        return success(result);
    }

    /**
     * 提交调换房申请
     *
     * @param params 调换房申请信息
     * @return 提交结果
     */
    @PostMapping
    public AjaxResult submitExchange(@RequestBody Map<String, Object> params) {
        try {
            Long contractId = Long.parseLong(params.get("contractId").toString());

            // 查询合同信息
            HzContract contract = contractService.selectContractById(contractId);
            if (contract == null) {
                return error("合同不存在");
            }

            // 创建调换房申请
            HzHouseExchange exchange = new HzHouseExchange();
            exchange.setTenantId(contract.getTenantId());
            exchange.setOldContractId(contractId);
            exchange.setOldHouseId(contract.getHouseId());
            exchange.setOldHouseCode(contract.getHouseCode());

            // 注意：目前只选择原房源，新房源需要管理员后续分配
            // 或者在前端增加选择新房源的步骤
            exchange.setNewHouseId(null);
            exchange.setNewHouseCode("待分配");

            exchange.setExchangeReason(params.get("reason") != null ? params.get("reason").toString() : "");
            exchange.setApplyTime(params.get("applyDate") != null ? params.get("applyDate").toString() : DateUtils.getDate());

            // 设置创建人
            exchange.setCreateBy(contract.getTenantName());
            exchange.setCreateTime(DateUtils.getNowDate());

            int result = exchangeService.insertExchange(exchange);

            if (result > 0) {
                return success("调换房申请提交成功，等待管理员审核");
            } else {
                return error("提交失败");
            }

        } catch (Exception e) {
            logger.error("提交调换房申请失败", e);
            return error("提交失败：" + e.getMessage());
        }
    }

    /**
     * 将状态代码转换为文本
     */
    private String getStatusText(String status) {
        switch (status) {
            case "0": return "审批中";
            case "1": return "审批通过";
            case "2": return "审批拒绝";
            default: return "未知状态";
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
}
