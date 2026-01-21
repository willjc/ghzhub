package com.ruoyi.web.controller.system;

import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.system.domain.HzCommitment;
import com.ruoyi.system.domain.HzCommitmentVO;
import com.ruoyi.system.service.IHzCommitmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * 承诺书记录Controller
 *
 * @author ruoyi
 */
@RestController
@RequestMapping("/system/commitment")
public class HzCommitmentController extends BaseController {

    @Autowired
    private IHzCommitmentService hzCommitmentService;

    /**
     * 查询承诺书记录列表
     */
    @PreAuthorize("@ss.hasPermi('gangzhu:commitment:list')")
    @GetMapping("/list")
    public TableDataInfo list(HzCommitment commitment) {
        startPage();
        List<HzCommitmentVO> list = hzCommitmentService.selectCommitmentVOList(commitment);
        return getDataTable(list);
    }

    /**
     * 导出承诺书记录列表
     */
    @PreAuthorize("@ss.hasPermi('gangzhu:commitment:export')")
    @Log(title = "承诺书记录", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, HzCommitment commitment) {
        List<HzCommitmentVO> list = hzCommitmentService.selectCommitmentVOList(commitment);
        ExcelUtil<HzCommitmentVO> util = new ExcelUtil<>(HzCommitmentVO.class);
        util.exportExcel(response, list, "承诺书记录数据");
    }

    /**
     * 获取承诺书记录详细信息
     */
    @PreAuthorize("@ss.hasPermi('gangzhu:commitment:query')")
    @GetMapping(value = "/{commitmentId}")
    public AjaxResult getInfo(@PathVariable("commitmentId") Long commitmentId) {
        return success(hzCommitmentService.selectCommitmentVOById(commitmentId));
    }

    /**
     * 修改承诺书记录状态
     */
    @PreAuthorize("@ss.hasPermi('gangzhu:commitment:edit')")
    @Log(title = "承诺书记录", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody HzCommitment commitment) {
        return toAjax(hzCommitmentService.updateCommitment(commitment));
    }

    /**
     * 删除承诺书记录
     */
    @PreAuthorize("@ss.hasPermi('gangzhu:commitment:remove')")
    @Log(title = "承诺书记录", businessType = BusinessType.DELETE)
    @DeleteMapping("/{commitmentIds}")
    public AjaxResult remove(@PathVariable Long[] commitmentIds) {
        for (Long commitmentId : commitmentIds) {
            hzCommitmentService.deleteCommitmentById(commitmentId);
        }
        return success();
    }
}
