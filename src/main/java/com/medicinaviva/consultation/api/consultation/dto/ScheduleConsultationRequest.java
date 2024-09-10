package com.medicinaviva.consultation.api.consultation.dto;

import lombok.Data;

@Data
public class ScheduleConsultationRequest {
    private Long sheduleId;
    private boolean isTeleConsultation;
}
