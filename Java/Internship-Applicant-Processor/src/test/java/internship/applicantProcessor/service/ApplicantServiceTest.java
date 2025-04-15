package internship.applicantProcessor.service;

import internship.applicantProcessor.model.Applicant;
import internship.applicantProcessor.model.ApplicantDeliveryDateTime;
import internship.applicantProcessor.model.ApplicantName;
import internship.applicantProcessor.repository.ApplicantRepository;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Comprehensive test suite for {@link ApplicantService}.
 * Verifies business logic for applicant processing, ranking, and statistics generation.
 */
@ExtendWith(MockitoExtension.class)
class ApplicantServiceTest {

    // =================================================
    // TEST CONSTANTS
    // =================================================
    private static final ApplicantName TEST_NAME = new ApplicantName("John", null, "Doe");
    private static final String TEST_EMAIL = "john.doe@test.com";
    private static final ApplicantDeliveryDateTime EARLY_DATE =
            ApplicantDeliveryDateTime.parsePreValidated("2024-01-01T09:00:00");
    private static final ApplicantDeliveryDateTime LATE_DATE =
            ApplicantDeliveryDateTime.parsePreValidated("2024-01-03T14:00:00");
    private static final double TEST_SCORE = 7.5;

    @Mock
    private ApplicantRepository mockRepository;
    private ApplicantService applicantService;

    @BeforeEach
    void setUp() {
        applicantService = new ApplicantService(mockRepository);
    }

    // =================================================
    // TEST CASES: REPOSITORY INTERACTIONS
    // =================================================

    /**
     * Tests proper delegation of applicant storage to repository.
     */
    @Test
    void addApplicantToRepository_WithValidApplicant_DelegatesToRepository() {
        // Given
        Applicant applicant = new Applicant(TEST_NAME, TEST_EMAIL, EARLY_DATE, TEST_SCORE);

        // When
        applicantService.addApplicantToRepository(applicant);

        // Then
        verify(mockRepository).addApplicant(applicant);
    }

    // =================================================
    // TEST CASES: DATE-BASED OPERATIONS
    // =================================================

    /**
     * Tests edge case handling for empty applicant lists when finding earliest date.
     */
    @Test
    void findEarliestDeliveryDate_WithNoApplicants_ReturnsNull() {
        // Given
        when(mockRepository.getApplicants()).thenReturn(List.of());

        // When & Then
        assertNull(applicantService.findEarliestDeliveryDate());
    }

    /**
     * Verifies correct identification of latest submission date from multiple applicants.
     */
    @Test
    void findLatestDeliveryDate_WithMultipleApplicants_ReturnsCorrectDate() {
        // Given
        Applicant early = createTestApplicant(EARLY_DATE);
        Applicant late = createTestApplicant(LATE_DATE);
        when(mockRepository.getApplicants()).thenReturn(List.of(early, late));

        // When & Then
        assertEquals(LATE_DATE, applicantService.findLatestDeliveryDate());
    }

    // =================================================
    // TEST CASES: STATISTICS OUTPUT
    // =================================================

    /**
     * Tests JSON output generation for empty applicant lists.
     * Verifies all statistics fields default to zero/empty values.
     */
    @Test
    void getJsonFormatOutput_WithNoApplicants_ReturnsEmptyStats() {
        // Given
        when(mockRepository.getUniqueApplicantCount()).thenReturn(0);
        when(mockRepository.getApplicants()).thenReturn(List.of());

        // When
        String json = applicantService.getJsonFormatOutput();

        // Then
        assertTrue(json.contains("\"uniqueApplicants\": 0"));
        assertTrue(json.contains("\"topApplicants\": []"));
        assertTrue(json.contains("\"averageScore\": 0.0"));
    }

    /**
     * Verifies correct JSON statistics generation with actual applicant data:
     * - Unique applicant count
     * - Properly ordered top applicants
     * - Accurate average score calculation
     */
    @Test
    void getJsonFormatOutput_WithApplicants_ReturnsCorrectStats() {
        // Given
        when(mockRepository.getUniqueApplicantCount()).thenReturn(3);
        when(mockRepository.getApplicants()).thenReturn(List.of(
                createTestApplicant("Doe", 8.0, EARLY_DATE),
                createTestApplicant("Smith", 7.0, LATE_DATE),
                createTestApplicant("Jones", 6.0, LATE_DATE)
        ));

        // When
        String json = applicantService.getJsonFormatOutput();

        // Then
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("uniqueApplicants", 3);
        JsonArray topApplicants = new JsonArray();
        topApplicants.add("Doe");
        topApplicants.add("Smith");
        topApplicants.add("Jones");
        jsonObject.add("topApplicants", topApplicants);
        jsonObject.addProperty("averageScore", 7.5);

        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String prettyJson = gson.toJson(jsonObject);

        // Assert
        assertEquals(prettyJson, json);
    }

    // =================================================
    // HELPER METHODS
    // =================================================

    /**
     * Creates test applicant with fixed name/email/score and variable delivery date.
     * @param dateTime Delivery datetime for test applicant
     * @return Configured applicant instance
     */
    private Applicant createTestApplicant(ApplicantDeliveryDateTime dateTime) {
        return new Applicant(TEST_NAME, TEST_EMAIL, dateTime, TEST_SCORE);
    }

    /**
     * Creates fully configurable test applicant.
     * @param lastName Last name for applicant
     * @param score Test score value
     * @param dateTime Delivery datetime
     * @return Configured applicant instance
     */
    private Applicant createTestApplicant(String lastName, double score, ApplicantDeliveryDateTime dateTime) {
        return new Applicant(
                new ApplicantName("Test", null, lastName),
                "test@test.com",
                dateTime,
                score
        );
    }
}