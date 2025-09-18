package com.project.pillpal.reminder.service;

import com.project.pillpal.medication.entity.Medication;
import com.project.pillpal.medication.repository.MedicationRepository;
import com.project.pillpal.reminder.dto.CreateReminderRequest;
import com.project.pillpal.reminder.dto.ReminderResponse;
import com.project.pillpal.reminder.dto.UpdateReminderRequest;
import com.project.pillpal.reminder.entity.Frequency;
import com.project.pillpal.reminder.entity.Reminder;
import com.project.pillpal.reminder.mapper.ReminderMapperImpl;
import com.project.pillpal.reminder.repository.ReminderRepository;
import com.project.pillpal.user.entity.Role;
import com.project.pillpal.user.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
class ReminderServiceTest {

    @Mock
    private ReminderRepository reminderRepository;

    @Mock
    private ReminderMapperImpl reminderMapperImpl;

    @Mock
    private MedicationRepository medicationRepository;

    @InjectMocks
    private ReminderService reminderService;

    private User user;
    private Medication medication;
    private Reminder reminder;
    private ReminderResponse reminderResponse;
    private CreateReminderRequest createRequest;
    private UpdateReminderRequest updateRequest;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setUsername("testuser");
        user.setRole(Role.ROLE_USER);

        medication = new Medication();
        medication.setId(1L);
        medication.setName("Test Medication");
        medication.setUser(user);

        reminder = new Reminder();
        reminder.setId(1L);
        reminder.setTime(LocalTime.of(9, 0));
        reminder.setFrequency(Frequency.DAILY);
        reminder.setEnabled(true);
        reminder.setMedication(medication);

        reminderResponse = new ReminderResponse(1L, LocalTime.of(9, 0), Frequency.DAILY, true, 1L);

        createRequest = new CreateReminderRequest(LocalTime.of(9, 0), Frequency.DAILY, true, 1L);
        updateRequest = new UpdateReminderRequest(LocalTime.of(10, 0), Frequency.WEEKLY, false);
    }

    @Test
    void testGetAllReminders() {
        List<Reminder> reminders = Arrays.asList(reminder);
        when(reminderRepository.findAll()).thenReturn(reminders);
        when(reminderMapperImpl.toResponse(reminder)).thenReturn(reminderResponse);

        List<ReminderResponse> result = reminderService.getAllReminders();

        assertEquals(1, result.size());
        assertEquals(reminderResponse, result.get(0));
        verify(reminderRepository).findAll();
    }

    @Test
    void testGetReminderByIdSuccess() {
        when(reminderRepository.findById(1L)).thenReturn(Optional.of(reminder));
        when(reminderMapperImpl.toResponse(reminder)).thenReturn(reminderResponse);

        ReminderResponse result = reminderService.getReminderById(1L);

        assertEquals(reminderResponse, result);
        verify(reminderRepository).findById(1L);
    }

    @Test
    void testGetReminderByIdNotFound() {
        when(reminderRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(com.project.pillpal.exceptions.ResourceNotFoundException.class,
                () -> reminderService.getReminderById(999L));
        verify(reminderRepository).findById(999L);
    }

    @Test
    void testCreateForMedicationSuccess() {
        when(reminderRepository.save(any(Reminder.class))).thenReturn(reminder);

        Reminder result = reminderService.createForMedication(medication, LocalTime.of(9, 0), Frequency.DAILY, true);

        assertNotNull(result);
        assertEquals(medication, result.getMedication());
        assertEquals(LocalTime.of(9, 0), result.getTime());
        assertEquals(Frequency.DAILY, result.getFrequency());
        assertTrue(result.isEnabled());
        verify(reminderRepository).save(any(Reminder.class));
    }

    @Test
    void testCreateForMedicationWithNullMedication() {
        assertThrows(IllegalArgumentException.class,
                () -> reminderService.createForMedication(null, LocalTime.of(9, 0), Frequency.DAILY, true));
        verify(reminderRepository, never()).save(any(Reminder.class));
    }

    @Test
    void testCreateForMedicationWithNullTime() {
        assertThrows(IllegalArgumentException.class,
                () -> reminderService.createForMedication(medication, null, Frequency.DAILY, true));
        verify(reminderRepository, never()).save(any(Reminder.class));
    }

    @Test
    void testCreateForMedicationWithNullFrequency() {
        assertThrows(IllegalArgumentException.class,
                () -> reminderService.createForMedication(medication, LocalTime.of(9, 0), null, true));
        verify(reminderRepository, never()).save(any(Reminder.class));
    }

    @Test
    void testCreateReminderSuccess() {
        when(medicationRepository.findById(1L)).thenReturn(Optional.of(medication));
        when(reminderRepository.save(any(Reminder.class))).thenReturn(reminder);
        when(reminderMapperImpl.toResponse(reminder)).thenReturn(reminderResponse);

        ReminderResponse result = reminderService.createReminder(createRequest);

        assertNotNull(result);
        verify(medicationRepository).findById(1L);
        verify(reminderRepository).save(any(Reminder.class));
    }

    @Test
    void testCreateReminderMedicationNotFound() {
        CreateReminderRequest requestWithNonExistentId = new CreateReminderRequest(
                LocalTime.of(9, 0), Frequency.DAILY, true, 999L);

        when(medicationRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(com.project.pillpal.exceptions.ResourceNotFoundException.class,
                () -> reminderService.createReminder(requestWithNonExistentId));
        verify(medicationRepository).findById(999L);
        verify(reminderRepository, never()).save(any(Reminder.class));
    }

    @Test
    void testUpdateReminderSuccess() {
        when(reminderRepository.findById(1L)).thenReturn(Optional.of(reminder));
        when(reminderRepository.save(any(Reminder.class))).thenReturn(reminder);
        when(reminderMapperImpl.toResponse(reminder)).thenReturn(reminderResponse);

        ReminderResponse result = reminderService.updateReminder(1L, updateRequest);

        assertNotNull(result);
        verify(reminderRepository).findById(1L);
        verify(reminderRepository).save(any(Reminder.class));
    }

    @Test
    void testUpdateReminderNotFound() {
        when(reminderRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(com.project.pillpal.exceptions.ResourceNotFoundException.class,
                () -> reminderService.updateReminder(999L, updateRequest));
        verify(reminderRepository).findById(999L);
        verify(reminderRepository, never()).save(any(Reminder.class));
    }

    @Test
    void testDeleteReminderSuccess() {
        when(reminderRepository.existsById(1L)).thenReturn(true);

        reminderService.deleteReminder(1L);

        verify(reminderRepository).existsById(1L);
        verify(reminderRepository).deleteById(1L);
    }

    @Test
    void testDeleteReminderNotFound() {
        when(reminderRepository.existsById(999L)).thenReturn(false);

        assertThrows(com.project.pillpal.exceptions.ResourceNotFoundException.class,
                () -> reminderService.deleteReminder(999L));
        verify(reminderRepository).existsById(999L);
        verify(reminderRepository, never()).deleteById(any(Long.class));
    }
}
