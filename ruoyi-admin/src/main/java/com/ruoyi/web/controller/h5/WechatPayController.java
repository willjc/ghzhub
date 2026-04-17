package com.ruoyi.web.controller.h5;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.utils.DateUtils;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.ruoyi.system.domain.HzBill;
import com.ruoyi.system.domain.HzContract;
import com.ruoyi.system.domain.HzHouse;
import com.ruoyi.system.mapper.HzBillMapper;
import com.ruoyi.system.mapper.HzContractMapper;
import com.ruoyi.system.mapper.HzHouseMapper;
import com.ruoyi.system.service.IHzHouseOrderService;
import com.ruoyi.system.service.IHzUserMessageService;
import com.ruoyi.system.service.WechatPayService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

/**
 * 微信支付 Controller
 * 提供预支付、回调通知、支付结果查询接口
 */
@RestController
@RequestMapping("/h5/pay/wechat")
@ConditionalOnProperty(prefix = "wechat.pay", name = "enabled", havingValue = "true")
public class WechatPayController extends BaseController {

    @Value("${wechat.pay.h5-notify-url}")
    private String notifyUrl;

    @Autowired
    private WechatPayService wechatPayService;

    @Autowired
    private HzBillMapper billMapper;

    @Autowired
    private IHzHouseOrderService houseOrderService;

    @Autowired
    private HzContractMapper contractMapper;

    @Autowired
    private HzHouseMapper houseMapper;

    @Autowired
    private IHzUserMessageService messageService;

    /**
     * 预支付
     * 请求体：{ billNo, payType("jsapi"|"h5"), openid?, clientIp? }
     */
    @PostMapping("/prepay")
    public AjaxResult prepay(@RequestBody Map<String, Object> params,
                             HttpServletRequest request) {
        String billNo   = (String) params.get("billNo");
        String payType  = (String) params.get("payType");
        String openid   = (String) params.get("openid");
        String clientIp = params.containsKey("clientIp")
                ? (String) params.get("clientIp")
                : getClientIp(request);

        if (billNo == null || billNo.isEmpty()) {
            return error("账单号不能为空");
        }

        // 先按 billNo 查，查不到再按 billId（纯数字时）兜底
        LambdaQueryWrapper<HzBill> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(HzBill::getBillNo, billNo)
               .eq(HzBill::getDelFlag, "0")
               .last("LIMIT 1");
        HzBill bill = billMapper.selectOne(wrapper);
        if (bill == null && billNo.matches("\\d+")) {
            bill = billMapper.selectById(Long.parseLong(billNo));
        }
        if (bill == null) return error("账单不存在");
        if ("1".equals(bill.getBillStatus())) return error("账单已支付");

        // 如果是押金账单（bill_type='1'），检查合同是否还在有效期内
        if ("1".equals(bill.getBillType())) {
            HzContract contract = contractMapper.selectById(bill.getContractId());
            if (contract == null || !"2".equals(contract.getContractStatus())) {
                return error("合同已失效，无法支付押金");
            }
            // 检查是否在30分钟窗口内
            String signTimeStr = contract.getSignTime();
            if (signTimeStr != null && !signTimeStr.isEmpty()) {
                try {
                    String s = signTimeStr.replace("T", " ");
                    java.util.Date signTime = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(s);
                    long elapsed = System.currentTimeMillis() - signTime.getTime();
                    if (elapsed > 30 * 60 * 1000L) {
                        return error("合同已超时失效，请重新签约");
                    }
                } catch (Exception ignored) {}
            }
        }

        int totalFen = bill.getUnpaidAmount()
                           .multiply(new BigDecimal("100"))
                           .intValue();
        String desc = "港好住-押金缴纳";

        try {
            if ("jsapi".equals(payType)) {
                if (openid == null || openid.isEmpty()) return error("JSAPI 支付需要传入 openid");
                Map<String, String> jsapiParams = wechatPayService.prepayJsapi(
                        billNo, totalFen, desc, openid, notifyUrl);
                return success(jsapiParams);

            } else if ("h5".equals(payType)) {
                String mwebUrl = wechatPayService.prepayH5(
                        billNo, totalFen, desc, clientIp, notifyUrl);
                Map<String, String> result = new HashMap<>();
                result.put("mwebUrl", mwebUrl);
                return success(result);

            } else {
                return error("不支持的支付类型：" + payType);
            }
        } catch (Exception e) {
            logger.error("微信预支付失败，billNo={}", billNo, e);
            return error("预支付失败：" + e.getMessage());
        }
    }

