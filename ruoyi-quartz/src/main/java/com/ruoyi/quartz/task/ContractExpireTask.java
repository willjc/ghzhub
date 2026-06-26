package com.ruoyi.quartz.task;

import com.ruoyi.system.mapper.HzContractMapper;
import com.ruoyi.system.mapper.HzBillMapper;
import com.ruoyi.system.mapper.HzHouseMapper;
import com.ruoyi.system.mapper.HzDocumentMapper;
import com.ruoyi.system.mapper.HzCheckInMapper;
import com.ruoyi.system.mapper.HzUserMapper;
import com.ruoyi.system.mapper.HzBatchTenantMapper;
import com.ruoyi.system.domain.HzContract;
import com.ruoyi.system.domain.HzBill;
import com.ruoyi.system.domain.HzHouse;
import com.ruoyi.system.domain.HzDocument;
import com.ruoyi.system.domain.HzCheckIn;
import com.ruoyi.system.domain.HzUser;
import com.ruoyi.system.domain.HzBatchTenant;
import com.ruoyi.system.service.IHzContractService;
import com.ruoyi.system.service.IHzHouseOrderService;
import com.ruoyi.system.service.IHzUserMessageService;
import com.ruoyi.system.service.ISysConfigService;
import com.ruoyi.system.service.WechatPayService;
import com.ruoyi.common.utils.DateUtils;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 合同超时失效定时任务
 *
 * <p>在 sys_job 表中配置：
 * <ul>
 *   <li>invoke_target = contractExpireTask.checkExpiredContracts()</li>
 *   <li>cron_expression = 0 * * * * ?（每分钟执行一次）</li>
 * </ul>
 *
 * @author ruoyi
 */
@Component("contractExpireTask")
public class ContractExpireTask {

    private static final Logger log = LoggerFactory.getLogger(ContractExpireTask.class);

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Autowired
    private HzContractMapper contractMapper;

    @Autowired
    private HzBillMapper billMapper;

    @Autowired
    private HzHouseMapper houseMapper;

    @Autowired
    private HzDocumentMapper documentMapper;

    @Autowired
    private HzCheckInMapper checkInMapper;

    @Autowired
    private IHzContractService contractService;

    @Autowired
    private IHzUserMessageService messageService;

    @Autowired
    private ISysConfigService configService;

    @Autowired
    private WechatPayService wechatPayService;

    @Autowired
    private IHzHouseOrderService houseOrderService;

    @Autowired
    private HzUserMapper userMapper;

    @Autowired
    private HzBatchTenantMapper batchTenantMapper;

    /**
     * 合同到期自动标记已到期（每天凌昨1点执行）
     *
     * <p>查询到期日 < 当天 且 合同状态为“履行中(3)” 且未续租的合同，
     * 将合同状态更新为“已到期(4)”，发送消息提醒。
     * <b>不自动释放房源、不自动解约</b>，房源维持已出租状态，
     * 租户无法查看未付账单、无法缴费、无法发起退租。
     */
    public void execute() {
        log.info("开始执行合同到期标记定时任务...");
        try {
            String todayStr = java.time.LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

            // 查询到期未续租的履行中合同
            LambdaQueryWrapper<HzContract> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(HzContract::getContractStatus, "3")  // 履行中
                    .lt(HzContract::getEndDate, todayStr)     // end_date < 今天
                    .ne(HzContract::getIsRenewed, "1")        // 未续租
                    .eq(HzContract::getDelFlag, "0");

            List<HzContract> expiredContracts = contractMapper.selectList(wrapper);
            log.info("查询到{}条到期未续租的履行中合同", expiredContracts.size());

            if (expiredContracts.isEmpty()) {
                log.info("无到期合同需要处理，任务结束");
                return;
            }

            int processedCount = 0;
            for (HzContract contract : expiredContracts) {
                try {
                    // 1. 更新合同状态为“已到期(4)”
                    contractMapper.update(null, new LambdaUpdateWrapper<HzContract>()
                            .eq(HzContract::getContractId, contract.getContractId())
                            .set(HzContract::getContractStatus, "4"));
                    
                    // 2. 不释放房源、不软删入住单——维持原状，由管理员手动处理
                    //    租户侧已通过合同状态=4拦截账单显示、缴费、退租操作
                    
                    // 3. 发送消息提醒
                    if (contract.getTenantId() != null) {
                        String title = "合同到期通知";
                        String content = "您的合同" + (contract.getContractNo() != null ? contract.getContractNo() : "")
                                + "已到期，如需继续租住或办理退租，请联系管理员。";
                        messageService.sendMessage(contract.getTenantId(), "contract", title, content);
                    }

                    processedCount++;
                    log.info("合同到期处理完成：contractId={}, contractNo={}", contract.getContractId(), contract.getContractNo());
                } catch (Exception e) {
                    log.error("处理到期合同失败：contractId={}", contract.getContractId(), e);
                }
            }

            log.info("合同到期标记任务完成：共处理{}条", processedCount);
        } catch (Exception e) {
            log.error("合同到期标记定时任务执行失败", e);
        }
    }

