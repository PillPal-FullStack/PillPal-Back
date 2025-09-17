package com.project.pillpal.medicationintake.dto;

public record MedicationIntakeResponse(
        Long id,
        String dateTime,
        String status,
        Long medicationId
) {}
