package com.ruoyi.web.controller.h5;

import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.system.domain.HzEnterpriseBill;
import com.ruoyi.system.service.IHzEnterpriseBillService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 企业账单用户端Controller
 *
 * @author ruoyi
 */
@RestController
@RequestMapping("/h5/app/enterpriseBill")
public class HzEnterpriseBillAppController extends BaseController {

    @Autowired
    private IHzEnterpriseBillService enterpriseBillService;

    /**
     * 获取我的企业账单列表（根据登录用户手机号）
     */
    @GetMapping("/myBills")
    public AjaxResult getMyBills(@RequestParam String phone) {
        if (phone == null || phone.trim().isEmpty()) {
            return error("联系方式不能为空");
        }
        List<HzEnterpriseBill> list = enterpriseBillService.selectBillsByContactPhone(phone);
        return success(list);
    }

    /**
     * 获取企业账单详细信息
     */
    @GetMapping("/{billId}")
    public AjaxResult getInfo(@PathVariable("billId") Long billId) {
        return success(enterpriseBillService.selectEnterpriseBillById(billId));
    }

    /**
     * 支付企业账单
     */
    @PostMapping("/pay")
    public AjaxResult pay(@RequestBody(required = false) com.alibaba.fastjson2.JSONObject params) {
        if (params == null) {
            return error("参数不能为空");
        }
        Long billId = params.getLong("billId");
        String payMethod = params.getString("payMethod");
        String transactionNo = params.getString("transactionNo");

        if (billId == null) {
            return error("账单ID不能为空");
        }

        // 简单实现：直接标记为已支付
        // 实际项目应对接支付接口
        int result = enterpriseBillService.payBill(billId, payMethod != null ? payMethod : "在线支付", transactionNo);
        return toAjax(result);
    }
}
