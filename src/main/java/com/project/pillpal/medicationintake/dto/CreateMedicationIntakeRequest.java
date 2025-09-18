package com.project.pillpal.medicationintake.dto;

import jakarta.validation.constraints.NotNull;

public record CreateMedicationIntakeRequest(
        @NotNull Long medicationId,
        @NotNull String status) {
}
