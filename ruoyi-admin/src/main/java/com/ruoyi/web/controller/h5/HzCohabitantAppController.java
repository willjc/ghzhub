package com.ruoyi.web.controller.h5;

import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.system.domain.HzCheckIn;
import com.ruoyi.system.domain.HzCoTenant;
import com.ruoyi.system.domain.HzContract;
import com.ruoyi.system.service.IHzCheckInService;
import com.ruoyi.system.service.IHzCoTenantService;
import com.ruoyi.system.service.IHzContractService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * H5用户端 - 合租户申请API
 */
@RestController
@RequestMapping("/h5/app/cohabitant")
public class HzCohabitantAppController extends BaseController {

    @Autowired
    private IHzCoTenantService coTenantService;

    @Autowired
    private IHzCheckInService checkInService;

    @Autowired
    private IHzContractService contractService;

    /**
     * 获取用户的合租户申请列表
     *
     * @param tenantId 租户ID
     * @return 合租户申请列表
     */
    @GetMapping("/list/{tenantId}")
    public AjaxResult getCohabitantList(@PathVariable Long tenantId) {
        List<HzCoTenant> list = coTenantService.selectCoTenantListByTenantId(tenantId);

        // 转换为前端需要的格式
        List<Map<String, Object>> result = list.stream().map(coTenant -> {
            Map<String, Object> item = new HashMap<>();
            item.put("id", coTenant.getCoTenantId());
            item.put("status", coTenant.getStatus());
            item.put("statusText", getStatusText(coTenant.getStatus()));
            item.put("contractNo", coTenant.getContractNo());
            item.put("applicant", coTenant.getMainTenantName());
            item.put("idCard", coTenant.getIdCard());
            item.put("phone", coTenant.getPhone());
            item.put("roomAddress", coTenant.getRoomAddress());
            item.put("applyTime", coTenant.getApplyTime());
            item.put("relationship", coTenant.getRelationship());
            item.put("reason", coTenant.getRemark());
            return item;
        }).toList();

        return success(result);
    }

    /**
     * 获取合租户申请详情
     *
     * @param coTenantId 合租户ID
     * @return 合租户申请详情
     */
    @GetMapping("/{coTenantId}")
    public AjaxResult getCohabitantDetail(@PathVariable Long coTenantId) {
        HzCoTenant coTenant = coTenantService.selectCoTenantById(coTenantId);

        if (coTenant == null) {
            return error("合租户申请不存在");
        }

        Map<String, Object> result = new HashMap<>();
        result.put("id", coTenant.getCoTenantId());
        result.put("status", coTenant.getStatus());
        result.put("statusText", getStatusText(coTenant.getStatus()));
        result.put("contractNo", coTenant.getContractNo());
        result.put("applicant", coTenant.getMainTenantName());
        result.put("idCard", coTenant.getIdCard());
        result.put("phone", coTenant.getPhone());
        result.put("roomAddress", coTenant.getRoomAddress());
        result.put("applyTime", coTenant.getApplyTime());
        result.put("relationship", coTenant.getRelationship());
        result.put("reason", coTenant.getRemark());
        result.put("auditOpinion", coTenant.getAuditOpinion());
        result.put("auditTime", coTenant.getAuditTime());

        return success(result);
    }

    /**
     * 获取用户已确认的合同列表（可添加合租户的房源）
     *
     * @param tenantId 租户ID
     * @return 已确认的合同列表
     */
    @GetMapping("/confirmed/{tenantId}")
    public AjaxResult getConfirmedContractList(@PathVariable Long tenantId) {
        // 查询该用户所有已入住确认的入住单 (status='2','3','4'，兼容老数据)
        List<HzCheckIn> checkInList = checkInService.selectConfirmedCheckInListByTenantId(tenantId);

        // 转换为前端需要的格式（展示合同信息）
        // 只保留履行中的合同（contract_status='3'）
        List<Map<String, Object>> result = checkInList.stream().map(checkIn -> {
            // 根据合同ID查询合同信息
            HzContract contract = null;
            if (checkIn.getContractId() != null) {
                contract = contractService.selectContractById(checkIn.getContractId());
            }

            Map<String, Object> item = new HashMap<>();
            item.put("recordId", checkIn.getRecordId());
            item.put("contractId", checkIn.getContractId());

            if (contract != null) {
                item.put("contractNo", contract.getContractNo());
                item.put("houseAddress", contract.getHouseAddress());
                item.put("rentPrice", contract.getRentPrice());
                item.put("contractStatus", contract.getContractStatus());
            }

            // 从备注中解析房源信息
            String remark = checkIn.getRemark();
            item.put("community", extractInfo(remark, "项目："));
            item.put("room", extractInfo(remark, "房间："));
            item.put("rentPeriod", extractInfo(remark, "租期："));

            return item;
        }).filter(item -> {
            // 过滤掉合同不存在或已删除的记录
            Object contractId = item.get("contractId");
            if (contractId == null) {
                return false;
            }
            // 如果没有 contractNo，说明合同已被删除或不存在
            if (item.get("contractNo") == null) {
                return false;
            }
            // 合住人申请：只保留履行中的合同（contract_status='3'）
            return "3".equals(item.get("contractStatus"));
        }).toList();

        return success(result);
    }

    /**
     * 提交合租户申请
     *
     * @param params 合租户申请信息
     * @return 提交结果
     */
    @PostMapping
    public AjaxResult submitCohabitant(@RequestBody Map<String, Object> params) {
        try {
            Long contractId = Long.parseLong(params.get("contractId").toString());

            // 查询合同信息
            HzContract contract = contractService.selectContractById(contractId);
            if (contract == null) {
                return error("合同不存在");
            }

            // 创建合租户申请
            HzCoTenant coTenant = new HzCoTenant();
            coTenant.setContractId(contractId);
            coTenant.setTenantName(params.get("tenantName") != null ? params.get("tenantName").toString() : "");
            coTenant.setIdCard(params.get("idCard") != null ? params.get("idCard").toString() : "");
            coTenant.setPhone(params.get("phone") != null ? params.get("phone").toString() : "");
            coTenant.setRelationship(params.get("relationship") != null ? params.get("relationship").toString() : "");

            // 设置创建人
            coTenant.setCreateBy(contract.getTenantName());
            coTenant.setCreateTime(DateUtils.getNowDate());

            int result = coTenantService.insertCoTenant(coTenant);

            if (result > 0) {
                return success("合租户申请提交成功，等待管理员审核");
            } else {
                return error("提交失败");
            }

        } catch (Exception e) {
            logger.error("提交合租户申请失败", e);
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
