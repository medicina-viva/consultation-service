package com.medicinaviva.consultation.api.schedule.dto;

import java.sql.Time;
import java.util.Date;

import lombok.Data;

@Data
public class UpdateScheduleRequest {
    private Date availableDate;
    private Time startTime;
    private Time endTime;
}
