package com.ruoyi.web.controller.h5;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.system.domain.HzBill;
import com.ruoyi.system.domain.HzContract;
import com.ruoyi.system.domain.HzEnterpriseBill;
import com.ruoyi.system.domain.HzHouse;
import com.ruoyi.system.mapper.HzBillMapper;
import com.ruoyi.system.mapper.HzContractMapper;
import com.ruoyi.system.mapper.HzEnterpriseBillMapper;
import com.ruoyi.system.mapper.HzHouseMapper;
import com.ruoyi.system.service.IHzHouseOrderService;
import com.ruoyi.system.service.IHzUserMessageService;
import com.ruoyi.system.service.WechatPayService;

import jakarta.servlet.http.HttpServletRequest;

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

    @Autowired
    private HzEnterpriseBillMapper enterpriseBillMapper;

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

        // 合同已到期(4)或已解约(5)时拒绝支付：合同结束后账单不再允许支付，避免租客错交
        if (bill.getContractId() != null) {
            HzContract contract = contractMapper.selectById(bill.getContractId());
            if (contract != null && ("4".equals(contract.getContractStatus())
                    || "5".equals(contract.getContractStatus()))) {
                return error("合同已到期或已解约，无法支付，请联系管理员");
            }
        }

        // 租金账单（bill_type='2'）按期数顺序校验：不允许跳缴
        if ("2".equals(bill.getBillType()) && bill.getContractId() != null) {
            Integer currentSeq = bill.getBillSeq();
            if (currentSeq != null && currentSeq > 1) {
                LambdaQueryWrapper<HzBill> seqWrapper = new LambdaQueryWrapper<>();
                seqWrapper.eq(HzBill::getContractId, bill.getContractId())
                          .eq(HzBill::getBillType, "2")
                          .ne(HzBill::getBillStatus, "1")
                          .eq(HzBill::getDelFlag, "0")
                          .lt(HzBill::getBillSeq, currentSeq)
                          .last("LIMIT 1");
                HzBill earlierUnpaid = billMapper.selectOne(seqWrapper);
                if (earlierUnpaid != null) {
                    return error("请先缴纳第" + earlierUnpaid.getBillSeq() + "期房租后再支付本期");
                }
            }
        }

        int totalFen = bill.getUnpaidAmount()
                           .multiply(new BigDecimal("100"))
                           .intValue();
        // 根据账单类型动态设置描述，避免与微信首次请求参数不一致
        String desc = buildBillDesc(bill);

        // 旧微信订单清理：防止重复下单拿到已过期的旧 prepay_id 导致"订单已过期失效"
        String outTradeNo = resolveOutTradeNo(bill);
        if (outTradeNo == null) {
            return error("该账单可能已在微信支付，请返回账单页刷新后重试");
        }

        try {
            if ("jsapi".equals(payType)) {
                if (openid == null || openid.isEmpty()) return error("JSAPI 支付需要传入 openid");
                Map<String, String> jsapiParams = prepayJsapiWithRetry(
                        outTradeNo, billNo, totalFen, desc, openid, notifyUrl);
                return success(jsapiParams);

            } else if ("h5".equals(payType)) {
                String mwebUrl = wechatPayService.prepayH5(
                        outTradeNo, totalFen, desc, clientIp, notifyUrl);
                persistLastOutTradeNo(billNo, outTradeNo);
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
     * 根据账单类型构造微信支付描述。
     * 1=押金 2=租金 3=水费 4=电费 5=燃气费 6=物业费 7=其他
     */
    private String buildBillDesc(HzBill bill) {
        String type = bill.getBillType();
        if ("1".equals(type)) return "港好住-押金缴纳";
        if ("2".equals(type)) return "港好住-房租缴纳";
        if ("3".equals(type)) return "港好住-水费缴纳";
        if ("4".equals(type)) return "港好住-电费缴纳";
        if ("5".equals(type)) return "港好住-燃气费缴纳";
        if ("6".equals(type)) return "港好住-物业费缴纳";
        return "港好住-费用缴纳";
    }

    /**
     * 解析本次下单应使用的 out_trade_no。
     * <p>
     * 微信机制：prepay_id 有效期仅 2 小时，且同一 out_trade_no 参数重入下单时
     * 可能返回已过期的旧 prepay_id，收银台会报"订单已过期失效，请重新下单再发起支付"，
     * 用户重试也无法自愈。官方建议：关闭旧订单后换新商户单号下单。
     * <p>
     * 处理规则（仅当 last_out_trade_no 非空、即微信侧已有订单时）：
     * 1. 微信已支付(SUCCESS)   → 返回 null，由调用方引导走查单同步，防止重复支付
     * 2. 支付中(USERPAYING)    → 返回 null，不关单，引导用户到微信完成付款
     * 3. 其它(未支付/已关闭/查询失败) → 关闭旧订单，返回 billNo + -R-时间戳 的新单号
     * 首次下单（last_out_trade_no 为空）直接返回 billNo。
     */
    private String resolveOutTradeNo(HzBill bill) {
        String billNo = bill.getBillNo();
        String lastOutTradeNo = bill.getLastOutTradeNo();
        if (lastOutTradeNo == null || lastOutTradeNo.isEmpty()) {
            return billNo;
        }

        // 向微信查旧订单状态
        String tradeState = null;
        try {
            Map<String, Object> wxResult = wechatPayService.queryByOutTradeNo(lastOutTradeNo);
            tradeState = (String) wxResult.get("trade_state");
        } catch (Exception e) {
            logger.warn("查询旧微信订单状态失败，outTradeNo={}: {}", lastOutTradeNo, e.getMessage());
        }

        if ("SUCCESS".equals(tradeState)) {
            // 微信侧已支付但本地未更新（回调丢失），不允许再次下单，引导走 sync 兜底补单
            logger.info("微信订单已支付但本地账单未更新，拦截重复下单，billNo={}, outTradeNo={}", billNo, lastOutTradeNo);
            return null;
        }
        if ("USERPAYING".equals(tradeState)) {
            // 用户正在微信收银台付款中，不能关单
            logger.info("微信订单支付中，拦截重复下单，billNo={}, outTradeNo={}", billNo, lastOutTradeNo);
            return null;
        }

        // 未支付/已关闭/查询失败：关闭旧订单后换新单号下单
        try {
            wechatPayService.closeOrder(lastOutTradeNo);
            logger.info("已关闭旧微信订单：outTradeNo={}", lastOutTradeNo);
        } catch (Exception e) {
            logger.warn("关闭旧微信订单失败（可能已关闭/不存在），继续换单下单：{}", e.getMessage());
        }
        String freshOutTradeNo = billNo + "-R-" + (System.currentTimeMillis() % 1000000);
        logger.info("换新商户单号下单，billNo={}, freshOutTradeNo={}", billNo, freshOutTradeNo);
        return freshOutTradeNo;
    }

    /**
     * JSAPI 预支付，带"请求重入参数不一致"自动恢复。
     * 微信会缓存首次预下单的参数，若账单金额/描述发生过变化（如迁移数据修正、代码迭代），
     * 同一 out_trade_no 重提交将被拒绝。此时自动关闭旧订单，并使用临时后缀重试。
     *
     * 注意：成功下单后会把实际下到微信的 outTradeNo 写回 hz_bill.last_out_trade_no，
     * 用于回调丢失时主动查单兜底——带 -R- 后缀的单号才是用户实际付款成功的那一笔。
     *
     * @param outTradeNo resolveOutTradeNo 解析出的本次下单单号
     * @param billNo     原始账单号，用于写回 last_out_trade_no 与生成重试单号
     */
    private Map<String, String> prepayJsapiWithRetry(String outTradeNo, String billNo, int totalFen, String desc,
                                                     String openid, String notifyUrl) {
        try {
            Map<String, String> ret = wechatPayService.prepayJsapi(outTradeNo, totalFen, desc, openid, notifyUrl);
            // 首次下单成功，把实际下到微信的 outTradeNo 写回 last_out_trade_no
            persistLastOutTradeNo(billNo, outTradeNo);
            return ret;
        } catch (Exception e) {
            String msg = e.getMessage() == null ? "" : e.getMessage();
            boolean isReentryConflict = msg.contains("INVALID_REQUEST")
                    && (msg.contains("请求重入") || msg.contains("参数与首次请求"));
            if (!isReentryConflict) {
                throw e;
            }
            // 尝试关闭旧订单，失败不阻断后续重试
            try {
                wechatPayService.closeOrder(outTradeNo);
                logger.warn("微信预支付参数冲突，已关闭旧订单：outTradeNo={}", outTradeNo);
            } catch (Exception closeEx) {
                logger.warn("关闭旧微信订单失败（可能已关闭/过期），继续重试：{}", closeEx.getMessage());
            }
            // out_trade_no 上限 32 字符，-R- + 6位 共 9 字符，预留够用
            String retryOutTradeNo = billNo + "-R-" + (System.currentTimeMillis() % 1000000);
            Map<String, String> ret = wechatPayService.prepayJsapi(
                    retryOutTradeNo, totalFen, desc, openid, notifyUrl);
            // 关键：把重试单号持久化到 hz_bill.last_out_trade_no
            // 否则一旦微信回调丢失，syncPayResult / ContractExpireTask 用原始 billNo 查微信
            // 永远拿不到 SUCCESS（原始单号已被关闭），账单状态会一直滞留未支付。
            persistLastOutTradeNo(billNo, retryOutTradeNo);
            logger.info("微信预支付重试成功，原billNo={}, retryOutTradeNo={}", billNo, retryOutTradeNo);
            return ret;
        }
    }

    /**
     * 把实际下单到微信的 outTradeNo 持久化到 hz_bill.last_out_trade_no。
     * 失败仅记录警告，不阻断主支付流程（兜底字段，不强一致）。
     */
    private void persistLastOutTradeNo(String billNo, String outTradeNo) {
        try {
            int rows = billMapper.update(null, new LambdaUpdateWrapper<HzBill>()
                    .eq(HzBill::getBillNo, billNo)
                    .eq(HzBill::getDelFlag, "0")
                    .set(HzBill::getLastOutTradeNo, outTradeNo));
            if (rows == 0) {
                logger.warn("持久化 last_out_trade_no 未命中行，billNo={}, outTradeNo={}", billNo, outTradeNo);
            }
        } catch (Exception e) {
            logger.warn("持久化 last_out_trade_no 失败，billNo={}, outTradeNo={}: {}", billNo, outTradeNo, e.getMessage());
        }
    }

    /**
     * 将可能带重试后缀的 out_trade_no 还原为原始 billNo。
     * 后缀格式：-R-\d+
     */
    private String stripRetrySuffix(String outTradeNo) {
        if (outTradeNo == null) return null;
        return outTradeNo.replaceAll("-R-\\d+$", "");
    }

    /**
     * 企业账单预支付（仅 JSAPI）
     * 请求体：{ billId, openid }
     */
    @PostMapping("/prepayEnterprise")
    public AjaxResult prepayEnterprise(@RequestBody Map<String, Object> params) {
        Object billIdObj = params.get("billId");
        String openid = (String) params.get("openid");

        if (billIdObj == null) return error("账单ID不能为空");
        if (openid == null || openid.isEmpty()) return error("JSAPI 支付需要传入 openid");

        Long billId;
        try {
            billId = Long.valueOf(billIdObj.toString());
        } catch (NumberFormatException e) {
            return error("账单ID格式错误");
        }

        HzEnterpriseBill bill = enterpriseBillMapper.selectEnterpriseBillById(billId);
        if (bill == null) return error("账单不存在");
        if ("2".equals(bill.getBillStatus())) return error("账单已支付");
        if (!"1".equals(bill.getBillStatus())) return error("账单未通过审核，暂不能支付");

        BigDecimal finalAmount = bill.getFinalAmount();
        if (finalAmount == null || finalAmount.compareTo(BigDecimal.ZERO) <= 0) {
            return error("账单金额异常");
        }

        int totalFen = finalAmount.multiply(new BigDecimal("100")).intValue();
        String batchName = bill.getBatchName() != null ? bill.getBatchName() : bill.getBillNo();
        String desc = "港好住-企业批次：" + batchName;
        if (desc.length() > 127) {
            desc = desc.substring(0, 127);
        }

        try {
            Map<String, String> jsapiParams = wechatPayService.prepayJsapi(
                    bill.getBillNo(), totalFen, desc, openid, notifyUrl);
            return success(jsapiParams);
        } catch (Exception e) {
            logger.error("企业账单微信预支付失败，billNo={}", bill.getBillNo(), e);
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
            
            // 如果是重试下单生成的 out_trade_no，还原为原始 billNo 后再查账单
            String lookupBillNo = stripRetrySuffix(outTradeNo);
            
            if (!"SUCCESS".equals(tradeState)) {
                logger.warn("【微信回调】支付未成功，tradeState={}, outTradeNo={}", tradeState, outTradeNo);
                resp.put("code", "SUCCESS");
                resp.put("message", "成功");
                return ResponseEntity.ok(resp);
            }
            
            LambdaQueryWrapper<HzBill> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(HzBill::getBillNo, lookupBillNo)
                   .eq(HzBill::getDelFlag, "0")
                   .last("LIMIT 1");
            HzBill bill = billMapper.selectOne(wrapper);
            
            if (bill == null) {
                // 兜底：尝试匹配企业账单
                LambdaQueryWrapper<HzEnterpriseBill> ebWrapper = new LambdaQueryWrapper<>();
                ebWrapper.eq(HzEnterpriseBill::getBillNo, lookupBillNo)
                         .eq(HzEnterpriseBill::getDelFlag, "0")
                         .last("LIMIT 1");
                HzEnterpriseBill enterpriseBill = enterpriseBillMapper.selectOne(ebWrapper);
                if (enterpriseBill != null) {
                    if ("2".equals(enterpriseBill.getBillStatus())) {
                        logger.info("【微信回调-企业账单】已支付（幂等），outTradeNo={}", outTradeNo);
                    } else {
                        enterpriseBill.setBillStatus("2");
                        enterpriseBill.setPayTime(new Date());
                        enterpriseBill.setPayMethod("wechat");
                        enterpriseBill.setTransactionNo(transactionId);
                        enterpriseBill.setUpdateTime(new Date());
                        enterpriseBillMapper.updateEnterpriseBill(enterpriseBill);
                        logger.info("【微信回调-企业账单】状态已更新为已支付，billId={}, outTradeNo={}",
                                enterpriseBill.getBillId(), outTradeNo);
                    }
                    resp.put("code", "SUCCESS");
                    resp.put("message", "成功");
                    return ResponseEntity.ok(resp);
                }
                logger.error("【微信回调】按 outTradeNo={} 在 hz_bill / hz_enterprise_bill 中均查不到账单", outTradeNo);
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
                        // 无预订单模式：押金支付成功后，将房源状态从已预订改为已出租
                        HzContract contract = contractMapper.selectById(bill.getContractId());
                        if (contract != null && contract.getHouseId() != null) {
                            houseMapper.update(null, new LambdaUpdateWrapper<HzHouse>()
                                .eq(HzHouse::getHouseId, contract.getHouseId())
                                .in(HzHouse::getHouseStatus, "0", "1")
                                .set(HzHouse::getHouseStatus, "2"));
                            logger.info("【微信回调】无预订单模式押金支付成功，房源状态→已出租, contractId={}", contract.getContractId());
                        }
                    }
                }

                // 合同状态推进：押金 + 首期租金均付清才进入「3 履行中」
                // 调用统一方法，由其内部判断双条件是否齐全（兼顾"先押金后租金"和"先租金后押金"两种顺序）
                if ("1".equals(bill.getBillType()) || "2".equals(bill.getBillType())) {
                    try {
                        houseOrderService.tryAdvanceContractToFulfilling(bill.getContractId());
                    } catch (Exception advEx) {
                        logger.warn("【微信回调】tryAdvanceContractToFulfilling 失败，不影响主流程: {}", advEx.getMessage());
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
        if (bill == null) {
            // 兜底：按企业账单 billNo 查询
            LambdaQueryWrapper<HzEnterpriseBill> ebWrapper = new LambdaQueryWrapper<>();
            ebWrapper.eq(HzEnterpriseBill::getBillNo, billNo)
                     .eq(HzEnterpriseBill::getDelFlag, "0")
                     .last("LIMIT 1");
            HzEnterpriseBill enterpriseBill = enterpriseBillMapper.selectOne(ebWrapper);
            if (enterpriseBill != null) {
                return syncEnterpriseBillPay(enterpriseBill);
            }
            return error("账单不存在");
        }

        // 已支付无需同步
        if ("1".equals(bill.getBillStatus())) {
            Map<String, Object> data = new HashMap<>();
            data.put("paid", true);
            data.put("billStatus", "1");
            return success(data);
        }

        // 2. 向微信主动查单
        try {
            // 关键：优先使用 last_out_trade_no（实际下到微信、可能带 -R- 后缀）查微信，
            // 原始 billNo 在重试场景下已被 closeOrder 关闭，查不到 SUCCESS。
            // 同时保留对老账单（重试机制上线前）的兼容：老账单 last_out_trade_no 为空，降级用 billNo。
            String lookupOutTradeNo;
            if (bill.getLastOutTradeNo() != null && !bill.getLastOutTradeNo().isEmpty()) {
                lookupOutTradeNo = bill.getLastOutTradeNo();
            } else if (bill.getBillNo() != null) {
                lookupOutTradeNo = bill.getBillNo();
            } else {
                lookupOutTradeNo = String.valueOf(bill.getBillId());
            }
            Map<String, Object> wxResult = wechatPayService.queryByOutTradeNo(lookupOutTradeNo);
            String tradeState = (String) wxResult.get("trade_state");
            String transactionId = (String) wxResult.get("transaction_id");

            Map<String, Object> data = new HashMap<>();
            data.put("tradeState", tradeState);
            data.put("queriedOutTradeNo", lookupOutTradeNo);

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
                        // 无预订单模式：押金支付成功后，将房源状态从已预订改为已出租
                        HzContract contract = contractMapper.selectById(bill.getContractId());
                        if (contract != null && contract.getHouseId() != null) {
                            houseMapper.update(null, new LambdaUpdateWrapper<HzHouse>()
                                .eq(HzHouse::getHouseId, contract.getHouseId())
                                .in(HzHouse::getHouseStatus, "0", "1")
                                .set(HzHouse::getHouseStatus, "2"));
                            logger.info("主动查单同步-无预订单模式押金支付成功，房源状态→已出租, contractId={}", contract.getContractId());
                        }
                    }
                }

                // 合同状态推进：押金 + 首期租金均付清才进入「3 履行中」
                if ("1".equals(bill.getBillType()) || "2".equals(bill.getBillType())) {
                    try {
                        houseOrderService.tryAdvanceContractToFulfilling(bill.getContractId());
                    } catch (Exception advEx) {
                        logger.warn("主动查单同步-tryAdvanceContractToFulfilling 失败，不影响主流程: {}", advEx.getMessage());
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

    /**
     * 企业账单主动查单同步（兜底：回调未到达时使用）
     */
    private AjaxResult syncEnterpriseBillPay(HzEnterpriseBill enterpriseBill) {
        // 已支付无需同步
        if ("2".equals(enterpriseBill.getBillStatus())) {
            Map<String, Object> data = new HashMap<>();
            data.put("paid", true);
            data.put("billStatus", "2");
            return success(data);
        }
        try {
            Map<String, Object> wxResult = wechatPayService.queryByOutTradeNo(enterpriseBill.getBillNo());
            String tradeState = (String) wxResult.get("trade_state");
            String transactionId = (String) wxResult.get("transaction_id");

            Map<String, Object> data = new HashMap<>();
            data.put("tradeState", tradeState);

            if ("SUCCESS".equals(tradeState)) {
                enterpriseBill.setBillStatus("2");
                enterpriseBill.setPayTime(new Date());
                enterpriseBill.setPayMethod("wechat");
                enterpriseBill.setTransactionNo(transactionId);
                enterpriseBill.setUpdateTime(new Date());
                enterpriseBillMapper.updateEnterpriseBill(enterpriseBill);
                logger.info("企业账单主动查单同步成功，billNo={}", enterpriseBill.getBillNo());
                data.put("paid", true);
                data.put("billStatus", "2");
            } else {
                data.put("paid", false);
                data.put("billStatus", enterpriseBill.getBillStatus());
            }
            return success(data);
        } catch (Exception e) {
            logger.error("企业账单主动查单失败，billNo={}", enterpriseBill.getBillNo(), e);
            return error("查单失败：" + e.getMessage());
        }
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
