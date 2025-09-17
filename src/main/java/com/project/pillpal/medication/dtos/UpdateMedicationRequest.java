package com.project.pillpal.medication.dtos;

import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record UpdateMedicationRequest(
        @Size(max = 100, message = "Name must not exceed 100 characters")
        String name,

        @Size(max = 500, message = "Description must not exceed 500 characters")
        String description,

        String imgUrl,

        @Size(max = 50, message = "Dosage must not exceed 50 characters")
        String dosage,

        Boolean active,

        LocalDate startDate,

        LocalDate endDate,

        Boolean lifetime) {
}
