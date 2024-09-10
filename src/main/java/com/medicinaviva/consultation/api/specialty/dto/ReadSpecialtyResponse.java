package com.medicinaviva.consultation.api.specialty.dto;

import java.util.List;

import lombok.Data;

@Data
public class ReadSpecialtyResponse {
    private long id;
    private String name;
    private String description;
    private List<ReadSubSpecialtyResponse> subspecialties;
}
