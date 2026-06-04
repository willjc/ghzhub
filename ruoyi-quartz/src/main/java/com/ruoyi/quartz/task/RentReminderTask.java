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
 * <p>每天9:00执行，按三档触发催交提醒：
 * <ul>
 *   <li>T-7：应付日期距今 7 天，发送一次 7 天后到期提醒</li>
 *   <li>T-3：应付日期距今 3 天，发送一次 3 天后到期提醒</li>
 *   <li>逾期：应付日期已过且未缴，每天发送一次逾期提醒</li>
 * </ul>
 * 同一账单同一档位当天只发送一次。
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

    /** 催交档位：T-7 */
    private static final String TITLE_T7 = "房租缴纳提醒（7天后到期）";
    /** 催交档位：T-3 */
    private static final String TITLE_T3 = "房租缴纳提醒（3天后到期）";
    /** 催交档位：逾期 */
    private static final String TITLE_OVERDUE = "房租逾期提醒";

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
            DateTimeFormatter fmt = DateTimeFormatter.ISO_LOCAL_DATE;
            String todayStr = today.format(fmt);
            String d7Str = today.plusDays(7).format(fmt);
            String d3Str = today.plusDays(3).format(fmt);

            // T-7 到期提醒：due_date = today + 7
            int t7 = sendRemindersByDueDate(eqDueDate(d7Str), TITLE_T7, todayStr, "将于7天后到期");
            // T-3 到期提醒：due_date = today + 3
            int t3 = sendRemindersByDueDate(eqDueDate(d3Str), TITLE_T3, todayStr, "将于3天后到期");
            // 逾期提醒：due_date < today 且未缴
            int overdue = sendRemindersByDueDate(ltDueDate(todayStr), TITLE_OVERDUE, todayStr, "已逾期");

            log.info("房租催交任务完成：T-7={}条，T-3={}条，逾期={}条", t7, t3, overdue);
        } catch (Exception e) {
            log.error("房租催交定时任务执行失败", e);
        }
    }

    /** 构建 due_date 等于指定日期的查询条件 */
    private LambdaQueryWrapper<HzBill> eqDueDate(String dateStr) {
        LambdaQueryWrapper<HzBill> wrapper = baseWrapper();
        wrapper.eq(HzBill::getDueDate, dateStr);
        return wrapper;
    }

    /** 构建 due_date 小于指定日期的查询条件 */
    private LambdaQueryWrapper<HzBill> ltDueDate(String dateStr) {
        LambdaQueryWrapper<HzBill> wrapper = baseWrapper();
        wrapper.lt(HzBill::getDueDate, dateStr);
        return wrapper;
    }

    /** 公共查询条件：租金 + 待支付 + 未删除 + 排除已终止合同 */
    private LambdaQueryWrapper<HzBill> baseWrapper() {
        LambdaQueryWrapper<HzBill> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(HzBill::getBillType, BILL_TYPE_RENT)
                .eq(HzBill::getBillStatus, BILL_STATUS_UNPAID)
                .eq(HzBill::getDelFlag, "0")
                // 排除合同已到期(4)、已解约(5)、超时失效(6)的账单
                .notInSql(HzBill::getContractId,
                        "SELECT contract_id FROM hz_contract WHERE contract_status IN ('4','5','6')");
        return wrapper;
    }

    /**
     * 按某档查询未缴账单并逐条发催交消息
     * @return 本档实际发送条数
     */
    private int sendRemindersByDueDate(LambdaQueryWrapper<HzBill> wrapper, String title,
                                       String todayStr, String contextDesc) {
        List<HzBill> bills = billMapper.selectList(wrapper);
        if (bills.isEmpty()) {
            return 0;
        }
        int sent = 0;
        for (HzBill bill : bills) {
            try {
                Long userId = bill.getTenantId();
                if (userId == null) {
                    log.warn("账单{}的租户ID为空，跳过", bill.getBillNo());
                    continue;
                }
                String billPeriod = bill.getBillPeriod() != null ? bill.getBillPeriod() : "";
                if (isAlreadySentToday(userId, billPeriod, title, todayStr)) {
                    continue;
                }
                String content = buildMessageContent(bill, contextDesc);
                messageService.sendMessage(userId, MESSAGE_TYPE_BILL, title, content);
                sent++;
            } catch (Exception e) {
                log.error("处理账单{}催交消息失败", bill.getBillNo(), e);
            }
        }
        return sent;
    }

    /**
     * 检查今天是否已向该用户发送过该档催交消息
     * 去重键：user_id + message_type + message_title(档位) + 账单周期关键词 + 当天日期
     */
    private boolean isAlreadySentToday(Long userId, String billPeriod, String title, String todayStr) {
        LambdaQueryWrapper<HzUserMessage> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(HzUserMessage::getUserId, userId)
                .eq(HzUserMessage::getMessageType, MESSAGE_TYPE_BILL)
                .eq(HzUserMessage::getMessageTitle, title)
                .like(HzUserMessage::getMessageContent, billPeriod)
                .apply("DATE(create_time) = {0}", todayStr);
        return messageMapper.selectCount(wrapper) > 0;
    }

    /**
     * 构建催交消息内容
     * @param bill 账单
     * @param contextDesc 场景描述（"将于7天后到期" / "将于3天后到期" / "已逾期"）
     */
    private String buildMessageContent(HzBill bill, String contextDesc) {
        StringBuilder sb = new StringBuilder();
        String period = bill.getBillPeriod() != null ? bill.getBillPeriod() : "";
        sb.append("您").append(period).append("的房租");
        if (bill.getBillAmount() != null) {
            sb.append("（¥").append(bill.getBillAmount()).append("）");
        }
        if (bill.getDueDate() != null) {
            sb.append(contextDesc).append("（应付日期：").append(bill.getDueDate()).append("）");
        } else {
            sb.append(contextDesc);
        }
        sb.append("，请及时缴纳。");
        return sb.toString();
    }
}
