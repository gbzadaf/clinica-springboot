package com.gabrielf.clinica.dto;

import com.gabrielf.clinica.model.Doctor;

import java.time.LocalDateTime;
import java.util.UUID;

public record DoctorResponse(
        UUID id,
        String name,
        String crm,
        String specialty,
        String email,
        String phone,
        LocalDateTime createdAt

) {
    public static DoctorResponse from(Doctor doctor) {
        return new DoctorResponse(
                doctor.getId(),
                doctor.getName(),
                doctor.getCrm(),
                doctor.getSpecialty(),
                doctor.getEmail(),
                doctor.getPhone(),
                doctor.getCreatedAt()
        );
    }
}