    /**
     * 检查并处理超时合同，释放被锁定的房源
     */
    public void checkExpiredContracts() {
        log.info("开始检查超时合同...");
        try {
            int countA = processSignedButUnpaid();
            int countB = processUnsignedTimeout();
            int countC = processNoMaterialUpload();
            if (countA + countB + countC > 0) {
                log.info("超时合同处理完成：规则A={}条, 规则B={}条, 规则C={}条", countA, countB, countC);
            }
        } catch (Exception e) {
            log.error("超时合同检查失败", e);
        }
    }

    /**
     * 规则A：签署后30分钟未缴押金
     * 条件：contract_status='2', sign_time IS NOT NULL, del_flag='0'
     * 如果 sign_time + 30分钟 < NOW() 且无已支付押金账单 → 失效
     */
    private int processSignedButUnpaid() {
        List<HzContract> contracts = contractMapper.selectList(
                new LambdaQueryWrapper<HzContract>()
                        .eq(HzContract::getContractStatus, "2")
                        .isNotNull(HzContract::getSignTime)
                        .ne(HzContract::getContractType, "2")  // 排除续租合同
                        .eq(HzContract::getDelFlag, "0"));

        LocalDateTime now = LocalDateTime.now();
        int count = 0;

        for (HzContract contract : contracts) {
            // 批次配租用户押金缴纳时效与普通用户相同，不豁免

            LocalDateTime signTime = parseDateTime(contract.getSignTime());
            if (signTime == null) {
                continue;
            }

            if (now.isAfter(signTime.plusMinutes(30))) {
                // 检查是否有已支付押金账单
                Long paidDeposit = billMapper.selectCount(
                        new LambdaQueryWrapper<HzBill>()
                                .eq(HzBill::getContractId, contract.getContractId())
                                .eq(HzBill::getBillType, "1")
                                .eq(HzBill::getBillStatus, "1"));

                if (paidDeposit == null || paidDeposit == 0) {
                    // 失效前主动向微信查单，防止回调丢失导致误失效
                    if (tryRecoverPaymentFromWechat(contract)) {
                        log.info("合同 {} 微信查单确认已支付，跳过失效", contract.getContractId());
                        continue;
                    }
                    expireContract(contract, "签署后30分钟未缴押金");
                    count++;
                }
            }
        }
        return count;
    }

    /**
     * 规则B：未签署合同60分钟超时
     * 条件：contract_status='0', del_flag='0'
     * 如果 create_time + 60分钟 < NOW() → 失效
     */
    private int processUnsignedTimeout() {
        List<HzContract> contracts = contractMapper.selectList(
                new LambdaQueryWrapper<HzContract>()
                        .eq(HzContract::getContractStatus, "0")
                        .ne(HzContract::getContractType, "2")  // 排除续租合同
                        .eq(HzContract::getDelFlag, "0"));

        LocalDateTime now = LocalDateTime.now();
        int count = 0;

        for (HzContract contract : contracts) {
            // 批次配租用户豁免所有时效限制
            if (isBatchTenant(contract.getTenantId())) {
                continue;
            }

            Date createTime = contract.getCreateTime();
            if (createTime == null) {
                continue;
            }

            LocalDateTime createLdt = createTime.toInstant()
                    .atZone(ZoneId.systemDefault())
                    .toLocalDateTime();

            if (now.isAfter(createLdt.plusMinutes(60))) {
                expireContract(contract, "未签署合同60分钟超时");
                count++;
            }
        }
        return count;
    }

