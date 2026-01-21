package com.ruoyi.web.controller.system;

import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.system.domain.HzHouseExchange;
import com.ruoyi.system.service.IHzHouseExchangeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * 调换房管理Controller
 *
 * @author ruoyi
 */
@RestController
@RequestMapping("/gangzhu/exchange")
public class HzHouseExchangeController extends BaseController {
    @Autowired
    private IHzHouseExchangeService exchangeService;

    /**
     * 查询调换房申请列表
     */
    @PreAuthorize("@ss.hasPermi('gangzhu:exchange:list')")
    @GetMapping("/list")
    public TableDataInfo list(HzHouseExchange exchange) {
        startPage();
        List<HzHouseExchange> list = exchangeService.selectExchangeList(exchange);
        return getDataTable(list);
    }

    /**
     * 导出调换房申请列表
     */
    @PreAuthorize("@ss.hasPermi('gangzhu:exchange:export')")
    @Log(title = "调换房管理", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, HzHouseExchange exchange) {
        List<HzHouseExchange> list = exchangeService.selectExchangeList(exchange);
        ExcelUtil<HzHouseExchange> util = new ExcelUtil<HzHouseExchange>(HzHouseExchange.class);
        util.exportExcel(response, list, "调换房申请数据");
    }

    /**
     * 获取调换房申请详细信息
     */
    @PreAuthorize("@ss.hasPermi('gangzhu:exchange:query')")
    @GetMapping(value = "/{exchangeId}")
    public AjaxResult getInfo(@PathVariable("exchangeId") Long exchangeId) {
        return success(exchangeService.selectExchangeById(exchangeId));
    }

    /**
     * 新增调换房申请
     */
    @PreAuthorize("@ss.hasPermi('gangzhu:exchange:add')")
    @Log(title = "调换房管理", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody HzHouseExchange exchange) {
        return toAjax(exchangeService.insertExchange(exchange));
    }

    /**
     * 修改调换房申请
     */
    @PreAuthorize("@ss.hasPermi('gangzhu:exchange:edit')")
    @Log(title = "调换房管理", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody HzHouseExchange exchange) {
        return toAjax(exchangeService.updateExchange(exchange));
    }

    /**
     * 删除调换房申请
     */
    @PreAuthorize("@ss.hasPermi('gangzhu:exchange:remove')")
    @Log(title = "调换房管理", businessType = BusinessType.DELETE)
    @DeleteMapping("/{exchangeIds}")
    public AjaxResult remove(@PathVariable Long[] exchangeIds) {
        for (Long exchangeId : exchangeIds) {
            exchangeService.deleteExchangeById(exchangeId);
        }
        return success();
    }

    /**
     * 审核调换房申请
     */
    @PreAuthorize("@ss.hasPermi('gangzhu:exchange:audit')")
    @Log(title = "调换房管理", businessType = BusinessType.UPDATE)
    @PostMapping("/audit")
    public AjaxResult audit(@RequestBody HzHouseExchange exchange) {
        return toAjax(exchangeService.auditExchange(
            exchange.getExchangeId(),
            exchange.getApproveResult(),
            exchange.getApproveOpinion()
        ));
    }
}
