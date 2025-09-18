package com.project.pillpal.medication.service;

import com.project.pillpal.medication.dtos.MedicationStatusResponse;
import com.project.pillpal.medication.entity.Medication;
import com.project.pillpal.medication.repository.MedicationRepository;
import com.project.pillpal.medicationintake.dto.MedicationIntakeResponse;
import com.project.pillpal.medicationintake.entity.MedicationIntake;
import com.project.pillpal.medicationintake.entity.Status;
import com.project.pillpal.medicationintake.service.MedicationIntakeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class MedicationStatusService {

    private final MedicationRepository medicationRepository;
    private final MedicationIntakeService medicationIntakeService;

    public List<MedicationStatusResponse> getMedicationsByTimeOfDay() {
        log.info("Getting medications status by time of day");

        List<Medication> activeMedications = medicationRepository.findByActiveTrue();
        LocalDate today = LocalDate.now();
        LocalDateTime startOfDay = today.atStartOfDay();
        LocalDateTime endOfDay = today.atTime(LocalTime.MAX);

        return activeMedications.stream()
                .map(medication -> buildMedicationStatusResponse(medication, startOfDay, endOfDay))
                .collect(java.util.stream.Collectors.toList());
    }

    private MedicationStatusResponse buildMedicationStatusResponse(Medication medication,
            LocalDateTime startOfDay,
            LocalDateTime endOfDay) {
        log.debug("Building status response for medication {}", medication.getId());

        List<MedicationIntake> todayIntakes = medicationIntakeService
                .getIntakesByMedicationIdAndDateRange(medication.getId(), startOfDay, endOfDay);

        String status = determineStatus(todayIntakes);

        List<MedicationIntakeResponse> intakeResponses = todayIntakes.stream()
                .map(this::mapToIntakeResponse)
                .collect(Collectors.toList());

        return new MedicationStatusResponse(
                medication.getId(),
                medication.getName(),
                medication.getDosage(),
                null,
                status,
                intakeResponses,
                medication.getActive());
    }

    private MedicationIntakeResponse mapToIntakeResponse(MedicationIntake intake) {
        return new MedicationIntakeResponse(
                intake.getId(),
                intake.getDateTime().toString(),
                intake.getStatus().name(),
                intake.getMedication().getId());
    }

    private String determineStatus(List<MedicationIntake> todayIntakes) {
        if (todayIntakes.isEmpty()) {
            return "pending";
        }

        boolean hasTaken = todayIntakes.stream()
                .anyMatch(intake -> intake.getStatus() == Status.TAKEN);

        if (hasTaken) {
            return "taken";
        }

        boolean hasSkipped = todayIntakes.stream()
                .anyMatch(intake -> intake.getStatus() == Status.SKIPPED);

        if (hasSkipped) {
            return "skipped";
        }

        return "pending";
    }
}
