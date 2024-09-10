package com.medicinaviva.consultation.service.contract;

import com.medicinaviva.consultation.model.exception.BusinessException;
import com.medicinaviva.consultation.model.exception.ConflictException;
import com.medicinaviva.consultation.persistence.entity.Consultation;

public interface ConsultationService {
    Consultation create(Consultation consultation) throws BusinessException, ConflictException;
}
