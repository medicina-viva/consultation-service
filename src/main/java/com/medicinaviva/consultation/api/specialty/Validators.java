package com.medicinaviva.consultation.api.specialty;

import java.util.ArrayList;
import java.util.List;

import com.medicinaviva.consultation.api.common.validation.ValidationBuilder;
import com.medicinaviva.consultation.api.common.validation.ValidationComposite;
import com.medicinaviva.consultation.api.common.validation.contract.Validator;
import com.medicinaviva.consultation.api.specialty.dto.CreateSpecialtyRequest;

public class Validators {
    protected static String createValidator(CreateSpecialtyRequest request) {
        List<Validator> validators = new ArrayList<>();
        validators.addAll(ValidationBuilder.of("Name", request.getName()).required().build());
        if (!request.getSubspecialties().isEmpty()) {
            for (int i = 0; i < request.getSubspecialties().size(); i++) {
                validators.addAll(ValidationBuilder.of("name for item: " + (i + 1) + " on  subspecialtiess",
                        request.getSubspecialties().get(i).getName()).required().build());
            }
        }
        return new ValidationComposite(validators).validate();
    }
}
