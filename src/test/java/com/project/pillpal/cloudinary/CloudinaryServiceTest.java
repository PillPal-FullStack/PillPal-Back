package com.project.pillpal.cloudinary;

import com.cloudinary.Cloudinary;
import com.cloudinary.Uploader;
import com.cloudinary.Url;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CloudinaryServiceTest {

    @Mock
    private Cloudinary cloudinary;

    @Mock
    private Uploader uploader;

    @Mock
    private Url url;

    private CloudinaryService cloudinaryService;

    @BeforeEach
    void setUp() {
        cloudinaryService = new CloudinaryService(cloudinary);
    }

    @Test
    void testUploadFile() throws IOException {
        when(cloudinary.uploader()).thenReturn(uploader);
        MultipartFile file = new MockMultipartFile("file", "test.jpg", "image/jpeg", "test content".getBytes());
        Map<String, Object> expectedResult = new HashMap<>();
        expectedResult.put("public_id", "test123");
        expectedResult.put("url", "https://example.com/test123.jpg");

        when(uploader.upload(any(byte[].class), anyMap())).thenReturn(expectedResult);

        Map<String, Object> result = cloudinaryService.uploadFile(file);

        assertNotNull(result);
        assertEquals("test123", result.get("public_id"));
        verify(uploader).upload(any(byte[].class), anyMap());
    }

    @Test
    void testDeleteFile() throws IOException {
        when(cloudinary.uploader()).thenReturn(uploader);
        String publicId = "test123";
        Map<String, Object> expectedResult = new HashMap<>();
        expectedResult.put("result", "ok");

        when(uploader.destroy(eq(publicId), anyMap())).thenReturn(expectedResult);

        assertDoesNotThrow(() -> cloudinaryService.deleteFile(publicId));
        verify(uploader).destroy(eq(publicId), anyMap());
    }

    @Test
    void testUploadMedicationImage() throws IOException {
        when(cloudinary.uploader()).thenReturn(uploader);
        MultipartFile file = new MockMultipartFile("file", "medication.jpg", "image/jpeg", "test content".getBytes());
        Map<String, Object> expectedResult = new HashMap<>();
        expectedResult.put("public_id", "pillpal/medications/test123");
        expectedResult.put("url", "https://example.com/test123.jpg");

        when(uploader.upload(any(byte[].class), anyMap())).thenReturn(expectedResult);

        Map<String, Object> result = cloudinaryService.uploadMedicationImage(file);

        assertNotNull(result);
        assertEquals("pillpal/medications/test123", result.get("public_id"));
        verify(uploader).upload(any(byte[].class), anyMap());
    }

    @Test
    void testUploadMedicationImageWithNullFile() {
        assertThrows(IllegalArgumentException.class, () -> cloudinaryService.uploadMedicationImage(null));
    }

    @Test
    void testUploadMedicationImageWithEmptyFile() {
        MultipartFile file = new MockMultipartFile("file", "test.jpg", "image/jpeg", new byte[0]);
        assertThrows(IllegalArgumentException.class, () -> cloudinaryService.uploadMedicationImage(file));
    }

    @Test
    void testUploadMedicationImageWithNonImageFile() {
        MultipartFile file = new MockMultipartFile("file", "test.txt", "text/plain", "test content".getBytes());
        assertThrows(IllegalArgumentException.class, () -> cloudinaryService.uploadMedicationImage(file));
    }

    @Test
    void testUploadMedicationImageWithNullContentType() {
        MultipartFile file = new MockMultipartFile("file", "test.jpg", null, "test content".getBytes());
        assertThrows(IllegalArgumentException.class, () -> cloudinaryService.uploadMedicationImage(file));
    }

    @Test
    void testDeleteMedicationImage() throws IOException {
        when(cloudinary.uploader()).thenReturn(uploader);
        String publicId = "test123";
        Map<String, Object> expectedResult = new HashMap<>();
        expectedResult.put("result", "ok");

        when(uploader.destroy(eq(publicId), anyMap())).thenReturn(expectedResult);

        assertDoesNotThrow(() -> cloudinaryService.deleteMedicationImage(publicId));
        verify(uploader).destroy(eq(publicId), anyMap());
    }

    @Test
    void testDeleteMedicationImageWithNullPublicId() {
        assertThrows(IllegalArgumentException.class, () -> cloudinaryService.deleteMedicationImage(null));
    }

    @Test
    void testDeleteMedicationImageWithEmptyPublicId() {
        assertThrows(IllegalArgumentException.class, () -> cloudinaryService.deleteMedicationImage(""));
    }

    @Test
    void testDeleteMedicationImageWithWhitespacePublicId() {
        assertThrows(IllegalArgumentException.class, () -> cloudinaryService.deleteMedicationImage("   "));
    }

    @Test
    void testDeleteMedicationImageWithWarningResult() throws IOException {
        when(cloudinary.uploader()).thenReturn(uploader);
        String publicId = "test123";
        Map<String, Object> expectedResult = new HashMap<>();
        expectedResult.put("result", "not found");

        when(uploader.destroy(eq(publicId), anyMap())).thenReturn(expectedResult);

        assertDoesNotThrow(() -> cloudinaryService.deleteMedicationImage(publicId));
        verify(uploader).destroy(eq(publicId), anyMap());
    }

    @Test
    void testGetImageUrl() {
        when(cloudinary.url()).thenReturn(url);
        String publicId = "test123";
        String expectedUrl = "https://example.com/test123.jpg";

        when(url.generate(publicId)).thenReturn(expectedUrl);
        String result = cloudinaryService.getImageUrl(publicId);
        assertEquals(expectedUrl, result);
        verify(url).generate(publicId);
    }

    @Test
    void testGetImageUrlWithNullPublicId() {
        String result = cloudinaryService.getImageUrl(null);
        assertNull(result);
    }

    @Test
    void testGetImageUrlWithEmptyPublicId() {
        String result = cloudinaryService.getImageUrl("");
        assertNull(result);
    }

    @Test
    void testGetImageUrlWithWhitespacePublicId() {
        String result = cloudinaryService.getImageUrl("   ");
        assertNull(result);
    }

    @Test
    void testExtractPublicIdFromUrl() {
        String imageUrl = "https://res.cloudinary.com/test/image/upload/v1234567890/pillpal/medications/test123.jpg";
        String result = cloudinaryService.extractPublicIdFromUrl(imageUrl);
        assertEquals("pillpal/medications/test123", result);
    }

    @Test
    void testExtractPublicIdFromUrlWithoutVersion() {
        String imageUrl = "https://res.cloudinary.com/test/image/upload/pillpal/medications/test123.jpg";
        String result = cloudinaryService.extractPublicIdFromUrl(imageUrl);
        assertEquals("pillpal/medications/test123", result);
    }

    @Test
    void testExtractPublicIdFromUrlWithoutExtension() {
        String imageUrl = "https://res.cloudinary.com/test/image/upload/pillpal/medications/test123";
        String result = cloudinaryService.extractPublicIdFromUrl(imageUrl);
        assertEquals("pillpal/medications/test123", result);
    }

    @Test
    void testExtractPublicIdFromUrlWithNull() {
        String result = cloudinaryService.extractPublicIdFromUrl(null);
        assertNull(result);
    }

    @Test
    void testExtractPublicIdFromUrlWithEmpty() {
        String result = cloudinaryService.extractPublicIdFromUrl("");
        assertNull(result);
    }

    @Test
    void testExtractPublicIdFromUrlWithWhitespace() {
        String result = cloudinaryService.extractPublicIdFromUrl("   ");
        assertNull(result);
    }

    @Test
    void testExtractPublicIdFromUrlWithInvalidUrl() {
        String imageUrl = "https://example.com/invalid-url";
        String result = cloudinaryService.extractPublicIdFromUrl(imageUrl);
        assertNull(result);
    }

    @Test
    void testExtractPublicIdFromUrlWithException() {
        String imageUrl = "https://res.cloudinary.com/test/image/upload/";
        String result = cloudinaryService.extractPublicIdFromUrl(imageUrl);
        assertEquals("", result);
    }
}