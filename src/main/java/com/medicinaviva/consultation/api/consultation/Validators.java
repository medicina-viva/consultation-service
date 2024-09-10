package com.medicinaviva.consultation.api.consultation;


import java.util.ArrayList;
import java.util.List;

import com.medicinaviva.consultation.api.common.validation.ValidationBuilder;
import com.medicinaviva.consultation.api.common.validation.ValidationComposite;
import com.medicinaviva.consultation.api.common.validation.contract.Validator;
import com.medicinaviva.consultation.api.consultation.dto.ScheduleConsultationRequest;

public class Validators {
    protected static String createValidator(ScheduleConsultationRequest request) {
        List<Validator> validators = new ArrayList<>();
        validators.addAll(ValidationBuilder.of("Schedule", request.getSheduleId()).required().build());
        return new ValidationComposite(validators).validate();
    }
}
