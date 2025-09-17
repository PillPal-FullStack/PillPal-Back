package com.project.pillpal.reminder.mapper;

import com.project.pillpal.reminder.dto.CreateReminderRequest;
import com.project.pillpal.reminder.dto.ReminderResponse;
import com.project.pillpal.reminder.dto.UpdateReminderRequest;
import com.project.pillpal.reminder.entity.Reminder;

public interface ReminderMapper {
    ReminderResponse toResponse(Reminder reminder);
    Reminder toEntity(CreateReminderRequest request);
    void updateEntity(Reminder reminder, UpdateReminderRequest request);
}
