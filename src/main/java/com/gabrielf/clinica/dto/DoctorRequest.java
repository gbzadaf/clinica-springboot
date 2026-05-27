package com.gabrielf.clinica.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record DoctorRequest(
        @NotBlank
        String name,

        @NotBlank
        String crm,

        @NotBlank
        String speciality,

        @NotBlank @Email
        String email,

        String phone

) {}
