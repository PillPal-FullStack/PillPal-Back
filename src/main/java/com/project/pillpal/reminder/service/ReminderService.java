package com.project.pillpal.reminder.service;

import com.project.pillpal.reminder.dto.CreateReminderRequest;
import com.project.pillpal.reminder.dto.ReminderResponse;
import com.project.pillpal.reminder.dto.UpdateReminderRequest;
import com.project.pillpal.reminder.entity.Frequency;
import com.project.pillpal.reminder.entity.Reminder;
import com.project.pillpal.reminder.mapper.ReminderMapperImpl;
import com.project.pillpal.reminder.repository.ReminderRepository;
import com.project.pillpal.medication.entity.Medication;
import com.project.pillpal.medication.repository.MedicationRepository;
import com.project.pillpal.exceptions.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReminderService {
    private final ReminderRepository reminderRepository;
    private final ReminderMapperImpl reminderMapperImpl;
    private final MedicationRepository medicationRepository;

    public List<ReminderResponse> getAllReminders() {
        List<Reminder> reminders = reminderRepository.findAll();
        return reminders.stream()
                .map(reminder -> reminderMapperImpl.toResponse(reminder))
                .toList();
    }

    public Reminder createForMedication(Medication medication,
            LocalTime time,
            Frequency frequency,
            Boolean enabled) {
        if (medication == null) {
            throw new IllegalArgumentException("Medication must not be null");
        }
        if (time == null) {
            throw new IllegalArgumentException("Reminder time must not be null");
        }
        if (frequency == null) {
            throw new IllegalArgumentException("Reminder frequency must not be null");
        }

        Reminder reminder = new Reminder();
        reminder.setMedication(medication);
        reminder.setTime(time);
        reminder.setFrequency(frequency);
        reminder.setEnabled(enabled != null ? enabled : true);

        return reminderRepository.save(reminder);
    }

    public ReminderResponse createReminder(CreateReminderRequest request) {
        log.info("Creating new reminder for medication id: {}", request.medicationId());

        Medication medication = medicationRepository.findById(request.medicationId())
                .orElseThrow(
                        () -> new ResourceNotFoundException("Medication not found with id: " + request.medicationId()));

        Reminder reminder = new Reminder();
        reminder.setMedication(medication);
        reminder.setTime(request.time());
        reminder.setFrequency(request.frequency());
        reminder.setEnabled(request.enabled());

        Reminder savedReminder = reminderRepository.save(reminder);
        log.info("Successfully created reminder with id: {} for medication: {}", savedReminder.getId(),
                request.medicationId());

        return reminderMapperImpl.toResponse(savedReminder);
    }

    public ReminderResponse getReminderById(Long id) {
        log.info("Getting reminder by id: {}", id);
        return reminderRepository.findById(id)
                .map(reminderMapperImpl::toResponse)
                .orElseThrow(() -> new ResourceNotFoundException("Reminder not found with id: " + id));
    }

    public List<ReminderResponse> getRemindersByMedicationId(Long medicationId) {
        log.info("Getting reminders for medication id: {}", medicationId);
        List<Reminder> reminders = reminderRepository.findByMedicationId(medicationId);
        return reminders.stream()
                .map(reminderMapperImpl::toResponse)
                .toList();
    }

    public ReminderResponse updateReminder(Long id, UpdateReminderRequest request) {
        log.info("Updating reminder with id: {}", id);

        Reminder reminder = reminderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Reminder not found with id: " + id));

        if (request.time() != null) {
            reminder.setTime(request.time());
        }
        if (request.frequency() != null) {
            reminder.setFrequency(request.frequency());
        }
        if (request.enabled() != null) {
            reminder.setEnabled(request.enabled());
        }

        Reminder savedReminder = reminderRepository.save(reminder);
        log.info("Successfully updated reminder with id: {}", id);

        return reminderMapperImpl.toResponse(savedReminder);
    }

    public void deleteReminder(Long id) {
        log.info("Deleting reminder with id: {}", id);

        if (!reminderRepository.existsById(id)) {
            throw new ResourceNotFoundException("Reminder not found with id: " + id);
        }

        reminderRepository.deleteById(id);
        log.info("Successfully deleted reminder with id: {}", id);
    }

    public ReminderResponse toggleReminderEnabled(Long id) {
        log.info("Toggling reminder enabled status for id: {}", id);

        Reminder reminder = reminderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Reminder not found with id: " + id));

        reminder.setEnabled(!reminder.isEnabled());
        Reminder savedReminder = reminderRepository.save(reminder);

        log.info("Successfully toggled reminder enabled status to {} for id: {}", savedReminder.isEnabled(), id);
        return reminderMapperImpl.toResponse(savedReminder);
    }

}
