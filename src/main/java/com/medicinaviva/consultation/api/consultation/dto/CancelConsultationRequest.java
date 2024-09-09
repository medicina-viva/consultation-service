package com.medicinaviva.consultation.api.consultation.dto;

import lombok.Data;

@Data
public class CancelConsultationRequest {
    private String userId;
    private String motive;
}
