package com.gabrielf.clinica.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;
import java.util.UUID;

public record AppointmentRequest(
        @NotNull
        UUID patientId,

        @NotNull
        UUID doctorId,

        @NotNull @Future
        LocalDateTime scheduleAt,

        Integer durationTime,
        String notes

) {}
