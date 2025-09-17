package com.project.pillpal.medicationintake.mapper;

import com.project.pillpal.medicationintake.dto.MedicationIntakeResponse;
import com.project.pillpal.medicationintake.entity.MedicationIntake;

public class MedicationIntakeMapperImpl implements MedicationIntakeMapper{
    public MedicationIntakeResponse toResponse(MedicationIntake intake) {
        if (intake == null) {
            return null;
        }

        return new MedicationIntakeResponse(
                intake.getId(),
                intake.getDateTime().toString(),
                intake.getStatus().name(),
                intake.getMedication() != null ? intake.getMedication().getId() : null
        );
    }
}
