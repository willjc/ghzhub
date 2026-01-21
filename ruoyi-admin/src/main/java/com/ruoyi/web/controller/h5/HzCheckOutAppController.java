package com.ruoyi.web.controller.h5;

import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.system.domain.HzCheckoutApply;
import com.ruoyi.system.service.IHzCheckoutService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import jakarta.servlet.http.HttpServletRequest;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * H5用户端 - 退租办理API
 */
@RestController
@RequestMapping("/h5/app/checkout")
public class HzCheckOutAppController extends BaseController {

    @Autowired
    private IHzCheckoutService checkoutService;

    /**
     * 获取用户的退租申请列表
     *
     * @param tenantId 租户ID
     * @return 退租申请列表
     */
    @GetMapping("/list/{tenantId}")
    public AjaxResult getCheckoutList(@PathVariable Long tenantId) {
        List<Map<String, Object>> list = checkoutService.selectCheckoutApplyListWithHouseInfo(tenantId);
        return success(list);
    }

    /**
     * 获取退租申请详情
     *
     * @param applyId 退租申请ID
     * @return 退租申请详情
     */
    @GetMapping("/{applyId}")
    public AjaxResult getCheckoutDetail(@PathVariable Long applyId) {
        HzCheckoutApply apply = checkoutService.selectCheckoutApplyByApplyId(applyId);

        if (apply == null) {
            return error("退租申请不存在");
        }

        // 组装返回数据
        Map<String, Object> result = new HashMap<>();
        result.put("applyId", apply.getApplyId());
        result.put("contractId", apply.getContractId());
        result.put("tenantId", apply.getTenantId());
        result.put("houseId", apply.getHouseId());
        result.put("applyTime", apply.getApplyTime());
        result.put("planCheckoutDate", apply.getPlanCheckoutDate());
        result.put("checkoutReason", apply.getCheckoutReason());
        result.put("applyStatus", apply.getApplyStatus());
        result.put("approveBy", apply.getApproveBy());
        result.put("approveTime", apply.getApproveTime());
        result.put("approveOpinion", apply.getApproveOpinion());

        // 费用信息（审批通过后才会有）
        result.put("waterFee", apply.getWaterFee() != null ? apply.getWaterFee() : BigDecimal.ZERO);
        result.put("electricFee", apply.getElectricFee() != null ? apply.getElectricFee() : BigDecimal.ZERO);
        result.put("gasFee", apply.getGasFee() != null ? apply.getGasFee() : BigDecimal.ZERO);
        result.put("heatingFee", apply.getHeatingFee() != null ? apply.getHeatingFee() : BigDecimal.ZERO);
        result.put("propertyFee", apply.getPropertyFee() != null ? apply.getPropertyFee() : BigDecimal.ZERO);
        result.put("damageDeduction", apply.getDamageDeduction() != null ? apply.getDamageDeduction() : BigDecimal.ZERO);
        result.put("damageDescription", apply.getDamageDescription());
        result.put("depositRefund", apply.getDepositRefund() != null ? apply.getDepositRefund() : BigDecimal.ZERO);

        // 表读数
        result.put("meterReadingWater", apply.getMeterReadingWater());
        result.put("meterReadingElectric", apply.getMeterReadingElectric());
        result.put("meterReadingGas", apply.getMeterReadingGas());
        result.put("keyReturned", apply.getKeyReturned());

        return success(result);
    }

    /**
     * 从请求头中的Token解析出hz_user的ID
     * Token格式：hz_token_{userId}_{timestamp}
     */
    private Long getHzUserIdFromToken() {
        try {
            HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
            String token = request.getHeader("Authorization");

            if (token == null || token.isEmpty()) {
                return null;
            }

            // 移除 "Bearer " 前缀（如果有）
            if (token.startsWith("Bearer ")) {
                token = token.substring(7);
            }

            // 解析token: hz_token_{userId}_{timestamp}
            if (token.startsWith("hz_token_")) {
                String[] parts = token.split("_");
                if (parts.length >= 3) {
                    return Long.parseLong(parts[2]); // parts[0]=hz, parts[1]=token, parts[2]=userId
                }
            }

            return null;
        } catch (Exception e) {
            logger.error("解析token失败", e);
            return null;
        }
    }

