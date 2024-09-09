package com.medicinaviva.consultation.service.impl;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.medicinaviva.consultation.model.exception.BusinessException;
import com.medicinaviva.consultation.model.exception.ConflictException;
import com.medicinaviva.consultation.persistence.entity.Specialty;
import com.medicinaviva.consultation.persistence.entity.SubSpecialty;
import com.medicinaviva.consultation.persistence.repository.SpecialtyRepository;
import com.medicinaviva.consultation.persistence.repository.SubSpecialtyRepository;
import com.medicinaviva.consultation.service.contract.SpecialtyService;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class SpecialtyServiceImpl implements SpecialtyService {

    private final SpecialtyRepository specialtyRepository;
    private final SubSpecialtyRepository subspecialtyRepository;

    @Override
    @CacheEvict(value = "specialties", allEntries = true)
    public Specialty create(Specialty speciality) throws BusinessException, ConflictException {
        this.isValidSpecialty(speciality.getName());
        this.createSubSpecialtyIfNotExist(speciality.getSubspecialties());
        return this.specialtyRepository.save(speciality);
    }

    @Override
    @Cacheable(value = "exist_specialties", key = "#id")
    public boolean existsById(Long id) {
        return this.specialtyRepository.existsByIdAndActive(id, true);
    }

    @Override
    @Cacheable(value = "specialties")
    public List<Specialty> readAll() {
        return this.specialtyRepository.findAll();
    }

    private void createSubSpecialtyIfNotExist(Set<SubSpecialty> subSpecialties) throws BusinessException {
        List<SubSpecialty> nonExistSubspecialties = subSpecialties
                .stream()
                .filter(sub -> {
                    return !this.subspecialtyRepository
                            .existByName(sub.getName());
                }).collect(Collectors.toList());

        if (!nonExistSubspecialties.isEmpty()) {
            this.subspecialtyRepository.saveAll(nonExistSubspecialties);
        }
    }

    private void isValidSpecialty(String name) throws ConflictException {
        boolean result = this.specialtyRepository.existByName(name);
        if (result) {
            throw new ConflictException("Specialty with the name: \"" + name
                    + "\" already exists. Consider activating it if it is not currently active."
            );
        }
    }
}
