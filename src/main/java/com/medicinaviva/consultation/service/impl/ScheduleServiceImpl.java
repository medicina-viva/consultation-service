package com.medicinaviva.consultation.service.impl;

import java.util.List;
import java.util.Optional;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;

import com.medicinaviva.consultation.model.exception.BusinessException;
import com.medicinaviva.consultation.model.exception.ConflictException;
import com.medicinaviva.consultation.model.exception.NotFoundException;
import com.medicinaviva.consultation.persistence.entity.Schedule;
import com.medicinaviva.consultation.persistence.repository.ScheduleRepository;
import com.medicinaviva.consultation.service.contract.ScheduleService;
import com.medicinaviva.consultation.utils.FuncUtils;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class ScheduleServiceImpl implements ScheduleService {
        private final ScheduleRepository secureRepository;

        @Override
        @CacheEvict(value = { "schedules", "doctor_schedules" }, allEntries = true)
        public Schedule create(Schedule schedule) throws ConflictException, BusinessException {
                String userId = FuncUtils.getUserIdentifier();
                schedule.setDoctorId(userId);
                schedule.setId(null);
                if (!this.isValidSchedule(schedule))
                        throw new ConflictException("This schedule is already added.");
                schedule.setActive(true);
                return this.secureRepository.save(schedule);
        }

        @Override
        @Cacheable(value = "schedules", key = "#id")
        public Schedule read(Long id) throws NotFoundException {
                return this.secureRepository
                                .findByIdAndActive(id, true)
                                .orElseThrow(() -> new NotFoundException(
                                                String.format("Could not find schedule with code: %d.", id)));
        }

        @Override
        @Cacheable(value = "doctor_schedules")
        public List<Schedule> readByDoctorId(String doctorId) {
                return this.secureRepository
                                .findByDoctorIdAndActive(doctorId, true);
        }

        @Override
        @Cacheable(value = "schedules")
        public List<Schedule> readAll() {
                return this.secureRepository.findAll();
        }

        @Override
        @CachePut(value = "schedules", key = "#schedule.id")
        @CacheEvict(value = "doctor_schedules", allEntries = true)
        public Schedule update(Schedule schedule) throws NotFoundException, ConflictException, BusinessException {
                Schedule savedSchedule = this.read(schedule.getId());
                if (!this.isValidSchedule(schedule))
                        throw new ConflictException("This schedule is already added.");

                savedSchedule.setAvailableDate(schedule.getAvailableDate() != null ? schedule.getAvailableDate()
                                : savedSchedule.getAvailableDate());
                savedSchedule.setStartTime(schedule.getStartTime() != null ? schedule.getStartTime()
                                : savedSchedule.getStartTime());
                savedSchedule.setEndTime(
                                schedule.getEndTime() != null ? schedule.getEndTime() : savedSchedule.getEndTime());
                return this.secureRepository.save(savedSchedule);
        }

        @Override
        @Caching(evict = {
                        @CacheEvict(value = "schedules", key = "#id"),
                        @CacheEvict(value = "doctor_schedules", allEntries = true)
        })
        public void delete(Long id) throws NotFoundException {
                Schedule schedule = this.read(id);
                schedule.setActive(false);
                this.secureRepository.save(schedule);
        }

        private boolean isValidSchedule(Schedule schedule) throws BusinessException {
                if (FuncUtils.isEqual(schedule.getStartTime(), schedule.getEndTime())
                                || FuncUtils.isAfter(schedule.getStartTime(), schedule.getEndTime()))
                        throw new BusinessException("The Start time must be after End time.");

                Optional<Schedule> savedSchedule = this.secureRepository
                                .findScheduleByEndTimeBetween(schedule.getDoctorId(),
                                                schedule.getAvailableDate(),
                                                FuncUtils.minusMinutes(schedule.getStartTime(), 15),
                                                schedule.getStartTime());

                if (savedSchedule.isPresent())
                        throw new BusinessException("Schedule conflict. You have a schedule that start at: "
                                        + savedSchedule.get().getStartTime() + " and end at: "
                                        + savedSchedule.get().getEndTime() +
                                        ". You're trying to create a schedule that starts at: "
                                        + schedule.getStartTime()
                                        + " and ends at: " + schedule.getEndTime() +
                                        ", what cannot be possible, you must have alt least 15 min of break time.");

                savedSchedule = this.secureRepository
                                .findScheduleByStartTime(schedule.getDoctorId(), schedule.getAvailableDate(),
                                                schedule.getStartTime());

                if (savedSchedule.isPresent())
                        return false;

                savedSchedule = this.secureRepository
                                .findScheduleByEndTime(schedule.getDoctorId(), schedule.getAvailableDate(),
                                                schedule.getEndTime());

                return savedSchedule.isEmpty();
        }
}
