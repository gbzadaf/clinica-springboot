package com.gabrielf.clinica.dto;

import com.gabrielf.clinica.model.Patient;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

public record PatientResponse(
        UUID id,
        String name,
        String email,
        String cpf,
        String phone,
        LocalDate dateOfBirth,
        LocalDateTime createdAt

) {
    public static PatientResponse from(Patient patient) {
        return new PatientResponse(
                patient.getId(),
                patient.getName(),
                patient.getEmail(),
                patient.getCpf(),
                patient.getPhone(),
                patient.getDateOfBirth(),
                patient.getCreatedAt()

        );
    }
}
