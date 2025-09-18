package com.project.pillpal.medicationintake.service;

import com.project.pillpal.medication.entity.Medication;
import com.project.pillpal.medication.repository.MedicationRepository;
import com.project.pillpal.medicationintake.entity.MedicationIntake;
import com.project.pillpal.medicationintake.entity.Status;
import com.project.pillpal.medicationintake.repository.MedicationIntakeRepository;
import com.project.pillpal.exceptions.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class MedicationIntakeService {

    private final MedicationIntakeRepository medicationIntakeRepository;
    private final MedicationRepository medicationRepository;

    public MedicationIntake markAsTaken(Long medicationId) {
        log.info("Marking medication {} as taken", medicationId);

        Medication medication = medicationRepository.findById(medicationId)
                .orElseThrow(() -> new ResourceNotFoundException("Medication not found with id: " + medicationId));

        MedicationIntake intake = new MedicationIntake();
        intake.setDateTime(LocalDateTime.now());
        intake.setStatus(Status.TAKEN);
        intake.setMedication(medication);

        MedicationIntake savedIntake = medicationIntakeRepository.save(intake);
        log.info("Successfully marked medication {} as taken", medicationId);

        return savedIntake;
    }

    public MedicationIntake markAsSkipped(Long medicationId) {
        log.info("Marking medication {} as skipped", medicationId);

        Medication medication = medicationRepository.findById(medicationId)
                .orElseThrow(() -> new ResourceNotFoundException("Medication not found with id: " + medicationId));

        MedicationIntake intake = new MedicationIntake();
        intake.setDateTime(LocalDateTime.now());
        intake.setStatus(Status.SKIPPED);
        intake.setMedication(medication);

        MedicationIntake savedIntake = medicationIntakeRepository.save(intake);
        log.info("Successfully marked medication {} as skipped", medicationId);

        return savedIntake;
    }

    public List<MedicationIntake> getIntakesByMedicationId(Long medicationId) {
        log.debug("Getting intakes for medication {}", medicationId);
        return medicationIntakeRepository.findByMedicationId(medicationId);
    }

    public List<MedicationIntake> getIntakesByMedicationIdAndDateRange(Long medicationId, LocalDateTime startDate,
            LocalDateTime endDate) {
        log.debug("Getting intakes for medication {} between {} and {}", medicationId, startDate, endDate);
        return medicationIntakeRepository.findByMedicationIdAndDateTimeBetween(medicationId, startDate, endDate);
    }
}