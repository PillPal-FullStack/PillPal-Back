package com.project.pillpal.medicationintake.mapper;

import com.project.pillpal.medication.entity.Medication;
import com.project.pillpal.medicationintake.dto.MedicationIntakeResponse;
import com.project.pillpal.medicationintake.entity.MedicationIntake;
import com.project.pillpal.medicationintake.entity.Status;
import com.project.pillpal.user.entity.Role;
import com.project.pillpal.user.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class MedicationIntakeMapperTest {

    private MedicationIntakeMapper medicationIntakeMapper;
    private MedicationIntake intake;

    @BeforeEach
    void setUp() {
        medicationIntakeMapper = new MedicationIntakeMapperImpl();

        User user = new User();
        user.setId(1L);
        user.setUsername("testuser");
        user.setEmail("test@example.com");
        user.setPassword("password123");
        user.setRole(Role.ROLE_USER);

        Medication medication = new Medication();
        medication.setId(1L);
        medication.setName("Test Medication");
        medication.setDosage("100mg");
        medication.setActive(true);
        medication.setUser(user);

        intake = new MedicationIntake();
        intake.setId(1L);
        intake.setDateTime(LocalDateTime.now());
        intake.setStatus(Status.TAKEN);
        intake.setMedication(medication);
    }

    @Test
    void testToResponse() {
        MedicationIntakeResponse response = medicationIntakeMapper.toResponse(intake);

        assertNotNull(response);
        assertEquals(intake.getId(), response.id());
        assertEquals(intake.getDateTime().toString(), response.dateTime());
        assertEquals(intake.getStatus().name(), response.status());
        assertEquals(intake.getMedication().getId(), response.medicationId());
    }

    @Test
    void testToResponseWithDifferentStatuses() {
        intake.setStatus(Status.SKIPPED);
        MedicationIntakeResponse response = medicationIntakeMapper.toResponse(intake);
        assertEquals(Status.SKIPPED.name(), response.status());

        intake.setStatus(Status.PENDING);
        response = medicationIntakeMapper.toResponse(intake);
        assertEquals(Status.PENDING.name(), response.status());
    }

    @Test
    void testToResponseWithSpecificDateTime() {
        LocalDateTime specificTime = LocalDateTime.of(2024, 1, 15, 14, 30);
        intake.setDateTime(specificTime);

        MedicationIntakeResponse response = medicationIntakeMapper.toResponse(intake);
        assertEquals(specificTime.toString(), response.dateTime());
    }

    @Test
    void testToResponseWithNullIntake() {
        MedicationIntakeResponse response = medicationIntakeMapper.toResponse(null);
        assertNull(response);
    }

    @Test
    void testToResponseWithNullMedication() {
        intake.setMedication(null);
        MedicationIntakeResponse response = medicationIntakeMapper.toResponse(intake);

        assertNotNull(response);
        assertEquals(intake.getId(), response.id());
        assertEquals(intake.getDateTime().toString(), response.dateTime());
        assertEquals(intake.getStatus().name(), response.status());
        assertNull(response.medicationId());
    }

    @Test
    void testToResponseWithNullId() {
        intake.setId(null);
        MedicationIntakeResponse response = medicationIntakeMapper.toResponse(intake);

        assertNotNull(response);
        assertNull(response.id());
        assertEquals(intake.getDateTime().toString(), response.dateTime());
        assertEquals(intake.getStatus().name(), response.status());
        assertEquals(intake.getMedication().getId(), response.medicationId());
    }

    @Test
    void testToResponseWithNullDateTime() {
        intake.setDateTime(null);
        MedicationIntakeResponse response = medicationIntakeMapper.toResponse(intake);

        assertNotNull(response);
        assertEquals(intake.getId(), response.id());
        assertNull(response.dateTime());
        assertEquals(intake.getStatus().name(), response.status());
        assertEquals(intake.getMedication().getId(), response.medicationId());
    }

    @Test
    void testToResponseWithNullStatus() {
        intake.setStatus(null);
        MedicationIntakeResponse response = medicationIntakeMapper.toResponse(intake);

        assertNotNull(response);
        assertEquals(intake.getId(), response.id());
        assertEquals(intake.getDateTime().toString(), response.dateTime());
        assertNull(response.status());
        assertEquals(intake.getMedication().getId(), response.medicationId());
    }

    @Test
    void testToResponseWithAllStatusValues() {
        intake.setStatus(Status.TAKEN);
        MedicationIntakeResponse response = medicationIntakeMapper.toResponse(intake);
        assertEquals("TAKEN", response.status());

        intake.setStatus(Status.SKIPPED);
        response = medicationIntakeMapper.toResponse(intake);
        assertEquals("SKIPPED", response.status());

        intake.setStatus(Status.PENDING);
        response = medicationIntakeMapper.toResponse(intake);
        assertEquals("PENDING", response.status());
    }

    @Test
    void testToResponseWithDifferentMedicationIds() {
        intake.getMedication().setId(100L);
        MedicationIntakeResponse response = medicationIntakeMapper.toResponse(intake);
        assertEquals(100L, response.medicationId());

        intake.getMedication().setId(999L);
        response = medicationIntakeMapper.toResponse(intake);
        assertEquals(999L, response.medicationId());
    }

    @Test
    void testToResponseWithDifferentIntakeIds() {
        intake.setId(50L);
        MedicationIntakeResponse response = medicationIntakeMapper.toResponse(intake);
        assertEquals(50L, response.id());

        intake.setId(200L);
        response = medicationIntakeMapper.toResponse(intake);
        assertEquals(200L, response.id());
    }

    @Test
    void testToResponseWithEdgeCaseDateTime() {
        LocalDateTime edgeTime = LocalDateTime.of(1970, 1, 1, 0, 0, 0);
        intake.setDateTime(edgeTime);
        MedicationIntakeResponse response = medicationIntakeMapper.toResponse(intake);
        assertEquals(edgeTime.toString(), response.dateTime());

        LocalDateTime futureTime = LocalDateTime.of(2099, 12, 31, 23, 59, 59);
        intake.setDateTime(futureTime);
        response = medicationIntakeMapper.toResponse(intake);
        assertEquals(futureTime.toString(), response.dateTime());
    }

}
