package com.project.pillpal.medicationintake.service;

import com.project.pillpal.medication.entity.Medication;
import com.project.pillpal.medication.repository.MedicationRepository;
import com.project.pillpal.medicationintake.dto.MedicationIntakeResponse;
import com.project.pillpal.medicationintake.entity.MedicationIntake;
import com.project.pillpal.medicationintake.entity.Status;
import com.project.pillpal.medicationintake.mapper.MedicationIntakeMapper;
import com.project.pillpal.medicationintake.repository.MedicationIntakeRepository;
import com.project.pillpal.user.entity.Role;
import com.project.pillpal.user.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MedicationIntakeServiceTest {

    @Mock
    private MedicationIntakeRepository medicationIntakeRepository;

    @Mock
    private MedicationIntakeMapper medicationIntakeMapper;

    @Mock
    private MedicationRepository medicationRepository;

    @InjectMocks
    private MedicationIntakeService medicationIntakeService;

    private Medication medication;
    private MedicationIntake intake;

    @BeforeEach
    void setUp() {
        User user = new User();
        user.setId(1L);
        user.setUsername("testuser");
        user.setRole(Role.ROLE_USER);

        medication = new Medication();
        medication.setId(1L);
        medication.setName("Test Medication");
        medication.setUser(user);

        intake = new MedicationIntake();
        intake.setId(1L);
        intake.setDateTime(LocalDateTime.now());
        intake.setStatus(Status.TAKEN);
        intake.setMedication(medication);

        MedicationIntakeResponse intakeResponse = new MedicationIntakeResponse(1L, LocalDateTime.now().toString(),
                "TAKEN", 1L);

    }

    @Test
    void testCreateIntakeSuccess() {
        when(medicationRepository.findById(1L)).thenReturn(Optional.of(medication));
        when(medicationIntakeRepository.save(any(MedicationIntake.class))).thenReturn(intake);

        MedicationIntake result = medicationIntakeService.createIntake(1L, Status.TAKEN);

        assertNotNull(result);
        assertEquals(Status.TAKEN, result.getStatus());
        assertEquals(medication, result.getMedication());
        verify(medicationRepository).findById(1L);
        verify(medicationIntakeRepository).save(any(MedicationIntake.class));
    }

    @Test
    void testCreateIntakeMedicationNotFound() {
        when(medicationRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(com.project.pillpal.exceptions.ResourceNotFoundException.class,
                () -> medicationIntakeService.createIntake(999L, Status.TAKEN));
        verify(medicationRepository).findById(999L);
        verify(medicationIntakeRepository, never()).save(any(MedicationIntake.class));
    }

    @Test
    void testGetAllIntakesForUser() {
        List<MedicationIntake> intakes = Collections.singletonList(intake);
        when(medicationIntakeRepository.findByMedicationUserIdOrderByDateTimeDesc(1L)).thenReturn(intakes);

        List<MedicationIntake> result = medicationIntakeService.getAllIntakesForUser(1L);

        assertEquals(1, result.size());
        assertEquals(intake, result.getFirst());
        verify(medicationIntakeRepository).findByMedicationUserIdOrderByDateTimeDesc(1L);
    }

    @Test
    void testGetAllIntakesForUserEmpty() {
        when(medicationIntakeRepository.findByMedicationUserIdOrderByDateTimeDesc(1L)).thenReturn(List.of());

        List<MedicationIntake> result = medicationIntakeService.getAllIntakesForUser(1L);

        assertTrue(result.isEmpty());
        verify(medicationIntakeRepository).findByMedicationUserIdOrderByDateTimeDesc(1L);
    }

}
