package com.medicinaviva.consultationmanagerservice.api.dto;

import lombok.Data;

import java.sql.Time;
import java.util.Date;

@Data
public class CreateScheduleRequest {
    private String doctorId;
    private Date availableDate;
    private Time startTime;
    private Time endTime;
}
