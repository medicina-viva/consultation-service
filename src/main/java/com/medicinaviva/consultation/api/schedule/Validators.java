package com.medicinaviva.consultation.api.schedule;


import java.util.ArrayList;
import java.util.List;

import com.medicinaviva.consultation.api.common.validation.ValidationBuilder;
import com.medicinaviva.consultation.api.common.validation.ValidationComposite;
import com.medicinaviva.consultation.api.common.validation.contract.Validator;
import com.medicinaviva.consultation.api.consultation.dto.CreateScheduleRequest;
import com.medicinaviva.consultation.api.schedule.dto.UpdateScheduleRequest;

public class Validators {
    protected static String createValidator(CreateScheduleRequest request) {
        List<Validator> validators = new ArrayList<>();
        validators.addAll(ValidationBuilder.of("Doctor", request.getDoctorId()).required().build());
        validators.addAll(ValidationBuilder.of("available date", request.getAvailableDate()).required().build());
        validators.addAll(ValidationBuilder.of("Start time", request.getStartTime()).required().build());
        validators.addAll(ValidationBuilder.of("End time", request.getEndTime()).required().build());
        return new ValidationComposite(validators).validate();
    }

    protected static String updateValidator(UpdateScheduleRequest request) {
        List<Validator> validators = new ArrayList<>();
        if (request.getAvailableDate() != null)
            validators.addAll(ValidationBuilder.of("available date", request.getAvailableDate()).required().build());

        if (request.getStartTime() != null)
            validators.addAll(ValidationBuilder.of("Start time", request.getStartTime()).required().build());

        if (request.getEndTime() != null)
            validators.addAll(ValidationBuilder.of("End time", request.getEndTime()).required().build());

        return new ValidationComposite(validators).validate();
    }
}
