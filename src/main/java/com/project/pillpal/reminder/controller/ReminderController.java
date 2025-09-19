package com.project.pillpal.reminder.controller;

import com.project.pillpal.reminder.dto.CreateReminderRequest;
import com.project.pillpal.reminder.dto.ReminderResponse;
import com.project.pillpal.reminder.dto.ReminderListResponse;
import com.project.pillpal.reminder.dto.UpdateReminderRequest;
import com.project.pillpal.reminder.service.ReminderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/reminders")
@Tag(name = "Reminder Management", description = "API for managing medication reminders")
@Slf4j
public class ReminderController {
        private final ReminderService reminderService;

        @GetMapping
        @Operation(summary = "Get all reminders", description = "Returns a list of all reminders in the system")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "List of reminders successfully obtained", content = @Content(schema = @Schema(implementation = ReminderResponse.class))),
                        @ApiResponse(responseCode = "500", description = "Internal server error")
        })
        public ResponseEntity<List<ReminderResponse>> getAllReminders() {
                log.info("Received request to get all reminders");
                List<ReminderResponse> reminders = reminderService.getAllReminders();
                return ResponseEntity.ok(reminders);
        }

        @GetMapping("/{id}")
        @Operation(summary = "Get reminder by ID", description = "Returns reminder by specified identifier")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Reminder found", content = @Content(schema = @Schema(implementation = ReminderResponse.class))),
                        @ApiResponse(responseCode = "404", description = "Reminder not found"),
                        @ApiResponse(responseCode = "500", description = "Internal server error")
        })
        public ResponseEntity<ReminderResponse> getReminderById(
                        @Parameter(description = "Reminder ID", required = true) @PathVariable Long id) {
                log.info("Received request to get reminder by id: {}", id);
                ReminderResponse response = reminderService.getReminderById(id);
                return ResponseEntity.ok(response);
        }

        @GetMapping("/medication/{medicationId}")
        @Operation(summary = "Get reminders by medication ID", description = "Returns all reminders for a specific medication")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "List of reminders for medication obtained or message if no reminders", content = @Content(schema = @Schema(implementation = ReminderListResponse.class))),
                        @ApiResponse(responseCode = "500", description = "Internal server error")
        })
        public ResponseEntity<ReminderListResponse> getRemindersByMedicationId(
                        @Parameter(description = "Medication ID", required = true) @PathVariable Long medicationId) {
                log.info("Received request to get reminders for medication: {}", medicationId);
                List<ReminderResponse> responses = reminderService.getRemindersByMedicationId(medicationId);

                if (responses.isEmpty()) {
                        return ResponseEntity.ok(ReminderListResponse
                                        .withMessage("No hay recordatorios configurados para este medicamento"));
                }

                return ResponseEntity.ok(ReminderListResponse.withReminders(responses));
        }

        @PostMapping
        @Operation(summary = "Create new reminder", description = "Creates new reminder in the system")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "201", description = "Reminder successfully created", content = @Content(schema = @Schema(implementation = ReminderResponse.class))),
                        @ApiResponse(responseCode = "400", description = "Invalid request data"),
                        @ApiResponse(responseCode = "404", description = "Medication not found"),
                        @ApiResponse(responseCode = "500", description = "Internal server error")
        })
        public ResponseEntity<ReminderResponse> createReminder(
                        @Parameter(description = "Data for creating reminder", required = true) @RequestBody CreateReminderRequest request) {
                log.info("Received request to create reminder for medication: {}", request.medicationId());
                ReminderResponse response = reminderService.createReminder(request);
                return ResponseEntity.status(HttpStatus.CREATED).body(response);
        }

        @PutMapping("/{id}")
        @Operation(summary = "Update reminder", description = "Updates existing reminder by ID")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Reminder successfully updated", content = @Content(schema = @Schema(implementation = ReminderResponse.class))),
                        @ApiResponse(responseCode = "404", description = "Reminder not found"),
                        @ApiResponse(responseCode = "400", description = "Invalid request data"),
                        @ApiResponse(responseCode = "500", description = "Internal server error")
        })
        public ResponseEntity<ReminderResponse> updateReminder(
                        @Parameter(description = "Reminder ID", required = true) @PathVariable Long id,
                        @Parameter(description = "Data for updating reminder", required = true) @RequestBody UpdateReminderRequest request) {
                log.info("Received request to update reminder with id: {}", id);
                ReminderResponse response = reminderService.updateReminder(id, request);
                return ResponseEntity.ok(response);
        }

        @PatchMapping("/{id}/toggle")
        @Operation(summary = "Toggle reminder enabled status", description = "Toggles the enabled/disabled status of a reminder")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Reminder status successfully toggled", content = @Content(schema = @Schema(implementation = ReminderResponse.class))),
                        @ApiResponse(responseCode = "404", description = "Reminder not found"),
                        @ApiResponse(responseCode = "500", description = "Internal server error")
        })
        public ResponseEntity<ReminderResponse> toggleReminderEnabled(
                        @Parameter(description = "Reminder ID", required = true) @PathVariable Long id) {
                log.info("Received request to toggle reminder enabled status for id: {}", id);
                ReminderResponse response = reminderService.toggleReminderEnabled(id);
                return ResponseEntity.ok(response);
        }

        @DeleteMapping("/{id}")
        @Operation(summary = "Delete reminder", description = "Deletes reminder by ID")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Reminder successfully deleted"),
                        @ApiResponse(responseCode = "404", description = "Reminder not found"),
                        @ApiResponse(responseCode = "500", description = "Internal server error")
        })
        public ResponseEntity<Map<String, Object>> deleteReminder(
                        @Parameter(description = "Reminder ID", required = true) @PathVariable Long id) {
                log.info("Received request to delete reminder with id: {}", id);

                reminderService.deleteReminder(id);

                Map<String, Object> response = new HashMap<>();
                response.put("success", true);
                response.put("message", "Reminder successfully deleted");
                response.put("reminderId", id);
                return ResponseEntity.ok(response);
        }
}
