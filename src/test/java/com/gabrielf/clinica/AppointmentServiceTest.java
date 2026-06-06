package com.gabrielf.clinica;

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
import com.gabrielf.clinica.services.AppointmentService;
import com.gabrielf.clinica.services.NotificationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AppointmentServiceTest {

    @Mock
    private AppointmentRepository appointmentRepository;

    @Mock
    private PatientRepository patientRepository;

    @Mock
    private DoctorRepository doctorRepository;

    @Mock
    private NotificationService notificationService;

    @InjectMocks
    private AppointmentService appointmentService;

    private Patient patient;
    private Doctor doctor;
    private Appointment appointment;
    private AppointmentRequest request;

    @BeforeEach
    void setUp() {
        patient = new Patient();
        patient.setId(UUID.randomUUID());
        patient.setName("Maria Souza");
        patient.setEmail("maria@email.com");
        patient.setActive(true);

        doctor = new Doctor();
        doctor.setId(UUID.randomUUID());
        doctor.setName("Dr. João Silva");
        doctor.setSpecialty("Cardiologia");
        doctor.setActive(true);

        request = new AppointmentRequest(
                patient.getId(),
                doctor.getId(),
                LocalDateTime.now().plusDays(2),
                30,
                "Consulta de rotina"
        );

        appointment = new Appointment();
        appointment.setId(UUID.randomUUID());
        appointment.setPatient(patient);
        appointment.setDoctor(doctor);
        appointment.setScheduledAt(request.scheduledAt());
        appointment.setDurationMinutes(30);
        appointment.setStatus(AppointmentStatus.SCHEDULED);
    }

    @Test
    @DisplayName("Deve criar agendamento com sucesso")
    void shouldCreateAppointmentSuccessfully() {
        // Arrange
        when(patientRepository.findById(request.patientId())).thenReturn(Optional.of(patient));
        when(doctorRepository.findById(request.doctorId())).thenReturn(Optional.of(doctor));
        when(appointmentRepository.existsConflict(any(), any(), any())).thenReturn(false);
        when(appointmentRepository.save(any(Appointment.class))).thenReturn(appointment);

        // Act
        AppointmentResponse response = appointmentService.create(request);

        // Assert
        assertNotNull(response);
        assertEquals(AppointmentStatus.SCHEDULED, response.status());
        assertEquals(patient.getId(), response.patientId());
        assertEquals(doctor.getId(), response.doctorId());
        verify(notificationService, times(1)).sendAppointmentConfirmation(any());
    }

    @Test
    @DisplayName("Deve lançar exceção quando paciente não encontrado")
    void shouldThrowExceptionWhenPatientNotFound() {
        // Arrange
        when(patientRepository.findById(request.patientId())).thenReturn(Optional.empty());

        // Act & Assert
        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> appointmentService.create(request)
        );

        assertEquals("Paciente não encontrado", exception.getMessage());
        verify(appointmentRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve lançar exceção quando médico não encontrado")
    void shouldThrowExceptionWhenDoctorNotFound() {
        // Arrange
        when(patientRepository.findById(request.patientId())).thenReturn(Optional.of(patient));
        when(doctorRepository.findById(request.doctorId())).thenReturn(Optional.empty());

        // Act & Assert
        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> appointmentService.create(request)
        );

        assertEquals("Médico não encontrado", exception.getMessage());
        verify(appointmentRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve lançar exceção quando há conflito de horário")
    void shouldThrowExceptionWhenScheduleConflict() {
        // Arrange
        when(patientRepository.findById(request.patientId())).thenReturn(Optional.of(patient));
        when(doctorRepository.findById(request.doctorId())).thenReturn(Optional.of(doctor));
        when(appointmentRepository.existsConflict(any(), any(), any())).thenReturn(true);

        // Act & Assert
        BusinessException exception = assertThrows(
                BusinessException.class,
                () -> appointmentService.create(request)
        );

        assertEquals("Médico já possui consulta neste horário", exception.getMessage());
        verify(appointmentRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve cancelar agendamento com sucesso")
    void shouldCancelAppointmentSuccessfully() {
        // Arrange
        appointment.setScheduledAt(LocalDateTime.now().plusDays(2));
        when(appointmentRepository.findById(appointment.getId())).thenReturn(Optional.of(appointment));
        when(appointmentRepository.save(any(Appointment.class))).thenReturn(appointment);

        // Act
        AppointmentResponse response = appointmentService.cancel(appointment.getId());

        // Assert
        assertEquals(AppointmentStatus.CANCELLED, response.status());
        verify(notificationService, times(1)).sendAppointmentCancellation(any());
    }

    @Test
    @DisplayName("Deve lançar exceção ao cancelar agendamento já cancelado")
    void shouldThrowExceptionWhenCancellingAlreadyCancelledAppointment() {
        // Arrange
        appointment.setStatus(AppointmentStatus.CANCELLED);
        when(appointmentRepository.findById(appointment.getId())).thenReturn(Optional.of(appointment));

        // Act & Assert
        BusinessException exception = assertThrows(
                BusinessException.class,
                () -> appointmentService.cancel(appointment.getId())
        );

        assertEquals("Agendamento já cancelado", exception.getMessage());
    }

    @Test
    @DisplayName("Deve lançar exceção ao cancelar com menos de 24h de antecedência")
    void shouldThrowExceptionWhenCancellingWithLessThan24Hours() {
        // Arrange
        appointment.setScheduledAt(LocalDateTime.now().plusHours(12));
        when(appointmentRepository.findById(appointment.getId())).thenReturn(Optional.of(appointment));

        // Act & Assert
        BusinessException exception = assertThrows(
                BusinessException.class,
                () -> appointmentService.cancel(appointment.getId())
        );

        assertEquals("Cancelamento deve ser feito com 24 horas de antecedência", exception.getMessage());
    }

    @Test
    @DisplayName("Deve confirmar agendamento com sucesso")
    void shouldConfirmAppointmentSuccessfully() {
        // Arrange
        when(appointmentRepository.findById(appointment.getId())).thenReturn(Optional.of(appointment));
        when(appointmentRepository.save(any(Appointment.class))).thenReturn(appointment);

        // Act
        AppointmentResponse response = appointmentService.confirm(appointment.getId());

        // Assert
        assertEquals(AppointmentStatus.CONFIRMED, response.status());
    }

    @Test
    @DisplayName("Deve lançar exceção ao confirmar agendamento não agendado")
    void shouldThrowExceptionWhenConfirmingNonScheduledAppointment() {
        // Arrange
        appointment.setStatus(AppointmentStatus.CANCELLED);
        when(appointmentRepository.findById(appointment.getId())).thenReturn(Optional.of(appointment));

        // Act & Assert
        BusinessException exception = assertThrows(
                BusinessException.class,
                () -> appointmentService.confirm(appointment.getId())
        );

        assertEquals("Apenas agendamentos com status SCHEDULED podem ser confirmados", exception.getMessage());
    }

}
