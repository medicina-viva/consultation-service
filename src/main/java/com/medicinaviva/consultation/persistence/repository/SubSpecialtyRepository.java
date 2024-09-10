package com.medicinaviva.consultation.persistence.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.medicinaviva.consultation.persistence.entity.SubSpecialty;

public interface SubSpecialtyRepository extends JpaRepository<SubSpecialty, Long> {

    boolean existsByIdAndActive(Long id, boolean active);

    @Query("SELECT COUNT(s) > 0 "
            + "FROM Specialty s "
            + "WHERE "
            + "UPPER(REPLACE(s.name, ' ', '')) = UPPER(REPLACE(:name, ' ', ''))")
    boolean existByName(@Param("name") String name);
}
