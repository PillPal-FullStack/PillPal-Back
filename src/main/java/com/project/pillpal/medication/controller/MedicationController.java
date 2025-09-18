package com.project.pillpal.medication.controller;

import com.project.pillpal.medication.dtos.CreateMedicationRequest;
import com.project.pillpal.medication.dtos.MedicationResponse;

import com.project.pillpal.medication.dtos.UpdateMedicationRequest;
import com.project.pillpal.medication.service.MedicationService;
import com.project.pillpal.medication.dtos.MedicationStatusResponse;

import com.project.pillpal.user.auth.AuthUtils;
import com.project.pillpal.user.entity.User;
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
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/medications")
@RequiredArgsConstructor
@Tag(name = "Medication Management", description = "API for managing medications")
@Slf4j
public class MedicationController {

        private final MedicationService medicationService;

        @GetMapping
        @Operation(summary = "Get all medications", description = "Returns a list of all medications in the system")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "List of medications successfully obtained", content = @Content(schema = @Schema(implementation = MedicationResponse.class))),
                        @ApiResponse(responseCode = "500", description = "Internal server error")
        })
        public ResponseEntity<List<MedicationResponse>> getAllMedications() {
                log.info("Received request to get all medications");
                List<MedicationResponse> responses = medicationService.getAllMedications();
                return ResponseEntity.ok(responses);
        }

        @GetMapping("/{id}")
        @Operation(summary = "Get medication by ID", description = "Returns medication by specified identifier")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Medication found", content = @Content(schema = @Schema(implementation = MedicationResponse.class))),
                        @ApiResponse(responseCode = "404", description = "Medication not found"),
                        @ApiResponse(responseCode = "500", description = "Internal server error")
        })
        public ResponseEntity<MedicationResponse> getMedicationById(
                        @Parameter(description = "Medication ID", required = true) @PathVariable Long id) {
                log.info("Received request to get medication by id: {}", id);
                MedicationResponse response = medicationService.getMedicationById(id);
                return ResponseEntity.ok(response);
        }

        @PostMapping(consumes = "application/json")
        @Operation(summary = "Create new medication", description = "Creates new medication in the system")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "201", description = "Medication successfully created", content = @Content(schema = @Schema(implementation = MedicationResponse.class))),
                        @ApiResponse(responseCode = "400", description = "Invalid request data"),
                        @ApiResponse(responseCode = "401", description = "User not authenticated"),
                        @ApiResponse(responseCode = "500", description = "Internal server error")
        })
        public ResponseEntity<MedicationResponse> createMedication(
                        @Parameter(description = "Data for creating medication", required = true) @RequestBody CreateMedicationRequest request) {

                log.info("Received request to create medication: {}", request.name());
                User authenticatedUser = AuthUtils.getAuthenticatedUser();
                MedicationResponse response = medicationService.createMedication(request, null, authenticatedUser);
                return ResponseEntity.status(HttpStatus.CREATED).body(response);
        }

        @PostMapping(value = "/with-image", consumes = "multipart/form-data")
        @Operation(summary = "Create new medication with image", description = "Creates new medication in the system with image upload")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "201", description = "Medication successfully created", content = @Content(schema = @Schema(implementation = MedicationResponse.class))),
                        @ApiResponse(responseCode = "400", description = "Invalid request data"),
                        @ApiResponse(responseCode = "401", description = "User not authenticated"),
                        @ApiResponse(responseCode = "500", description = "Internal server error")
        })
        public ResponseEntity<MedicationResponse> createMedicationWithImage(
                        @Parameter(description = "Medication name", required = true) @RequestParam String name,
                        @Parameter(description = "Medication description") @RequestParam(required = false) String description,
                        @Parameter(description = "Medication dosage", required = true) @RequestParam String dosage,
                        @Parameter(description = "Active status", required = true) @RequestParam Boolean active,
                        @Parameter(description = "Start date", required = true) @RequestParam String startDate,
                        @Parameter(description = "End date") @RequestParam(required = false) String endDate,
                        @Parameter(description = "Lifetime status", required = true) @RequestParam Boolean lifetime,
                        @Parameter(description = "Image file", content = @Content(mediaType = "multipart/form-data", schema = @Schema(type = "string", format = "binary"))) @RequestParam(value = "image", required = false) MultipartFile image) {

                log.info("Received request to create medication with image: {}", name);
                User authenticatedUser = AuthUtils.getAuthenticatedUser();

                CreateMedicationRequest request = new CreateMedicationRequest(
                                name,
                                description,
                                null,
                                dosage,
                                active,
                                java.time.LocalDate.parse(startDate),
                                endDate != null ? java.time.LocalDate.parse(endDate) : null,
                                lifetime);

                MedicationResponse response = medicationService.createMedication(request, image, authenticatedUser);
                return ResponseEntity.status(HttpStatus.CREATED).body(response);
        }

        @PutMapping(value = "/{id}", consumes = "application/json")
        @Operation(summary = "Update medication", description = "Updates existing medication by ID")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Medication successfully updated", content = @Content(schema = @Schema(implementation = MedicationResponse.class))),
                        @ApiResponse(responseCode = "404", description = "Medication not found"),
                        @ApiResponse(responseCode = "400", description = "Invalid request data"),
                        @ApiResponse(responseCode = "401", description = "User not authenticated or not authorized"),
                        @ApiResponse(responseCode = "500", description = "Internal server error")
        })
        public ResponseEntity<MedicationResponse> updateMedication(
                        @Parameter(description = "Medication ID", required = true) @PathVariable Long id,
                        @Parameter(description = "Data for updating medication", required = true) @RequestBody UpdateMedicationRequest request) {

                log.info("Received request to update medication with id: {}", id);
                User authenticatedUser = AuthUtils.getAuthenticatedUser();
                MedicationResponse response = medicationService.updateMedication(id, request, null, authenticatedUser);
                return ResponseEntity.ok(response);
        }

        @PutMapping(value = "/{id}/with-image", consumes = "multipart/form-data")
        @Operation(summary = "Update medication with image", description = "Updates existing medication by ID with image upload")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Medication successfully updated", content = @Content(schema = @Schema(implementation = MedicationResponse.class))),
                        @ApiResponse(responseCode = "404", description = "Medication not found"),
                        @ApiResponse(responseCode = "400", description = "Invalid request data"),
                        @ApiResponse(responseCode = "401", description = "User not authenticated or not authorized"),
                        @ApiResponse(responseCode = "500", description = "Internal server error")
        })
        public ResponseEntity<MedicationResponse> updateMedicationWithImage(
                        @Parameter(description = "Medication ID", required = true) @PathVariable Long id,
                        @Parameter(description = "Medication name") @RequestParam(required = false) String name,
                        @Parameter(description = "Medication description") @RequestParam(required = false) String description,
                        @Parameter(description = "Medication dosage") @RequestParam(required = false) String dosage,
                        @Parameter(description = "Active status") @RequestParam(required = false) Boolean active,
                        @Parameter(description = "Start date") @RequestParam(required = false) String startDate,
                        @Parameter(description = "End date") @RequestParam(required = false) String endDate,
                        @Parameter(description = "Lifetime status") @RequestParam(required = false) Boolean lifetime,
                        @Parameter(description = "Image file", content = @Content(mediaType = "multipart/form-data", schema = @Schema(type = "string", format = "binary"))) @RequestParam(value = "image", required = false) MultipartFile image) {

                log.info("Received request to update medication with image, id: {}", id);
                User authenticatedUser = AuthUtils.getAuthenticatedUser();

                UpdateMedicationRequest request = new UpdateMedicationRequest(
                                name,
                                description,
                                null,
                                dosage,
                                active,
                                startDate != null ? java.time.LocalDate.parse(startDate) : null,
                                endDate != null ? java.time.LocalDate.parse(endDate) : null,
                                lifetime);

                MedicationResponse response = medicationService.updateMedication(id, request, image, authenticatedUser);
                return ResponseEntity.ok(response);
        }

        @DeleteMapping("/{id}")
        @Operation(summary = "Delete medication", description = "Deletes medication by ID")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Medication successfully deleted"),
                        @ApiResponse(responseCode = "404", description = "Medication not found"),
                        @ApiResponse(responseCode = "401", description = "User not authenticated or not authorized"),
                        @ApiResponse(responseCode = "500", description = "Internal server error")
        })
        public ResponseEntity<Map<String, Object>> deleteMedication(
                        @Parameter(description = "Medication ID", required = true) @PathVariable Long id) {
                log.info("Received request to delete medication with id: {}", id);

                User authenticatedUser = AuthUtils.getAuthenticatedUser();
                medicationService.deleteMedication(id, authenticatedUser);

                Map<String, Object> response = new HashMap<>();
                response.put("success", true);
                response.put("message", "Medication successfully deleted");
                response.put("medicationId", id);
                return ResponseEntity.ok(response);
        }

        @GetMapping("/user/{userId}")
        @Operation(summary = "Get medications by user ID", description = "Returns all medications for a specific user")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "List of medications for user obtained", content = @Content(schema = @Schema(implementation = MedicationResponse.class))),
                        @ApiResponse(responseCode = "500", description = "Internal server error")
        })
        public ResponseEntity<List<MedicationResponse>> getMedicationsByUserId(
                        @Parameter(description = "User ID", required = true) @PathVariable Long userId) {
                log.info("Received request to get medications for user: {}", userId);
                List<MedicationResponse> responses = medicationService.getMedicationsByUserId(userId);
                return ResponseEntity.ok(responses);
        }

        @GetMapping("/active")
        @Operation(summary = "Get active medications", description = "Returns all active medications")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "List of active medications obtained", content = @Content(schema = @Schema(implementation = MedicationResponse.class))),
                        @ApiResponse(responseCode = "500", description = "Internal server error")
        })
        public ResponseEntity<List<MedicationResponse>> getActiveMedications() {
                log.info("Received request to get active medications");
                List<MedicationResponse> responses = medicationService.getActiveMedications();
                return ResponseEntity.ok(responses);
        }

        @GetMapping("/status")
        @Operation(summary = "Get medications status by time of day", description = "Returns medications with status based on current time and today's intake records")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Medications status obtained", content = @Content(schema = @Schema(implementation = MedicationStatusResponse.class))),
                        @ApiResponse(responseCode = "500", description = "Internal server error")
        })
        public ResponseEntity<List<MedicationStatusResponse>> getMedicationsByTimeOfDay() {
                log.info("Received request to get medications status by time of day");
                List<MedicationStatusResponse> responses = medicationService.getMedicationsByTimeOfDay();
                return ResponseEntity.ok(responses);
        }

        @GetMapping("/{id}/status")
        @Operation(summary = "Get medication status by ID", description = "Returns status for a specific medication based on today's intake records")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Medication status obtained", content = @Content(schema = @Schema(implementation = MedicationStatusResponse.class))),
                        @ApiResponse(responseCode = "404", description = "Medication not found"),
                        @ApiResponse(responseCode = "500", description = "Internal server error")
        })
        public ResponseEntity<MedicationStatusResponse> getMedicationStatusById(
                        @Parameter(description = "Medication ID", required = true) @PathVariable Long id) {
                log.info("Received request to get medication status by id: {}", id);
                MedicationStatusResponse response = medicationService.getMedicationStatusById(id);
                return ResponseEntity.ok(response);
        }

        @GetMapping("/search")
        @Operation(summary = "Search medications by name", description = "Searches for medications by partial name match (case-insensitive)")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Search results found", content = @Content(schema = @Schema(implementation = MedicationResponse.class))),
                        @ApiResponse(responseCode = "400", description = "Invalid search parameter"),
                        @ApiResponse(responseCode = "500", description = "Internal server error")
        })
        public ResponseEntity<List<MedicationResponse>> searchMedicationsByName(
                        @Parameter(description = "Name or partial name to search for", required = true) @RequestParam String name) {

                log.info("Received request to search medications by name: {}", name);

                if (name == null || name.trim().isEmpty()) {
                        return ResponseEntity.badRequest().build();
                }

                List<MedicationResponse> responses = medicationService.searchMedicationsByName(name.trim());
                return ResponseEntity.ok(responses);
        }

        @GetMapping("/user/{userId}/search")
        @Operation(summary = "Search medications by name for specific user", description = "Searches for medications by partial name match for a specific user (case-insensitive)")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Search results found", content = @Content(schema = @Schema(implementation = MedicationResponse.class))),
                        @ApiResponse(responseCode = "400", description = "Invalid search parameter"),
                        @ApiResponse(responseCode = "500", description = "Internal server error")
        })
        public ResponseEntity<List<MedicationResponse>> searchMedicationsByUserIdAndName(
                        @Parameter(description = "User ID", required = true) @PathVariable Long userId,
                        @Parameter(description = "Name or partial name to search for", required = true) @RequestParam String name) {

                log.info("Received request to search medications for user {} by name: {}", userId, name);

                if (name == null || name.trim().isEmpty()) {
                        return ResponseEntity.badRequest().build();
                }

                List<MedicationResponse> responses = medicationService.searchMedicationsByUserIdAndName(userId,
                                name.trim());
                return ResponseEntity.ok(responses);
        }

}