package com.project.pillpal.medicationintake.mapper;

import com.project.pillpal.medicationintake.dto.MedicationIntakeResponse;
import com.project.pillpal.medicationintake.entity.MedicationIntake;

public interface MedicationIntakeMapper {
    MedicationIntakeResponse toResponse(MedicationIntake intake);
}
