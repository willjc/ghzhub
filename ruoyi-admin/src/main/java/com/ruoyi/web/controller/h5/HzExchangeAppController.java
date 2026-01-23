package com.ruoyi.web.controller.h5;

import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.system.domain.HzCheckIn;
import com.ruoyi.system.domain.HzContract;
import com.ruoyi.system.domain.HzHouseExchange;
import com.ruoyi.system.service.IHzCheckInService;
import com.ruoyi.system.service.IHzContractService;
import com.ruoyi.system.service.IHzHouseExchangeService;
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
     *
     * @param tenantId 租户ID
     * @return 已确认的合同列表
     */
    @GetMapping("/confirmed/{tenantId}")
    public AjaxResult getConfirmedContractList(@PathVariable Long tenantId) {
        // 查询该用户所有已入住确认的入住单 (status='4')
        List<HzCheckIn> checkInList = checkInService.selectConfirmedCheckInListByTenantId(tenantId);

        // 转换为前端需要的格式（展示合同信息）
        // 过滤掉已删除的合同（del_flag='2'）
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
            return item.get("contractNo") != null;
        }).toList();

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
