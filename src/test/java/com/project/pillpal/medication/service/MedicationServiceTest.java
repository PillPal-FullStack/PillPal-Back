package com.project.pillpal.medication.service;

import com.project.pillpal.medication.dtos.CreateMedicationRequest;
import com.project.pillpal.medication.dtos.MedicationResponse;
import com.project.pillpal.medication.dtos.UpdateMedicationRequest;
import com.project.pillpal.medication.entity.Medication;
import com.project.pillpal.medication.mapper.MedicationMapper;
import com.project.pillpal.medication.repository.MedicationRepository;
import com.project.pillpal.reminder.service.ReminderService;
import com.project.pillpal.user.entity.Role;
import com.project.pillpal.user.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MedicationServiceTest {

    @Mock
    private MedicationRepository medicationRepository;

    @Mock
    private MedicationMapper medicationMapper;

    @Mock
    private MedicationImageService medicationImageService;

    @Mock
    private MedicationStatusService medicationStatusService;

    @Mock
    private ReminderService reminderService;

    @InjectMocks
    private MedicationService medicationService;

    private User user;
    private Medication medication;
    private MedicationResponse medicationResponse;
    private CreateMedicationRequest createRequest;
    private UpdateMedicationRequest updateRequest;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setUsername("testuser");
        user.setRole(Role.ROLE_USER);

        medication = new Medication();
        medication.setId(1L);
        medication.setName("Test Medication");
        medication.setDescription("Test Description");
        medication.setDosage("100mg");
        medication.setActive(true);
        medication.setStartDate(LocalDate.now());
        medication.setEndDate(LocalDate.now().plusDays(30));
        medication.setLifetime(false);
        medication.setUser(user);

        medicationResponse = new MedicationResponse(1L, "Test Medication", "Test Description",
                "http://example.com/image.jpg", "100mg", true, LocalDate.now(),
                LocalDate.now().plusDays(30), false, 1L);

        createRequest = new CreateMedicationRequest("Test Medication", "Test Description",
                null, "100mg", true, LocalDate.now(), LocalDate.now().plusDays(30), false,
                false, null, null, null);

        updateRequest = new UpdateMedicationRequest("Updated Medication", "Updated Description",
                null, "200mg", true, LocalDate.now().plusDays(1), LocalDate.now().plusDays(60), true);
    }

    @Test
    void testGetAllMedications() {
        List<Medication> medications = Collections.singletonList(medication);
        when(medicationRepository.findAll()).thenReturn(medications);
        when(medicationMapper.toResponse(medication)).thenReturn(medicationResponse);

        List<MedicationResponse> result = medicationService.getAllMedications();

        assertEquals(1, result.size());
        assertEquals(medicationResponse, result.getFirst());
        verify(medicationRepository).findAll();
    }

    @Test
    void testGetMedicationsByUserId() {
        List<Medication> medications = Collections.singletonList(medication);
        when(medicationRepository.findByUserId(1L)).thenReturn(medications);
        when(medicationMapper.toResponse(medication)).thenReturn(medicationResponse);

        List<MedicationResponse> result = medicationService.getMedicationsByUserId(1L);

        assertEquals(1, result.size());
        assertEquals(medicationResponse, result.getFirst());
        verify(medicationRepository).findByUserId(1L);
    }

    @Test
    void testGetActiveMedications() {
        List<Medication> medications = Collections.singletonList(medication);
        when(medicationRepository.findByActiveTrue()).thenReturn(medications);
        when(medicationMapper.toResponse(medication)).thenReturn(medicationResponse);

        List<MedicationResponse> result = medicationService.getActiveMedications();

        assertEquals(1, result.size());
        assertEquals(medicationResponse, result.getFirst());
        verify(medicationRepository).findByActiveTrue();
    }

    @Test
    void testGetMedicationByIdSuccess() {
        when(medicationRepository.findById(1L)).thenReturn(Optional.of(medication));
        when(medicationMapper.toResponse(medication)).thenReturn(medicationResponse);

        MedicationResponse result = medicationService.getMedicationById(1L);

        assertEquals(medicationResponse, result);
        verify(medicationRepository).findById(1L);
    }

    @Test
    void testGetMedicationByIdNotFound() {
        when(medicationRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(com.project.pillpal.exceptions.ResourceNotFoundException.class,
                () -> medicationService.getMedicationById(999L));
        verify(medicationRepository).findById(999L);
    }

    @Test
    void testCreateMedicationSuccess() {
        when(medicationMapper.toEntity(createRequest)).thenReturn(medication);
        when(medicationRepository.save(any(Medication.class))).thenReturn(medication);
        when(medicationMapper.toResponse(medication)).thenReturn(medicationResponse);

        MedicationResponse result = medicationService.createMedication(createRequest, null, user);

        assertNotNull(result);
        verify(medicationRepository).save(any(Medication.class));
        verify(medicationMapper).toResponse(medication);
    }

    @Test
    void testUpdateMedicationSuccess() {
        when(medicationRepository.findById(1L)).thenReturn(Optional.of(medication));
        when(medicationRepository.save(any(Medication.class))).thenReturn(medication);
        when(medicationMapper.toResponse(medication)).thenReturn(medicationResponse);

        MedicationResponse result = medicationService.updateMedication(1L, updateRequest, null, user);

        assertNotNull(result);
        verify(medicationRepository, atLeastOnce()).findById(1L);
        verify(medicationRepository).save(any(Medication.class));
    }

    @Test
    void testUpdateMedicationNotFound() {
        when(medicationRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(com.project.pillpal.exceptions.ResourceNotFoundException.class,
                () -> medicationService.updateMedication(999L, updateRequest, null, user));
        verify(medicationRepository).findById(999L);
        verify(medicationRepository, never()).save(any(Medication.class));
    }

    @Test
    void testDeleteMedicationSuccess() {
        when(medicationRepository.findById(1L)).thenReturn(Optional.of(medication));

        medicationService.deleteMedication(1L, user);

        verify(medicationRepository).findById(1L);
        verify(medicationRepository).delete(medication);
    }

    @Test
    void testDeleteMedicationNotFound() {
        when(medicationRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(com.project.pillpal.exceptions.ResourceNotFoundException.class,
                () -> medicationService.deleteMedication(999L, user));
        verify(medicationRepository).findById(999L);
        verify(medicationRepository, never()).delete(any(Medication.class));
    }

    @Test
    void testGetMedicationsByUserAndActive() {
        List<Medication> medications = Collections.singletonList(medication);
        when(medicationRepository.findByUserIdAndActiveTrue(1L)).thenReturn(medications);
        when(medicationMapper.toResponse(medication)).thenReturn(medicationResponse);

        List<MedicationResponse> result = medicationService.getMedicationsByUserAndActive(1L, true);

        assertEquals(1, result.size());
        verify(medicationRepository).findByUserIdAndActiveTrue(1L);
    }

    @Test
    void testGetMedicationsByUserAndInactive() {
        List<Medication> medications = Collections.singletonList(medication);
        when(medicationRepository.findByUserId(1L)).thenReturn(medications);
        when(medicationMapper.toResponse(medication)).thenReturn(medicationResponse);

        List<MedicationResponse> result = medicationService.getMedicationsByUserAndActive(1L, false);

        assertEquals(1, result.size());
        verify(medicationRepository).findByUserId(1L);
    }

    @Test
    void testExistsById() {
        when(medicationRepository.existsById(1L)).thenReturn(true);

        boolean result = medicationService.existsById(1L);

        assertTrue(result);
        verify(medicationRepository).existsById(1L);
    }

    @Test
    void testCount() {
        when(medicationRepository.count()).thenReturn(5L);

        long result = medicationService.count();

        assertEquals(5L, result);
        verify(medicationRepository).count();
    }

    @Test
    void testCountByUserId() {
        List<Medication> medications = Arrays.asList(medication, medication);
        when(medicationRepository.findByUserId(1L)).thenReturn(medications);

        long result = medicationService.countByUserId(1L);

        assertEquals(2L, result);
        verify(medicationRepository).findByUserId(1L);
    }

    @Test
    void testSearchMedicationsByName() {
        List<Medication> medications = Collections.singletonList(medication);
        when(medicationRepository.findByNameContainingIgnoreCase("test")).thenReturn(medications);
        when(medicationMapper.toResponse(medication)).thenReturn(medicationResponse);

        List<MedicationResponse> result = medicationService.searchMedicationsByName("test");

        assertEquals(1, result.size());
        verify(medicationRepository).findByNameContainingIgnoreCase("test");
    }

    @Test
    void testSearchMedicationsByUserIdAndName() {
        List<Medication> medications = Collections.singletonList(medication);
        when(medicationRepository.findByUserIdAndNameContainingIgnoreCase(1L, "test")).thenReturn(medications);
        when(medicationMapper.toResponse(medication)).thenReturn(medicationResponse);

        List<MedicationResponse> result = medicationService.searchMedicationsByUserIdAndName(1L, "test");

        assertEquals(1, result.size());
        verify(medicationRepository).findByUserIdAndNameContainingIgnoreCase(1L, "test");
    }

    @Test
    void testGetMedicationsByTimeOfDay() {
        com.project.pillpal.medication.dtos.MedicationStatusResponse statusResponse = new com.project.pillpal.medication.dtos.MedicationStatusResponse(
                1L, "Test", "100mg",
                java.time.LocalTime.of(9, 0), "TAKEN", List.of(), true);
        List<com.project.pillpal.medication.dtos.MedicationStatusResponse> statusResponses = List.of(statusResponse);
        when(medicationStatusService.getMedicationsByTimeOfDay()).thenReturn(statusResponses);

        List<com.project.pillpal.medication.dtos.MedicationStatusResponse> result = medicationService
                .getMedicationsByTimeOfDay();

        assertEquals(1, result.size());
        verify(medicationStatusService).getMedicationsByTimeOfDay();
    }

    @Test
    void testGetMedicationStatusById() {
        com.project.pillpal.medication.dtos.MedicationStatusResponse statusResponse = new com.project.pillpal.medication.dtos.MedicationStatusResponse(
                1L, "Test", "100mg",
                java.time.LocalTime.of(9, 0), "TAKEN", List.of(), true);
        when(medicationStatusService.getMedicationStatusById(1L)).thenReturn(statusResponse);

        com.project.pillpal.medication.dtos.MedicationStatusResponse result = medicationService
                .getMedicationStatusById(1L);

        assertEquals(statusResponse, result);
        verify(medicationStatusService).getMedicationStatusById(1L);
    }

    @Test
    void testCreateMedicationWithLifetime() {
        CreateMedicationRequest lifetimeRequest = new CreateMedicationRequest("Lifetime Medication", "Description",
                null, "100mg", true, LocalDate.now(), null, true,
                false, null, null, null);

        when(medicationMapper.toEntity(lifetimeRequest)).thenReturn(medication);
        when(medicationRepository.save(any(Medication.class))).thenReturn(medication);
        when(medicationMapper.toResponse(medication)).thenReturn(medicationResponse);

        MedicationResponse result = medicationService.createMedication(lifetimeRequest, null, user);

        assertNotNull(result);
        verify(medicationRepository).save(any(Medication.class));
    }

    @Test
    void testCreateMedicationWithReminder() {
        CreateMedicationRequest reminderRequest = new CreateMedicationRequest("Medication with Reminder", "Description",
                null, "100mg", true, LocalDate.now(), LocalDate.now().plusDays(30), false,
                true, java.time.LocalTime.of(9, 0), com.project.pillpal.reminder.entity.Frequency.DAILY, true);

        when(medicationMapper.toEntity(reminderRequest)).thenReturn(medication);
        when(medicationRepository.save(any(Medication.class))).thenReturn(medication);
        when(medicationMapper.toResponse(medication)).thenReturn(medicationResponse);

        MedicationResponse result = medicationService.createMedication(reminderRequest, null, user);

        assertNotNull(result);
        verify(reminderService).createForMedication(any(Medication.class), any(), any(), any());
    }

    @Test
    void testUpdateMedicationWithUnauthorizedUser() {
        User otherUser = new User();
        otherUser.setId(2L);
        medication.setUser(otherUser);

        when(medicationRepository.findById(1L)).thenReturn(Optional.of(medication));

        assertThrows(com.project.pillpal.exceptions.UnauthorizedAccessException.class,
                () -> medicationService.updateMedication(1L, updateRequest, null, user));
    }

    @Test
    void testDeleteMedicationWithUnauthorizedUser() {
        User otherUser = new User();
        otherUser.setId(2L);
        medication.setUser(otherUser);

        when(medicationRepository.findById(1L)).thenReturn(Optional.of(medication));

        assertThrows(com.project.pillpal.exceptions.UnauthorizedAccessException.class,
                () -> medicationService.deleteMedication(1L, user));
    }
}
