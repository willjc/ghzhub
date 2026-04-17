package com.ruoyi.quartz.task;

import com.ruoyi.system.mapper.HzContractMapper;
import com.ruoyi.system.mapper.HzBillMapper;
import com.ruoyi.system.mapper.HzHouseMapper;
import com.ruoyi.system.mapper.HzDocumentMapper;
import com.ruoyi.system.domain.HzContract;
import com.ruoyi.system.domain.HzBill;
import com.ruoyi.system.domain.HzHouse;
import com.ruoyi.system.domain.HzDocument;
import com.ruoyi.system.service.IHzContractService;
import com.ruoyi.system.service.IHzUserMessageService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;

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
    private IHzContractService contractService;

    @Autowired
    private IHzUserMessageService messageService;

    /**
     * 合同到期自动释放房源（每天凌晨1点执行）
     *
     * <p>查询到期日 < 当天 且 合同状态为"履行中(3)" 且未续租的合同，
     * 将合同状态更新为"已到期(4)"，房源状态改为"空置(0)"，并发送消息提醒。
     */
    public void execute() {
        log.info("开始执行合同到期释放房源定时任务...");
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
                    // 1. 更新合同状态为"已到期(4)"
                    contractMapper.update(null, new LambdaUpdateWrapper<HzContract>()
                            .eq(HzContract::getContractId, contract.getContractId())
                            .set(HzContract::getContractStatus, "4"));

                    // 2. 将对应房源状态改为"空置(0)"
                    if (contract.getHouseId() != null) {
                        houseMapper.update(null, new LambdaUpdateWrapper<HzHouse>()
                                .eq(HzHouse::getHouseId, contract.getHouseId())
                                .eq(HzHouse::getHouseStatus, "2")  // 仅已出租状态才释放
                                .set(HzHouse::getHouseStatus, "0"));
                    }

                    // 3. 发送消息提醒
                    if (contract.getTenantId() != null) {
                        String title = "合同到期通知";
                        String content = "您的合同" + (contract.getContractNo() != null ? contract.getContractNo() : "")
                                + "已到期，房源已释放。如需继续租住，请联系管理员。";
                        messageService.sendMessage(contract.getTenantId(), "contract", title, content);
                    }

                    processedCount++;
                    log.info("合同到期处理完成：contractId={}, contractNo={}", contract.getContractId(), contract.getContractNo());
                } catch (Exception e) {
                    log.error("处理到期合同失败：contractId={}", contract.getContractId(), e);
                }
            }

            log.info("合同到期释放房源任务完成：共处理{}条", processedCount);
        } catch (Exception e) {
            log.error("合同到期释放房源定时任务执行失败", e);
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
                        .eq(HzContract::getDelFlag, "0"));

        LocalDateTime now = LocalDateTime.now();
        int count = 0;

        for (HzContract contract : contracts) {
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
                        .eq(HzContract::getDelFlag, "0"));

        LocalDateTime now = LocalDateTime.now();
        int count = 0;

        for (HzContract contract : contracts) {
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
                        .eq(HzContract::getDelFlag, "0"));

        LocalDateTime now = LocalDateTime.now();
        int count = 0;

        for (HzContract contract : contracts) {
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
     * 将合同标记为超时失效（状态6），并释放关联房源（事务内执行）
     */
    private void expireContract(HzContract contract, String reason) {
        contractService.expireContractAndReleaseHouse(contract.getContractId(), contract.getHouseId());
        log.info("合同超时失效：contractId={}, reason={}", contract.getContractId(), reason);
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
}