    /**
     * 微信支付结果回调
     * /h5/** 已在 SecurityConfig 中 permitAll，无需额外配置
     */
    @PostMapping("/notify")
    public ResponseEntity<Map<String, String>> notify(HttpServletRequest request) {
        Map<String, String> resp = new HashMap<>();
        try {
            byte[] body = request.getInputStream().readAllBytes();
            logger.info("【微信回调】收到通知，body长度={}", body.length);

            Map<String, String> headers = new HashMap<>();
            Enumeration<String> headerNames = request.getHeaderNames();
            while (headerNames.hasMoreElements()) {
                String name = headerNames.nextElement();
                headers.put(name, request.getHeader(name));
            }
            logger.info("【微信回调】Wechatpay-Serial={}, Wechatpay-Timestamp={}",
                    headers.get("wechatpay-serial"), headers.get("wechatpay-timestamp"));

            Map<String, Object> notifyData = wechatPayService.parseNotify(body, headers);
            logger.info("【微信回调】验签+解密成功，notifyData={}", notifyData);

            String tradeState    = (String) notifyData.get("trade_state");
            String outTradeNo    = (String) notifyData.get("out_trade_no");
            String transactionId = (String) notifyData.get("transaction_id");

            if (!"SUCCESS".equals(tradeState)) {
                logger.warn("【微信回调】支付未成功，tradeState={}, outTradeNo={}", tradeState, outTradeNo);
                resp.put("code", "SUCCESS");
                resp.put("message", "成功");
                return ResponseEntity.ok(resp);
            }

            LambdaQueryWrapper<HzBill> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(HzBill::getBillNo, outTradeNo)
                   .eq(HzBill::getDelFlag, "0")
                   .last("LIMIT 1");
            HzBill bill = billMapper.selectOne(wrapper);

            if (bill == null) {
                logger.error("【微信回调】按 billNo={} 查不到账单，请检查 out_trade_no 是否与 hz_bill.bill_no 一致", outTradeNo);
                // 仍返回 SUCCESS 防止微信无限重试
                resp.put("code", "SUCCESS");
                resp.put("message", "成功");
                return ResponseEntity.ok(resp);
            }

            if ("1".equals(bill.getBillStatus())) {
                logger.info("【微信回调】账单已支付（幂等），outTradeNo={}", outTradeNo);
            } else {
                bill.setBillStatus("1");
                bill.setPaidAmount(bill.getBillAmount());
                bill.setUnpaidAmount(BigDecimal.ZERO);
                bill.setPayTime(DateUtils.getTime());
                bill.setPayMethod("wechat");
                bill.setTransactionNo(transactionId);
                billMapper.updateById(bill);
                logger.info("【微信回调】账单状态已更新为已支付，billId={}, outTradeNo={}", bill.getBillId(), outTradeNo);

                // 发送支付成功消息
                try {
                    String amount = bill.getBillAmount() != null ? bill.getBillAmount().toString() : "0";
                    if ("1".equals(bill.getBillType())) {
                        messageService.sendMessage(bill.getTenantId(), "bill", "押金缴纳成功",
                                "您的押金（¥" + amount + "）已缴纳成功");
                    } else if ("2".equals(bill.getBillType())) {
                        String period = bill.getBillPeriod() != null ? bill.getBillPeriod() : "";
                        messageService.sendMessage(bill.getTenantId(), "bill", "房租缴纳成功",
                                "您" + period + "的房租（¥" + amount + "）已缴纳成功");
                    }
                } catch (Exception msgEx) {
                    logger.warn("【微信回调】发送支付消息失败，不影响主流程: {}", msgEx.getMessage());
                }

                // 押金账单 → 触发订单状态推进 + 房源状态更新
                if ("1".equals(bill.getBillType())) {
                    if (bill.getOrderNo() != null) {
                        houseOrderService.onDepositPaid(bill.getOrderNo());
                        logger.info("【微信回调】押金账单已触发 onDepositPaid，orderNo={}", bill.getOrderNo());
                    } else {
                        // 直签合同模式：押金支付成功后，将房源状态从已预订改为已出租
                        HzContract contract = contractMapper.selectById(bill.getContractId());
                        if (contract != null && contract.getHouseId() != null) {
                            houseMapper.update(null, new LambdaUpdateWrapper<HzHouse>()
                                .eq(HzHouse::getHouseId, contract.getHouseId())
                                .eq(HzHouse::getHouseStatus, "1")
                                .set(HzHouse::getHouseStatus, "2"));
                            // 合同状态推进到 '3'（履行中）
                            contractMapper.update(null, new LambdaUpdateWrapper<HzContract>()
                                .eq(HzContract::getContractId, contract.getContractId())
                                .set(HzContract::getContractStatus, "3"));
                            logger.info("【微信回调】直签合同押金支付成功，房源状态→已出租，合同状态→履行中, contractId={}", contract.getContractId());
                        }
                    }
                }
            }

            resp.put("code", "SUCCESS");
            resp.put("message", "成功");
            return ResponseEntity.ok(resp);

        } catch (Exception e) {
            logger.error("【微信回调】处理失败，原因：{}", e.getMessage(), e);
            resp.put("code", "FAIL");
            resp.put("message", "处理失败");
            return ResponseEntity.status(500).body(resp);
        }
    }

