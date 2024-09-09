package com.medicinaviva.consultation.api.consultation.dto;

import java.sql.Time;
import java.util.Date;

import lombok.Data;

@Data
public class ReadConsultationResponse {
    private String doctorId;
    private String patientId;
    private Date consultationDate;
    private Time consultationTime;
    private boolean isTeleConsultation;
}
