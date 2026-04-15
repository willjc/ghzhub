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
import com.ruoyi.system.domain.HzEnterpriseBatch;
import com.ruoyi.system.domain.HzEnterpriseBill;
import com.ruoyi.system.service.IHzEnterpriseBatchService;
import com.ruoyi.system.service.IHzEnterpriseBillService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletResponse;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 企业批次Controller
 *
 * @author ruoyi
 */
@RestController
@RequestMapping("/system/enterpriseBatch")
public class HzEnterpriseBatchController extends BaseController {

    @Autowired
    private IHzEnterpriseBatchService enterpriseBatchService;

    @Autowired
    private IHzEnterpriseBillService enterpriseBillService;

    /**
     * 查询企业批次列表
     */
    @PreAuthorize("@ss.hasPermi('system:enterpriseBatch:list')")
    @GetMapping("/list")
    public TableDataInfo list(HzEnterpriseBatch enterpriseBatch) {
        Page<HzEnterpriseBatch> page = PageUtils.getPage();
        IPage<HzEnterpriseBatch> pageResult = enterpriseBatchService.selectEnterpriseBatchPage(page, enterpriseBatch);
        TableDataInfo rspData = new TableDataInfo();
        rspData.setCode(HttpStatus.SUCCESS);
        rspData.setRows(pageResult.getRecords());
        rspData.setTotal(pageResult.getTotal());
        rspData.setMsg("查询成功");
        return rspData;
    }

    /**
     * 导出企业批次列表
     */
    @PreAuthorize("@ss.hasPermi('system:enterpriseBatch:export')")
    @Log(title = "企业批次", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, HzEnterpriseBatch enterpriseBatch) {
        List<HzEnterpriseBatch> list = enterpriseBatchService.selectEnterpriseBatchList(enterpriseBatch);
        ExcelUtil<HzEnterpriseBatch> util = new ExcelUtil<>(HzEnterpriseBatch.class);
        util.exportExcel(response, list, "企业批次数据");
    }

    /**
     * 获取企业批次详细信息
     */
    @PreAuthorize("@ss.hasPermi('system:enterpriseBatch:query')")
    @GetMapping(value = "/{batchId}")
    public AjaxResult getInfo(@PathVariable("batchId") Long batchId) {
        return success(enterpriseBatchService.selectEnterpriseBatchById(batchId));
    }

    /**
     * 新增企业批次
     */
    @PreAuthorize("@ss.hasPermi('system:enterpriseBatch:add')")
    @Log(title = "企业批次", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody HzEnterpriseBatch enterpriseBatch) {
        return toAjax(enterpriseBatchService.insertEnterpriseBatch(enterpriseBatch));
    }

    /**
     * 修改企业批次
     */
    @PreAuthorize("@ss.hasPermi('system:enterpriseBatch:edit')")
    @Log(title = "企业批次", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody HzEnterpriseBatch enterpriseBatch) {
        return toAjax(enterpriseBatchService.updateEnterpriseBatch(enterpriseBatch));
    }

    /**
     * 删除企业批次
     */
    @PreAuthorize("@ss.hasPermi('system:enterpriseBatch:remove')")
    @Log(title = "企业批次", businessType = BusinessType.DELETE)
    @DeleteMapping("/{batchIds}")
    public AjaxResult remove(@PathVariable Long[] batchIds) {
        return toAjax(enterpriseBatchService.deleteEnterpriseBatchByIds(batchIds));
    }

