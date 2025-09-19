package com.project.pillpal.reminder.mapper;

import com.project.pillpal.reminder.dto.CreateReminderRequest;
import com.project.pillpal.reminder.dto.ReminderResponse;
import com.project.pillpal.reminder.dto.UpdateReminderRequest;
import com.project.pillpal.reminder.entity.Reminder;
import org.springframework.stereotype.Component;

@Component
public class ReminderMapperImpl implements ReminderMapper{
    public ReminderResponse toResponse(Reminder reminder) {
        if (reminder == null) {
            return null;
        }

        return new ReminderResponse(
                reminder.getId(),
                reminder.getTime(),
                reminder.getFrequency(),
                reminder.isEnabled(),
                reminder.getMedication() != null ? reminder.getMedication().getId() : null
        );
    }

    public Reminder toEntity(CreateReminderRequest request) {
        if (request == null) {
            return null;
        }

        Reminder reminder = new Reminder();
        reminder.setTime(request.time());
        reminder.setFrequency(request.frequency());
        reminder.setEnabled(request.enabled());
        return reminder;
    }

    public void updateEntity(Reminder reminder, UpdateReminderRequest request) {
        if (reminder == null || request == null) {
            return;
        }

        if (request.time() != null) {
            reminder.setTime(request.time());
        }
        if (request.frequency() != null) {
            reminder.setFrequency(request.frequency());
        }
        if (request.enabled() != null) {
            reminder.setEnabled(request.enabled());
        }
    }
}
