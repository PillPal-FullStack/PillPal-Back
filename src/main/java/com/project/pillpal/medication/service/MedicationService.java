package com.project.pillpal.medication.service;

import com.project.pillpal.medication.dtos.CreateMedicationRequest;
import com.project.pillpal.medication.dtos.MedicationResponse;
import com.project.pillpal.medication.dtos.MedicationStatusResponse;
import com.project.pillpal.medication.dtos.UpdateMedicationRequest;
import com.project.pillpal.medication.entity.Medication;
import com.project.pillpal.medication.mapper.MedicationMapper;
import com.project.pillpal.medication.repository.MedicationRepository;
import com.project.pillpal.user.entity.User;
import com.project.pillpal.exceptions.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class MedicationService {

    private final MedicationRepository medicationRepository;
    private final MedicationMapper medicationMapper;
    private final MedicationImageService medicationImageService;
    private final MedicationStatusService medicationStatusService;

    public List<MedicationResponse> getAllMedications() {
        log.info("Getting all medications");
        List<Medication> medications = medicationRepository.findAll();
        return medications.stream()
                .map(medicationMapper::toResponse)
                .collect(java.util.stream.Collectors.toList());
    }

    public List<MedicationResponse> getMedicationsByUserId(Long userId) {
        log.info("Getting medications for user {}", userId);
        List<Medication> medications = medicationRepository.findByUserId(userId);
        return medications.stream()
                .map(medicationMapper::toResponse)
                .collect(java.util.stream.Collectors.toList());
    }

    public List<MedicationResponse> getActiveMedications() {
        log.info("Getting active medications");
        List<Medication> medications = medicationRepository.findByActiveTrue();
        return medications.stream()
                .map(medicationMapper::toResponse)
                .collect(java.util.stream.Collectors.toList());
    }

    public MedicationResponse getMedicationById(Long id) {
        log.info("Getting medication by id {}", id);
        return medicationRepository.findById(id)
                .map(medicationMapper::toResponse)
                .orElseThrow(() -> new ResourceNotFoundException("Medication not found with id: " + id));
    }

    public MedicationResponse createMedication(CreateMedicationRequest request, MultipartFile image, User user) {
        log.info("Creating new medication: {} for user: {}", request.name(), user.getId());

        Medication medication = medicationMapper.toEntity(request);
        medication.setUser(user);

        if (medication.getLifetime() != null && medication.getLifetime()) {
            medication.setEndDate(null);
        }

        if (image != null && !image.isEmpty()) {
            medicationImageService.uploadImageForMedication(medication, image);
        }

        Medication savedMedication = medicationRepository.save(medication);
        log.info("Successfully created medication with id: {} for user: {}", savedMedication.getId(), user.getId());

        return medicationMapper.toResponse(savedMedication);
    }

    public MedicationResponse updateMedication(Long id, UpdateMedicationRequest request, MultipartFile image,
            User user) {
        log.info("Updating medication with id: {} for user: {}", id, user.getId());

        Medication medication = medicationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Medication not found with id: " + id));

        if (!medication.getUser().getId().equals(user.getId())) {
            throw new com.project.pillpal.exceptions.UnauthorizedAccessException(
                    "You are not authorized to update this medication");
        }

        medicationMapper.updateEntity(medication, request);

        if (image != null && !image.isEmpty()) {
            medicationImageService.updateImageForMedication(medication, image);
        }

        medicationRepository.save(medication);
        log.info("Successfully updated medication with id: {} for user: {}", id, user.getId());

        Medication refreshedMedication = medicationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Medication not found with id: " + id));

        return medicationMapper.toResponse(refreshedMedication);
    }

    public void deleteMedication(Long id, User user) {
        log.info("Deleting medication with id: {} for user: {}", id, user.getId());

        Medication medication = medicationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Medication not found with id: " + id));

        if (!medication.getUser().getId().equals(user.getId())) {
            throw new com.project.pillpal.exceptions.UnauthorizedAccessException(
                    "You are not authorized to delete this medication");
        }

        medicationImageService.deleteImageFromMedication(medication);

        medicationRepository.delete(medication);
        log.info("Successfully deleted medication with id: {} for user: {}", id, user.getId());
    }

    public List<MedicationResponse> getMedicationsByUserAndActive(Long userId, boolean active) {
        log.info("Getting medications for user {} with active status: {}", userId, active);
        List<Medication> medications = active
                ? medicationRepository.findByUserIdAndActiveTrue(userId)
                : medicationRepository.findByUserId(userId);
        return medications.stream()
                .map(medicationMapper::toResponse)
                .collect(java.util.stream.Collectors.toList());
    }

    public boolean existsById(Long id) {
        log.debug("Checking if medication exists with id: {}", id);
        return medicationRepository.existsById(id);
    }

    public long count() {
        log.debug("Counting total medications");
        return medicationRepository.count();
    }

    public long countByUserId(Long userId) {
        log.debug("Counting medications for user: {}", userId);
        return medicationRepository.findByUserId(userId).size();
    }

    public List<MedicationResponse> searchMedicationsByName(String name) {
        log.info("Searching medications by name: {}", name);
        List<Medication> medications = medicationRepository.findByNameContainingIgnoreCase(name);
        return medications.stream()
                .map(medicationMapper::toResponse)
                .collect(java.util.stream.Collectors.toList());
    }

    public List<MedicationResponse> searchMedicationsByUserIdAndName(Long userId, String name) {
        log.info("Searching medications for user {} by name: {}", userId, name);
        List<Medication> medications = medicationRepository.findByUserIdAndNameContainingIgnoreCase(userId, name);
        return medications.stream()
                .map(medicationMapper::toResponse)
                .collect(java.util.stream.Collectors.toList());
    }

    public List<MedicationStatusResponse> getMedicationsByTimeOfDay() {
        log.info("Getting medications status by time of day");
        return medicationStatusService.getMedicationsByTimeOfDay();
    }

    public MedicationStatusResponse getMedicationStatusById(Long medicationId) {
        log.info("Getting medication status by id {}", medicationId);
        return medicationStatusService.getMedicationStatusById(medicationId);
    }

}