package com.ruoyi.web.controller.system;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.constant.HttpStatus;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.common.utils.PageUtils;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.system.domain.HzServiceOrder;
import com.ruoyi.system.service.IHzServiceOrderService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 保洁订单 Controller（管理端）
 * 与 HzMoveOrderController 共用同一 Service，通过 setOrderType("1") 区分
 *
 * @author ruoyi
 */
@RestController
@RequestMapping("/gangzhu/cleanOrder")
public class HzCleanOrderController extends BaseController
{
    private static final String ORDER_TYPE = "1";

    @Autowired
    private IHzServiceOrderService orderService;

    @PreAuthorize("@ss.hasPermi('gangzhu:cleanOrder:list')")
    @GetMapping("/list")
    public TableDataInfo list(HzServiceOrder order)
    {
        order.setOrderType(ORDER_TYPE);
        Page<HzServiceOrder> page = PageUtils.getPage();
        IPage<HzServiceOrder> result = orderService.selectOrderPage(
                order, (int) page.getCurrent(), (int) page.getSize());
        TableDataInfo data = new TableDataInfo();
        data.setCode(HttpStatus.SUCCESS);
        data.setRows(result.getRecords());
        data.setTotal(result.getTotal());
        data.setMsg("查询成功");
        return data;
    }

    @PreAuthorize("@ss.hasPermi('gangzhu:cleanOrder:export')")
    @Log(title = "保洁订单", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, HzServiceOrder order)
    {
        order.setOrderType(ORDER_TYPE);
        List<HzServiceOrder> list = orderService.selectOrderList(order);
        ExcelUtil<HzServiceOrder> util = new ExcelUtil<>(HzServiceOrder.class);
        util.exportExcel(response, list, "保洁订单数据");
    }

    @PreAuthorize("@ss.hasPermi('gangzhu:cleanOrder:query')")
    @GetMapping("/{orderId}")
    public AjaxResult getInfo(@PathVariable Long orderId)
    {
        return success(orderService.selectOrderById(orderId));
    }

    /**
     * 分配服务公司
     * Body: { orderId, companyId, assignRemark }
     */
    @PreAuthorize("@ss.hasPermi('gangzhu:cleanOrder:assign')")
    @Log(title = "保洁订单分配", businessType = BusinessType.UPDATE)
    @PostMapping("/assign")
    public AjaxResult assign(@RequestBody Map<String, Object> body)
    {
        Long orderId = body.get("orderId") != null ? Long.valueOf(body.get("orderId").toString()) : null;
        Long companyId = body.get("companyId") != null ? Long.valueOf(body.get("companyId").toString()) : null;
        String assignRemark = body.get("assignRemark") != null ? body.get("assignRemark").toString() : null;
        if (orderId == null || companyId == null) return error("订单ID或公司ID不能为空");
        return toAjax(orderService.assignOrder(orderId, companyId, assignRemark));
    }

    /**
     * 标记完成
     */
    @PreAuthorize("@ss.hasPermi('gangzhu:cleanOrder:finish')")
    @Log(title = "保洁订单完成", businessType = BusinessType.UPDATE)
    @PutMapping("/finish/{orderId}")
    public AjaxResult finish(@PathVariable Long orderId)
    {
        return toAjax(orderService.finishOrder(orderId));
    }

    @PreAuthorize("@ss.hasPermi('gangzhu:cleanOrder:remove')")
    @Log(title = "保洁订单", businessType = BusinessType.DELETE)
    @DeleteMapping("/{orderIds}")
    public AjaxResult remove(@PathVariable Long[] orderIds)
    {
        return toAjax(orderService.deleteOrderByIds(orderIds));
    }
}
