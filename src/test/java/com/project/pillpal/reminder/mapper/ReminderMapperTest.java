package com.project.pillpal.reminder.mapper;

import com.project.pillpal.medication.entity.Medication;
import com.project.pillpal.reminder.dto.CreateReminderRequest;
import com.project.pillpal.reminder.dto.ReminderResponse;
import com.project.pillpal.reminder.dto.UpdateReminderRequest;
import com.project.pillpal.reminder.entity.Frequency;
import com.project.pillpal.reminder.entity.Reminder;
import com.project.pillpal.user.entity.Role;
import com.project.pillpal.user.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.*;

class ReminderMapperTest {

    private ReminderMapperImpl reminderMapper;
    private Reminder reminder;

    @BeforeEach
    void setUp() {
        reminderMapper = new ReminderMapperImpl();

        User user = new User();
        user.setId(1L);
        user.setUsername("testuser");
        user.setEmail("test@example.com");
        user.setPassword("password123");
        user.setRole(Role.ROLE_USER);

        Medication medication = new Medication();
        medication.setId(1L);
        medication.setName("Test Medication");
        medication.setDosage("100mg");
        medication.setActive(true);
        medication.setUser(user);

        reminder = new Reminder();
        reminder.setId(1L);
        reminder.setTime(LocalTime.of(9, 0));
        reminder.setFrequency(Frequency.DAILY);
        reminder.setEnabled(true);
        reminder.setMedication(medication);
    }

    @Test
    void testToResponse() {
        ReminderResponse response = reminderMapper.toResponse(reminder);

        assertNotNull(response);
        assertEquals(reminder.getId(), response.id());
        assertEquals(reminder.getTime(), response.time());
        assertEquals(reminder.getFrequency(), response.frequency());
        assertEquals(reminder.isEnabled(), response.enabled());
        assertEquals(reminder.getMedication().getId(), response.medicationId());
    }

    @Test
    void testToEntityFromCreateRequest() {
        CreateReminderRequest request = new CreateReminderRequest(
                LocalTime.of(10, 0), Frequency.WEEKLY, true, 1L);

        Reminder entity = reminderMapper.toEntity(request);

        assertNotNull(entity);
        assertEquals(request.time(), entity.getTime());
        assertEquals(request.frequency(), entity.getFrequency());
        assertEquals(request.enabled(), entity.isEnabled());
        assertNull(entity.getMedication());
    }

    @Test
    void testUpdateEntityFromUpdateRequest() {
        UpdateReminderRequest request = new UpdateReminderRequest(
                LocalTime.of(11, 0), Frequency.WEEKLY, false);

        reminderMapper.updateEntity(reminder, request);

        assertEquals(request.time(), reminder.getTime());
        assertEquals(request.frequency(), reminder.getFrequency());
        assertEquals(request.enabled(), reminder.isEnabled());
    }

    @Test
    void testUpdateEntityWithNullValues() {
        UpdateReminderRequest request = new UpdateReminderRequest(null, null, null);

        LocalTime originalTime = reminder.getTime();
        Frequency originalFrequency = reminder.getFrequency();
        boolean originalEnabled = reminder.isEnabled();

        reminderMapper.updateEntity(reminder, request);

        assertEquals(originalTime, reminder.getTime());
        assertEquals(originalFrequency, reminder.getFrequency());
        assertEquals(originalEnabled, reminder.isEnabled());
    }

    @Test
    void testToResponseWithDifferentFrequencies() {
        reminder.setFrequency(Frequency.WEEKLY);
        ReminderResponse response = reminderMapper.toResponse(reminder);
        assertEquals(Frequency.WEEKLY, response.frequency());

        reminder.setFrequency(Frequency.WEEKLY);
        response = reminderMapper.toResponse(reminder);
        assertEquals(Frequency.WEEKLY, response.frequency());
    }

    @Test
    void testToResponseWithDisabledReminder() {
        reminder.setEnabled(false);
        ReminderResponse response = reminderMapper.toResponse(reminder);
        assertFalse(response.enabled());
    }
}
