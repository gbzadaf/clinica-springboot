package com.gabrielf.clinica.controller;

import com.gabrielf.clinica.dto.AppointmentRequest;
import com.gabrielf.clinica.dto.AppointmentResponse;
import com.gabrielf.clinica.services.AppointmentService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/v1/appointments")
public class AppointmentController {

    private final AppointmentService appointmentService;

    public AppointmentController(AppointmentService appointmentService) {
        this.appointmentService = appointmentService;
    }

    @PostMapping
    public ResponseEntity<AppointmentResponse> create(@RequestBody @Valid AppointmentRequest request) {
        return  ResponseEntity.status(HttpStatus.CREATED).body(appointmentService.create(request));

    }

    @GetMapping
    public ResponseEntity<Page<AppointmentResponse>> findAll(Pageable pageable) {
        return ResponseEntity.ok(appointmentService.findAll(pageable));

    }

    @GetMapping("/{id}")
    public ResponseEntity<AppointmentResponse> findById(@PathVariable UUID id) {
        return ResponseEntity.ok(appointmentService.findById(id));

    }

    @GetMapping("/patient/{patientId}")
    public ResponseEntity<List<AppointmentResponse>> findByPatient(@PathVariable UUID patientId) {
        return ResponseEntity.ok(appointmentService.findByPatient(patientId));

    }

    @GetMapping("/doctor/{doctorId}")
    public ResponseEntity<List<AppointmentResponse>> findByDoctor(@PathVariable UUID doctorId) {
        return ResponseEntity.ok(appointmentService.findByDoctor(doctorId));

    }

    @PatchMapping("/{id}/cancel")
    public ResponseEntity<AppointmentResponse> cancel(@PathVariable UUID id) {
        return ResponseEntity.ok(appointmentService.cancel(id));

    }

    @PatchMapping("/{id}/confirm")
    public ResponseEntity<AppointmentResponse> confirm(@PathVariable UUID id) {
        return ResponseEntity.ok(appointmentService.confirm(id));

    }
}
