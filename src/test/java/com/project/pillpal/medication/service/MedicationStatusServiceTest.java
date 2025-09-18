package com.project.pillpal.medication.service;

import com.project.pillpal.medication.dtos.MedicationStatusResponse;
import com.project.pillpal.medication.entity.Medication;
import com.project.pillpal.medication.repository.MedicationRepository;
import com.project.pillpal.medicationintake.entity.MedicationIntake;
import com.project.pillpal.medicationintake.entity.Status;
import com.project.pillpal.medicationintake.service.MedicationIntakeService;
import com.project.pillpal.exceptions.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MedicationStatusServiceTest {

    @Mock
    private MedicationRepository medicationRepository;

    @Mock
    private MedicationIntakeService medicationIntakeService;

    @InjectMocks
    private MedicationStatusService medicationStatusService;

    private Medication medication;
    private MedicationIntake takenIntake;
    private MedicationIntake skippedIntake;

    @BeforeEach
    void setUp() {
        medication = new Medication();
        medication.setId(1L);
        medication.setName("Test Medication");
        medication.setDosage("100mg");
        medication.setActive(true);

        takenIntake = new MedicationIntake();
        takenIntake.setId(1L);
        takenIntake.setDateTime(LocalDateTime.now());
        takenIntake.setStatus(Status.TAKEN);
        takenIntake.setMedication(medication);

        skippedIntake = new MedicationIntake();
        skippedIntake.setId(2L);
        skippedIntake.setDateTime(LocalDateTime.now());
        skippedIntake.setStatus(Status.SKIPPED);
        skippedIntake.setMedication(medication);
    }

    @Test
    void testGetMedicationsByTimeOfDayWithTakenStatus() {
        when(medicationRepository.findByActiveTrue()).thenReturn(Collections.singletonList(medication));
        when(medicationIntakeService.getIntakesByMedicationIdAndDateRange(anyLong(), any(LocalDateTime.class),
                any(LocalDateTime.class)))
                .thenReturn(Collections.singletonList(takenIntake));

        List<MedicationStatusResponse> result = medicationStatusService.getMedicationsByTimeOfDay();

        assertEquals(1, result.size());
        MedicationStatusResponse response = result.getFirst();
        assertEquals(1L, response.medicationId());
        assertEquals("Test Medication", response.medicationName());
        assertEquals("100mg", response.dosage());
        assertEquals("taken", response.status());
        assertTrue(response.active());
        assertEquals(1, response.todayIntakes().size());
    }

    @Test
    void testGetMedicationsByTimeOfDayWithSkippedStatus() {
        when(medicationRepository.findByActiveTrue()).thenReturn(Collections.singletonList(medication));
        when(medicationIntakeService.getIntakesByMedicationIdAndDateRange(anyLong(), any(LocalDateTime.class),
                any(LocalDateTime.class)))
                .thenReturn(Collections.singletonList(skippedIntake));

        List<MedicationStatusResponse> result = medicationStatusService.getMedicationsByTimeOfDay();

        assertEquals(1, result.size());
        MedicationStatusResponse response = result.getFirst();
        assertEquals("skipped", response.status());
    }

    @Test
    void testGetMedicationsByTimeOfDayWithPendingStatus() {
        when(medicationRepository.findByActiveTrue()).thenReturn(Collections.singletonList(medication));
        when(medicationIntakeService.getIntakesByMedicationIdAndDateRange(anyLong(), any(LocalDateTime.class),
                any(LocalDateTime.class)))
                .thenReturn(Collections.emptyList());

        List<MedicationStatusResponse> result = medicationStatusService.getMedicationsByTimeOfDay();

        assertEquals(1, result.size());
        MedicationStatusResponse response = result.getFirst();
        assertEquals("pending", response.status());
        assertTrue(response.todayIntakes().isEmpty());
    }

    @Test
    void testGetMedicationsByTimeOfDayWithMultipleMedications() {
        Medication medication2 = new Medication();
        medication2.setId(2L);
        medication2.setName("Test Medication 2");
        medication2.setDosage("200mg");
        medication2.setActive(true);

        when(medicationRepository.findByActiveTrue()).thenReturn(Arrays.asList(medication, medication2));
        when(medicationIntakeService.getIntakesByMedicationIdAndDateRange(anyLong(), any(LocalDateTime.class),
                any(LocalDateTime.class)))
                .thenReturn(Collections.singletonList(takenIntake))
                .thenReturn(Collections.emptyList());

        List<MedicationStatusResponse> result = medicationStatusService.getMedicationsByTimeOfDay();

        assertEquals(2, result.size());
        assertEquals("taken", result.get(0).status());
        assertEquals("pending", result.get(1).status());
    }

    @Test
    void testGetMedicationStatusByIdSuccess() {
        when(medicationRepository.findById(1L)).thenReturn(Optional.of(medication));
        when(medicationIntakeService.getIntakesByMedicationIdAndDateRange(anyLong(), any(LocalDateTime.class),
                any(LocalDateTime.class)))
                .thenReturn(Collections.singletonList(takenIntake));

        MedicationStatusResponse result = medicationStatusService.getMedicationStatusById(1L);

        assertEquals(1L, result.medicationId());
        assertEquals("Test Medication", result.medicationName());
        assertEquals("100mg", result.dosage());
        assertEquals("taken", result.status());
        assertTrue(result.active());
    }

    @Test
    void testGetMedicationStatusByIdNotFound() {
        when(medicationRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> medicationStatusService.getMedicationStatusById(999L));
    }

    @Test
    void testGetMedicationStatusByIdWithSkippedStatus() {
        when(medicationRepository.findById(1L)).thenReturn(Optional.of(medication));
        when(medicationIntakeService.getIntakesByMedicationIdAndDateRange(anyLong(), any(LocalDateTime.class),
                any(LocalDateTime.class)))
                .thenReturn(Collections.singletonList(skippedIntake));

        MedicationStatusResponse result = medicationStatusService.getMedicationStatusById(1L);

        assertEquals("skipped", result.status());
    }

    @Test
    void testGetMedicationStatusByIdWithPendingStatus() {
        when(medicationRepository.findById(1L)).thenReturn(Optional.of(medication));
        when(medicationIntakeService.getIntakesByMedicationIdAndDateRange(anyLong(), any(LocalDateTime.class),
                any(LocalDateTime.class)))
                .thenReturn(Collections.emptyList());

        MedicationStatusResponse result = medicationStatusService.getMedicationStatusById(1L);

        assertEquals("pending", result.status());
        assertTrue(result.todayIntakes().isEmpty());
    }

    @Test
    void testGetMedicationStatusByIdWithMixedStatuses() {
        MedicationIntake pendingIntake = new MedicationIntake();
        pendingIntake.setId(3L);
        pendingIntake.setDateTime(LocalDateTime.now());
        pendingIntake.setStatus(Status.PENDING);
        pendingIntake.setMedication(medication);

        when(medicationRepository.findById(1L)).thenReturn(Optional.of(medication));
        when(medicationIntakeService.getIntakesByMedicationIdAndDateRange(anyLong(), any(LocalDateTime.class),
                any(LocalDateTime.class)))
                .thenReturn(Arrays.asList(takenIntake, pendingIntake));

        MedicationStatusResponse result = medicationStatusService.getMedicationStatusById(1L);

        assertEquals("taken", result.status());
        assertEquals(2, result.todayIntakes().size());
    }

    @Test
    void testGetMedicationStatusByIdWithOnlySkippedAndPending() {
        MedicationIntake pendingIntake = new MedicationIntake();
        pendingIntake.setId(3L);
        pendingIntake.setDateTime(LocalDateTime.now());
        pendingIntake.setStatus(Status.PENDING);
        pendingIntake.setMedication(medication);

        when(medicationRepository.findById(1L)).thenReturn(Optional.of(medication));
        when(medicationIntakeService.getIntakesByMedicationIdAndDateRange(anyLong(), any(LocalDateTime.class),
                any(LocalDateTime.class)))
                .thenReturn(Arrays.asList(skippedIntake, pendingIntake));

        MedicationStatusResponse result = medicationStatusService.getMedicationStatusById(1L);

        assertEquals("skipped", result.status());
        assertEquals(2, result.todayIntakes().size());
    }

    @Test
    void testGetMedicationStatusByIdWithOnlyPending() {
        MedicationIntake pendingIntake = new MedicationIntake();
        pendingIntake.setId(3L);
        pendingIntake.setDateTime(LocalDateTime.now());
        pendingIntake.setStatus(Status.PENDING);
        pendingIntake.setMedication(medication);

        when(medicationRepository.findById(1L)).thenReturn(Optional.of(medication));
        when(medicationIntakeService.getIntakesByMedicationIdAndDateRange(anyLong(), any(LocalDateTime.class),
                any(LocalDateTime.class)))
                .thenReturn(List.of(pendingIntake));

        MedicationStatusResponse result = medicationStatusService.getMedicationStatusById(1L);

        assertEquals("pending", result.status());
        assertEquals(1, result.todayIntakes().size());
    }
}