    /**
     * 规则C：押金已缴后3日未上传资料
     * 条件：contract_status='2', del_flag='0'
     * 如果押金已缴且 pay_time + 3天 < NOW() 且无已提交资料 → 失效
     */
    private int processNoMaterialUpload() {
        List<HzContract> contracts = contractMapper.selectList(
                new LambdaQueryWrapper<HzContract>()
                        .eq(HzContract::getContractStatus, "2")
                        .ne(HzContract::getContractType, "2")  // 排除续租合同
                        .eq(HzContract::getDelFlag, "0"));

        LocalDateTime now = LocalDateTime.now();
        int count = 0;

        for (HzContract contract : contracts) {
            // 批次配租用户豁免所有时效限制
            if (isBatchTenant(contract.getTenantId())) {
                continue;
            }

            // 查询已支付的押金账单
            HzBill depositBill = billMapper.selectOne(
                    new LambdaQueryWrapper<HzBill>()
                            .eq(HzBill::getContractId, contract.getContractId())
                            .eq(HzBill::getBillType, "1")
                            .eq(HzBill::getBillStatus, "1")
                            .last("LIMIT 1"));

            if (depositBill == null || depositBill.getPayTime() == null) {
                continue;
            }

            LocalDateTime payTime = parseDateTime(depositBill.getPayTime());
            if (payTime == null) {
                continue;
            }

            if (now.isAfter(payTime.plusDays(3))) {
                // 检查是否已提交资料（按 contract_id 查）
                Long docCount = documentMapper.selectCount(
                        new LambdaQueryWrapper<HzDocument>()
                                .eq(HzDocument::getContractId, contract.getContractId())
                                .eq(HzDocument::getDelFlag, "0"));

                if (docCount == null || docCount == 0) {
                    // 再按 tenant_id 查
                    if (contract.getTenantId() != null) {
                        docCount = documentMapper.selectCount(
                                new LambdaQueryWrapper<HzDocument>()
                                        .eq(HzDocument::getTenantId, contract.getTenantId())
                                        .eq(HzDocument::getDelFlag, "0"));
                    }
                }

                if (docCount == null || docCount == 0) {
                    expireContract(contract, "押金已缴后3日未上传资料");
                    count++;
                }
            }
        }
        return count;
    }

    /**
     * 失效前主动向微信查单，确认押金是否实际已支付（防止回调丢失导致误失效）。
     * 如果微信确认已支付，则补录本地账单状态并触发合同状态推进，返回 true（跳过失效）。
     * 如果微信确认未支付或查询失败，返回 false（继续失效流程）。
     */
    private boolean tryRecoverPaymentFromWechat(HzContract contract) {
        // 查询该合同的押金账单
        HzBill depositBill = billMapper.selectOne(
                new LambdaQueryWrapper<HzBill>()
                        .eq(HzBill::getContractId, contract.getContractId())
                        .eq(HzBill::getBillType, "1")
                        .eq(HzBill::getBillStatus, "0")
                        .eq(HzBill::getDelFlag, "0")
                        .last("LIMIT 1"));
        if (depositBill == null || depositBill.getBillNo() == null) {
            return false;
        }

        try {
            // 优先使用 last_out_trade_no（实际下到微信、可能带 -R- 后缀）查微信。
            // 原始 billNo 在重试场景下已被 closeOrder 关闭，查不到 SUCCESS。
            String lookupOutTradeNo = (depositBill.getLastOutTradeNo() != null && !depositBill.getLastOutTradeNo().isEmpty())
                    ? depositBill.getLastOutTradeNo()
                    : depositBill.getBillNo();
            Map<String, Object> wxResult = wechatPayService.queryByOutTradeNo(lookupOutTradeNo);
            String tradeState = (String) wxResult.get("trade_state");
            String transactionId = (String) wxResult.get("transaction_id");

            if (!"SUCCESS".equals(tradeState)) {
                log.info("微信查单确认未支付，contractId={}, billNo={}, queriedOutTradeNo={}, tradeState={}",
                        contract.getContractId(), depositBill.getBillNo(), lookupOutTradeNo, tradeState);
                return false;
            }

            // 微信确认已支付，补录本地账单
            depositBill.setBillStatus("1");
            depositBill.setPaidAmount(depositBill.getBillAmount());
            depositBill.setUnpaidAmount(java.math.BigDecimal.ZERO);
            depositBill.setPayTime(DateUtils.getTime());
            depositBill.setPayMethod("wechat");
            depositBill.setTransactionNo(transactionId);
            billMapper.updateById(depositBill);
            log.info("微信查单兜底：押金账单已补录为已支付，contractId={}, billNo={}, queriedOutTradeNo={}, transactionId={}",
                    contract.getContractId(), depositBill.getBillNo(), lookupOutTradeNo, transactionId);

            // 触发订单状态推进（押金支付成功 → 订单进入下一阶段）
            if (depositBill.getOrderNo() != null && !depositBill.getOrderNo().isEmpty()) {
                try {
                    houseOrderService.onDepositPaid(depositBill.getOrderNo());
                } catch (Exception e) {
                    log.warn("微信查单兜底：onDepositPaid 执行异常，不影响主流程, contractId={}: {}",
                            contract.getContractId(), e.getMessage());
                }
            }

            // 触发合同状态推进（检查押金+首期租金是否双满足）
            try {
                houseOrderService.tryAdvanceContractToFulfilling(contract.getContractId());
            } catch (Exception e) {
                log.warn("微信查单兜底：tryAdvanceContractToFulfilling 执行异常，不影响主流程, contractId={}: {}",
                        contract.getContractId(), e.getMessage());
            }

            return true;
        } catch (Exception e) {
            // 查单失败（网络超时等），安全兜底：按未支付处理，继续走失效流程
            log.warn("微信查单失败，按未支付处理继续失效, contractId={}, billNo={}: {}",
                    contract.getContractId(), depositBill.getBillNo(), e.getMessage());
            return false;
        }
    }

