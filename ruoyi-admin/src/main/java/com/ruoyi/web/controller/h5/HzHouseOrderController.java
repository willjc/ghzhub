package com.ruoyi.web.controller.h5;

import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.system.domain.HzDocument;
import com.ruoyi.system.domain.HzHouseOrder;
import com.ruoyi.system.service.IHzDocumentService;
import com.ruoyi.system.service.IHzHouseOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * H5端选房预订单Controller
 *
 * @author ruoyi
 */
@RestController
@RequestMapping("/h5/order")
public class HzHouseOrderController extends BaseController {

    @Autowired
    private IHzHouseOrderService orderService;

    @Autowired
    private IHzDocumentService documentService;

    /**
     * 选房确认，创建预订单
     * 请求体：{ "tenantId": 1, "houseId": 101 }
     */
    @PostMapping("/create")
    public AjaxResult createOrder(@RequestBody Map<String, Long> body) {
        Long tenantId = body.get("tenantId");
        Long houseId = body.get("houseId");
        if (tenantId == null || houseId == null) {
            return error("参数不完整");
        }
        return orderService.createOrder(tenantId, houseId);
    }

    /**
     * 查询订单状态及剩余时间
     *
     * @param orderNo 预订单号
     */
    @GetMapping("/status/{orderNo}")
    public AjaxResult getOrderStatus(@PathVariable String orderNo) {
        Map<String, Object> status = orderService.getOrderStatus(orderNo);
        if (status == null) {
            return error("订单不存在");
        }
        return success(status);
    }

    /**
     * 用户主动取消预订单
     * 请求体：{ "orderNo": "HO...", "tenantId": 1 }
     */
    @PostMapping("/cancel")
    public AjaxResult cancelOrder(@RequestBody Map<String, Object> body) {
        String orderNo = (String) body.get("orderNo");
        Long tenantId = ((Number) body.get("tenantId")).longValue();
        orderService.cancelOrder(orderNo, tenantId);
        return success();
    }

    /**
     * 获取用户待上传资料的订单列表（含倒计时 + 工作证明/学历证明状态）
     *
     * <p>返回结构：每个订单含 orderNo / docRemainSeconds / contractId 等基础字段，
     * 以及 workProof（type=3 工作证明）和 eduProof（type=2 学历证明）两个对象，
     * 形如 {documentId, auditStatus, auditOpinion, filePath}，用于前端渲染状态卡。</p>
     *
     * @param tenantId 租户ID
     */
    @GetMapping("/pending-upload/{tenantId}")
    public AjaxResult getPendingUploadOrders(@PathVariable Long tenantId) {
        List<HzHouseOrder> orders = orderService.getPendingUploadOrders(tenantId);
        List<Map<String, Object>> result = new ArrayList<>();
        // 一次性查询该 tenant 的全部资料，按类型取最新一条
        List<HzDocument> allDocs = documentService.selectDocumentListByTenantId(tenantId);
        HzDocument latestWorkProof = pickLatestByType(allDocs, "3");
        HzDocument latestEduProof  = pickLatestByType(allDocs, "2");
        Map<String, Object> workProofMap = toDocMap(latestWorkProof);
        Map<String, Object> eduProofMap  = toDocMap(latestEduProof);
        for (HzHouseOrder order : orders) {
            Map<String, Object> map = new HashMap<>();
            map.put("orderId", order.getOrderId());
            map.put("orderNo", order.getOrderNo());
            map.put("contractId", order.getContractId());
            map.put("houseId", order.getHouseId());
            map.put("docRemainSeconds", order.getDocRemainSeconds());
            map.put("docUploadExpireTime", order.getDocUploadExpireTime());
            map.put("workProof", workProofMap);
            map.put("eduProof", eduProofMap);
            result.add(map);
        }
        return success(result);
    }

    /** 按 documentType 从资料列表挑出最新的一条（按 documentId 倒序最大值） */
    private HzDocument pickLatestByType(List<HzDocument> docs, String type) {
        if (docs == null || docs.isEmpty()) return null;
        HzDocument best = null;
        for (HzDocument d : docs) {
            if (!type.equals(d.getDocumentType())) continue;
            if ("1".equals(d.getDelFlag())) continue;
            if (best == null || (d.getDocumentId() != null
                    && (best.getDocumentId() == null || d.getDocumentId() > best.getDocumentId()))) {
                best = d;
            }
        }
        return best;
    }

    /** 转换资料对象为前端需要的字段子集 */
    private Map<String, Object> toDocMap(HzDocument doc) {
        if (doc == null) return null;
        Map<String, Object> m = new HashMap<>();
        m.put("documentId", doc.getDocumentId());
        m.put("documentType", doc.getDocumentType());
        m.put("auditStatus", doc.getAuditStatus());
        m.put("auditOpinion", doc.getAuditOpinion());
        m.put("filePath", doc.getFilePath());
        m.put("documentName", doc.getDocumentName());
        return m;
    }

    /**
     * 入住前置检查：是否存在未完成的待上传资料订单
     *
     * @param tenantId 租户ID
     */
    @GetMapping("/checkin-check/{tenantId}")
    public AjaxResult checkinCheck(@PathVariable Long tenantId) {
        return success(orderService.checkinCheck(tenantId));
    }
}
