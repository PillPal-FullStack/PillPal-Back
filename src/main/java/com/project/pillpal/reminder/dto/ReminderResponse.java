package com.project.pillpal.reminder.dto;

import com.project.pillpal.reminder.entity.Frequency;

import java.time.LocalTime;

public record ReminderResponse(
        Long id,
        LocalTime time,
        Frequency frequency,
        boolean enabled,
        Long medicationId
) {}
