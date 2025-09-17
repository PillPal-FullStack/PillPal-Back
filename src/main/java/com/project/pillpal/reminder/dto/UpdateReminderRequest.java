package com.project.pillpal.reminder.dto;

import com.project.pillpal.reminder.entity.Frequency;

import java.time.LocalTime;

public record UpdateReminderRequest(
        LocalTime time,
        Frequency frequency,
        Boolean enabled
) {}
