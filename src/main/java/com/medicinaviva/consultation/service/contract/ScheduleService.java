package com.medicinaviva.consultation.service.contract;

import java.util.List;

import com.medicinaviva.consultation.model.exception.BusinessException;
import com.medicinaviva.consultation.model.exception.ConflictException;
import com.medicinaviva.consultation.model.exception.NotFoundException;
import com.medicinaviva.consultation.persistence.entity.Schedule;

public interface ScheduleService {
    Schedule create(Schedule schedule) throws ConflictException, BusinessException;

    Schedule read(Long id) throws NotFoundException;

    List<Schedule> readByDoctorId(String doctorId);


    Schedule update(Schedule schedule) throws NotFoundException, ConflictException, BusinessException;

    List<Schedule> readAll();

    void delete(Long id) throws NotFoundException;
}