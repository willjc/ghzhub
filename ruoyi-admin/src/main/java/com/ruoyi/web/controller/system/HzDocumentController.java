package com.ruoyi.web.controller.system;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.ruoyi.common.constant.HttpStatus;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.page.PageDomain;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.core.page.TableSupport;
import com.ruoyi.system.service.IHzDocumentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * 资料审核管理Controller（管理端）
 *
 * <p>列表查询带筛选 + 联表用户/合同/项目/房源，审核动作复用 /h5/document/audit。</p>
 *
 * @author ruoyi
 */
@RestController("adminDocumentController")
@RequestMapping("/system/document")
public class HzDocumentController extends BaseController {

    @Autowired
    private IHzDocumentService documentService;

    /**
     * 分页查询资料文档列表
     */
    @PreAuthorize("@ss.hasPermi('gangzhu:document:list')")
    @GetMapping("/list")
    public TableDataInfo list(
            @RequestParam(value = "auditStatus", required = false) String auditStatus,
            @RequestParam(value = "documentType", required = false) String documentType,
            @RequestParam(value = "tenantName", required = false) String tenantName,
            @RequestParam(value = "contractNo", required = false) String contractNo,
            @RequestParam(value = "startTime", required = false) String startTime,
            @RequestParam(value = "endTime", required = false) String endTime) {
        PageDomain pageDomain = TableSupport.buildPageRequest();
        Integer pageNum = pageDomain.getPageNum();
        Integer pageSize = pageDomain.getPageSize();
        if (pageNum == null || pageNum <= 0) pageNum = 1;
        if (pageSize == null || pageSize <= 0) pageSize = 10;

        Map<String, Object> query = new HashMap<>();
        query.put("auditStatus", auditStatus);
        query.put("documentType", documentType);
        query.put("tenantName", tenantName);
        query.put("contractNo", contractNo);
        // 时间区间补全到日界限，避免漏掉边界数据
        if (startTime != null && !startTime.isEmpty() && startTime.length() == 10) {
            startTime = startTime + " 00:00:00";
        }
        if (endTime != null && !endTime.isEmpty() && endTime.length() == 10) {
            endTime = endTime + " 23:59:59";
        }
        query.put("startTime", startTime);
        query.put("endTime", endTime);

        IPage<Map<String, Object>> page = documentService.selectDocumentPageForAdmin(query, pageNum, pageSize);

        TableDataInfo rspData = new TableDataInfo();
        rspData.setCode(HttpStatus.SUCCESS);
        rspData.setMsg("查询成功");
        rspData.setRows(page.getRecords());
        rspData.setTotal(page.getTotal());
        return rspData;
    }
}
