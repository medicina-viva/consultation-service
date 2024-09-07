package com.medicinaviva.consultation.api.controller;


import com.medicinaviva.consultation.api.dto.CreateScheduleRequest;
import com.medicinaviva.consultation.api.dto.UpdateScheduleRequest;
import com.medicinaviva.consultation.api.validation.ValidationBuilder;
import com.medicinaviva.consultation.api.validation.ValidationComposite;
import com.medicinaviva.consultation.api.validation.contract.Validator;

import java.util.ArrayList;
import java.util.List;

public class ScheduleControllerValidators {
    static String createValidator(CreateScheduleRequest request) {
        List<Validator> validators = new ArrayList<>();
        validators.addAll(ValidationBuilder.of("Doctor", request.getDoctorId()).required().build());
        validators.addAll(ValidationBuilder.of("available date", request.getAvailableDate()).required().build());
        validators.addAll(ValidationBuilder.of("Start time", request.getStartTime()).required().build());
        validators.addAll(ValidationBuilder.of("End time", request.getEndTime()).required().build());
        return new ValidationComposite(validators).validate();
    }

    static String updateValidator(UpdateScheduleRequest request) {
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
