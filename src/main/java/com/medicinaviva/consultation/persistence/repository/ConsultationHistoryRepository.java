package com.medicinaviva.consultation.persistence.repository;

import com.medicinaviva.consultation.persistence.entity.ConsultationHistory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ConsultationHistoryRepository extends JpaRepository<ConsultationHistory, Long> {

}
