package com.ruoyi.quartz.task;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.ruoyi.system.domain.HzBill;
import com.ruoyi.system.domain.HzUserMessage;
import com.ruoyi.system.mapper.HzBillMapper;
import com.ruoyi.system.mapper.HzUserMessageMapper;
import com.ruoyi.system.service.IHzUserMessageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * 房租催交定时任务
 *
 * <p>每天9:00执行，查询5天内到期但未缴纳的租金账单，
 * 给对应用户发送催交消息提醒。同一账单同一天不重复发送。
 *
 * <p>在 sys_job 表中配置：
 * <ul>
 *   <li>invoke_target = rentReminderTask.execute()</li>
 *   <li>cron_expression = 0 0 9 * * ?（每天9点执行）</li>
 * </ul>
 *
 * @author ruoyi
 */
@Component("rentReminderTask")
public class RentReminderTask {

    private static final Logger log = LoggerFactory.getLogger(RentReminderTask.class);

    /** 账单类型：租金 */
    private static final String BILL_TYPE_RENT = "2";

    /** 账单状态：待支付 */
    private static final String BILL_STATUS_UNPAID = "0";

    /** 消息类型：账单提醒 */
    private static final String MESSAGE_TYPE_BILL = "bill";

    /** 催交消息标题前缀 */
    private static final String TITLE_PREFIX = "房租缴纳提醒";

    @Autowired
    private HzBillMapper billMapper;

    @Autowired
    private HzUserMessageMapper messageMapper;

    @Autowired
    private IHzUserMessageService messageService;

    /**
     * 执行房租催交任务
     */
    public void execute() {
        log.info("开始执行房租催交定时任务...");
        try {
            LocalDate today = LocalDate.now();
            LocalDate deadline = today.plusDays(5);
            String todayStr = today.format(DateTimeFormatter.ISO_LOCAL_DATE);
            String deadlineStr = deadline.format(DateTimeFormatter.ISO_LOCAL_DATE);

            // 1. 查询5天内到期且未缴纳的租金账单
            LambdaQueryWrapper<HzBill> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(HzBill::getBillType, BILL_TYPE_RENT)
                    .eq(HzBill::getBillStatus, BILL_STATUS_UNPAID)
                    .eq(HzBill::getDelFlag, "0")
                    .ge(HzBill::getDueDate, todayStr)
                    .le(HzBill::getDueDate, deadlineStr);

            List<HzBill> bills = billMapper.selectList(wrapper);
            log.info("查询到{}条5天内到期未缴租金账单", bills.size());

            if (bills.isEmpty()) {
                log.info("无需催交的账单，任务结束");
                return;
            }

            int sentCount = 0;
            int skipCount = 0;

            // 2. 逐条处理，发送催交消息
            for (HzBill bill : bills) {
                try {
                    Long userId = bill.getTenantId();
                    if (userId == null) {
                        log.warn("账单{}的租户ID为空，跳过", bill.getBillNo());
                        continue;
                    }

                    // 3. 防重复：检查今天是否已对该账单周期发过催交消息
                    String billPeriod = bill.getBillPeriod() != null ? bill.getBillPeriod() : "";
                    if (isAlreadySentToday(userId, billPeriod, todayStr)) {
                        skipCount++;
                        continue;
                    }

                    // 4. 发送催交消息
                    String title = TITLE_PREFIX;
                    String content = buildMessageContent(bill);
                    messageService.sendMessage(userId, MESSAGE_TYPE_BILL, title, content);
                    sentCount++;
                    log.debug("已向用户{}发送账单{}的催交消息", userId, bill.getBillNo());
                } catch (Exception e) {
                    log.error("处理账单{}催交消息失败", bill.getBillNo(), e);
                }
            }

            log.info("房租催交任务完成：共{}条待催账单，发送{}条，跳过{}条（已发送）",
                    bills.size(), sentCount, skipCount);
        } catch (Exception e) {
            log.error("房租催交定时任务执行失败", e);
        }
    }

    /**
     * 检查今天是否已向该用户发送过该账单周期的催交消息
     *
     * <p>通过 user_id + message_type + message_title + 账单周期关键词 + 当天日期 判断，
     * 避免在消息内容中嵌入内部标识。
     *
     * @param userId 用户ID
     * @param billPeriod 账单周期（如 "2026-07"）
     * @param todayStr 今天日期字符串
     * @return true=已发送过
     */
    private boolean isAlreadySentToday(Long userId, String billPeriod, String todayStr) {
        LambdaQueryWrapper<HzUserMessage> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(HzUserMessage::getUserId, userId)
                .eq(HzUserMessage::getMessageType, MESSAGE_TYPE_BILL)
                .eq(HzUserMessage::getMessageTitle, TITLE_PREFIX)
                .like(HzUserMessage::getMessageContent, billPeriod)
                .apply("DATE(create_time) = {0}", todayStr);
        return messageMapper.selectCount(wrapper) > 0;
    }

    /**
     * 构建催交消息内容
     *
     * @param bill 账单信息
     * @return 消息内容
     */
    private String buildMessageContent(HzBill bill) {
        StringBuilder sb = new StringBuilder();
        String period = bill.getBillPeriod() != null ? bill.getBillPeriod() : "";
        sb.append("您").append(period).append("的房租");
        if (bill.getBillAmount() != null) {
            sb.append("（¥").append(bill.getBillAmount()).append("）");
        }
        if (bill.getDueDate() != null) {
            sb.append("将于").append(bill.getDueDate()).append("到期");
        } else {
            sb.append("即将到期");
        }
        sb.append("，请及时缴纳。");
        return sb.toString();
    }
}
