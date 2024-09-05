package com.medicinaviva.consultationmanagerservice.persistence.repository;

import com.medicinaviva.consultationmanagerservice.persistence.entity.Schedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.sql.Time;
import java.util.Date;
import java.util.List;
import java.util.Optional;

public interface ScheduleRepository extends JpaRepository<Schedule, Long> {
    @Query("SELECT sc  FROM Schedule sc WHERE sc.active = true")
    List<Schedule> findAll();

    Optional<Schedule> findByIdAndActive(Long id, boolean active);

    List<Schedule> findByDoctorIdAndActive(String id, boolean active);

    @Query("SELECT sc " +
            "FROM Schedule sc " +
            "WHERE " +
            "sc.doctorId=:doctorId " +
            "AND sc.availableDate=:availableDate " +
            "AND  sc.startTime=:startTime " +
            "AND sc.active = true")
    Optional<Schedule> findScheduleByStartTime(@Param("doctorId") String doctorId,
                                    @Param("availableDate") Date availableDate,
                                    @Param("startTime") Time startTime);

    @Query("SELECT sc " +
            "FROM Schedule sc " +
            "WHERE " +
            "sc.doctorId=:doctorId " +
            "AND sc.availableDate=:availableDate " +
            "AND  sc.endTime=:endTime " +
            "AND sc.active = true")
    Optional<Schedule> findScheduleByEndTime(@Param("doctorId") String doctorId,
                                               @Param("availableDate") Date availableDate,
                                             @Param("endTime") Time endTime);

    @Query("SELECT sc " +
            "FROM Schedule sc " +
            "WHERE " +
            "sc.doctorId=:doctorId " +
            "AND sc.availableDate=:availableDate " +
            "AND  sc.endTime BETWEEN :endTime AND :endTimePlusBreak   " +
            "AND sc.active = true")
    Optional<Schedule> findScheduleByEndTimeBetween(@Param("doctorId") String doctorId,
                                               @Param("availableDate") Date availableDate,
                                               @Param("endTime") Time endTime,
                                                    @Param("endTimePlusBreak") Time endTimePlusBreak);
}
