package com.medicinaviva.consultation.service.impl;

import com.medicinaviva.consultation.model.enums.ConsultationStatus;
import com.medicinaviva.consultation.model.enums.KafkaTopics;
import com.medicinaviva.consultation.model.event.ConsultationEvent;
import com.medicinaviva.consultation.model.exception.BusinessException;
import com.medicinaviva.consultation.model.exception.ConflictException;
import com.medicinaviva.consultation.model.exception.NotFoundException;
import com.medicinaviva.consultation.persistence.entity.Consultation;
import com.medicinaviva.consultation.persistence.entity.ConsultationHistory;
import com.medicinaviva.consultation.persistence.entity.Schedule;
import com.medicinaviva.consultation.persistence.repository.ConsultationHistoryRepository;
import com.medicinaviva.consultation.persistence.repository.ConsultationRepository;
import com.medicinaviva.consultation.persistence.repository.ScheduleRepository;
import com.medicinaviva.consultation.service.contract.ConsultationService;
import com.medicinaviva.consultation.utils.FuncUtils;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;


@Service
@Transactional
@RequiredArgsConstructor
public class ConsultationServiceImpl implements ConsultationService {
    private final ConsultationRepository consultationRepository;
    private final ScheduleRepository scheduleRepository;
    private final ConsultationHistoryRepository consultationHistoryRepository;
    private final KafkaTemplate<String, ConsultationEvent> kafkaTemplate;

    @Override
    @Caching(evict = {
            @CacheEvict(value = "consultations", allEntries = true),
            @CacheEvict(value = "schedules", allEntries = true),
            @CacheEvict(value = "doctor_schedules", allEntries = true)
    })
    public Consultation create(Consultation consultation) throws BusinessException, ConflictException {
        this.isValidConsultation(consultation);
        consultation.setId(null);
        consultation.setActive(true);
        consultation.setConsultationStatus(ConsultationStatus.PENDING.getValue());
        consultation = this.consultationRepository.save(consultation);
        Schedule schedule = this.scheduleRepository.
                findScheduleByStartTime(
                        consultation.getDoctorId(),
                        consultation.getConsultationDate(),
                        consultation.getConsultationTime()).get();

        schedule.setActive(false);
        this.scheduleRepository.save(schedule);

        ConsultationEvent event  = FuncUtils.consultationEventFactory(consultation);
        kafkaTemplate.send(KafkaTopics.CONSULTATION_SCHEDULED.getValue(),event);
        return consultation;
    }

    @Override
    @Cacheable(value = "consultations", key = "#id")
    public Consultation read(Long id) throws NotFoundException {
        return this.consultationRepository
                .findByIdAndActive(id, true)
                .orElseThrow(() -> new NotFoundException(String.format("Could not find consultation with code: %d.", id)));
    }

    @Override
    public List<Consultation> readByPatientId(String patientId) {
        return this.consultationRepository.findByPatientIdAndActive(patientId,true);
    }

    @Override
    public List<Consultation> readByDoctorId(String patientId) {
        return this.consultationRepository.findByDoctorIdAndActive(patientId,true);
    }

    @Override
    @CachePut(value = "consultations", key = "#id")
    public void confirm(Long id) throws NotFoundException, BusinessException {
        Consultation consultation = this.read(id);
        if (!consultation.getConsultationStatus().equals(ConsultationStatus.PENDING.getValue()))
            throw new BusinessException("Can only confirm pending consultations.");

        consultation.setConsultationStatus(ConsultationStatus.CONFIRMED.getValue());
        this.consultationRepository.save(consultation);

        ConsultationHistory history = ConsultationHistory
                .builder()
                .id(null)
                .consultationId(consultation.getDoctorId())
                .userId(consultation.getPatientId())
                .description("Confirm consultation.")
                .consultationStatus(ConsultationStatus.CONFIRMED.getValue())
                .build();
                
        this.consultationHistoryRepository.save(history);
        ConsultationEvent event  = FuncUtils.consultationEventFactory(consultation);
        kafkaTemplate.send(KafkaTopics.CONSULTATION_SCHEDULED.getValue(),event);
    }

    @Override
    @CachePut(value = "consultations", key = "#id")
    public void cancel(Long id, String userId, String motive) throws NotFoundException, BusinessException {
        Consultation consultation = this.read(id);
        if (!consultation.getConsultationStatus().equals(ConsultationStatus.PENDING.getValue())
                || !consultation.getConsultationStatus().equals(ConsultationStatus.CONFIRMED.getValue()))
            throw new BusinessException("Can only cancel pending or confirmed consultations.");

        consultation.setConsultationStatus(ConsultationStatus.CANCELED.getValue());
        this.consultationRepository.save(consultation);

        ConsultationHistory history = ConsultationHistory
                .builder()
                .id(null)
                .consultationId(consultation.getDoctorId())
                .userId(userId)
                .description(motive)
                .consultationStatus(ConsultationStatus.CONFIRMED.getValue())
                .build();
                
        this.consultationHistoryRepository.save(history);
        ConsultationEvent event  = FuncUtils.consultationEventFactory(consultation);
        kafkaTemplate.send(KafkaTopics.CONSULTATION_SCHEDULED.getValue(),event);
    }

