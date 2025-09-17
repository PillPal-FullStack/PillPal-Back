package com.project.pillpal.medication.mapper;

import com.project.pillpal.medication.dtos.CreateMedicationRequest;
import com.project.pillpal.medication.dtos.MedicationResponse;
import com.project.pillpal.medication.dtos.UpdateMedicationRequest;
import com.project.pillpal.medication.entity.Medication;

public interface MedicationMapper {
    MedicationResponse toResponse(Medication medication);
    Medication toEntity(CreateMedicationRequest request);
    void updateEntity(Medication medication, UpdateMedicationRequest request);
}
