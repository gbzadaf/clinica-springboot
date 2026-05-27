package com.gabrielf.clinica.dto;

public record TokenResponse(
       String token,
       String email,
       String role

) {}