    /**
     * 扫描未支付账单，主动向微信查单兜底，补回回调丢失场景。
     * <p>背景：2026-06-18 发现张阁阁代付房租后未勾账问题——原有 tryRecoverPaymentFromWechat
     * 只覆盖押金（bill_type=1）且只在合同超时失效扫描时才调用，租金回调丢失无人被顶。</p>
     * <p>本方法扫描范围：hz_bill 中 bill_status='0'、last_out_trade_no 非空（表明曾下单到微信）、
     * 且下单时间超过 5 分钟的账单。逆向查微信、确认 SUCCESS 后补回本地状态。</p>
     * <p>sys_job 配置：invoke_target = contractExpireTask.recoverLostWechatNotify()，建议每 5 分钟执行一次。</p>
     */
    public int recoverLostWechatNotify() {
        // 扫描所有未支付、已下单到微信、且距下单超过 5 分钟的账单
        // 不限 bill_type：押金（原 tryRecoverPaymentFromWechat 仅在失效扫描时才被动调用）、租金、水电燃、物业费全部覆盖
        // 限制 update_time 在 24 小时内，避免重复扫描老账单造成微信查单压力
        java.time.LocalDateTime windowStart = java.time.LocalDateTime.now().minusDays(1);
        Date windowStartDate = Date.from(windowStart.atZone(ZoneId.systemDefault()).toInstant());

        List<HzBill> candidates = billMapper.selectList(
                new LambdaQueryWrapper<HzBill>()
                        .eq(HzBill::getBillStatus, "0")
                        .eq(HzBill::getDelFlag, "0")
                        .isNotNull(HzBill::getLastOutTradeNo)
                        .ne(HzBill::getLastOutTradeNo, "")
                        .ge(HzBill::getUpdateTime, windowStartDate));

        if (candidates == null || candidates.isEmpty()) {
            return 0;
        }
        log.info("[RecoverLostWechatNotify] 开始扫描可能丢失回调的账单，候选数={}", candidates.size());

        int recovered = 0;
        for (HzBill bill : candidates) {
            if (bill.getLastOutTradeNo() == null || bill.getLastOutTradeNo().isEmpty()) {
                continue;
            }
            try {
                Map<String, Object> wxResult = wechatPayService.queryByOutTradeNo(bill.getLastOutTradeNo());
                String tradeState = (String) wxResult.get("trade_state");
                String transactionId = (String) wxResult.get("transaction_id");
                if (!"SUCCESS".equals(tradeState)) {
                    continue;
                }

                // 微信确认已支付，但本地仍为未支付 → 补回
                bill.setBillStatus("1");
                bill.setPaidAmount(bill.getBillAmount());
                bill.setUnpaidAmount(java.math.BigDecimal.ZERO);
                bill.setPayTime(DateUtils.getTime());
                bill.setPayMethod("wechat");
                bill.setTransactionNo(transactionId);
                billMapper.updateById(bill);
                recovered++;
                log.info("[RecoverLostWechatNotify] 补回成功 billId={}, billNo={}, lastOutTradeNo={}, transactionId={}",
                        bill.getBillId(), bill.getBillNo(), bill.getLastOutTradeNo(), transactionId);

                // 押金账单补回 → 推进订单状态
                if ("1".equals(bill.getBillType()) && bill.getOrderNo() != null && !bill.getOrderNo().isEmpty()) {
                    try {
                        houseOrderService.onDepositPaid(bill.getOrderNo());
                    } catch (Exception e) {
                        log.warn("[RecoverLostWechatNotify] onDepositPaid 异常 billId={}: {}", bill.getBillId(), e.getMessage());
                    }
                }
                // 押金/首期租金 → 推进合同到履行中
                if ("1".equals(bill.getBillType()) || "2".equals(bill.getBillType())) {
                    try {
                        houseOrderService.tryAdvanceContractToFulfilling(bill.getContractId());
                    } catch (Exception e) {
                        log.warn("[RecoverLostWechatNotify] tryAdvanceContractToFulfilling 异常 billId={}: {}",
                                bill.getBillId(), e.getMessage());
                    }
                }
            } catch (Exception e) {
                // 单个账单查单失败不阻断整体扫描
                log.warn("[RecoverLostWechatNotify] 查单失败 billId={}, lastOutTradeNo={}: {}",
                        bill.getBillId(), bill.getLastOutTradeNo(), e.getMessage());
            }
        }
        log.info("[RecoverLostWechatNotify] 扫描完成，补回账单数={}", recovered);
        return recovered;
    }

