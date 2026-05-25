package com.gabrielf.clinica.repository;

import com.gabrielf.clinica.model.Appointment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface AppointmentRepository extends JpaRepository<Appointment, UUID> {

    List<Appointment> findByPatientId(UUID patientId);
    List<Appointment> findByDoctorId(UUID doctorId);

    // Verifica conflito de horário para o médico
    @Query("""
        SELECT COUNT(a) > 0 FROM Appointment a
        WHERE a.doctor.id = :doctorId
        AND a.status NOT IN ('CANCELLED')
        AND a.scheduledAt < :end
        AND FUNCTION('TIMESTAMPADD', MINUTE, a.durationMinutes, a.scheduledAt) > :start
    """)
    boolean existsConflict(
            @Param("doctorId") UUID doctorId,
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end
    );

    List<Appointment> findByDoctorIdAndScheduledAtBetween(
            UUID doctorId,
            LocalDateTime start,
            LocalDateTime end
    );
}
