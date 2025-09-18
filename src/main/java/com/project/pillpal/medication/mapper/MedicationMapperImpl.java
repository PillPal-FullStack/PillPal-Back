package com.project.pillpal.medication.mapper;

import com.project.pillpal.medication.dtos.CreateMedicationRequest;
import com.project.pillpal.medication.dtos.MedicationResponse;
import com.project.pillpal.medication.dtos.UpdateMedicationRequest;
import com.project.pillpal.medication.entity.Medication;
import org.springframework.stereotype.Component;

@Component
public class MedicationMapperImpl implements MedicationMapper {

    @Override
    public MedicationResponse toResponse(Medication medication) {
        if (medication == null) {
            return null;
        }
        return new MedicationResponse(
                medication.getId(),
                medication.getName(),
                medication.getDescription(),
                medication.getImgUrl(),
                medication.getDosage(),
                medication.getActive(),
                medication.getStartDate(),
                medication.getEndDate(),
                medication.getLifetime(),
                medication.getUser() != null ? medication.getUser().getId() : null);
    }

    @Override
    public Medication toEntity(CreateMedicationRequest request) {
        if (request == null) {
            return null;
        }

        Medication medication = new Medication();
        medication.setName(request.name());
        medication.setDescription(request.description());
        medication.setImgUrl(request.imgUrl());
        medication.setDosage(request.dosage() != null ? request.dosage() : "Not specified");
        medication.setActive(request.active() != null ? request.active() : true);
        medication.setStartDate(request.startDate());
        medication.setEndDate(request.endDate());
        medication.setLifetime(request.lifetime() != null ? request.lifetime() : false);
        return medication;
    }

    @Override
    public void updateEntity(Medication medication, UpdateMedicationRequest request) {
        if (medication == null || request == null) {
            return;
        }

        if (request.name() != null && !request.name().trim().isEmpty()) {
            medication.setName(request.name());
        }

        if (request.description() != null && !isPlaceholderValue(request.description())) {
            medication.setDescription(request.description().trim().isEmpty() ? null : request.description());
        }

        if (request.imgUrl() != null && !isPlaceholderValue(request.imgUrl())) {
            medication.setImgUrl(request.imgUrl().trim().isEmpty() ? null : request.imgUrl());
        }

        if (request.dosage() != null && !isPlaceholderValue(request.dosage())) {
            medication.setDosage(request.dosage().trim().isEmpty() ? null : request.dosage());
        }
        if (request.active() != null) {
            medication.setActive(request.active());
        }
        if (request.startDate() != null) {
            medication.setStartDate(request.startDate());
        }
        if (request.endDate() != null) {
            medication.setEndDate(request.endDate());
        }
        if (request.lifetime() != null) {
            medication.setLifetime(request.lifetime());
        }
    }

    private boolean isPlaceholderValue(String value) {
        if (value == null) {
            return false;
        }
        String trimmed = value.trim();
        return trimmed.isEmpty() ||
                "string".equals(trimmed) ||
                "null".equals(trimmed) ||
                "undefined".equals(trimmed);
    }
}
