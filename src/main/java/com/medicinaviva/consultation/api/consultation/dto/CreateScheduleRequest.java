package com.medicinaviva.consultation.api.consultation.dto;

import java.sql.Time;
import java.util.Date;

import lombok.Data;

@Data
public class CreateScheduleRequest {
    private Date availableDate;
    private Time startTime;
    private Time endTime;
}
