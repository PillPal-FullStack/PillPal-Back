package com.project.pillpal.reminder.dto;

import java.util.List;

public record ReminderListResponse(
        List<ReminderResponse> reminders,
        String message) {
    public static ReminderListResponse withReminders(List<ReminderResponse> reminders) {
        return new ReminderListResponse(reminders, null);
    }

    public static ReminderListResponse withMessage(String message) {
        return new ReminderListResponse(null, message);
    }
}
