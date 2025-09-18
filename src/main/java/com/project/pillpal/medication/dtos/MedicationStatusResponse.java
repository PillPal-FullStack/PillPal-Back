package com.project.pillpal.medication.dtos;

import com.project.pillpal.medicationintake.dto.MedicationIntakeResponse;

import java.time.LocalTime;
import java.util.List;

public record MedicationStatusResponse(
                Long medicationId,
                String medicationName,
                String dosage,
                LocalTime nextReminderTime,
                String status,
                List<MedicationIntakeResponse> todayIntakes,
                Boolean active) {
}