    /**
     * 将合同标记为超时失效（状态6），并释放关联房源（事务内执行）
     */
    private void expireContract(HzContract contract, String reason) {
        contractService.expireContractAndReleaseHouse(contract.getContractId(), contract.getHouseId());
        // 联动：把该合同对应"待办理(0)/待审核(1)"的入住单软删，避免管理端继续办理
        softDeleteOrphanCheckInsByContract(contract.getContractId(), "合同超时失效自动软删");
        log.info("合同超时失效：contractId={}, reason={}", contract.getContractId(), reason);
    }

    /**
     * 软删除与指定合同关联且仍处于"待办理(0)/待审核(1)"状态的入住单。
     * 当合同被定时任务标记为已到期/已超时失效后，对应入住单不应再被办理。
     */
    private int softDeleteOrphanCheckInsByContract(Long contractId, String reason) {
        if (contractId == null) {
            return 0;
        }
        try {
            int rows = checkInMapper.update(null, new LambdaUpdateWrapper<HzCheckIn>()
                    .eq(HzCheckIn::getContractId, contractId)
                    .eq(HzCheckIn::getDelFlag, "0")
                    .in(HzCheckIn::getStatus, "0", "1")
                    .set(HzCheckIn::getDelFlag, "1")
                    .set(HzCheckIn::getUpdateBy, "contractExpireTask")
                    .set(HzCheckIn::getUpdateTime, new Date())
                    .setSql("remark = CONCAT(IFNULL(remark,''), ' | " + reason + "')"));
            if (rows > 0) {
                log.info("联动软删入住单：contractId={}, rows={}, reason={}", contractId, rows, reason);
            }
            return rows;
        } catch (Exception e) {
            log.warn("联动软删入住单失败 contractId={}: {}", contractId, e.getMessage());
            return 0;
        }
    }

    /**
     * 解析日期时间字符串，兼容 "yyyy-MM-dd HH:mm:ss" 和 "yyyy-MM-ddTHH:mm:ss" 格式
     */
    private LocalDateTime parseDateTime(String dateTimeStr) {
        if (dateTimeStr == null || dateTimeStr.trim().isEmpty()) {
            return null;
        }
        try {
            String normalized = dateTimeStr.replace("T", " ").trim();
            return LocalDateTime.parse(normalized, FORMATTER);
        } catch (Exception e) {
            log.warn("日期解析失败: {}", dateTimeStr);
            return null;
        }
    }

