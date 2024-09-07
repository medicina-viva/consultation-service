package com.medicinaviva.consultation.api.dto;

import lombok.Data;

@Data
public class CancelConsultationRequest {
    private String userId;
    private String motive;
}
