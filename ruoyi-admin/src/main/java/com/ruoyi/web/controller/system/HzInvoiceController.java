package com.ruoyi.web.controller.system;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.system.domain.HzBill;
import com.ruoyi.system.domain.HzInvoiceApply;
import com.ruoyi.system.mapper.HzBillMapper;
import com.ruoyi.system.mapper.HzInvoiceApplyMapper;
import com.ruoyi.system.service.IHzInvoiceApplyService;
import com.ruoyi.system.service.IHzInvoiceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import jakarta.servlet.http.HttpServletResponse;
import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * 开票管理Controller
 *
 * @author ruoyi
 */
@RestController("adminInvoiceController")
@RequestMapping("/gangzhu/invoice")
public class HzInvoiceController extends BaseController
{
    @Autowired
    private IHzInvoiceApplyService invoiceApplyService;

    @Autowired
    private IHzInvoiceService invoiceService;

    @Autowired
    private HzInvoiceApplyMapper invoiceApplyMapper;

    @Autowired
    private HzBillMapper billMapper;

    /**
     * 查询开票申请列表
     */
    @PreAuthorize("@ss.hasPermi('gangzhu:invoice:list')")
    @GetMapping("/list")
    public TableDataInfo list(HzInvoiceApply invoiceApply)
    {
        startPage();
        LambdaQueryWrapper<HzInvoiceApply> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(StringUtils.isNotEmpty(invoiceApply.getApplyNo()), HzInvoiceApply::getApplyNo, invoiceApply.getApplyNo())
               .like(StringUtils.isNotEmpty(invoiceApply.getInvoiceTitle()), HzInvoiceApply::getInvoiceTitle, invoiceApply.getInvoiceTitle())
               .eq(StringUtils.isNotEmpty(invoiceApply.getApplyStatus()), HzInvoiceApply::getApplyStatus, invoiceApply.getApplyStatus())
               .eq(HzInvoiceApply::getDelFlag, "0")
               .orderByDesc(HzInvoiceApply::getCreateTime);
        List<HzInvoiceApply> list = invoiceApplyMapper.selectList(wrapper);
        return getDataTable(list);
    }

    /**
     * 导出开票申请列表
     */
    @PreAuthorize("@ss.hasPermi('gangzhu:invoice:export')")
    @Log(title = "开票管理", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, HzInvoiceApply invoiceApply)
    {
        LambdaQueryWrapper<HzInvoiceApply> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(StringUtils.isNotEmpty(invoiceApply.getApplyNo()), HzInvoiceApply::getApplyNo, invoiceApply.getApplyNo())
               .like(StringUtils.isNotEmpty(invoiceApply.getInvoiceTitle()), HzInvoiceApply::getInvoiceTitle, invoiceApply.getInvoiceTitle())
               .eq(StringUtils.isNotEmpty(invoiceApply.getApplyStatus()), HzInvoiceApply::getApplyStatus, invoiceApply.getApplyStatus())
               .eq(HzInvoiceApply::getDelFlag, "0")
               .orderByDesc(HzInvoiceApply::getCreateTime);
        List<HzInvoiceApply> list = invoiceApplyMapper.selectList(wrapper);
        ExcelUtil<HzInvoiceApply> util = new ExcelUtil<HzInvoiceApply>(HzInvoiceApply.class);
        util.exportExcel(response, list, "开票申请数据");
    }

    /**
     * 获取开票申请详细信息
     */
    @PreAuthorize("@ss.hasPermi('gangzhu:invoice:query')")
    @GetMapping(value = "/{applyId}")
    public AjaxResult getInfo(@PathVariable("applyId") Long applyId)
    {
        HzInvoiceApply invoiceApply = invoiceApplyService.selectInvoiceApplyById(applyId);
        if (invoiceApply == null)
        {
            return error("开票申请不存在");
        }

        Map<String, Object> result = new HashMap<>();
        result.put("apply", invoiceApply);

        // 获取关联账单信息（从bill_ids中获取第一个账单ID）
        if (invoiceApply.getBillIds() != null && !invoiceApply.getBillIds().isEmpty())
        {
            try
            {
                Long firstBillId = Long.valueOf(invoiceApply.getBillIds().split(",")[0].trim());
                HzBill bill = billMapper.selectById(firstBillId);
                result.put("bill", bill);
            }
            catch (Exception e)
            {
                // 忽略解析错误
            }
        }

        // 如果已开票，通过apply_id反向查询发票信息
        if ("2".equals(invoiceApply.getApplyStatus()))
        {
            com.ruoyi.system.domain.HzInvoice invoice = invoiceService.selectInvoiceByApplyId(applyId);
            if (invoice != null)
            {
                result.put("invoice", invoice);
            }
        }

        return success(result);
    }

