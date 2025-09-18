package com.project.pillpal.medication.service;

import com.project.pillpal.cloudinary.CloudinaryService;
import com.project.pillpal.exceptions.CloudinaryException;
import com.project.pillpal.medication.entity.Medication;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MedicationImageServiceTest {

    @Mock
    private CloudinaryService cloudinaryService;

    @Mock
    private MultipartFile mockMultipartFile;

    @InjectMocks
    private MedicationImageService medicationImageService;

    private Medication medication;

    @BeforeEach
    void setUp() {
        medication = new Medication();
        medication.setId(1L);
        medication.setName("Test Medication");
        medication.setImgUrl("http://example.com/old-image.jpg");
    }

    @Test
    void testUploadImageForMedicationSuccess() throws IOException {
        Map<String, Object> uploadResult = new HashMap<>();
        uploadResult.put("secure_url", "http://example.com/image.jpg");

        when(cloudinaryService.uploadMedicationImage(any(MultipartFile.class))).thenReturn(uploadResult);

        medicationImageService.uploadImageForMedication(medication, mockMultipartFile);

        assertThat(medication.getImgUrl()).isEqualTo("http://example.com/image.jpg");
        verify(cloudinaryService).uploadMedicationImage(mockMultipartFile);
    }

    @Test
    void testUploadImageForMedicationWithNullImage() throws IOException {
        medicationImageService.uploadImageForMedication(medication, null);

        verify(cloudinaryService, never()).uploadMedicationImage(any(MultipartFile.class));
    }

    @Test
    void testUploadImageForMedicationWithEmptyImage() throws IOException {
        when(mockMultipartFile.isEmpty()).thenReturn(true);

        medicationImageService.uploadImageForMedication(medication, mockMultipartFile);

        verify(cloudinaryService, never()).uploadMedicationImage(any(MultipartFile.class));
    }

    @Test
    void testUploadImageForMedicationThrowsException() throws IOException {
        when(cloudinaryService.uploadMedicationImage(any(MultipartFile.class)))
                .thenThrow(new IOException("Cloudinary error"));

        assertThatThrownBy(() -> medicationImageService.uploadImageForMedication(medication, mockMultipartFile))
                .isInstanceOf(CloudinaryException.class)
                .hasMessage("Failed to upload medication image");
    }

    @Test
    void testUpdateImageForMedicationSuccess() throws IOException {
        Map<String, Object> uploadResult = new HashMap<>();
        uploadResult.put("secure_url", "http://example.com/new-image.jpg");

        when(cloudinaryService.uploadMedicationImage(any(MultipartFile.class))).thenReturn(uploadResult);
        when(cloudinaryService.extractPublicIdFromUrl(anyString())).thenReturn("old-image-id");
        doNothing().when(cloudinaryService).deleteMedicationImage(anyString());

        medicationImageService.updateImageForMedication(medication, mockMultipartFile);

        assertThat(medication.getImgUrl()).isEqualTo("http://example.com/new-image.jpg");
        verify(cloudinaryService).deleteMedicationImage("old-image-id");
        verify(cloudinaryService).uploadMedicationImage(mockMultipartFile);
    }

    @Test
    void testUpdateImageForMedicationWithNullImage() throws IOException {
        medicationImageService.updateImageForMedication(medication, null);

        verify(cloudinaryService, never()).uploadMedicationImage(any(MultipartFile.class));
        verify(cloudinaryService, never()).deleteMedicationImage(anyString());
    }

    @Test
    void testUpdateImageForMedicationWithEmptyImage() throws IOException {
        when(mockMultipartFile.isEmpty()).thenReturn(true);

        medicationImageService.updateImageForMedication(medication, mockMultipartFile);

        verify(cloudinaryService, never()).uploadMedicationImage(any(MultipartFile.class));
        verify(cloudinaryService, never()).deleteMedicationImage(anyString());
    }

    @Test
    void testUpdateImageForMedicationWithNoExistingImage() throws IOException {
        medication.setImgUrl(null);
        Map<String, Object> uploadResult = new HashMap<>();
        uploadResult.put("secure_url", "http://example.com/new-image.jpg");

        when(cloudinaryService.uploadMedicationImage(any(MultipartFile.class))).thenReturn(uploadResult);

        medicationImageService.updateImageForMedication(medication, mockMultipartFile);

        assertThat(medication.getImgUrl()).isEqualTo("http://example.com/new-image.jpg");
        verify(cloudinaryService).uploadMedicationImage(mockMultipartFile);
        verify(cloudinaryService, never()).deleteMedicationImage(anyString());
    }

    @Test
    void testUpdateImageForMedicationThrowsException() throws IOException {
        when(cloudinaryService.uploadMedicationImage(any(MultipartFile.class)))
                .thenThrow(new IOException("Cloudinary error"));

        assertThatThrownBy(() -> medicationImageService.updateImageForMedication(medication, mockMultipartFile))
                .isInstanceOf(CloudinaryException.class)
                .hasMessage("Failed to update medication image");
    }

    @Test
    void testDeleteImageFromMedicationSuccess() throws IOException {
        when(cloudinaryService.extractPublicIdFromUrl(anyString())).thenReturn("old-image-id");

        medicationImageService.deleteImageFromMedication(medication);

        assertThat(medication.getImgUrl()).isNull();
        verify(cloudinaryService).extractPublicIdFromUrl("http://example.com/old-image.jpg");
        verify(cloudinaryService).deleteMedicationImage("old-image-id");
    }

    @Test
    void testDeleteImageFromMedicationWithNullUrl() throws IOException {
        medication.setImgUrl(null);

        medicationImageService.deleteImageFromMedication(medication);

        verify(cloudinaryService, never()).extractPublicIdFromUrl(anyString());
        verify(cloudinaryService, never()).deleteMedicationImage(anyString());
    }

    @Test
    void testDeleteImageFromMedicationWithEmptyUrl() throws IOException {
        medication.setImgUrl("");

        medicationImageService.deleteImageFromMedication(medication);

        verify(cloudinaryService, never()).extractPublicIdFromUrl(anyString());
        verify(cloudinaryService, never()).deleteMedicationImage(anyString());
    }

    @Test
    void testDeleteImageFromMedicationWithBlankUrl() throws IOException {
        medication.setImgUrl("   ");

        medicationImageService.deleteImageFromMedication(medication);

        verify(cloudinaryService, never()).extractPublicIdFromUrl(anyString());
        verify(cloudinaryService, never()).deleteMedicationImage(anyString());
    }

    @Test
    void testDeleteImageFromMedicationWithNullPublicId() throws IOException {
        when(cloudinaryService.extractPublicIdFromUrl(anyString())).thenReturn(null);

        medicationImageService.deleteImageFromMedication(medication);

        verify(cloudinaryService).extractPublicIdFromUrl("http://example.com/old-image.jpg");
        verify(cloudinaryService, never()).deleteMedicationImage(anyString());
    }

    @Test
    void testDeleteImageFromMedicationThrowsException() throws IOException {
        when(cloudinaryService.extractPublicIdFromUrl(anyString())).thenReturn("old-image-id");
        doThrow(new IOException("Cloudinary error")).when(cloudinaryService).deleteMedicationImage("old-image-id");

        assertThatThrownBy(() -> medicationImageService.deleteImageFromMedication(medication))
                .isInstanceOf(CloudinaryException.class)
                .hasMessage("Failed to delete medication image");
    }
}