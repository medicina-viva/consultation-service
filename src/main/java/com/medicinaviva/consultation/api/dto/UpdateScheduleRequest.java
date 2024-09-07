package com.medicinaviva.consultation.api.dto;

import lombok.Data;

import java.sql.Time;
import java.util.Date;

@Data
public class UpdateScheduleRequest {
    private Date availableDate;
    private Time startTime;
    private Time endTime;
}
