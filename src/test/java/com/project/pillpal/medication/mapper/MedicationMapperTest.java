package com.project.pillpal.medication.mapper;

import com.project.pillpal.medication.dtos.CreateMedicationRequest;
import com.project.pillpal.medication.dtos.MedicationResponse;
import com.project.pillpal.medication.dtos.UpdateMedicationRequest;
import com.project.pillpal.medication.entity.Medication;
import com.project.pillpal.user.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class MedicationMapperTest {

    @InjectMocks
    private MedicationMapperImpl medicationMapper;

    private Medication medication;
    private CreateMedicationRequest createRequest;
    private UpdateMedicationRequest updateRequest;

    @BeforeEach
    void setUp() {
        User user = new User();
        user.setId(1L);
        user.setUsername("testuser");
        user.setEmail("test@example.com");

        medication = new Medication();
        medication.setId(1L);
        medication.setName("Test Medication");
        medication.setDescription("Test Description");
        medication.setImgUrl("http://example.com/image.jpg");
        medication.setDosage("100mg");
        medication.setActive(true);
        medication.setStartDate(LocalDate.now());
        medication.setEndDate(LocalDate.now().plusDays(30));
        medication.setLifetime(false);
        medication.setUser(user);

        createRequest = new CreateMedicationRequest(
                "New Medication",
                "New Description",
                "http://example.com/new-image.jpg",
                "200mg",
                true,
                LocalDate.now(),
                LocalDate.now().plusDays(60),
                false,
                true,
                null,
                null,
                true);

        updateRequest = new UpdateMedicationRequest(
                "Updated Medication",
                "Updated Description",
                "http://example.com/updated-image.jpg",
                "300mg",
                false,
                LocalDate.now().plusDays(1),
                LocalDate.now().plusDays(90),
                true);
    }

    @Test
    void testToResponseSuccess() {

        MedicationResponse response = medicationMapper.toResponse(medication);

        assertThat(response).isNotNull();
        assertThat(response.id()).isEqualTo(1L);
        assertThat(response.name()).isEqualTo("Test Medication");
        assertThat(response.description()).isEqualTo("Test Description");
        assertThat(response.imgUrl()).isEqualTo("http://example.com/image.jpg");
        assertThat(response.dosage()).isEqualTo("100mg");
        assertThat(response.active()).isTrue();
        assertThat(response.startDate()).isEqualTo(LocalDate.now());
        assertThat(response.endDate()).isEqualTo(LocalDate.now().plusDays(30));
        assertThat(response.lifetime()).isFalse();
        assertThat(response.userId()).isEqualTo(1L);
    }

    @Test
    void testToResponseWithNullMedication() {

        MedicationResponse response = medicationMapper.toResponse(null);

        assertThat(response).isNull();
    }

    @Test
    void testToResponseWithNullUser() {
        medication.setUser(null);
        MedicationResponse response = medicationMapper.toResponse(medication);
        assertThat(response).isNotNull();
        assertThat(response.userId()).isNull();
    }

    @Test
    void testToEntitySuccess() {
        Medication result = medicationMapper.toEntity(createRequest);
        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("New Medication");
        assertThat(result.getDescription()).isEqualTo("New Description");
        assertThat(result.getImgUrl()).isEqualTo("http://example.com/new-image.jpg");
        assertThat(result.getDosage()).isEqualTo("200mg");
        assertThat(result.getActive()).isTrue();
        assertThat(result.getStartDate()).isEqualTo(LocalDate.now());
        assertThat(result.getEndDate()).isEqualTo(LocalDate.now().plusDays(60));
        assertThat(result.getLifetime()).isFalse();
    }

    @Test
    void testToEntityWithNullRequest() {
        Medication result = medicationMapper.toEntity(null);
        assertThat(result).isNull();
    }

    @Test
    void testToEntityWithNullDosage() {
        CreateMedicationRequest requestWithNullDosage = new CreateMedicationRequest(
                "Test Medication",
                "Test Description",
                "http://example.com/image.jpg",
                null,
                true,
                LocalDate.now(),
                LocalDate.now().plusDays(30),
                false,
                true,
                null,
                null,
                true);

        Medication result = medicationMapper.toEntity(requestWithNullDosage);
        assertThat(result).isNotNull();
        assertThat(result.getDosage()).isEqualTo("Not specified");
    }

    @Test
    void testToEntityWithNullActive() {
        CreateMedicationRequest requestWithNullActive = new CreateMedicationRequest(
                "Test Medication",
                "Test Description",
                "http://example.com/image.jpg",
                "100mg",
                null,
                LocalDate.now(),
                LocalDate.now().plusDays(30),
                false,
                true,
                null,
                null,
                true);

        Medication result = medicationMapper.toEntity(requestWithNullActive);
        assertThat(result).isNotNull();
        assertThat(result.getActive()).isTrue();
    }

    @Test
    void testToEntityWithNullLifetime() {
        CreateMedicationRequest requestWithNullLifetime = new CreateMedicationRequest(
                "Test Medication",
                "Test Description",
                "http://example.com/image.jpg",
                "100mg",
                true,
                LocalDate.now(),
                LocalDate.now().plusDays(30),
                null,
                true,
                null,
                null,
                true);

        Medication result = medicationMapper.toEntity(requestWithNullLifetime);
        assertThat(result).isNotNull();
        assertThat(result.getLifetime()).isFalse();
    }

    @Test
    void testUpdateEntitySuccess() {
        medicationMapper.updateEntity(medication, updateRequest);
        assertThat(medication.getName()).isEqualTo("Updated Medication");
        assertThat(medication.getDescription()).isEqualTo("Updated Description");
        assertThat(medication.getImgUrl()).isEqualTo("http://example.com/updated-image.jpg");
        assertThat(medication.getDosage()).isEqualTo("300mg");
        assertThat(medication.getActive()).isFalse();
        assertThat(medication.getStartDate()).isEqualTo(LocalDate.now().plusDays(1));
        assertThat(medication.getEndDate()).isEqualTo(LocalDate.now().plusDays(90));
        assertThat(medication.getLifetime()).isTrue();
    }

    @Test
    void testUpdateEntityWithNullMedication() {
        medicationMapper.updateEntity(null, updateRequest);
    }

    @Test
    void testUpdateEntityWithNullRequest() {
        medicationMapper.updateEntity(medication, null);
        assertThat(medication.getName()).isEqualTo("Test Medication");
    }

    @Test
    void testUpdateEntityWithEmptyName() {
        UpdateMedicationRequest requestWithEmptyName = new UpdateMedicationRequest(
                "",
                "Updated Description",
                "http://example.com/updated-image.jpg",
                "300mg",
                false,
                LocalDate.now().plusDays(1),
                LocalDate.now().plusDays(90),
                true);
        medicationMapper.updateEntity(medication, requestWithEmptyName);
        assertThat(medication.getName()).isEqualTo("Test Medication");
        assertThat(medication.getDescription()).isEqualTo("Updated Description");
    }

    @Test
    void testUpdateEntityWithBlankName() {
        UpdateMedicationRequest requestWithBlankName = new UpdateMedicationRequest(
                "   ",
                "Updated Description",
                "http://example.com/updated-image.jpg",
                "300mg",
                false,
                LocalDate.now().plusDays(1),
                LocalDate.now().plusDays(90),
                true);

        medicationMapper.updateEntity(medication, requestWithBlankName);

        assertThat(medication.getName()).isEqualTo("Test Medication");
        assertThat(medication.getDescription()).isEqualTo("Updated Description");
    }

    @Test
    void testUpdateEntityWithPlaceholderValues() {
        UpdateMedicationRequest requestWithPlaceholders = new UpdateMedicationRequest(
                "Updated Medication",
                "string",
                "null",
                "undefined",
                false,
                LocalDate.now().plusDays(1),
                LocalDate.now().plusDays(90),
                true);

        medicationMapper.updateEntity(medication, requestWithPlaceholders);

        assertThat(medication.getName()).isEqualTo("Updated Medication");
        assertThat(medication.getDescription()).isEqualTo("Test Description");
        assertThat(medication.getImgUrl()).isEqualTo("http://example.com/image.jpg");
        assertThat(medication.getDosage()).isEqualTo("100mg");
    }

    @Test
    void testUpdateEntityWithEmptyStringValues() {
        UpdateMedicationRequest requestWithEmptyStrings = new UpdateMedicationRequest(
                "Updated Medication",
                "",
                "",
                "",
                false,
                LocalDate.now().plusDays(1),
                LocalDate.now().plusDays(90),
                true);

        medicationMapper.updateEntity(medication, requestWithEmptyStrings);

        assertThat(medication.getName()).isEqualTo("Updated Medication");
        assertThat(medication.getDescription()).isEqualTo("Test Description");
        assertThat(medication.getImgUrl()).isEqualTo("http://example.com/image.jpg");
        assertThat(medication.getDosage()).isEqualTo("100mg");
    }

    @Test
    void testUpdateEntityWithNullValues() {
        UpdateMedicationRequest requestWithNulls = new UpdateMedicationRequest(
                "Updated Medication",
                null,
                null,
                null,
                null,
                null,
                null,
                null);

        medicationMapper.updateEntity(medication, requestWithNulls);
        assertThat(medication.getName()).isEqualTo("Updated Medication");
        assertThat(medication.getDescription()).isEqualTo("Test Description");
        assertThat(medication.getImgUrl()).isEqualTo("http://example.com/image.jpg");
        assertThat(medication.getDosage()).isEqualTo("100mg");
        assertThat(medication.getActive()).isTrue();
        assertThat(medication.getStartDate()).isEqualTo(LocalDate.now());
        assertThat(medication.getEndDate()).isEqualTo(LocalDate.now().plusDays(30));
        assertThat(medication.getLifetime()).isFalse();
    }
}