package com.gabrielf.clinica.dto;

import com.gabrielf.clinica.model.enums.UserRole;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record RegisterRequest(
      @NotBlank
     String name,

     @NotBlank @Email
     String email,

     @NotBlank
     String password,

     @NotNull
     UserRole role

) {}
