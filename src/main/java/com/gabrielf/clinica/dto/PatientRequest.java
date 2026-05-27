package com.gabrielf.clinica.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

import java.time.LocalDate;

public record PatientRequest(
        @NotBlank
        String name,

        @NotBlank @Email
        String email,

        @NotBlank
        String cpf,

        String phone,
        LocalDate dateOfBirth

) {}
