package com.medicinaviva.consultation.api.specialty.dto;

import java.util.List;

import lombok.Data;

@Data
public class CreateSpecialtyRequest {
    private String name;
    private String description;
    private List<CreateSubSpecialtyRequest> subspecialties;
}