    @Override
    @Caching(evict = {
            @CacheEvict(value = "doctor_schedules", allEntries = true),
            @CacheEvict(value = "schedules", allEntries = true)
    })
    @CachePut(value = "consultations", key = "#consultation.id")
    public Consultation update(Consultation consultation) throws BusinessException, ConflictException, NotFoundException {
        Consultation savedConsultation = this.read(consultation.getId());
        if (savedConsultation.getConsultationStatus().equals(ConsultationStatus.FINISHED.getValue()))
            throw new BusinessException("Cannot update this consultation because it has finished.");

        if (savedConsultation.getConsultationStatus().equals(ConsultationStatus.CONFIRMED.getValue()))
            throw new BusinessException("Cannot update this consultation because it has been confirmed.");

        if (savedConsultation.getConsultationStatus().equals(ConsultationStatus.CANCELED.getValue()))
            throw new BusinessException("Cannot update this consultation because it has been canceled.");

        this.isValidConsultation(consultation);

        savedConsultation.setConsultationDate(consultation.getConsultationDate() != null ?
                consultation.getConsultationDate() : savedConsultation.getConsultationDate());

        savedConsultation.setConsultationDate(consultation.getConsultationTime() != null ?
                consultation.getConsultationTime() : savedConsultation.getConsultationTime());

        savedConsultation
                .setTeleConsultation(
                        savedConsultation.isTeleConsultation() == consultation.isTeleConsultation() ?
                                savedConsultation.isTeleConsultation() : consultation.isTeleConsultation());

        if (savedConsultation.getConsultationDate() != consultation.getConsultationDate()
                && savedConsultation.getConsultationTime() != consultation.getConsultationTime()
                && consultation.getConsultationDate() != null && consultation.getConsultationTime() != null ) {
            Schedule schedule = this.scheduleRepository.
                    findScheduleByStartTime(
                            savedConsultation.getDoctorId(),
                            consultation.getConsultationDate(),
                            consultation.getConsultationTime()).get();

            schedule.setActive(false);
            this.scheduleRepository.save(schedule);
            savedConsultation.setConsultationStatus(ConsultationStatus.PENDING.getValue());
        }

        savedConsultation = this.consultationRepository.save(savedConsultation);

        return savedConsultation;
    }

    @Override
    @CacheEvict(value = "consultations", key = "#id")
    public void delete(Long id) throws NotFoundException, BusinessException {
        Consultation consultation = this.read(id);
        if (consultation.getConsultationStatus().equals(ConsultationStatus.FINISHED.getValue()))
            throw new BusinessException("Cannot remove this consultation because it has finished.");

        if (consultation.getConsultationStatus().equals(ConsultationStatus.CONFIRMED.getValue()))
            throw new BusinessException("To remove this consultation you must cancel first.");

        LocalDateTime consultationDate = FuncUtils.convertDateToLocalDateTime(consultation.getConsultationDate());
        if (LocalDateTime.now().isBefore(consultationDate.plusHours(24)))
            throw new BusinessException("Cannot remove this consultation because,it has 24 hours " +
                    "to be Remanded after the consultation date.");
        consultation.setActive(false);
        this.consultationRepository.save(consultation);
    }

    private void isValidConsultation(Consultation consultation) throws ConflictException, BusinessException {
       if(consultation.getDoctorId() != null
               &&  consultation.getConsultationDate() != null
               && consultation.getConsultationTime() != null){

           Optional<Consultation> savedConsultation = this.consultationRepository
                   .findByPatientConsultationId(
                           consultation.getPatientId(),
                           consultation.getConsultationDate(),
                           consultation.getConsultationTime());

           if (savedConsultation.isPresent())
               throw new ConflictException("You've scheduled consultation for this date and time.");

           Optional<Schedule> schedule = this.scheduleRepository.
                   findScheduleByStartTime(
                           consultation.getDoctorId(),
                           consultation.getConsultationDate(),
                           consultation.getConsultationTime());

           if (schedule.isEmpty())
               throw new BusinessException("Doctor does not have break for this date and time," +
                       "please verify the doctor schedule and try again.");
       }
    }
}
