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
        private User user;
        private Medication medication;
        private Reminder reminder;

        @BeforeEach
        void setUp() {
                mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();

                user = new User();
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
}
