package com.ruoyi.web.task;

import com.ruoyi.system.service.IHzAppointmentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 预约看房定时任务
 *
 * @author ruoyi
 */
@Component("hzAppointmentTask")
public class HzAppointmentTask
{
    private static final Logger log = LoggerFactory.getLogger(HzAppointmentTask.class);

    @Autowired
    private IHzAppointmentService appointmentService;

    /**
     * 自动过期处理
     * 将预约日期已过且状态为待确认/已确认/用户已确认看房的预约更新为已过期
     */
    public void autoExpire()
    {
        log.info("开始执行预约过期处理任务");
        try {
            int expiredCount = appointmentService.autoExpireAppointments();
            log.info("预约过期处理任务完成,共处理{}条过期预约", expiredCount);
        } catch (Exception e) {
            log.error("预约过期处理任务执行失败", e);
        }
    }
}