    /**
     * 提交退租申请
     *
     * @param requestData 请求数据
     * @return 结果
     */
    @PostMapping("/apply")
    public AjaxResult submitCheckoutApply(@RequestBody Map<String, Object> requestData) {
        Long contractId = Long.valueOf(requestData.get("contractId").toString());
        Long houseId = Long.valueOf(requestData.get("houseId").toString());
        String planCheckoutDate = requestData.get("planCheckoutDate").toString();
        String checkoutReason = requestData.get("checkoutReason") != null ?
                               requestData.get("checkoutReason").toString() : "";

        // 获取签名数据（base64格式）
        String signature = requestData.get("signature") != null ?
                          requestData.get("signature").toString() : null;

        // 从token中获取租户ID
        Long tenantId = getHzUserIdFromToken();
        if (tenantId == null) {
            return error("获取用户信息失败，请重新登录");
        }

        // 先��询是否存在已取消的退租申请（相同合同+租户）
        HzCheckoutApply existingApply = checkoutService.selectCancelledApply(contractId, tenantId);

        HzCheckoutApply apply;
        boolean isNew = false;

        if (existingApply != null) {
            // 存在已取消的申请，更新该记录
            apply = existingApply;
            apply.setPlanCheckoutDate(parseDate(planCheckoutDate));
            apply.setCheckoutReason(checkoutReason);
            apply.setTenantSignature(signature);
            apply.setApplyTime(new Date()); // 更新申请时间
            apply.setApplyStatus("0"); // 状态改为审批中
            apply.setApproveBy(null);
            apply.setApproveTime(null);
            apply.setApproveOpinion(null);
            apply.setUpdateBy("用户端");
        } else {
            // 不存在已取消的申请，创建新记录
            isNew = true;
            apply = new HzCheckoutApply();
            apply.setContractId(contractId);
            apply.setTenantId(tenantId);
            apply.setHouseId(houseId);
            apply.setPlanCheckoutDate(parseDate(planCheckoutDate));
            apply.setCheckoutReason(checkoutReason);
            apply.setTenantSignature(signature);
            apply.setApplyTime(new Date());
            apply.setApplyStatus("0"); // 初始状态为审批中
            apply.setCreateBy("用户端");
        }

        int result;
        if (isNew) {
            result = checkoutService.insertCheckoutApply(apply);
        } else {
            result = checkoutService.updateCheckoutApply(apply);
        }

        if (result > 0) {
            Map<String, Object> data = new HashMap<>();
            data.put("applyId", apply.getApplyId());
            return AjaxResult.success("提交成功", data);
        } else {
            return error("提交失败");
        }
    }

    /**
     * 修改退租申请
     *
     * @param applyId 申请ID
     * @param requestData 请求数据
     * @return 结果
     */
    @PutMapping("/{applyId}")
    public AjaxResult updateCheckoutApply(@PathVariable Long applyId,
                                          @RequestBody Map<String, Object> requestData) {
        HzCheckoutApply apply = checkoutService.selectCheckoutApplyByApplyId(applyId);

        if (apply == null) {
            return error("退租申请不存在");
        }

        // 只有审批中的申请可以修改
        if (!"0".equals(apply.getApplyStatus())) {
            return error("只有审批中的申请可以修改");
        }

        if (requestData.containsKey("planCheckoutDate")) {
            apply.setPlanCheckoutDate(parseDate(requestData.get("planCheckoutDate").toString()));
        }
        if (requestData.containsKey("checkoutReason")) {
            apply.setCheckoutReason(requestData.get("checkoutReason").toString());
        }

        apply.setUpdateBy(SecurityUtils.getUsername());

        int result = checkoutService.updateCheckoutApply(apply);

        if (result > 0) {
            return success("修改成功");
        } else {
            return error("修改失败");
        }
    }

    /**
     * 取消退租申请
     *
     * @param applyId 申请ID
     * @return 结果
     */
    @DeleteMapping("/{applyId}")
    public AjaxResult cancelCheckoutApply(@PathVariable Long applyId) {
        HzCheckoutApply apply = checkoutService.selectCheckoutApplyByApplyId(applyId);

        if (apply == null) {
            return error("退租申请不存在");
        }

        // 只有审批中的申请可以取消
        if (!"0".equals(apply.getApplyStatus())) {
            return error("只有审批中的申请可以取消");
        }

        int result = checkoutService.cancelCheckoutApply(applyId);

        if (result > 0) {
            return success("取消成功");
        } else {
            return error("取消失败");
        }
    }

    /**
     * 获取退租确认信息（费用详情）
     *
     * @param applyId 申请ID
     * @return 费用详情
     */
    @GetMapping("/confirm/{applyId}")
    public AjaxResult getCheckoutConfirm(@PathVariable Long applyId) {
        try {
            Map<String, Object> result = checkoutService.getCheckoutConfirmInfo(applyId);
            return success(result);
        } catch (RuntimeException e) {
            return error(e.getMessage());
        } catch (Exception e) {
            return error("获取退租确认信息失败");
        }
    }

    /**
     * 提交退租确认（用户签字）
     *
     * @param applyId 申请ID
     * @param requestData 请求数据（包含签字信息）
     * @return 结果
     */
    @PostMapping("/confirm/{applyId}")
    public AjaxResult submitCheckoutConfirm(@PathVariable Long applyId,
                                            @RequestBody Map<String, Object> requestData) {
        String tenantSignature = requestData.get("tenantSignature") != null ?
                                  requestData.get("tenantSignature").toString() : "";

        if (tenantSignature.isEmpty()) {
            return error("请先签字");
        }

        int result = checkoutService.submitCheckoutConfirm(applyId, tenantSignature);

        if (result > 0) {
            return success("退租确认成功");
        } else {
            return error("退租确认失败");
        }
    }

    /**
     * 解析日期字符串
     */
    private java.util.Date parseDate(String dateStr) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            return sdf.parse(dateStr);
        } catch (Exception e) {
            return null;
        }
    }
}
