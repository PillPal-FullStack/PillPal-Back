package com.project.pillpal.medicationintake.repository;

import com.project.pillpal.medicationintake.entity.MedicationIntake;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MedicationIntakeRepository extends JpaRepository<MedicationIntake, Long> {
}
