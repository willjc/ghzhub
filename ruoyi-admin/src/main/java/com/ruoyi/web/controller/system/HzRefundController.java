package com.ruoyi.web.controller.system;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.system.domain.HzCheckoutRecord;
import com.ruoyi.system.domain.HzRefundApplyVO;
import com.ruoyi.system.mapper.HzCheckoutRecordMapper;
import com.ruoyi.system.service.IHzRefundService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

/**
 * 退款��理Controller
 *
 * @author ruoyi
 */
@RestController("adminRefundController")
@RequestMapping("/gangzhu/refund")
public class HzRefundController extends BaseController {

    @Autowired
    private IHzRefundService refundService;

    @Autowired
    private HzCheckoutRecordMapper checkoutRecordMapper;

    /**
     * 查询退款申请列表
     */
    @PreAuthorize("@ss.hasPermi('gangzhu:refund:list')")
    @GetMapping("/list")
    public TableDataInfo list(HzRefundApplyVO query) {
        startPage();
        List<HzRefundApplyVO> list = refundService.selectRefundList(
                query.getRefundNo(),
                query.getContractNo(),
                query.getRefundStatus()
        );
        return getDataTable(list);
    }

    /**
     * 获取退款申请详细信息
     */
    @PreAuthorize("@ss.hasPermi('gangzhu:refund:query')")
    @GetMapping(value = "/{refundId}")
    public AjaxResult getInfo(@PathVariable("refundId") Long refundId) {
        return success(refundService.selectRefundById(refundId));
    }

    /**
     * 审核退款申请
     */
    @PreAuthorize("@ss.hasPermi('gangzhu:refund:audit')")
    @Log(title = "退款管理", businessType = BusinessType.UPDATE)
    @PostMapping("/audit")
    public AjaxResult audit(@RequestBody HzRefundApplyVO vo) {
        return toAjax(refundService.auditRefund(
                vo.getRefundId(),
                vo.getRefundStatus(),
                vo.getApproveOpinion()
        ));
    }

    /**
     * 删除退款申请
     */
    @PreAuthorize("@ss.hasPermi('gangzhu:refund:remove')")
    @Log(title = "退款管理", businessType = BusinessType.DELETE)
    @DeleteMapping("/{refundId}")
    public AjaxResult remove(@PathVariable Long refundId) {
        return toAjax(refundService.deleteRefundById(refundId));
    }

    /**
     * 提交付款信息
     */
    @PreAuthorize("@ss.hasPermi('gangzhu:refund:payment')")
    @Log(title = "提交付款信息", businessType = BusinessType.UPDATE)
    @PostMapping("/payment")
    public AjaxResult submitPayment(@RequestBody HzRefundApplyVO vo) {
        // 通过退租申请ID查找退租记录
        LambdaUpdateWrapper<HzCheckoutRecord> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(HzCheckoutRecord::getApplyId, vo.getRefundId())
               .set(HzCheckoutRecord::getPaymentMethod, vo.getPaymentMethod())
               .set(HzCheckoutRecord::getPaymentVoucher, vo.getPaymentVoucher())
               .set(HzCheckoutRecord::getPaymentRemark, vo.getPaymentRemark())
               .set(HzCheckoutRecord::getRefundStatus, "1")  // 已退还
               .set(HzCheckoutRecord::getRefundTime, new Date())
               .set(HzCheckoutRecord::getUpdateBy, SecurityUtils.getUsername())
               .set(HzCheckoutRecord::getUpdateTime, new Date());

        int rows = checkoutRecordMapper.update(null, wrapper);
        return toAjax(rows);
    }
}
