package com.gabrielf.clinica.controller;

import com.gabrielf.clinica.dto.DoctorRequest;
import com.gabrielf.clinica.dto.DoctorResponse;
import com.gabrielf.clinica.services.DoctorService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/v1/doctors")
public class DoctorController {

    private final DoctorService doctorService;

    public DoctorController(DoctorService doctorService) {
        this.doctorService = doctorService;
    }

    @PostMapping
    public ResponseEntity<DoctorResponse> create(@RequestBody @Valid DoctorRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(doctorService.create(request));

    }

    @GetMapping
    public ResponseEntity<Page<DoctorResponse>> findAll(Pageable pageable) {
        return ResponseEntity.ok(doctorService.findAll(pageable));

    }

    @GetMapping("/specialty/{specialty}")
    public ResponseEntity<List<DoctorResponse>> findBySpecialty(@PathVariable String specialty) {
        return ResponseEntity.ok(doctorService.findBySpeciality(specialty));

    }

    @GetMapping("/{id}")
    public ResponseEntity<DoctorResponse> findById(@PathVariable UUID id) {
        return ResponseEntity.ok(doctorService.findById(id));

    }

    @PutMapping("/{id}")
    public ResponseEntity<DoctorResponse> update(@PathVariable UUID id, @RequestBody @Valid DoctorRequest request) {
        return ResponseEntity.ok(doctorService.update(id, request));

    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        doctorService.delete(id);
        return ResponseEntity.noContent().build();

    }










}