    // ============================================================
    // 入住超时自动解约 + 退款（每小时执行；sys_job 配置 invoke_target = contractExpireTask.processCheckinTimeoutAutoCancel()）
    // 触发条件：合同 status='3' 履行中 + 押金已付 + 首期租金已付 + 押金 pay_time 在配置 start-date 之后
    //          + 不存在已提交过的入住单(status>=1) + 押金 pay_time + 72h 已过
    // 阶段：24h/48h/60h/70h 提醒，72h 解约+退款（dry-run 时只发提醒不真正解约）
    // ============================================================

    /**
     * 入住超时自动解约任务入口（sys_job 每小时整点调用）。
     */
    public void processCheckinTimeoutAutoCancel() {
        log.info("[CheckinTimeout] 开始扫描入住超时自动解约");

        // 1. 总开关
        String enabled = readConfig("auto.cancel.enabled", "false");
        if (!"true".equalsIgnoreCase(enabled)) {
            log.info("[CheckinTimeout] auto.cancel.enabled=false，跳过本次扫描");
            return;
        }

        // 2. 启用日（仅对启用日之后创建的新合同生效）
        LocalDateTime startDate = parseDateTime(readConfig("auto.cancel.start-date", null));
        if (startDate == null) {
            log.warn("[CheckinTimeout] 未配置 auto.cancel.start-date，已跳过");
            return;
        }

        // 3. 超时小时数（默认 72）
        int timeoutHours;
        try {
            timeoutHours = Integer.parseInt(readConfig("auto.cancel.timeout-hours", "72"));
        } catch (NumberFormatException e) {
            timeoutHours = 72;
        }

        // 4. 演练模式
        boolean dryRun = "true".equalsIgnoreCase(readConfig("auto.cancel.dry-run", "true"));

        LocalDateTime now = LocalDateTime.now();

        // 5. 候选合同：status=3 履行中 + del_flag=0 + create_time >= startDate
        List<HzContract> candidates = contractMapper.selectList(
                new LambdaQueryWrapper<HzContract>()
                        .eq(HzContract::getContractStatus, "3")
                        .eq(HzContract::getDelFlag, "0")
                        .ge(HzContract::getCreateTime, java.sql.Timestamp.valueOf(startDate)));

        if (candidates.isEmpty()) {
            log.info("[CheckinTimeout] 无候选合同");
            return;
        }
        log.info("[CheckinTimeout] 候选合同 {} 条，dryRun={}, timeoutHours={}", candidates.size(), dryRun, timeoutHours);

        int reminderCount = 0;
        int cancelCount = 0;
        for (HzContract contract : candidates) {
            try {
                // 批次配租用户豁免入住超时解约
                if (isBatchTenant(contract.getTenantId())) {
                    continue;
                }

                // 用户提交过任何入住申请（status>=1）即永久豁免
                Long submittedCheckin = checkInMapper.selectCount(
                        new LambdaQueryWrapper<HzCheckIn>()
                                .eq(HzCheckIn::getContractId, contract.getContractId())
                                .ge(HzCheckIn::getStatus, "1")
                                .eq(HzCheckIn::getDelFlag, "0"));
                if (submittedCheckin != null && submittedCheckin > 0) {
                    continue;
                }

                // 押金账单：已支付 + 微信
                HzBill depositBill = billMapper.selectOne(
                        new LambdaQueryWrapper<HzBill>()
                                .eq(HzBill::getContractId, contract.getContractId())
                                .eq(HzBill::getBillType, "1")
                                .eq(HzBill::getBillStatus, "1")
                                .eq(HzBill::getPayMethod, "wechat")
                                .eq(HzBill::getDelFlag, "0")
                                .last("LIMIT 1"));
                if (depositBill == null || depositBill.getPayTime() == null) {
                    continue;
                }
                LocalDateTime depositPayTime = parseDateTime(depositBill.getPayTime());
                if (depositPayTime == null || depositPayTime.isBefore(startDate)) {
                    continue;
                }

                // 首期租金账单：已支付 + 微信，按 bill_date 升序取最早一笔
                HzBill firstRentBill = billMapper.selectOne(
                        new LambdaQueryWrapper<HzBill>()
                                .eq(HzBill::getContractId, contract.getContractId())
                                .eq(HzBill::getBillType, "2")
                                .eq(HzBill::getBillStatus, "1")
                                .eq(HzBill::getPayMethod, "wechat")
                                .eq(HzBill::getDelFlag, "0")
                                .orderByAsc(HzBill::getBillDate)
                                .last("LIMIT 1"));
                if (firstRentBill == null) {
                    // 用户只交了押金还没交租金（或者租金未通过微信支付），暂不处理
                    continue;
                }

                long elapsedHours = ChronoUnit.HOURS.between(depositPayTime, now);

                // 阶段提醒：24h / 48h / 60h / 70h（按整点扫描，命中等于触发）
                if (elapsedHours == 24 || elapsedHours == 48 || elapsedHours == 60 || elapsedHours == 70) {
                    if (sendCheckinReminder(contract, elapsedHours, timeoutHours)) {
                        reminderCount++;
                    }
                }

                // 解约：超过 timeoutHours
                if (elapsedHours >= timeoutHours) {
                    if (dryRun) {
                        log.info("[CheckinTimeout][DRY-RUN] 将解约 contractId={}, contractNo={}, elapsedH={}",
                                contract.getContractId(), contract.getContractNo(), elapsedHours);
                        // 演练期也发一次终止提醒（如果之前没发过）
                        sendCheckinReminder(contract, elapsedHours, timeoutHours);
                    } else {
                        if (doAutoCancelAndRefund(contract, depositBill, firstRentBill)) {
                            cancelCount++;
                        }
                    }
                }
            } catch (Exception e) {
                log.error("[CheckinTimeout] 处理合同失败 contractId={}", contract.getContractId(), e);
            }
        }
        log.info("[CheckinTimeout] 本次完成：发提醒 {} 条，解约 {} 条", reminderCount, cancelCount);
    }

