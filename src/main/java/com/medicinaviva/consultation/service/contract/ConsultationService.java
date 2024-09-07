package com.medicinaviva.consultation.service.contract;

import com.medicinaviva.consultation.model.exception.BusinessException;
import com.medicinaviva.consultation.model.exception.ConflictException;
import com.medicinaviva.consultation.model.exception.NotFoundException;
import com.medicinaviva.consultation.persistence.entity.Consultation;

import java.util.List;

public interface ConsultationService {
    Consultation create(Consultation consultation) throws BusinessException, ConflictException;

    Consultation read(Long id) throws NotFoundException;

    List<Consultation> readByPatientId(String patientId);

    List<Consultation> readByDoctorId(String patientId);

    void confirm(Long id) throws NotFoundException, BusinessException;

    void cancel(Long id, String userId, String motive) throws NotFoundException, BusinessException;

    Consultation update(Consultation consultation) throws BusinessException, ConflictException, NotFoundException;

    void delete(Long id) throws NotFoundException, BusinessException;
}
