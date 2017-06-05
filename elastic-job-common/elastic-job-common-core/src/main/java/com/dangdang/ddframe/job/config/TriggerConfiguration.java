package com.dangdang.ddframe.job.config;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import static com.dangdang.ddframe.job.config.trigger.TriggerType.CoreScheduleBuilder;


@Getter
@Setter
@EqualsAndHashCode
public class TriggerConfiguration {

    private Integer triggerType;
    private String triggerContent;
    private Long startDate;
    private Long endDate;
    private String zone;

    public TriggerConfiguration() {
    }

    /**
     *  兼容原来的core
     * @param core
     */
    public TriggerConfiguration(String core) {
        this.triggerContent = core;
        this.triggerType = CoreScheduleBuilder;
    }

    public TriggerConfiguration(Integer triggerType, Long startDate, Long endDate, String zone, String triggerContent) {
        this.triggerType = triggerType;
        this.startDate = startDate;
        this.endDate = endDate;
        this.zone = zone;
        this.triggerContent = triggerContent;
    }
}
