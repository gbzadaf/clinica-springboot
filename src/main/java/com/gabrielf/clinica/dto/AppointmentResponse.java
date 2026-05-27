package com.gabrielf.clinica.dto;

import com.gabrielf.clinica.model.Appointment;
import com.gabrielf.clinica.model.enums.AppointmentStatus;

import java.time.LocalDateTime;
import java.util.UUID;

public record AppointmentResponse(
        UUID id,
        UUID patientId,
        String patientName,
        UUID doctorId,
        String doctorName,
        String doctorSpecialty,
        LocalDateTime scheduledAt,
        Integer durationMinutes,
        AppointmentStatus status,
        String notes,
        LocalDateTime createdAt

) {
    public static AppointmentResponse from(Appointment a) {
        return new AppointmentResponse(
                a.getId(),
                a.getPatient().getId(),
                a.getPatient().getName(),
                a.getDoctor().getId(),
                a.getDoctor().getName(),
                a.getDoctor().getSpecialty(),
                a.getScheduledAt(),
                a.getDurationMinutes(),
                a.getStatus(),
                a.getNotes(),
                a.getCreatedAt()

        );
    }
}
