package com.by.schedule;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;
import org.springframework.scheduling.support.CronTrigger;

import com.by.service.SheInSyncDataService;

import lombok.Data;

/**
 * 动态配置定时器，替换掉写死的方式
 * @author Administrator
 *
 */
@Data
@Configuration
public class ScheduleConfig implements SchedulingConfigurer {
    @Value("${schedule.cron}")
    private String cron;

    @Autowired
    private SheInSyncDataService sheInSyncDataService;

    @Override
    public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {
        // 动态使用cron表达式设置循环间隔
        taskRegistrar.addTriggerTask(() -> sheInSyncDataService.sheInSyncData(), triggerContext -> {
            // 使用CronTrigger触发器，可动态修改cron表达式来操作循环规则
            CronTrigger cronTrigger = new CronTrigger(cron);
            Date nextExecutionTime = cronTrigger.nextExecutionTime(triggerContext);
            return nextExecutionTime;
        });
    }
}

