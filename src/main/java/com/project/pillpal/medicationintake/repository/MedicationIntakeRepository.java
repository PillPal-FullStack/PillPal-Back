package com.project.pillpal.medicationintake.repository;

import com.project.pillpal.medicationintake.entity.MedicationIntake;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface MedicationIntakeRepository extends JpaRepository<MedicationIntake, Long> {
    List<MedicationIntake> findByMedicationId(Long medicationId);

    List<MedicationIntake> findByMedicationIdAndDateTimeBetween(Long medicationId, LocalDateTime start,
            LocalDateTime end);

    List<MedicationIntake> findByMedicationUserIdOrderByDateTimeDesc(Long userId);
}
