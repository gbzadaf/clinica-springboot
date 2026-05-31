package com.gabrielf.clinica.services;

import com.gabrielf.clinica.dto.DoctorRequest;
import com.gabrielf.clinica.dto.DoctorResponse;
import com.gabrielf.clinica.model.Doctor;
import com.gabrielf.clinica.repository.DoctorRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class DoctorService {

    private final DoctorRepository doctorRepository;

    public DoctorService(DoctorRepository doctorRepository) {
        this.doctorRepository = doctorRepository;
    }

    public DoctorResponse create (DoctorRequest request) {
        if (doctorRepository.existsByCrm(request.crm())) {
            throw new RuntimeException("CRM já cadastrado");
        }

        Doctor doctor = new Doctor();
        doctor.setName(request.name());
        doctor.setCrm(request.crm());
        doctor.setSpecialty(request.specialty());
        doctor.setEmail(request.email());
        doctor.setPhone(request.phone());

        return DoctorResponse.from(doctorRepository.save(doctor));

    }

    public Page<DoctorResponse> findAll(Pageable pageable) {
         return doctorRepository.findAll(pageable)
                    .map(DoctorResponse::from);

    }

    public List<DoctorResponse> findBySpecialty(String specialty) {
        return  doctorRepository.findBySpecialtyIgnoreCaseAndActiveTrue(specialty)
                .stream()
                .map(DoctorResponse::from)
                .toList();
    }

    public DoctorResponse findById(UUID id) {
        return doctorRepository.findById(id).map(DoctorResponse::from)
                .orElseThrow(() -> new RuntimeException("Médico não encontrado"));

    }

    public DoctorResponse update(UUID id, DoctorRequest request) {
        Doctor doctor = doctorRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Médico não encontrado"));

        doctor.setName(request.name());
        doctor.setCrm(request.crm());
        doctor.setSpecialty(request.specialty());
        doctor.setEmail(request.email());
        doctor.setPhone(request.phone());

        return DoctorResponse.from(doctorRepository.save(doctor));

    }

    public void delete(UUID id) {
        Doctor doctor = doctorRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Médico não encontrado"));

        doctor.setActive(false);
        doctorRepository.save(doctor);

    }

}



