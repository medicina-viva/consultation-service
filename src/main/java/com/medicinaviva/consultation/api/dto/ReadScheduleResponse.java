package com.medicinaviva.consultation.api.dto;

import lombok.Data;

import java.sql.Time;
import java.time.LocalDateTime;
import java.util.Date;

@Data
public class ReadScheduleResponse {
    private Long id;
    private String doctorId;
    private Date availableDate;
    private Time startTime;
    private Time endTime;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
