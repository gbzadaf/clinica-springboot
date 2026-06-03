package com.gabrielf.clinica.services;

import com.gabrielf.clinica.dto.AppointmentRequest;
import com.gabrielf.clinica.dto.AppointmentResponse;
import com.gabrielf.clinica.exceptions.BusinessException;
import com.gabrielf.clinica.exceptions.ResourceNotFoundException;
import com.gabrielf.clinica.model.Appointment;
import com.gabrielf.clinica.model.Doctor;
import com.gabrielf.clinica.model.Patient;
import com.gabrielf.clinica.model.enums.AppointmentStatus;
import com.gabrielf.clinica.repository.AppointmentRepository;
import com.gabrielf.clinica.repository.DoctorRepository;
import com.gabrielf.clinica.repository.PatientRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class AppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final PatientRepository patientRepository;
    private final DoctorRepository doctorRepository;

    public AppointmentService(AppointmentRepository appointmentRepository, PatientRepository patientRepository,
                              DoctorRepository doctorRepository) {
        this.appointmentRepository = appointmentRepository;
        this.patientRepository = patientRepository;
        this.doctorRepository = doctorRepository;
    }

    public AppointmentResponse create(AppointmentRequest request) {
        Patient patient = patientRepository.findById(request.patientId())
                .orElseThrow(() -> new ResourceNotFoundException("Paciente não encontrado"));

        Doctor doctor = doctorRepository.findById(request.doctorId())
                .orElseThrow(() -> new ResourceNotFoundException("Médico não encontrado"));

        int duration = request.durationMinutes() != null ? request.durationMinutes() : 30;
        LocalDateTime start = request.scheduledAt();
        LocalDateTime end = start.plusMinutes(duration);

        boolean conflit = appointmentRepository.existsConflict(doctor.getId(), start, end);
        if (conflit) {
            throw  new BusinessException("Médico já possui consulta neste horário");

        }

        Appointment appointment = new Appointment();
        appointment.setPatient(patient);
        appointment.setDoctor(doctor);
        appointment.setScheduledAt(start);
        appointment.setDurationMinutes(duration);
        appointment.setNotes(request.notes());
        appointment.setStatus(AppointmentStatus.SCHEDULED);

        return  AppointmentResponse.from(appointmentRepository.save(appointment));

    }

    public Page<AppointmentResponse> findAll(Pageable pageable) {
        return appointmentRepository.findAll(pageable)
                .map(AppointmentResponse::from);

    }

    public AppointmentResponse findById(UUID id) {
        return appointmentRepository.findById(id)
                .map(AppointmentResponse::from)
                .orElseThrow(() -> new ResourceNotFoundException("Agendamento não encontrado"));

    }

    public List<AppointmentResponse> findByPatient(UUID patientId) {
        return appointmentRepository.findByPatientId(patientId).stream()
                .map(AppointmentResponse::from)
                .toList();

    }

    public List<AppointmentResponse> findByDoctor(UUID doctorId) {
        return appointmentRepository.findByDoctorId(doctorId).stream()
                .map(AppointmentResponse::from)
                .toList();

    }

    public AppointmentResponse cancel(UUID id) {
        Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Agendamento não encontrado"));

        if (appointment.getStatus() == AppointmentStatus.CANCELLED) {
            throw new BusinessException("Agendamento já cancelado");
        }
        if (appointment.getScheduledAt().isBefore(LocalDateTime.now().plusHours(24))) {
            throw new BusinessException("Cancelamento deve ser feito com 24 horas de antecedência");
        }

        appointment.setStatus(AppointmentStatus.CANCELLED);
        return AppointmentResponse.from(appointmentRepository.save(appointment));

    }

    public AppointmentResponse confirm(UUID id) {
        Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Agendamento não encontrado"));

        if (appointment.getStatus() != AppointmentStatus.SCHEDULED) {
            throw new BusinessException("Apenas agendamentos com SCHEDULED podem ser confirmados");
        }

        appointment.setStatus(AppointmentStatus.CONFIRMED);
        return AppointmentResponse.from(appointmentRepository.save(appointment));

    }

}
