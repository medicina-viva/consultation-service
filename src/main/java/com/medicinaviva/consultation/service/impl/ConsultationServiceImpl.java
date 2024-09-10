package com.medicinaviva.consultation.service.impl;

import java.util.Optional;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Caching;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import com.medicinaviva.consultation.model.enums.ConsultationStatus;
import com.medicinaviva.consultation.model.enums.KafkaTopics;
import com.medicinaviva.consultation.model.event.ConsultationEvent;
import com.medicinaviva.consultation.model.exception.BusinessException;
import com.medicinaviva.consultation.model.exception.ConflictException;
import com.medicinaviva.consultation.persistence.entity.Consultation;
import com.medicinaviva.consultation.persistence.entity.Schedule;
import com.medicinaviva.consultation.persistence.repository.ConsultationRepository;
import com.medicinaviva.consultation.persistence.repository.ScheduleRepository;
import com.medicinaviva.consultation.service.contract.ConsultationService;
import com.medicinaviva.consultation.utils.FuncUtils;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class ConsultationServiceImpl implements ConsultationService {
    private final ConsultationRepository consultationRepository;
    private final ScheduleRepository scheduleRepository;
    private final KafkaTemplate<String, ConsultationEvent> kafkaTemplate;

    @Override
    @Caching(evict = {
            @CacheEvict(value = "consultations", allEntries = true),
            @CacheEvict(value = "schedules", allEntries = true),
            @CacheEvict(value = "doctor_schedules", allEntries = true)
    })
    public Consultation create(Consultation consultation) throws BusinessException, ConflictException {
        Optional<Schedule> schedule = this.scheduleRepository
                .findByIdAndActive(consultation.getScheduleId(), true);

        if (schedule.isEmpty())
            throw new BusinessException("Could not find schedule for the consultation.");
        schedule.get().setActive(false);
        this.scheduleRepository.save(schedule.get());

        String patientId = FuncUtils.getUserIdentifier();
        consultation.setId(null);
        consultation.setActive(true);
        consultation.setPatientId(patientId);
        consultation.setDoctorId(schedule.get().getDoctorId());
        consultation.setConsultationTime(schedule.get().getStartTime());
        consultation.setConsultationDate(schedule.get().getAvailableDate());
        consultation.setConsultationStatus(ConsultationStatus.PENDING.getValue());
        consultation = this.consultationRepository.save(consultation);

        ConsultationEvent event = FuncUtils.consultationEventFactory(consultation);
        kafkaTemplate.send(KafkaTopics.CONSULTATION_SCHEDULED.getValue(), event);
        return consultation;
    }
}
