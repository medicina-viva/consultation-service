package com.medicinaviva.consultation.api.dto;

import lombok.Data;

import java.sql.Time;
import java.util.Date;

@Data
public class ReadConsultationResponse {
    private String doctorId;
    private String patientId;
    private Date consultationDate;
    private Time consultationTime;
    private boolean isTeleConsultation;
}
