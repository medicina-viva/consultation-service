package com.medicinaviva.consultation.model.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ConsultationStatus {
    PENDING("PENDING"),
    CONFIRMED("CONFIRMED"),
    CANCELED("CANCELED"),
    REMANDED("REMANDED"),
    FINISHED("FINISHED");

    private String value;
}
