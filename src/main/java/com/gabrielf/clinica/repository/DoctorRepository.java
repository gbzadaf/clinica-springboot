package com.gabrielf.clinica.repository;

import com.gabrielf.clinica.model.Doctor;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface DoctorRepository extends JpaRepository<Doctor, UUID> {

    Optional<Doctor> findByEmail(String email);
    boolean existsByCrm(String crm);
    List<Doctor> findBySpecialtyIgnoreCaseAndActiveTrue(String specialty);
    List<Doctor> findByActiveTrue();
}
