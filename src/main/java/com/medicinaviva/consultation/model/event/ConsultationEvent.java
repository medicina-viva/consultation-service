package com.medicinaviva.consultation.model.event;

import java.sql.Time;
import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ConsultationEvent {
    private String patientId;
    private String doctorId;
    private Date consultationDate;
    private Time consultationTime;
    private String consultationStatus;
    private boolean isTeleConsultation;
}
