package com.project.pillpal.medicationintake.controller;

import com.project.pillpal.medicationintake.dto.CreateMedicationIntakeRequest;
import com.project.pillpal.medicationintake.dto.MedicationIntakeResponse;
import com.project.pillpal.medicationintake.entity.MedicationIntake;
import com.project.pillpal.medicationintake.entity.Status;
import com.project.pillpal.medicationintake.mapper.MedicationIntakeMapper;
import com.project.pillpal.medicationintake.service.MedicationIntakeService;
import com.project.pillpal.user.TestUserUtils;
import com.project.pillpal.user.entity.User;
import com.project.pillpal.exceptions.ResourceNotFoundException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/intakes")
@RequiredArgsConstructor
@Tag(name = "Medication Intakes", description = "API for managing medication intakes")
@Slf4j
public class MedicationIntakeController {

    private final MedicationIntakeService medicationIntakeService;
    private final MedicationIntakeMapper medicationIntakeMapper;
    private final TestUserUtils testUserUtils;

    @PostMapping(consumes = "application/json")
    @Operation(summary = "Create intake", description = "Creates a medication intake record with given status")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Intake created", content = @Content(schema = @Schema(implementation = MedicationIntakeResponse.class)))
    })
    public ResponseEntity<MedicationIntakeResponse> createIntake(@RequestBody CreateMedicationIntakeRequest request) {
        log.info("Received request to create intake for medication {} with status {}", request.medicationId(),
                request.status());
        Status status = Status.valueOf(request.status());
        MedicationIntake intake = medicationIntakeService.createIntake(request.medicationId(), status);
        return ResponseEntity.ok(medicationIntakeMapper.toResponse(intake));
    }

    @GetMapping("/me")
    @Operation(summary = "Get all intakes for current user", description = "Returns all intake records for the authenticated user, ordered by date descending")
    public ResponseEntity<List<MedicationIntakeResponse>> getAllIntakesForCurrentUser() {
        log.info("Received request to get all intake history for current user");
        User authenticatedUser = testUserUtils.getTestUser();
        List<MedicationIntake> intakes = medicationIntakeService.getAllIntakesForUser(authenticatedUser.getId());
        List<MedicationIntakeResponse> responses = intakes.stream().map(medicationIntakeMapper::toResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/medication/{medicationId}")
    @Operation(summary = "Get intakes by medication", description = "Returns intake history for a specific medication")
    public ResponseEntity<List<MedicationIntakeResponse>> getIntakesByMedication(@PathVariable Long medicationId) {
        log.info("Received request to get intake history for medication: {}", medicationId);
        List<MedicationIntake> intakes = medicationIntakeService.getIntakesByMedicationId(medicationId);
        if (intakes == null || intakes.isEmpty()) {
            throw new ResourceNotFoundException("No intakes found for this medication");
        }
        List<MedicationIntakeResponse> responses = intakes.stream().map(medicationIntakeMapper::toResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(responses);
    }

    @PostMapping("/{medicationId}/taken")
    @Operation(summary = "Mark medication as taken", description = "Quick action to mark medication as taken")
    public ResponseEntity<MedicationIntakeResponse> markAsTaken(@PathVariable Long medicationId) {
        log.info("Received request to mark medication {} as taken", medicationId);
        MedicationIntake intake = medicationIntakeService.createIntake(medicationId, Status.TAKEN);
        return ResponseEntity.ok(medicationIntakeMapper.toResponse(intake));
    }

    @PostMapping("/{medicationId}/skipped")
    @Operation(summary = "Mark medication as skipped", description = "Quick action to mark medication as skipped")
    public ResponseEntity<MedicationIntakeResponse> markAsSkipped(@PathVariable Long medicationId) {
        log.info("Received request to mark medication {} as skipped", medicationId);
        MedicationIntake intake = medicationIntakeService.createIntake(medicationId, Status.SKIPPED);
        return ResponseEntity.ok(medicationIntakeMapper.toResponse(intake));
    }
}