    /**
     * 发送阶段提醒。
     */
    private boolean sendCheckinReminder(HzContract contract, long elapsedHours, int timeoutHours) {
        if (contract.getTenantId() == null) {
            return false;
        }
        long remainingH = Math.max(0, timeoutHours - elapsedHours);
        String contractNo = contract.getContractNo() != null ? contract.getContractNo() : ("ID:" + contract.getContractId());
        String title;
        String content;
        if (remainingH <= 0) {
            title = "入住办理已超时";
            content = "您的合同" + contractNo + "已超过 " + timeoutHours + " 小时未提交入住申请，"
                    + "如未在系统处理前提交申请，合同将被自动解约并原路退款。";
        } else {
            title = "请尽快提交入住申请（剩余 " + remainingH + " 小时）";
            content = "您已完成押金与首期租金支付，请在剩余 " + remainingH + " 小时内进入小程序"
                    + "「我的-入住办理」提交入住申请，否则合同将自动解约并原路退款。";
        }
        try {
            messageService.sendMessage(contract.getTenantId(), "checkin-timeout", title, content);
            log.info("[CheckinTimeout] 发提醒 contractId={}, elapsedH={}, remainingH={}",
                    contract.getContractId(), elapsedHours, remainingH);
            return true;
        } catch (Exception e) {
            log.warn("[CheckinTimeout] 提醒发送失败 contractId={}: {}", contract.getContractId(), e.getMessage());
            return false;
        }
    }

