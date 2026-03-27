package com.ruoyi.quartz.task;

import com.ruoyi.system.service.IHzHouseOrderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 选房预订单过期处理定时任务
 *
 * <p>在 sys_job 表中配置：
 * <ul>
 *   <li>invoke_target = houseOrderExpireTask.checkExpiredOrders()</li>
 *   <li>cron_expression = 0 * * * * ?（每分钟执行一次）</li>
 * </ul>
 *
 * @author ruoyi
 */
@Component("houseOrderExpireTask")
public class HouseOrderExpireTask {

    private static final Logger log = LoggerFactory.getLogger(HouseOrderExpireTask.class);

    @Autowired
    private IHzHouseOrderService orderService;

    /**
     * 检查并处理过期的选房预订单，释放被锁定的房源
     */
    public void checkExpiredOrders() {
        log.info("开始检查过期选房预订单...");
        try {
            orderService.processExpiredOrders();
            log.info("过期订单检查完成");
        } catch (Exception e) {
            log.error("过期订单检查失败", e);
        }
    }
}
