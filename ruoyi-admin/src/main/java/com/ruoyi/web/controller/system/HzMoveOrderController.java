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
 * 搬家订单 Controller（管理端）
 * 与 HzCleanOrderController 共用同一 Service，通过 setOrderType("2") 区分
 *
 * @author ruoyi
 */
@RestController
@RequestMapping("/gangzhu/moveOrder")
public class HzMoveOrderController extends BaseController
{
    private static final String ORDER_TYPE = "2";

    @Autowired
    private IHzServiceOrderService orderService;

    @PreAuthorize("@ss.hasPermi('gangzhu:moveOrder:list')")
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

    @PreAuthorize("@ss.hasPermi('gangzhu:moveOrder:export')")
    @Log(title = "搬家订单", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, HzServiceOrder order)
    {
        order.setOrderType(ORDER_TYPE);
        List<HzServiceOrder> list = orderService.selectOrderList(order);
        ExcelUtil<HzServiceOrder> util = new ExcelUtil<>(HzServiceOrder.class);
        util.exportExcel(response, list, "搬家订单数据");
    }

    @PreAuthorize("@ss.hasPermi('gangzhu:moveOrder:query')")
    @GetMapping("/{orderId}")
    public AjaxResult getInfo(@PathVariable Long orderId)
    {
        return success(orderService.selectOrderById(orderId));
    }

    @PreAuthorize("@ss.hasPermi('gangzhu:moveOrder:assign')")
    @Log(title = "搬家订单分配", businessType = BusinessType.UPDATE)
    @PostMapping("/assign")
    public AjaxResult assign(@RequestBody Map<String, Object> body)
    {
        Long orderId = body.get("orderId") != null ? Long.valueOf(body.get("orderId").toString()) : null;
        Long companyId = body.get("companyId") != null ? Long.valueOf(body.get("companyId").toString()) : null;
        String assignRemark = body.get("assignRemark") != null ? body.get("assignRemark").toString() : null;
        if (orderId == null || companyId == null) return error("订单ID或公司ID不能为空");
        return toAjax(orderService.assignOrder(orderId, companyId, assignRemark));
    }

    @PreAuthorize("@ss.hasPermi('gangzhu:moveOrder:finish')")
    @Log(title = "搬家订单完成", businessType = BusinessType.UPDATE)
    @PutMapping("/finish/{orderId}")
    public AjaxResult finish(@PathVariable Long orderId)
    {
        return toAjax(orderService.finishOrder(orderId));
    }

    @PreAuthorize("@ss.hasPermi('gangzhu:moveOrder:remove')")
    @Log(title = "搬家订单", businessType = BusinessType.DELETE)
    @DeleteMapping("/{orderIds}")
    public AjaxResult remove(@PathVariable Long[] orderIds)
    {
        return toAjax(orderService.deleteOrderByIds(orderIds));
    }
}