    /**
     * 保存企业批次及房源关联（含费用计算和账单生成）
     */
    @PreAuthorize("@ss.hasPermi('system:enterpriseBatch:add')")
    @Log(title = "保存企业批次", businessType = BusinessType.INSERT)
    @Transactional
    @PostMapping("/saveWithHouses")
    public AjaxResult saveWithHouses(@RequestBody Map<String, Object> params) {
        try {
            // 解析批次信息
            Map<String, Object> batchInfo = (Map<String, Object>) params.get("batchInfo");
            HzEnterpriseBatch enterpriseBatch = new HzEnterpriseBatch();

            if (batchInfo.get("batchId") != null) {
                enterpriseBatch.setBatchId(Long.valueOf(batchInfo.get("batchId").toString()));
            }
            enterpriseBatch.setBatchName(batchInfo.get("batchName").toString());
            enterpriseBatch.setEnterpriseName(batchInfo.get("enterpriseName").toString());
            enterpriseBatch.setContactPerson(batchInfo.get("contactPerson").toString());
            enterpriseBatch.setContactPhone(batchInfo.get("contactPhone").toString());
            enterpriseBatch.setProjectIds(batchInfo.get("projectIds").toString());

            if (batchInfo.get("selectionStartDate") != null) {
                enterpriseBatch.setSelectionStartDate(new Date(Long.valueOf(batchInfo.get("selectionStartDate").toString())));
            }
            if (batchInfo.get("selectionEndDate") != null) {
                enterpriseBatch.setSelectionEndDate(new Date(Long.valueOf(batchInfo.get("selectionEndDate").toString())));
            }
            // 新增字段：入驻日期、截止日期
            if (batchInfo.get("checkInDate") != null) {
                enterpriseBatch.setCheckInDate(new Date(Long.valueOf(batchInfo.get("checkInDate").toString())));
            }
            if (batchInfo.get("checkOutDate") != null) {
                enterpriseBatch.setCheckOutDate(new Date(Long.valueOf(batchInfo.get("checkOutDate").toString())));
            }
            // 合同文件
            if (batchInfo.get("contractFile") != null) {
                enterpriseBatch.setContractFile(batchInfo.get("contractFile").toString());
            }
            if (batchInfo.get("remark") != null) {
                enterpriseBatch.setRemark(batchInfo.get("remark").toString());
            }
            if (batchInfo.get("houseCount") != null) {
                enterpriseBatch.setHouseCount(Integer.valueOf(batchInfo.get("houseCount").toString()));
            }

            // 解析房源列表（包含租金信息用于计算费用）
            List<Map<String, Object>> houseList = (List<Map<String, Object>>) params.get("houseList");
            List<Long> houseIds = new ArrayList<>();
            java.math.BigDecimal totalRent = java.math.BigDecimal.ZERO;
            if (houseList != null) {
                for (Map<String, Object> house : houseList) {
                    houseIds.add(Long.valueOf(house.get("houseId").toString()));
                    // 累加租金
                    if (house.get("rentPrice") != null) {
                        totalRent = totalRent.add(new java.math.BigDecimal(house.get("rentPrice").toString()));
                    }
                }
            }

            // 保存批次
            int result = enterpriseBatchService.saveEnterpriseBatchWithHouses(enterpriseBatch, houseIds);
            if (result <= 0) {
                return error("保存批次失败");
            }

            // 计算费用并生成账单
            Date checkInDate = enterpriseBatch.getCheckInDate();
            Date checkOutDate = enterpriseBatch.getCheckOutDate();

            if (checkInDate != null && checkOutDate != null) {
                // 计算月数（不满一个月算一个月）
                int months = enterpriseBillService.calculateMonths(checkInDate, checkOutDate);

                // 获取优惠金额
                java.math.BigDecimal discountAmount = java.math.BigDecimal.ZERO;
                if (batchInfo.get("discountAmount") != null) {
                    discountAmount = new java.math.BigDecimal(batchInfo.get("discountAmount").toString());
                }

                // 计算总价和优惠后总价
                java.math.BigDecimal totalAmount = totalRent.multiply(new java.math.BigDecimal(months));
                java.math.BigDecimal finalAmount = totalAmount.subtract(discountAmount);
                if (finalAmount.compareTo(java.math.BigDecimal.ZERO) < 0) {
                    finalAmount = java.math.BigDecimal.ZERO;
                }

                // 生成账单编号
                String billNo = enterpriseBillService.generateBillNo();

                // 创建账单
                HzEnterpriseBill bill = new HzEnterpriseBill();
                bill.setBillNo(billNo);
                bill.setBatchId(enterpriseBatch.getBatchId());
                bill.setBatchNo(enterpriseBatch.getBatchNo());
                bill.setBatchName(enterpriseBatch.getBatchName());
                bill.setEnterpriseName(enterpriseBatch.getEnterpriseName());
                bill.setContactPerson(enterpriseBatch.getContactPerson());
                bill.setContactPhone(enterpriseBatch.getContactPhone());
                bill.setHouseCount(enterpriseBatch.getHouseCount());
                bill.setTotalAmount(totalAmount);
                bill.setDiscountAmount(discountAmount);
                bill.setFinalAmount(finalAmount);
                bill.setCheckInDate(checkInDate);
                bill.setCheckOutDate(checkOutDate);
                bill.setMonths(months);
                bill.setContractFile(enterpriseBatch.getContractFile());
                bill.setBillStatus("0"); // 待审核
                bill.setApproveStatus("0"); // 待审核
                bill.setCreateBy(getUsername());
                bill.setCreateTime(new Date());

                enterpriseBillService.insertEnterpriseBill(bill);
            }

            return toAjax(result);
        } catch (Exception e) {
            e.printStackTrace();
            return error("保存失败: " + e.getMessage());
        }
    }

    /**
     * 获取企业批次关联的房源列表
     */
    @PreAuthorize("@ss.hasPermi('system:enterpriseBatch:query')")
    @GetMapping("/{batchId}/houses")
    public AjaxResult getBatchHouses(@PathVariable Long batchId) {
        return success(enterpriseBatchService.getBatchHouses(batchId));
    }
}
