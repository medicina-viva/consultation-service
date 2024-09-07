package com.medicinaviva.consultation.model.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum KafkaTopics {
    CONSULTATION_SCHEDULED("consultationScheduledTopic");
    private String value;
}
