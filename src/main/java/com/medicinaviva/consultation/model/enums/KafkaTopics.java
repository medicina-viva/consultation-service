package com.medicinaviva.consultation.model.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum KafkaTopics {
    CONSULTATION_SCHEDULED("consultationScheduledTopic"),
    CONSULTATION_CONFIRMED("consultationConfirmedTopic"),
    CONSULTATION_CANCELED("consultationCanceledTopic");

    private String value;
}
