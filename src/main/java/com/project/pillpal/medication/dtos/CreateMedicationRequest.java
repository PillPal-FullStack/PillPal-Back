package com.project.pillpal.medication.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record CreateMedicationRequest(
        @NotBlank(message = "Name is required") @Size(max = 100, message = "Name must not exceed 100 characters") String name,

        @Size(max = 500, message = "Description must not exceed 500 characters") String description,

        String imgUrl,

        @NotBlank(message = "Dosage is required") @Size(max = 50, message = "Dosage must not exceed 50 characters") String dosage,

        @NotNull(message = "Active status is required") Boolean active,

        @NotNull(message = "Start date is required") LocalDate startDate,

        LocalDate endDate,

        @NotNull(message = "Lifetime status is required") Boolean lifetime) {
}
