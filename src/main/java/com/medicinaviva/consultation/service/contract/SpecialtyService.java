package com.medicinaviva.consultation.service.contract;

import java.util.List;

import com.medicinaviva.consultation.model.exception.BusinessException;
import com.medicinaviva.consultation.model.exception.ConflictException;
import com.medicinaviva.consultation.persistence.entity.Specialty;

public interface SpecialtyService {

    Specialty create(Specialty speciality) throws BusinessException,ConflictException;

    boolean existsById(Long id);

    List<Specialty> readAll();
}