    /**
     * 完成开票（上传发票图片）
     */
    @PreAuthorize("@ss.hasPermi('gangzhu:invoice:complete')")
    @Log(title = "开票管理", businessType = BusinessType.UPDATE)
    @PostMapping("/complete")
    public AjaxResult complete(@RequestParam("applyId") Long applyId,
                               @RequestParam(value = "invoiceNo", required = false) String invoiceNo,
                               @RequestParam(value = "file", required = false) MultipartFile file)
    {
        HzInvoiceApply invoiceApply = invoiceApplyService.selectInvoiceApplyById(applyId);
        if (invoiceApply == null)
        {
            return error("开票申请不存在");
        }

        if (!"1".equals(invoiceApply.getApplyStatus()))
        {
            return error("当前状态不允许开票");
        }

        // 保存发票图片
        String invoicePdf = null;
        if (file != null && !file.isEmpty())
        {
            try
            {
                // 生成文件名
                String originalFilename = file.getOriginalFilename();
                if (originalFilename == null || originalFilename.isEmpty())
                {
                    return error("文件名不能为空");
                }
                String extension = originalFilename.substring(originalFilename.lastIndexOf("."));
                String newFileName = UUID.randomUUID().toString() + extension;

                // 保存文件
                String uploadPath = "D:/ruoyi/uploadPath/invoice/";
                File dir = new File(uploadPath);
                if (!dir.exists())
                {
                    dir.mkdirs();
                }
                File destFile = new File(uploadPath + newFileName);
                file.transferTo(destFile);

                // 设置相对路径
                invoicePdf = "/profile/invoice/" + newFileName;
            }
            catch (Exception e)
            {
                logger.error("上传发票图片失败", e);
                return error("上传发票图片失败");
            }
        }

        // 创建发票记录
        com.ruoyi.system.domain.HzInvoice invoice = new com.ruoyi.system.domain.HzInvoice();
        invoice.setApplyId(applyId);
        invoice.setTenantId(invoiceApply.getTenantId());
        invoice.setInvoiceNo(StringUtils.isNotEmpty(invoiceNo) ? invoiceNo : generateInvoiceNo());
        invoice.setInvoiceType(invoiceApply.getInvoiceType());
        invoice.setInvoiceTitle(invoiceApply.getInvoiceTitle());
        invoice.setTaxNo(invoiceApply.getTaxNo());
        invoice.setInvoiceAmount(invoiceApply.getInvoiceAmount());
        invoice.setInvoiceDate(DateUtils.getTime());
        invoice.setInvoiceFile(invoicePdf);
        invoice.setInvoiceStatus("0"); // 0=正常
        invoice.setCreateTime(DateUtils.getNowDate());
        invoice.setDelFlag("0");

        int result = invoiceService.insertInvoice(invoice);
        if (result > 0)
        {
            // 更新申请状态为已开票
            invoiceApply.setApplyStatus("2"); // 2=已开票
            invoiceApplyService.updateInvoiceApply(invoiceApply);

            // 更新账单的开票状态为已开票（从bill_ids中获取账单ID）
            if (invoiceApply.getBillIds() != null && !invoiceApply.getBillIds().isEmpty())
            {
                try
                {
                    String[] billIdArray = invoiceApply.getBillIds().split(",");
                    for (String billIdStr : billIdArray)
                    {
                        Long billId = Long.valueOf(billIdStr.trim());
                        LambdaUpdateWrapper<HzBill> wrapper = new LambdaUpdateWrapper<>();
                        wrapper.set(HzBill::getInvoiceStatus, "2") // 2=已开票
                               .eq(HzBill::getBillId, billId);
                        billMapper.update(null, wrapper);
                    }
                }
                catch (Exception e)
                {
                    logger.error("更新账单开票状态失败", e);
                }
            }

            return success(invoice);
        }
        return error("开票失败");
    }

    /**
     * 生成发票编号
     */
    private String generateInvoiceNo()
    {
        return "FP" + DateUtils.dateTimeNow() + String.format("%04d", (int)(Math.random() * 10000));
    }

    /**
     * 删除开票申请
     */
    @PreAuthorize("@ss.hasPermi('gangzhu:invoice:remove')")
    @Log(title = "开票管理", businessType = BusinessType.DELETE)
    @DeleteMapping("/{applyIds}")
    public AjaxResult remove(@PathVariable Long[] applyIds)
    {
        int count = 0;
        for (Long applyId : applyIds)
        {
            HzInvoiceApply invoiceApply = invoiceApplyService.selectInvoiceApplyById(applyId);
            if (invoiceApply != null && "0".equals(invoiceApply.getApplyStatus()))
            {
                // 只能删除待审核状态的申请
                count += invoiceApplyService.deleteInvoiceApplyById(applyId);
            }
        }
        return toAjax(count);
    }
}