    /**
     * 主动向微信查单并同步账单状态（回调未到达时的兜底接口）
     * 前端支付成功后可主动调用，确保账单状态与微信一致
     */
    @PostMapping("/sync/{billNo}")
    public AjaxResult syncPayResult(@PathVariable String billNo) {
        // 1. 从数据库查账单
        LambdaQueryWrapper<HzBill> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(HzBill::getBillNo, billNo)
               .eq(HzBill::getDelFlag, "0")
               .last("LIMIT 1");
        HzBill bill = billMapper.selectOne(wrapper);
        if (bill == null && billNo.matches("\\d+")) {
            bill = billMapper.selectById(Long.parseLong(billNo));
        }
        if (bill == null) return error("账单不存在");

        // 已支付无需同步
        if ("1".equals(bill.getBillStatus())) {
            Map<String, Object> data = new HashMap<>();
            data.put("paid", true);
            data.put("billStatus", "1");
            return success(data);
        }

        // 2. 向微信主动查单
        try {
            Map<String, Object> wxResult = wechatPayService.queryByOutTradeNo(
                    bill.getBillNo() != null ? bill.getBillNo() : String.valueOf(bill.getBillId()));
            String tradeState = (String) wxResult.get("trade_state");
            String transactionId = (String) wxResult.get("transaction_id");

            Map<String, Object> data = new HashMap<>();
            data.put("tradeState", tradeState);

            if ("SUCCESS".equals(tradeState)) {
                // 3. 微信已支付，更新本地账单
                bill.setBillStatus("1");
                bill.setPaidAmount(bill.getBillAmount());
                bill.setUnpaidAmount(BigDecimal.ZERO);
                bill.setPayTime(DateUtils.getTime());
                bill.setPayMethod("wechat");
                bill.setTransactionNo(transactionId);
                billMapper.updateById(bill);

                // 发送支付成功消息
                try {
                    String amount = bill.getBillAmount() != null ? bill.getBillAmount().toString() : "0";
                    if ("1".equals(bill.getBillType())) {
                        messageService.sendMessage(bill.getTenantId(), "bill", "押金缴纳成功",
                                "您的押金（¥" + amount + "）已缴纳成功");
                    } else if ("2".equals(bill.getBillType())) {
                        String period = bill.getBillPeriod() != null ? bill.getBillPeriod() : "";
                        messageService.sendMessage(bill.getTenantId(), "bill", "房租缴纳成功",
                                "您" + period + "的房租（¥" + amount + "）已缴纳成功");
                    }
                } catch (Exception msgEx) {
                    logger.warn("主动查单同步-发送支付消息失败，不影响主流程: {}", msgEx.getMessage());
                }

                // 押金账单 → 触发订单状态推进 + 房源状态更新
                if ("1".equals(bill.getBillType())) {
                    if (bill.getOrderNo() != null) {
                        houseOrderService.onDepositPaid(bill.getOrderNo());
                    } else {
                        // 直签合同模式：押金支付成功后，将房源状态从已预订改为已出租
                        HzContract contract = contractMapper.selectById(bill.getContractId());
                        if (contract != null && contract.getHouseId() != null) {
                            houseMapper.update(null, new LambdaUpdateWrapper<HzHouse>()
                                .eq(HzHouse::getHouseId, contract.getHouseId())
                                .eq(HzHouse::getHouseStatus, "1")
                                .set(HzHouse::getHouseStatus, "2"));
                            contractMapper.update(null, new LambdaUpdateWrapper<HzContract>()
                                .eq(HzContract::getContractId, contract.getContractId())
                                .set(HzContract::getContractStatus, "3"));
                            logger.info("主动查单同步-直签合同押金支付成功，房源状态→已出租，合同状态→履行中, contractId={}", contract.getContractId());
                        }
                    }
                }

                logger.info("主动查单同步成功，billNo={}", billNo);
                data.put("paid", true);
                data.put("billStatus", "1");
            } else {
                data.put("paid", false);
                data.put("billStatus", bill.getBillStatus());
            }
            return success(data);

        } catch (Exception e) {
            logger.error("主动查单失败，billNo={}", billNo, e);
            return error("查单失败：" + e.getMessage());
        }
    }

    /**
     * 前端轮询支付结果
     */
    @GetMapping("/query/{billNo}")
    public AjaxResult queryPayResult(@PathVariable String billNo) {
        LambdaQueryWrapper<HzBill> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(HzBill::getBillNo, billNo)
               .eq(HzBill::getDelFlag, "0")
               .last("LIMIT 1");
        HzBill bill = billMapper.selectOne(wrapper);
        if (bill == null) return error("账单不存在");

        Map<String, Object> result = new HashMap<>();
        result.put("billStatus", bill.getBillStatus());
        result.put("paid", "1".equals(bill.getBillStatus()));
        result.put("transactionNo", bill.getTransactionNo());
        result.put("payTime", bill.getPayTime());
        return success(result);
    }

    private String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("X-Real-IP");
        }
        if (ip == null || ip.isEmpty()) {
            ip = request.getRemoteAddr();
        }
        if (ip != null && ip.contains(",")) {
            ip = ip.split(",")[0].trim();
        }
        return ip;
    }
}
