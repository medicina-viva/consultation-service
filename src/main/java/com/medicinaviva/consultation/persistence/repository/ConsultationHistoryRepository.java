package com.medicinaviva.consultation.persistence.repository;

import com.medicinaviva.consultation.persistence.entity.Consultation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.sql.Time;
import java.util.Date;
import java.util.Optional;

public interface ConsultationRepository extends JpaRepository<Consultation, Long> {
    Optional<Consultation> findByIdAndActive(Long id, boolean active);

    @Query("SELECT cn " +
            "FROM Consultation cn " +
            "WHERE " +
            " cn.patientId = : patientId " +
            " AND  cn.consultationDate = :consultationDate " +
            " AND cn.consultationTime = :consultationTime " +
            " AND cn.consultationStatus = 'PENDING' OR cn.consultationStatus = 'CONFIRMED'")
    Optional<Consultation> findByPatientConsultationId(
            String patientConsultationId,
            Date consultationDate,
            Time consultationTime
    );
}
