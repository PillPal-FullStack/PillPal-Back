package com.project.pillpal.reminder.dto;

import com.project.pillpal.reminder.entity.Frequency;
import jakarta.validation.constraints.NotNull;

import java.time.LocalTime;

public record CreateReminderRequest(
        @NotNull(message = "Time is required")
        LocalTime time,

        @NotNull(message = "Frequency is required")
        Frequency frequency,

        boolean enabled,

        @NotNull(message = "Medication ID is required")
        Long medicationId
) {}
