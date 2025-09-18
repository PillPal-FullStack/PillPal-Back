package com.project.pillpal.medicationintake.mapper;

import com.project.pillpal.medicationintake.dto.MedicationIntakeResponse;
import com.project.pillpal.medicationintake.entity.MedicationIntake;
import org.springframework.stereotype.Component;

@Component
public class MedicationIntakeMapperImpl implements MedicationIntakeMapper {
    public MedicationIntakeResponse toResponse(MedicationIntake intake) {
        if (intake == null) {
            return null;
        }

        return new MedicationIntakeResponse(
                intake.getId(),
                intake.getDateTime() != null ? intake.getDateTime().toString() : null,
                intake.getStatus() != null ? intake.getStatus().name() : null,
                intake.getMedication() != null ? intake.getMedication().getId() : null);
    }
}
