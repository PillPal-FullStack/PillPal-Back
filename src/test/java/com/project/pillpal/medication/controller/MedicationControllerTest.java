package com.project.pillpal.medication.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.pillpal.medication.dtos.CreateMedicationRequest;
import com.project.pillpal.medication.dtos.UpdateMedicationRequest;
import com.project.pillpal.medication.entity.Medication;
import com.project.pillpal.medication.repository.MedicationRepository;
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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureWebMvc
@ActiveProfiles("test")
@Transactional
class MedicationControllerTest {

        @Autowired
        private WebApplicationContext webApplicationContext;

        @Autowired
        private MedicationRepository medicationRepository;

        @Autowired
        private UserRepository userRepository;

        @Autowired
        private ObjectMapper objectMapper;

        private MockMvc mockMvc;
        private User user;
        private Medication medication;

        @BeforeEach
        void setUp() {
                mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();

                user = userRepository.findByUsernameIgnoreCase("testuser").orElseGet(() -> {
                        User newUser = new User();
                        newUser.setUsername("testuser");
                        newUser.setEmail("test@example.com");
                        newUser.setPassword("password123");
                        newUser.setRole(Role.ROLE_USER);
                        return userRepository.save(newUser);
                });

                medication = new Medication();
                medication.setName("Test Medication");
                medication.setDescription("Test Description");
                medication.setDosage("100mg");
                medication.setActive(true);
                medication.setStartDate(LocalDate.now());
                medication.setEndDate(LocalDate.now().plusDays(30));
                medication.setLifetime(false);
                medication.setUser(user);
                medication = medicationRepository.save(medication);
        }

        @Test
        void testGetAllMedications() throws Exception {
                mockMvc.perform(get("/api/medications"))
                                .andExpect(status().isOk())
                                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                                .andExpect(jsonPath("$").isArray())
                                .andExpect(jsonPath("$[0].name").value("Test Medication"));
        }

        @Test
        void testGetMedicationById() throws Exception {
                mockMvc.perform(get("/api/medications/{id}", medication.getId()))
                                .andExpect(status().isOk())
                                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                                .andExpect(jsonPath("$.id").value(medication.getId()))
                                .andExpect(jsonPath("$.name").value("Test Medication"));
        }

        @Test
        void testGetMedicationByIdNotFound() throws Exception {
                mockMvc.perform(get("/api/medications/{id}", 999L))
                                .andExpect(status().isNotFound());
        }

        @Test
        void testUpdateMedicationNotFound() throws Exception {
                UpdateMedicationRequest request = new UpdateMedicationRequest(
                                "Updated Medication", "Updated Description", null, "300mg", true,
                                LocalDate.now().plusDays(1), LocalDate.now().plusDays(90), true);

                mockMvc.perform(put("/api/medications/{id}", 999L)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                                .andExpect(status().isNotFound());
        }

        @Test
        void testDeleteMedicationNotFound() throws Exception {
                mockMvc.perform(delete("/api/medications/{id}", 999L))
                                .andExpect(status().isNotFound());
        }

        @Test
        void testGetMedicationsByUserId() throws Exception {
                mockMvc.perform(get("/api/medications/user/{userId}", user.getId()))
                                .andExpect(status().isOk())
                                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                                .andExpect(jsonPath("$").isArray())
                                .andExpect(jsonPath("$[0].name").value("Test Medication"));
        }

        @Test
        void testGetActiveMedications() throws Exception {
                mockMvc.perform(get("/api/medications/active"))
                                .andExpect(status().isOk())
                                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                                .andExpect(jsonPath("$").isArray())
                                .andExpect(jsonPath("$[0].name").value("Test Medication"));
        }

        @Test
        void testGetMedicationsByTimeOfDay() throws Exception {
                mockMvc.perform(get("/api/medications/status"))
                                .andExpect(status().isOk())
                                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                                .andExpect(jsonPath("$").isArray());
        }

        @Test
        void testGetMedicationStatusById() throws Exception {
                mockMvc.perform(get("/api/medications/{id}/status", medication.getId()))
                                .andExpect(status().isOk())
                                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                                .andExpect(jsonPath("$.medicationId").value(medication.getId()));
        }

        @Test
        void testSearchMedicationsByName() throws Exception {
                mockMvc.perform(get("/api/medications/search")
                                .param("name", "Test"))
                                .andExpect(status().isOk())
                                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                                .andExpect(jsonPath("$").isArray())
                                .andExpect(jsonPath("$[0].name").value("Test Medication"));
        }

        @Test
        void testSearchMedicationsByNameEmpty() throws Exception {
                mockMvc.perform(get("/api/medications/search")
                                .param("name", ""))
                                .andExpect(status().isBadRequest());
        }

        @Test
        void testSearchMedicationsByUserIdAndName() throws Exception {
                mockMvc.perform(get("/api/medications/user/{userId}/search", user.getId())
                                .param("name", "Test"))
                                .andExpect(status().isOk())
                                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                                .andExpect(jsonPath("$").isArray())
                                .andExpect(jsonPath("$[0].name").value("Test Medication"));
        }

        @Test
        void testSearchMedicationsByUserIdAndNameEmpty() throws Exception {
                mockMvc.perform(get("/api/medications/user/{userId}/search", user.getId())
                                .param("name", ""))
                                .andExpect(status().isBadRequest());
        }
}
