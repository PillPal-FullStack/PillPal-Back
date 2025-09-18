package com.project.pillpal.reminder.repository;

import com.project.pillpal.reminder.entity.Reminder;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReminderRepository extends JpaRepository<Reminder, Long> {
    List<Reminder> findByMedicationId(Long medicationId);
}
