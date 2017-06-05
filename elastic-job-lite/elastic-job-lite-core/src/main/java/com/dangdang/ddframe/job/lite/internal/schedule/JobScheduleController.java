/*
 * Copyright 1999-2015 dangdang.com.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * </p>
 */

package com.dangdang.ddframe.job.lite.internal.schedule;

import com.dangdang.ddframe.job.config.TriggerConfiguration;
import com.dangdang.ddframe.job.config.trigger.TriggerType;
import com.dangdang.ddframe.job.exception.JobSystemException;
import lombok.RequiredArgsConstructor;
import org.quartz.CronScheduleBuilder;
import org.quartz.CronTrigger;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.TriggerKey;

import java.util.Date;
import java.util.TimeZone;

/**
 * 作业调度控制器.
 * 
 * @author zhangliang
 */
@RequiredArgsConstructor
public final class JobScheduleController {
    
    private final Scheduler scheduler;
    
    private final JobDetail jobDetail;
    
    private final String triggerIdentity;
    
    /**
     * 调度作业.
     * 
     */
    public void scheduleJob(final TriggerConfiguration triggerConfiguration) {
        try {
            if (!scheduler.checkExists(jobDetail.getKey())) {
                scheduler.scheduleJob(jobDetail, createTrigger(triggerConfiguration));
            }
            scheduler.start();
        } catch (final SchedulerException ex) {
            throw new JobSystemException(ex);
        }
    }
    
    /**
     * 重新调度作业.
     * 
     */
    public synchronized void rescheduleJob(final TriggerConfiguration triggerConfiguration) {
        try {
            CronTrigger trigger = (CronTrigger) scheduler.getTrigger(TriggerKey.triggerKey(triggerIdentity));
            if (!scheduler.isShutdown() && null != trigger  ) {
                scheduler.rescheduleJob(TriggerKey.triggerKey(triggerIdentity), createTrigger(triggerConfiguration));
            }
        } catch (final SchedulerException ex) {
            throw new JobSystemException(ex);
        }
    }

    private Trigger createTrigger(final TriggerConfiguration triggerConfiguration) {
        switch (triggerConfiguration.getTriggerType()) {
            case TriggerType.SimpleScheduleBuilder:
                return TriggerBuilder.newTrigger().withIdentity(triggerIdentity)
                        .startAt(new Date(triggerConfiguration.getStartDate()))
                        .endAt(new Date(triggerConfiguration.getEndDate()))
                        .build();
            case TriggerType.CoreScheduleBuilder:
                return TriggerBuilder.newTrigger().withIdentity(triggerIdentity)
                        .withSchedule(CronScheduleBuilder.cronSchedule(triggerConfiguration.getTriggerContent())
                                .inTimeZone(TimeZone.getTimeZone(triggerConfiguration.getZone()))
                                .withMisfireHandlingInstructionDoNothing())
                        .startAt(new Date(triggerConfiguration.getStartDate()))
                        .endAt(new Date(triggerConfiguration.getEndDate()))
                        .build();
        }
        throw new JobSystemException("并不是合法的TriggerType");
    }
    
    /**
     * 判断作业是否暂停.
     * 
     * @return 作业是否暂停
     */
    public synchronized boolean isPaused() {
        try {
            return !scheduler.isShutdown() && Trigger.TriggerState.PAUSED == scheduler.getTriggerState(new TriggerKey(triggerIdentity));
        } catch (final SchedulerException ex) {
            throw new JobSystemException(ex);
        }
    }
    
    /**
     * 暂停作业.
     */
    public synchronized void pauseJob() {
        try {
            if (!scheduler.isShutdown()) {
                scheduler.pauseAll();
            }
        } catch (final SchedulerException ex) {
            throw new JobSystemException(ex);
        }
    }
    
    /**
     * 恢复作业.
     */
    public synchronized void resumeJob() {
        try {
            if (!scheduler.isShutdown()) {
                scheduler.resumeAll();
            }
        } catch (final SchedulerException ex) {
            throw new JobSystemException(ex);
        }
    }
    
    /**
     * 立刻启动作业.
     */
    public synchronized void triggerJob() {
        try {
            if (!scheduler.isShutdown()) {
                scheduler.triggerJob(jobDetail.getKey());
            }
        } catch (final SchedulerException ex) {
            throw new JobSystemException(ex);
        }
    }
    
    /**
     * 关闭调度器.
     */
    public synchronized void shutdown() {
        try {
            if (!scheduler.isShutdown()) {
                scheduler.shutdown();
            }
        } catch (final SchedulerException ex) {
            throw new JobSystemException(ex);
        }
    }
}
