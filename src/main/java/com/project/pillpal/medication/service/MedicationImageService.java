package com.project.pillpal.medication.service;

import com.project.pillpal.cloudinary.CloudinaryService;
import com.project.pillpal.exceptions.CloudinaryException;
import com.project.pillpal.medication.entity.Medication;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class MedicationImageService {

    private final CloudinaryService cloudinaryService;

    public void uploadImageForMedication(Medication medication, MultipartFile image) {
        if (image == null || image.isEmpty()) {
            log.debug("No image provided for medication {}", medication.getId());
            return;
        }

        try {
            log.info("Uploading image for medication {}", medication.getId());
            Map<String, Object> uploadResult = cloudinaryService.uploadMedicationImage(image);
            String imageUrl = (String) uploadResult.get("secure_url");
            medication.setImgUrl(imageUrl);
            log.info("Successfully uploaded image for medication {}", medication.getId());
        } catch (Exception e) {
            log.error("Error uploading image for medication {}: {}", medication.getId(), e.getMessage());
            throw new CloudinaryException("Failed to upload medication image", e);
        }
    }

    public void updateImageForMedication(Medication medication, MultipartFile image) {
        if (image == null || image.isEmpty()) {
            log.debug("No new image provided for medication {}", medication.getId());
            return;
        }

        try {
            if (medication.getImgUrl() != null && !medication.getImgUrl().trim().isEmpty()) {
                deleteImageFromMedication(medication);
            }

            uploadImageForMedication(medication, image);
            log.info("Successfully updated image for medication {}", medication.getId());
        } catch (Exception e) {
            log.error("Error updating image for medication {}: {}", medication.getId(), e.getMessage());
            throw new CloudinaryException("Failed to update medication image", e);
        }
    }

    public void deleteImageFromMedication(Medication medication) {
        if (medication.getImgUrl() == null || medication.getImgUrl().trim().isEmpty()) {
            log.debug("No image to delete for medication {}", medication.getId());
            return;
        }

        try {
            log.info("Deleting image for medication {}", medication.getId());
            String publicId = cloudinaryService.extractPublicIdFromUrl(medication.getImgUrl());
            if (publicId != null) {
                cloudinaryService.deleteMedicationImage(publicId);
                medication.setImgUrl(null);
                log.info("Successfully deleted image for medication {}", medication.getId());
            } else {
                log.warn("Could not extract public ID from URL for medication {}", medication.getId());
            }
        } catch (Exception e) {
            log.error("Error deleting image for medication {}: {}", medication.getId(), e.getMessage());
            throw new CloudinaryException("Failed to delete medication image", e);
        }
    }
}
