package com.project.pillpal.medicationintake.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.pillpal.medication.entity.Medication;
import com.project.pillpal.medication.repository.MedicationRepository;
import com.project.pillpal.medicationintake.dto.CreateMedicationIntakeRequest;
import com.project.pillpal.medicationintake.entity.MedicationIntake;
import com.project.pillpal.medicationintake.entity.Status;
import com.project.pillpal.medicationintake.repository.MedicationIntakeRepository;
import com.project.pillpal.user.entity.Role;
import com.project.pillpal.user.entity.User;
import com.project.pillpal.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureWebMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureWebMvc
@ActiveProfiles("test")
@Transactional
class MedicationIntakeControllerTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private MedicationIntakeRepository medicationIntakeRepository;

    @Autowired
    private MedicationRepository medicationRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private MockMvc mockMvc;
    private Medication medication;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();

        User user = new User();
        user.setUsername("testuser");
        user.setEmail("test@example.com");
        user.setPassword("password123");
        user.setRole(Role.ROLE_USER);
        user = userRepository.save(user);

        medication = new Medication();
        medication.setName("Test Medication");
        medication.setDosage("100mg");
        medication.setActive(true);
        medication.setStartDate(LocalDate.now());
        medication.setEndDate(LocalDate.now().plusDays(30));
        medication.setLifetime(false);
        medication.setUser(user);
        medication = medicationRepository.save(medication);

        MedicationIntake intake = new MedicationIntake();
        intake.setDateTime(LocalDateTime.now());
        intake.setStatus(Status.TAKEN);
        intake.setMedication(medication);
        intake = medicationIntakeRepository.save(intake);
    }

    @Test
    void testCreateIntake() throws Exception {
        CreateMedicationIntakeRequest request = new CreateMedicationIntakeRequest(
                medication.getId(), "SKIPPED");

        mockMvc.perform(post("/api/intakes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value("SKIPPED"));
    }

    @Test
    void testGetAllIntakesForCurrentUser() throws Exception {
        mockMvc.perform(get("/api/intakes/me"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    void testGetIntakesByMedicationId() throws Exception {
        mockMvc.perform(get("/api/intakes/medication/{medicationId}", medication.getId()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    void testMarkAsTaken() throws Exception {
        mockMvc.perform(post("/api/intakes/{medicationId}/taken", medication.getId()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value("TAKEN"));
    }

    @Test
    void testMarkAsSkipped() throws Exception {
        mockMvc.perform(post("/api/intakes/{medicationId}/skipped", medication.getId()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value("SKIPPED"));
    }

    @Test
    void testCreateIntakeWithPENDINGStatus() throws Exception {
        CreateMedicationIntakeRequest request = new CreateMedicationIntakeRequest(
                medication.getId(), "PENDING");

        mockMvc.perform(post("/api/intakes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value("PENDING"));
    }

    @Test
    void testCreateIntakeWithTAKENStatus() throws Exception {
        CreateMedicationIntakeRequest request = new CreateMedicationIntakeRequest(
                medication.getId(), "TAKEN");

        mockMvc.perform(post("/api/intakes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value("TAKEN"));
    }

    @Test
    void testCreateIntakeWithInvalidStatus() throws Exception {
        CreateMedicationIntakeRequest request = new CreateMedicationIntakeRequest(
                medication.getId(), "INVALID_STATUS");

        mockMvc.perform(post("/api/intakes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testCreateIntakeWithNonExistentMedication() throws Exception {
        CreateMedicationIntakeRequest request = new CreateMedicationIntakeRequest(
                999L, "TAKEN");

        mockMvc.perform(post("/api/intakes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());
    }

    @Test
    void testGetIntakesByMedicationIdNotFound() throws Exception {
        mockMvc.perform(get("/api/intakes/medication/{medicationId}", 999L))
                .andExpect(status().isNotFound());
    }

    @Test
    void testMarkAsTakenWithNonExistentMedication() throws Exception {
        mockMvc.perform(post("/api/intakes/{medicationId}/taken", 999L))
                .andExpect(status().isNotFound());
    }

    @Test
    void testMarkAsSkippedWithNonExistentMedication() throws Exception {
        mockMvc.perform(post("/api/intakes/{medicationId}/skipped", 999L))
                .andExpect(status().isNotFound());
    }

    @Test
    void testCreateIntakeWithNullMedicationId() throws Exception {
        CreateMedicationIntakeRequest request = new CreateMedicationIntakeRequest(
                null, "TAKEN");

        mockMvc.perform(post("/api/intakes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void testCreateIntakeWithNullStatus() throws Exception {
        CreateMedicationIntakeRequest request = new CreateMedicationIntakeRequest(
                medication.getId(), null);

        mockMvc.perform(post("/api/intakes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void testCreateIntakeWithEmptyRequestBody() throws Exception {
        mockMvc.perform(post("/api/intakes")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}"))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void testCreateIntakeWithInvalidJson() throws Exception {
        mockMvc.perform(post("/api/intakes")
                .contentType(MediaType.APPLICATION_JSON)
                .content("invalid json"))
                .andExpect(status().isInternalServerError());
    }
}
