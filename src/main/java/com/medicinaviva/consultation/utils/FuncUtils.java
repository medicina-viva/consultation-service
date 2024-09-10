package com.medicinaviva.consultation.utils;

import java.sql.Time;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.hibernate.collection.spi.PersistentSet;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import com.medicinaviva.consultation.model.CustomJwtAuthToken;
import com.medicinaviva.consultation.model.event.ConsultationEvent;
import com.medicinaviva.consultation.model.exception.UnauthorizedException;
import com.medicinaviva.consultation.persistence.entity.Consultation;
import com.medicinaviva.consultation.persistence.entity.ConsultationHistory;
import com.medicinaviva.consultation.persistence.entity.Specialty;
import com.medicinaviva.consultation.persistence.entity.SubSpecialty;
import com.medicinaviva.consultation.persistence.repository.ConsultationHistoryRepository;

@Component
public class FuncUtils {
    public static LocalDateTime convertDateToLocalDateTime(Date date) {
        return date.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime();
    }

    public static boolean isEqual(Time argumentOne, Time argumentTwo) {
        return argumentOne.equals(argumentTwo);
    }

    public static boolean isAfter(Time argumentOne, Time argumentTwo) {
        return argumentOne.toLocalTime().isAfter(argumentOne.toLocalTime());

    }

    public static Time minusMinutes(Time time, int minutes) {
        return Time.valueOf(time.toLocalTime().minusMinutes(minutes));
    }

    public static ConsultationEvent consultationEventFactory(Consultation consultation) {
        return ConsultationEvent
                .builder()
                .patientId(consultation.getPatientId())
                .doctorId(consultation.getDoctorId())
                .consultationDate(consultation.getConsultationDate())
                .consultationTime(consultation.getConsultationTime())
                .consultationStatus(consultation.getConsultationStatus())
                .isTeleConsultation(consultation.isTeleConsultation())
                .build();
    }

    public static List<Specialty> convertToSet(List<Specialty> specialties) {
        return specialties
                .stream()
                .map(item -> {
                    if (item.getSubspecialties() instanceof PersistentSet) {
                        Set<SubSpecialty> standardSet = new HashSet<>((PersistentSet) item.getSubspecialties());
                        item.setSubspecialties(standardSet);
                    }
                    return item;
                }).toList();
    }

    public static String getUserIdentifier() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication instanceof CustomJwtAuthToken) {
            CustomJwtAuthToken jwtToken = (CustomJwtAuthToken) authentication;
            return jwtToken.getUserIdentifier();
        }
        throw new UnauthorizedException("Token is invalid.");
    }

    public static void addConsultationHistory(Consultation consultation, String desc,
            ConsultationHistoryRepository repo) {
        String userId = FuncUtils.getUserIdentifier();
        ConsultationHistory consultationHistory = ConsultationHistory
                .builder()
                .userId(userId)
                .description(desc)
                .consultation(consultation)
                .consultationStatus(consultation.getConsultationStatus())
                .build();
        repo.save(consultationHistory);
    }
}
