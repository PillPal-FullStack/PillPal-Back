package com.project.pillpal.medication.repository;

import com.project.pillpal.medication.entity.Medication;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface MedicationRepository extends JpaRepository<Medication, Long> {
    List<Medication> findByUserIdAndActiveTrue(Long userId);

    List<Medication> findByUserId(Long userId);

    List<Medication> findByActiveTrue();

    @Query("SELECT m FROM Medication m WHERE LOWER(m.name) LIKE LOWER(CONCAT('%', :name, '%'))")
    List<Medication> findByNameContainingIgnoreCase(@Param("name") String name);

    @Query("SELECT m FROM Medication m WHERE m.user.id = :userId AND LOWER(m.name) LIKE LOWER(CONCAT('%', :name, '%'))")
    List<Medication> findByUserIdAndNameContainingIgnoreCase(@Param("userId") Long userId, @Param("name") String name);
}
