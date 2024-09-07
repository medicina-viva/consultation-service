package com.medicinaviva.consultation.service.contract;

public interface Consultation {
    Consultation create(Consultation consultation);
    Consultation read(Long id);
    Consultation update(Consultation consultation);
    void delete(Long id);
}
