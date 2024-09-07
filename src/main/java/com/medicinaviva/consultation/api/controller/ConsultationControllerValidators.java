package com.medicinaviva.consultation.api.controller;


import com.medicinaviva.consultation.api.dto.CancelConsultationRequest;
import com.medicinaviva.consultation.api.dto.ScheduleConsultationRequest;
import com.medicinaviva.consultation.api.dto.UpdateConsultationRequest;
import com.medicinaviva.consultation.api.validation.ValidationBuilder;
import com.medicinaviva.consultation.api.validation.ValidationComposite;
import com.medicinaviva.consultation.api.validation.contract.Validator;

import java.util.ArrayList;
import java.util.List;

public class ConsultationControllerValidators {
    public static String createValidator(ScheduleConsultationRequest request) {
        List<Validator> validators = new ArrayList<>();
        validators.addAll(ValidationBuilder.of("Doctor", request.getDoctorId()).required().build());
        validators.addAll(ValidationBuilder.of("Patient", request.getDoctorId()).required().build());
        validators.addAll(ValidationBuilder.of("Consultation date", request.getConsultationDate()).required().build());
        validators.addAll(ValidationBuilder.of("Consultation time", request.getConsultationTime()).required().build());
        return new ValidationComposite(validators).validate();
    }

    public static String cancelConsultation(CancelConsultationRequest request) {
        List<Validator> validators = new ArrayList<>();
        validators.addAll(ValidationBuilder.of("User", request.getUserId()).required().build());
        validators.addAll(ValidationBuilder.of("motive", request.getMotive()).required().build());
        return new ValidationComposite(validators).validate();
    }

    public static String updateConsultation(UpdateConsultationRequest request) {
        List<Validator> validators = new ArrayList<>();
        if(request.getConsultationDate() != null)
            validators.addAll(ValidationBuilder.of("Consultation date", request.getConsultationDate()).required().build());
        if(request.getConsultationTime() != null)
            validators.addAll(ValidationBuilder.of("Consultation time", request.getConsultationTime()).required().build());
        return new ValidationComposite(validators).validate();
    }

}
