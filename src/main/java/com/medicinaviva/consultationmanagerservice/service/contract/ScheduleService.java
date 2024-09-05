package com.medicinaviva.consultationmanagerservice.service.contract;

import com.medicinaviva.consultationmanagerservice.exception.BusinessException;
import com.medicinaviva.consultationmanagerservice.exception.ConflictException;
import com.medicinaviva.consultationmanagerservice.exception.NotFoundException;
import com.medicinaviva.consultationmanagerservice.persistence.entity.Schedule;

import java.util.List;

public interface ScheduleService {
    Schedule create(Schedule schedule) throws ConflictException, BusinessException;

    Schedule read(Long id) throws NotFoundException;

    List<Schedule> readByDoctorId(String id) ;

    Schedule update(Schedule schedule) throws NotFoundException, ConflictException, BusinessException;

    List<Schedule> readAll();

    void delete(Long id) throws NotFoundException;
}