package com.project.pillpal.medication.dtos;

import java.time.LocalDate;

public record MedicationResponse(
        Long id,
        String name,
        String description,
        String imgUrl,
        String dosage,
        Boolean active,
        LocalDate startDate,
        LocalDate endDate,
        Boolean lifetime,
        Long userId
) {
}
