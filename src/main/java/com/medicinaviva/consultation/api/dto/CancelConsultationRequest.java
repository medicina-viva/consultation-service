package com.medicinaviva.consultation.api.dto;

import lombok.Data;

@Data
public class CancelConsultationResponse {
    private String consultationId;
    private String userId;
    private String motive;
}
