package com.medicinaviva.consultationmanagerservice.service.impl;

import com.medicinaviva.consultationmanagerservice.exception.ConflictException;
import com.medicinaviva.consultationmanagerservice.exception.BusinessException;
import com.medicinaviva.consultationmanagerservice.exception.NotFoundException;
import com.medicinaviva.consultationmanagerservice.persistence.entity.Schedule;
import com.medicinaviva.consultationmanagerservice.persistence.repository.ScheduleRepository;
import com.medicinaviva.consultationmanagerservice.service.contract.ScheduleService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.sql.Time;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class ScheduleServiceImpl implements ScheduleService {
    private final ScheduleRepository secureRepository;

    @Override
    public Schedule create(Schedule schedule) throws ConflictException, BusinessException {
        schedule.setId(null);
        if (!this.isValidSchedule(schedule)) throw new ConflictException("This schedule is already added.");
        schedule.setActive(true);
        return this.secureRepository.save(schedule);
    }

    @Override
    public Schedule read(Long id) throws NotFoundException {
        return this.secureRepository
                .findByIdAndActive(id, true)
                .orElseThrow(() -> new NotFoundException(String.format("Could not find schedule with code: %d.", id)));
    }

    @Override
    public List<Schedule> readByDoctorId(String id) {
        return this.secureRepository
                .findByDoctorIdAndActive(id, true);
    }

    @Override
    public List<Schedule> readAll() {
        return this.secureRepository.findAll();
    }


    @Override
    public Schedule update(Schedule schedule) throws NotFoundException, ConflictException, BusinessException {
        Schedule savedSchedule = this.read(schedule.getId());
        if (!this.isValidSchedule(schedule)) throw new ConflictException("This schedule is already added.");

        savedSchedule.setAvailableDate(schedule.getAvailableDate() != null ?
                schedule.getAvailableDate() : savedSchedule.getAvailableDate());

        savedSchedule.setStartTime(schedule.getStartTime() != null ?
                schedule.getStartTime() : savedSchedule.getStartTime());

        savedSchedule.setEndTime(schedule.getEndTime() != null ?
                schedule.getEndTime() : savedSchedule.getEndTime());

        return this.secureRepository.save(savedSchedule);
    }

    @Override
    public void delete(Long id) throws NotFoundException {
        Schedule schedule = this.read(id);
        schedule.setActive(false);
        this.secureRepository.save(schedule);
    }

    private boolean isValidSchedule(Schedule schedule) throws BusinessException {
        if(schedule.getStartTime().equals(schedule.getEndTime())
                || schedule.getStartTime().toLocalTime().isAfter(schedule.getEndTime().toLocalTime())
        ) throw new BusinessException("The Start time must be after End time.");

        Optional<Schedule> savedSchedule = this.secureRepository
                .findScheduleByEndTimeBetween(schedule.getDoctorId(),
                        schedule.getAvailableDate(),
                        Time.valueOf(schedule.getStartTime().toLocalTime().minusMinutes(15)),
                        schedule.getStartTime());

        if (savedSchedule.isPresent()) throw new BusinessException("Schedule conflict. You have a schedule that start at: "
                + savedSchedule.get().getStartTime() + " and end at: " + savedSchedule.get().getEndTime() +
                ". You're trying to create a schedule that starts at: " + schedule.getStartTime()
                + " and ends at: " + schedule.getEndTime() +
                ", what cannot be possible, you must have alt least 15 min of break time.");

        savedSchedule = this.secureRepository
                .findScheduleByStartTime(schedule.getDoctorId(), schedule.getAvailableDate(), schedule.getStartTime());

        if(savedSchedule.isPresent()) return false;

        savedSchedule = this.secureRepository
                .findScheduleByEndTime(schedule.getDoctorId(), schedule.getAvailableDate(), schedule.getEndTime());

        return savedSchedule.isEmpty();
    }
}