    /**
     * 真实自动解约 + 微信退款。
     * 流程：先调事务方法落地 hz_checkout_apply + hz_checkout_record + 改合同状态 + 释放房源 + 软删入住单 + 发消息；
     *       再事务外调微信退款 API；最后短事务更新 record.refund_status。
     */
    private boolean doAutoCancelAndRefund(HzContract contract, HzBill depositBill, HzBill firstRentBill) {
        BigDecimal depositAmt = depositBill.getBillAmount() != null ? depositBill.getBillAmount() : BigDecimal.ZERO;
        BigDecimal firstRentAmt = firstRentBill.getBillAmount() != null ? firstRentBill.getBillAmount() : BigDecimal.ZERO;
        BigDecimal totalRefund = depositAmt.add(firstRentAmt).setScale(2, RoundingMode.HALF_UP);

        // 必须有 transaction_no 才能原路退款
        if (depositBill.getTransactionNo() == null || depositBill.getTransactionNo().isEmpty()
                || firstRentBill.getTransactionNo() == null || firstRentBill.getTransactionNo().isEmpty()) {
            log.warn("[CheckinTimeout] 跳过：押金或租金账单缺少 transaction_no, contractId={}", contract.getContractId());
            return false;
        }

        // 1. 事务内：DB 落地
        Long applyId;
        try {
            applyId = contractService.createAutoCancelCheckoutApplyTx(contract.getContractId(), totalRefund, depositAmt);
        } catch (Exception e) {
            log.error("[CheckinTimeout] DB 写入失败 contractId={}", contract.getContractId(), e);
            return false;
        }
        if (applyId == null) {
            log.info("[CheckinTimeout] 合同已被处理，跳过 contractId={}", contract.getContractId());
            return false;
        }

        // 2. 事务外：调用微信退款（押金 + 首期租金 两笔）
        boolean depositOk = false;
        boolean rentOk = false;
        StringBuilder remark = new StringBuilder();
        long ts = System.currentTimeMillis();

        try {
            String outRefundDeposit = "AUTO_DEP" + ts + applyId;
            int depositFen = depositAmt.multiply(new BigDecimal("100")).intValue();
            Map<String, Object> r1 = wechatPayService.wechatRefund(
                    depositBill.getTransactionNo(), outRefundDeposit,
                    depositFen, depositFen, "入住超时自动解约-押金");
            depositOk = true;
            remark.append("押金已申请退款 单号:").append(outRefundDeposit);
            log.info("[CheckinTimeout] 押金退款已申请 contractId={}, refund={}", contract.getContractId(), r1);
        } catch (Exception e) {
            remark.append("押金退款失败:").append(e.getMessage()).append(" ");
            log.error("[CheckinTimeout] 押金微信退款失败 contractId={}", contract.getContractId(), e);
        }

        try {
            String outRefundRent = "AUTO_RENT" + ts + applyId;
            int rentFen = firstRentAmt.multiply(new BigDecimal("100")).intValue();
            Map<String, Object> r2 = wechatPayService.wechatRefund(
                    firstRentBill.getTransactionNo(), outRefundRent,
                    rentFen, rentFen, "入住超时自动解约-首期租金");
            rentOk = true;
            remark.append(" 首期租金已申请退款 单号:").append(outRefundRent);
            log.info("[CheckinTimeout] 首期租金退款已申请 contractId={}, refund={}", contract.getContractId(), r2);
        } catch (Exception e) {
            remark.append(" 首期租金退款失败:").append(e.getMessage());
            log.error("[CheckinTimeout] 首期租金微信退款失败 contractId={}", contract.getContractId(), e);
        }

        // 3. 短事务：更新 record 状态
        if (depositOk && rentOk) {
            contractService.markCheckoutRecordRefunded(applyId, "微信原路退款成功 | " + remark);
        } else {
            contractService.markCheckoutRecordRefundFailed(applyId,
                    "微信退款部分失败，请管理员在退款管理重试 | " + remark);
        }
        return true;
    }

    /**
     * 读取 sys_config 的便利方法
     */
    private String readConfig(String key, String defaultValue) {
        try {
            String v = configService.selectConfigByKey(key);
            return (v == null || v.isEmpty()) ? defaultValue : v;
        } catch (Exception e) {
            return defaultValue;
        }
    }

    /**
     * 判断指定租户是否为批次配租用户
     * 依据：hz_user.id_card 命中 hz_batch_tenant（未删除）任一记录
     */
    private boolean isBatchTenant(Long tenantId) {
        if (tenantId == null) return false;
        HzUser user = userMapper.selectById(tenantId);
        if (user == null || !StringUtils.hasText(user.getIdCard())) return false;
        QueryWrapper<HzBatchTenant> wrapper = new QueryWrapper<>();
        wrapper.eq("id_card", user.getIdCard())
                .eq("del_flag", "0")
                .last("LIMIT 1");
        Long count = batchTenantMapper.selectCount(wrapper);
        return count != null && count > 0;
    }
}
