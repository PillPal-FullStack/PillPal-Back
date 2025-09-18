package com.project.pillpal.reminder.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.pillpal.medication.entity.Medication;
import com.project.pillpal.medication.repository.MedicationRepository;
import com.project.pillpal.reminder.dto.CreateReminderRequest;
import com.project.pillpal.reminder.dto.UpdateReminderRequest;
import com.project.pillpal.reminder.entity.Frequency;
import com.project.pillpal.reminder.entity.Reminder;
import com.project.pillpal.reminder.repository.ReminderRepository;
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
import java.time.LocalTime;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureWebMvc
@ActiveProfiles("test")
@Transactional
class ReminderControllerTest {

        @Autowired
        private WebApplicationContext webApplicationContext;

        @Autowired
        private ReminderRepository reminderRepository;

        @Autowired
        private MedicationRepository medicationRepository;

        @Autowired
        private UserRepository userRepository;

        @Autowired
        private ObjectMapper objectMapper;

        private MockMvc mockMvc;
    private Medication medication;
        private Reminder reminder;

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
                medication.setLifetime(false);
                medication.setUser(user);
                medication = medicationRepository.save(medication);

                reminder = new Reminder();
                reminder.setTime(LocalTime.of(9, 0));
                reminder.setFrequency(Frequency.DAILY);
                reminder.setEnabled(true);
                reminder.setMedication(medication);
                reminder = reminderRepository.save(reminder);
        }

        @Test
        void testGetAllReminders() throws Exception {
                mockMvc.perform(get("/api/reminders"))
                                .andExpect(status().isOk())
                                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                                .andExpect(jsonPath("$").isArray())
                                .andExpect(jsonPath("$[0].time").value("09:00:00"));
        }

        @Test
        void testGetReminderById() throws Exception {
                mockMvc.perform(get("/api/reminders/{id}", reminder.getId()))
                                .andExpect(status().isOk())
                                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                                .andExpect(jsonPath("$.id").value(reminder.getId()))
                                .andExpect(jsonPath("$.time").value("09:00:00"));
        }

        @Test
        void testGetReminderByIdNotFound() throws Exception {
                mockMvc.perform(get("/api/reminders/{id}", 999L))
                                .andExpect(status().isNotFound());
        }

        @Test
        void testCreateReminder() throws Exception {
                CreateReminderRequest request = new CreateReminderRequest(
                                LocalTime.of(10, 0), Frequency.WEEKLY, true, medication.getId());

                mockMvc.perform(post("/api/reminders")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                                .andExpect(status().isCreated())
                                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                                .andExpect(jsonPath("$.time").value("10:00:00"));
        }

        @Test
        void testUpdateReminder() throws Exception {
                UpdateReminderRequest request = new UpdateReminderRequest(
                                LocalTime.of(11, 0), Frequency.WEEKLY, false);

                mockMvc.perform(put("/api/reminders/{id}", reminder.getId())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                                .andExpect(status().isOk())
                                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                                .andExpect(jsonPath("$.time").value("11:00:00"));
        }

        @Test
        void testUpdateReminderNotFound() throws Exception {
                UpdateReminderRequest request = new UpdateReminderRequest(
                                LocalTime.of(11, 0), Frequency.WEEKLY, false);

                mockMvc.perform(put("/api/reminders/{id}", 999L)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                                .andExpect(status().isNotFound());
        }

        @Test
        void testDeleteReminder() throws Exception {
                mockMvc.perform(delete("/api/reminders/{id}", reminder.getId()))
                                .andExpect(status().isOk())
                                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                                .andExpect(jsonPath("$.success").value(true))
                                .andExpect(jsonPath("$.message").value("Reminder successfully deleted"))
                                .andExpect(jsonPath("$.reminderId").value(reminder.getId()));
        }

        @Test
        void testDeleteReminderNotFound() throws Exception {
                mockMvc.perform(delete("/api/reminders/{id}", 999L))
                                .andExpect(status().isNotFound());
        }

        @Test
        void testGetRemindersByMedicationId() throws Exception {
                mockMvc.perform(get("/api/reminders/medication/{medicationId}", medication.getId()))
                                .andExpect(status().isOk())
                                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                                .andExpect(jsonPath("$.reminders").isArray())
                                .andExpect(jsonPath("$.reminders[0].time").value("09:00:00"));
        }

        @Test
        void testCreateReminderWithDailyFrequency() throws Exception {
                CreateReminderRequest request = new CreateReminderRequest(
                                LocalTime.of(8, 30), Frequency.DAILY, true, medication.getId());

                mockMvc.perform(post("/api/reminders")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                                .andExpect(status().isCreated())
                                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                                .andExpect(jsonPath("$.time").value("08:30:00"))
                                .andExpect(jsonPath("$.frequency").value("DAILY"))
                                .andExpect(jsonPath("$.enabled").value(true));
        }

        @Test
        void testCreateReminderWithWeeklyFrequency() throws Exception {
                CreateReminderRequest request = new CreateReminderRequest(
                                LocalTime.of(14, 15), Frequency.WEEKLY, false, medication.getId());

                mockMvc.perform(post("/api/reminders")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                                .andExpect(status().isCreated())
                                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                                .andExpect(jsonPath("$.time").value("14:15:00"))
                                .andExpect(jsonPath("$.frequency").value("WEEKLY"))
                                .andExpect(jsonPath("$.enabled").value(false));
        }

        @Test
        void testCreateReminderWithNonExistentMedication() throws Exception {
                CreateReminderRequest request = new CreateReminderRequest(
                                LocalTime.of(10, 0), Frequency.DAILY, true, 999L);

                mockMvc.perform(post("/api/reminders")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                                .andExpect(status().isNotFound());
        }

        @Test
        void testCreateReminderWithNullTime() throws Exception {
                CreateReminderRequest request = new CreateReminderRequest(
                                null, Frequency.DAILY, true, medication.getId());

                mockMvc.perform(post("/api/reminders")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                                .andExpect(status().isInternalServerError());
        }

        @Test
        void testCreateReminderWithNullFrequency() throws Exception {
                CreateReminderRequest request = new CreateReminderRequest(
                                LocalTime.of(10, 0), null, true, medication.getId());

                mockMvc.perform(post("/api/reminders")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                                .andExpect(status().isInternalServerError());
        }

        @Test
        void testCreateReminderWithNullMedicationId() throws Exception {
                CreateReminderRequest request = new CreateReminderRequest(
                                LocalTime.of(10, 0), Frequency.DAILY, true, null);

                mockMvc.perform(post("/api/reminders")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                                .andExpect(status().isInternalServerError());
        }

        @Test
        void testCreateReminderWithEmptyRequestBody() throws Exception {
                mockMvc.perform(post("/api/reminders")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("{}"))
                                .andExpect(status().isInternalServerError());
        }

        @Test
        void testCreateReminderWithInvalidJson() throws Exception {
                mockMvc.perform(post("/api/reminders")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("invalid json"))
                                .andExpect(status().isInternalServerError());
        }

        @Test
        void testUpdateReminderWithAllFields() throws Exception {
                UpdateReminderRequest request = new UpdateReminderRequest(
                                LocalTime.of(16, 45), Frequency.WEEKLY, false);

                mockMvc.perform(put("/api/reminders/{id}", reminder.getId())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                                .andExpect(status().isOk())
                                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                                .andExpect(jsonPath("$.time").value("16:45:00"))
                                .andExpect(jsonPath("$.frequency").value("WEEKLY"))
                                .andExpect(jsonPath("$.enabled").value(false));
        }

        @Test
        void testUpdateReminderWithPartialFields() throws Exception {
                UpdateReminderRequest request = new UpdateReminderRequest(
                                LocalTime.of(12, 30), null, null);

                mockMvc.perform(put("/api/reminders/{id}", reminder.getId())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                                .andExpect(status().isOk())
                                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                                .andExpect(jsonPath("$.time").value("12:30:00"));
        }

        @Test
        void testUpdateReminderWithEmptyRequestBody() throws Exception {
                mockMvc.perform(put("/api/reminders/{id}", reminder.getId())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("{}"))
                                .andExpect(status().isOk());
        }

        @Test
        void testUpdateReminderWithInvalidJson() throws Exception {
                mockMvc.perform(put("/api/reminders/{id}", reminder.getId())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("invalid json"))
                                .andExpect(status().isInternalServerError());
        }

        @Test
        void testToggleReminderEnabled() throws Exception {
                mockMvc.perform(patch("/api/reminders/{id}/toggle", reminder.getId()))
                                .andExpect(status().isOk())
                                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                                .andExpect(jsonPath("$.enabled").value(false));
        }

        @Test
        void testToggleReminderEnabledTwice() throws Exception {
                mockMvc.perform(patch("/api/reminders/{id}/toggle", reminder.getId()))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.enabled").value(false));

                mockMvc.perform(patch("/api/reminders/{id}/toggle", reminder.getId()))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.enabled").value(true));
        }

        @Test
        void testToggleReminderEnabledNotFound() throws Exception {
                mockMvc.perform(patch("/api/reminders/{id}/toggle", 999L))
                                .andExpect(status().isNotFound());
        }

        @Test
        void testGetRemindersByMedicationIdNotFound() throws Exception {
                mockMvc.perform(get("/api/reminders/medication/{medicationId}", 999L))
                                .andExpect(status().isOk())
                                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                                .andExpect(jsonPath("$.message")
                                                .value("No hay recordatorios configurados para este medicamento"));
        }

}
