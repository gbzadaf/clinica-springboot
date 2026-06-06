package com.gabrielf.clinica;

import com.gabrielf.clinica.dto.PatientRequest;
import com.gabrielf.clinica.dto.PatientResponse;
import com.gabrielf.clinica.exceptions.BusinessException;
import com.gabrielf.clinica.exceptions.ResourceNotFoundException;
import com.gabrielf.clinica.model.Patient;
import com.gabrielf.clinica.repository.PatientRepository;
import com.gabrielf.clinica.services.PatientService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PatientServiceTest {

    @Mock
    private PatientRepository patientRepository;

    @InjectMocks
    private PatientService patientService;

    private Patient patient;

    private PatientRequest request;

    @BeforeEach
    void setUp() {
        request = new PatientRequest(
                "Maria Souza",
                "maria@email.com",
                "12345678901",
                "21999999999",
                LocalDate.of(1990, 5, 15)
        );

        patient = new Patient();
        patient.setId(UUID.randomUUID());
        patient.setName("Maria Souza");
        patient.setEmail("maria@email.com");
        patient.setCpf("12345678901");
        patient.setPhone("21999999999");
        patient.setDateOfBirth(LocalDate.of(1990, 5, 15));
        patient.setActive(true);
    }

    @Test
    @DisplayName("Deve criar paciente com sucesso")
    void shouldCreatePatientSuccessfully() {
        // Arrange
        when(patientRepository.existsByEmail(request.email())).thenReturn(false);
        when(patientRepository.existsByCpf(request.cpf())).thenReturn(false);
        when(patientRepository.save(any(Patient.class))).thenReturn(patient);

        // Act
        PatientResponse response = patientService.create(request);

        // Assert
        assertNotNull(response);
        assertEquals("Maria Souza", response.name());
        assertEquals("maria@email.com", response.email());
        verify(patientRepository, times(1)).save(any(Patient.class));
    }

    @Test
    @DisplayName("Deve lançar exceção quando e-mail já cadastrado")
    void shouldThrowExceptionWhenEmailAlreadyExists() {
        // Arrange
        when(patientRepository.existsByEmail(request.email())).thenReturn(true);

        // Act & Assert
        BusinessException exception = assertThrows(
                BusinessException.class,
                () -> patientService.create(request)
        );

        assertEquals("Email já cadastrado", exception.getMessage());
        verify(patientRepository, never()).save(any(Patient.class));
    }

    @Test
    @DisplayName("Deve lançar exceção quando CPF já cadastrado")
    void shouldThrowExceptionWhenCpfAlreadyExists() {
        // Arrange
        when(patientRepository.existsByEmail(request.email())).thenReturn(false);
        when(patientRepository.existsByCpf(request.cpf())).thenReturn(true);

        // Act & Assert
        BusinessException exception = assertThrows(
                BusinessException.class,
                () -> patientService.create(request)
        );

        assertEquals("CPF já cadastrado", exception.getMessage());
        verify(patientRepository, never()).save(any(Patient.class));
    }

    @Test
    @DisplayName("Deve buscar paciente por ID com sucesso")
    void shouldFindPatientByIdSuccessfully() {
        // Arrange
        when(patientRepository.findById(patient.getId())).thenReturn(Optional.of(patient));

        // Act
        PatientResponse response = patientService.findById(patient.getId());

        // Assert
        assertNotNull(response);
        assertEquals(patient.getId(), response.id());
        assertEquals("Maria Souza", response.name());
    }

    @Test
    @DisplayName("Deve lançar exceção quando paciente não encontrado")
    void shouldThrowExceptionWhenPatientNotFound() {
        // Arrange
        UUID id = UUID.randomUUID();
        when(patientRepository.findById(id)).thenReturn(Optional.empty());

        // Act & Assert
        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> patientService.findById(id)
        );

        assertEquals("Paciente não encontrado", exception.getMessage());
    }

    @Test
    @DisplayName("Deve inativar paciente ao deletar")
    void shouldDeactivatePatientOnDelete() {
        // Arrange
        when(patientRepository.findById(patient.getId())).thenReturn(Optional.of(patient));
        when(patientRepository.save(any(Patient.class))).thenReturn(patient);

        // Act
        patientService.delete(patient.getId());

        // Assert
        assertFalse(patient.getActive());
        verify(patientRepository, times(1)).save(patient);
    }

}
