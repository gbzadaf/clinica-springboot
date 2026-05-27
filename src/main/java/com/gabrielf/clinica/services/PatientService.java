package com.gabrielf.clinica.services;

import com.gabrielf.clinica.dto.PatientRequest;
import com.gabrielf.clinica.dto.PatientResponse;
import com.gabrielf.clinica.model.Patient;
import com.gabrielf.clinica.repository.PatientRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class PatientService {

    private final PatientRepository patientRepository;

    public PatientService(PatientRepository patientRepository) {
        this.patientRepository = patientRepository;
    }

    public PatientResponse create(PatientRequest request) {
        if (patientRepository.existsByEmail(request.email())) {
            throw new RuntimeException("Email já cadastrado");
        }
        if (patientRepository.existsByCpf(request.cpf())) {
            throw new RuntimeException("CPF já cadastrado");
        }

        Patient patient = new Patient();
        patient.setName(request.name());
        patient.setEmail(request.email());
        patient.setCpf(request.cpf());
        patient.setPhone(request.phone());
        patient.setDateOfBirth(request.dateOfBirth());

        return PatientResponse.from(patientRepository.save(patient));
    }

    public Page<PatientResponse> findAll(Pageable pageable) {
        return patientRepository.findAll(pageable)
                .map(PatientResponse::from);

    }

    public PatientResponse findById(UUID id) {
        return patientRepository.findById(id).map(PatientResponse::from)
                .orElseThrow(() -> new RuntimeException("Paciente não encontrado"));

    }

    public PatientResponse update(UUID id, PatientRequest request) {
        Patient patient = patientRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Paciente não encontrado"));

        patient.setName(request.name());
        patient.setEmail(request.email());
        patient.setCpf(request.cpf());
        patient.setPhone(request.phone());
        patient.setDateOfBirth(request.dateOfBirth());

        return PatientResponse.from(patientRepository.save(patient));

    }

    public void delete(UUID id) {
        Patient patient = patientRepository.findById(id).
                orElseThrow(() -> new RuntimeException("Paciente não encontrado"));

        patient.setActive(false);
        patientRepository.save(patient);
    }
}
