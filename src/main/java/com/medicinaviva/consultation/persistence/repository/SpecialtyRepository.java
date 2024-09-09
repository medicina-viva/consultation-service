package com.medicinaviva.consultation.persistence.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.medicinaviva.consultation.persistence.entity.Specialty;

public interface SpecialtyRepository extends JpaRepository<Specialty, Long> {

    @Query("SELECT sp FROM Specialty sp WHERE sp.active =  true")
    List<Specialty> findAll();

    boolean existsByIdAndActive(Long id, boolean active);

    @Query("SELECT COUNT(s) > 0 "
            + "FROM Specialty s "
            + "WHERE "
            + "UPPER(REPLACE(s.name, ' ', '')) = UPPER(REPLACE(:name, ' ', ''))")
    boolean existByName(@Param("name") String name);
}
