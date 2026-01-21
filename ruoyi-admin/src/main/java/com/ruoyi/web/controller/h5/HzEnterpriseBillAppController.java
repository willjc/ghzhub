package com.ruoyi.web.controller.h5;

import com.ruoyi.common.config.RuoYiConfig;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.common.utils.file.FileUploadUtils;
import com.ruoyi.system.domain.HzEnterpriseBill;
import com.ruoyi.system.domain.HzEnterpriseBatchHouse;
import com.ruoyi.system.domain.HzEnterpriseBatch;
import com.ruoyi.system.mapper.HzEnterpriseBatchHouseMapper;
import com.ruoyi.system.mapper.HzEnterpriseBatchMapper;
import com.ruoyi.system.service.IHzEnterpriseBillService;
import com.ruoyi.system.service.IHzEnterpriseCheckoutService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import jakarta.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.OutputStream;
import java.util.*;

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

    @Autowired
    private IHzEnterpriseCheckoutService checkoutService;

    @Autowired
    private HzEnterpriseBatchHouseMapper batchHouseMapper;

    @Autowired
    private HzEnterpriseBatchMapper enterpriseBatchMapper;

    @Autowired
    private ResourceLoader resourceLoader;

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
     * 获取已支付的账单列表（用于入住办理）
     * 返回所有已支付账单，前端根据 personnelFile 判断是否已上传人员名单
     */
    @GetMapping("/paidBills")
    public AjaxResult getPaidBills(@RequestParam String phone) {
        if (phone == null || phone.trim().isEmpty()) {
            return error("联系方式不能为空");
        }
        List<HzEnterpriseBill> allBills = enterpriseBillService.selectBillsByContactPhone(phone);
        // 过滤出已支付状态的账单（不管是否已上传人员名单）
        List<HzEnterpriseBill> paidBills = new ArrayList<>();
        for (HzEnterpriseBill bill : allBills) {
            if ("2".equals(bill.getBillStatus())) {
                paidBills.add(bill);
            }
        }
        return success(paidBills);
    }

    /**
     * 获取入住办理信息（账单+房源信息）
     */
    @GetMapping("/checkinInfo/{billId}")
    public AjaxResult getCheckinInfo(@PathVariable("billId") Long billId) {
        HzEnterpriseBill bill = enterpriseBillService.selectEnterpriseBillById(billId);
        if (bill == null) {
            return error("账单不存在");
        }

        // 构建返回数据
        Map<String, Object> result = new HashMap<>();
        result.put("bill", bill);

        // 获取批次信息（包含项目名称）
        String batchName = bill.getBatchName();  // 账单中已包含批次名称
        String projectNames = "";  // 项目名称列表

        // 获取项目名称和房间号
        List<String> houseNos = new ArrayList<>();
        List<String> projectNameList = new ArrayList<>();

        if (bill.getBatchId() != null) {
            // 获取批次详情（包含项目名称）
            HzEnterpriseBatch batch = enterpriseBatchMapper.selectEnterpriseBatchById(bill.getBatchId());
            if (batch != null) {
                batchName = batch.getBatchName();
                if (StringUtils.isNotEmpty(batch.getProjectName())) {
                    projectNames = batch.getProjectName();
                }
            }

            // 获取房源详细信息（含房间号）
            List<Map<String, Object>> houseDetails = batchHouseMapper.selectHouseDetailsByBatchId(bill.getBatchId());
            if (houseDetails != null && !houseDetails.isEmpty()) {
                for (Map<String, Object> houseDetail : houseDetails) {
                    String houseNo = (String) houseDetail.get("house_no");
                    if (StringUtils.isNotEmpty(houseNo)) {
                        houseNos.add(houseNo);
                    }
                    // 如果批次没有项目名称，从房源中获取
                    if (StringUtils.isEmpty(projectNames)) {
                        String projectName = (String) houseDetail.get("project_name");
                        if (StringUtils.isNotEmpty(projectName) && !projectNameList.contains(projectName)) {
                            projectNameList.add(projectName);
                        }
                    }
                }
            }
        }

        // 如果从批次没获取到项目名称，使用从房源获取的列表
        if (StringUtils.isEmpty(projectNames) && !projectNameList.isEmpty()) {
            projectNames = String.join(", ", projectNameList);
        }

        result.put("batchName", batchName);           // 批次名称
        result.put("projectNames", projectNames);      // 项目名称（逗号分隔）
        result.put("houseNos", String.join(", ", houseNos));  // 房间号（逗号分隔）

        // 获取批次关联的房源（保留原有逻辑）
        if (bill.getBatchId() != null) {
            List<HzEnterpriseBatchHouse> houses = batchHouseMapper.selectHousesByBatchId(bill.getBatchId());
            result.put("houses", houses);
            result.put("houseCount", houses != null ? houses.size() : 0);
        } else {
            result.put("houses", new ArrayList<>());
            result.put("houseCount", 0);
        }

        return success(result);
    }

    /**
     * 提交入住办理（上传人员名单）
     */
    @PostMapping("/submitCheckin")
    public AjaxResult submitCheckin(@RequestParam("billId") Long billId,
                                     @RequestParam(value = "file", required = false) MultipartFile file) {
        try {
            String personnelFile = null;
            if (file != null && !file.isEmpty()) {
                // 上传文件
                String fileName = FileUploadUtils.upload(RuoYiConfig.getUploadPath(), file);
                personnelFile = fileName;
            }

            int result = enterpriseBillService.submitCheckin(billId, personnelFile);
            return toAjax(result);
        } catch (Exception e) {
            return error("上传失败：" + e.getMessage());
        }
    }

    /**
     * 下载人员名单Excel模版
     */
    @GetMapping("/downloadTemplate")
    public void downloadTemplate(HttpServletResponse response) {
        try {
            // 从静态资源目录读取模版文件
            Resource resource = resourceLoader.getResource("classpath:static/template/人员名单模版.xlsx");
            if (!resource.exists()) {
                // 如果模版文件不存在，返回提示
                response.setContentType("text/plain;charset=UTF-8");
                response.getWriter().write("模版文件不存在，请联系管理员");
                return;
            }

            File file = resource.getFile();
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            response.setHeader("Content-Disposition", "attachment; filename=personnel_template.xlsx");

            try (FileInputStream fis = new FileInputStream(file);
                 OutputStream os = response.getOutputStream()) {
                byte[] buffer = new byte[1024];
                int len;
                while ((len = fis.read(buffer)) > 0) {
                    os.write(buffer, 0, len);
                }
            }
        } catch (Exception e) {
            logger.error("下载模版文件失败", e);
        }
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

    /**
     * 获取可退租账单列表（已支付且已上传人员名单）
     */
    @GetMapping("/checkoutBills")
    public AjaxResult getCheckoutBills(@RequestParam String phone) {
        if (phone == null || phone.trim().isEmpty()) {
            return error("联系方式不能为空");
        }

        List<HzEnterpriseBill> allBills = enterpriseBillService.selectBillsByContactPhone(phone);
        List<Map<String, Object>> result = new ArrayList<>();

        for (HzEnterpriseBill bill : allBills) {
            // 只处理已支付且已上传人员名单的账单
            if ("2".equals(bill.getBillStatus()) && StringUtils.isNotEmpty(bill.getPersonnelFile())) {
                Map<String, Object> item = new HashMap<>();
                item.put("billId", bill.getBillId());
                item.put("billNo", bill.getBillNo());
                item.put("batchName", bill.getBatchName());
                item.put("enterpriseName", bill.getEnterpriseName());

                // 获取项目名称和房间号
                String projectNames = "";
                List<String> houseNos = new ArrayList<>();

                if (bill.getBatchId() != null) {
                    HzEnterpriseBatch batch = enterpriseBatchMapper.selectEnterpriseBatchById(bill.getBatchId());
                    if (batch != null && StringUtils.isNotEmpty(batch.getProjectName())) {
                        projectNames = batch.getProjectName();
                    }

                    List<Map<String, Object>> houseDetails = batchHouseMapper.selectHouseDetailsByBatchId(bill.getBatchId());
                    if (houseDetails != null && !houseDetails.isEmpty()) {
                        for (Map<String, Object> houseDetail : houseDetails) {
                            String houseNo = (String) houseDetail.get("house_no");
                            if (StringUtils.isNotEmpty(houseNo)) {
                                houseNos.add(houseNo);
                            }
                        }
                    }
                }

                item.put("projectNames", projectNames);
                item.put("houseNos", String.join(", ", houseNos));
                item.put("rentPeriod", formatDate(bill.getCheckInDate()) + " 至 " + formatDate(bill.getCheckOutDate()));
                item.put("finalAmount", bill.getFinalAmount());

                // 检查是否已申请退租
                var checkout = checkoutService.selectCheckoutByBillId(bill.getBillId());
                item.put("checkoutStatus", checkout != null ? "applied" : "pending");

                result.add(item);
            }
        }

        return success(result);
    }

    /**
     * 提交退租申请
     */
    @PostMapping("/submitCheckout")
    public AjaxResult submitCheckout(@RequestBody(required = false) com.alibaba.fastjson2.JSONObject params) {
        if (params == null) {
            return error("参数不能为空");
        }
        Long billId = params.getLong("billId");

        if (billId == null) {
            return error("账单ID不能为空");
        }

        try {
            int result = checkoutService.submitCheckout(billId);
            return toAjax(result);
        } catch (Exception e) {
            return error(e.getMessage());
        }
    }

    /**
     * 格式化日期
     */
    private String formatDate(Date date) {
        if (date == null) {
            return "";
        }
        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd");
        return sdf.format(date);
    }
}
