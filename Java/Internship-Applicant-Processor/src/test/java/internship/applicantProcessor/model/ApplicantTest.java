package internship.applicantProcessor.model;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Comprehensive tests for {@link Applicant} class.
 * Verifies score calculation logic including clamping and time-based adjustments.
 */
class ApplicantTest {

    // =================================================
    // TEST CONSTANTS
    // =================================================
    private static final ApplicantName TEST_NAME =
            new ApplicantName("John", null, "Doe");
    private static final String TEST_EMAIL = "john.doe@test.com";
    private static final ApplicantDeliveryDateTime BASE_DATE =
            ApplicantDeliveryDateTime.parsePreValidated("2024-01-01T10:00:00");

    // =================================================
    // TEST CASES: SCORE CLAMPING
    // =================================================

    /**
     * Verifies that scores are always clamped between 0.0 and 10.0.
     * @param inputScore Test score values including out-of-bounds cases
     */
    @ParameterizedTest(name = "Input score {0} should be clamped to [0,10]")
    @ValueSource(doubles = {-1.0, 0.0, 5.5, 10.0, 11.0})
    void calculateAdjustedScore_AlwaysClampsResult(double inputScore) {
        // Given
        Applicant applicant = new Applicant(TEST_NAME, TEST_EMAIL, BASE_DATE, inputScore);

        // When
        double result = applicant.calculateAdjustedScore(BASE_DATE, BASE_DATE);

        // Then
        assertTrue(result >= 0.0 && result <= 10.0,
                "Score should be clamped between 0 and 10");
    }

    // =================================================
    // TEST CASES: TIME-BASED ADJUSTMENTS
    // =================================================

    /**
     * Verifies correct application of time-based score adjustment rules.
     * @param deliveryTime When the application was submitted
     * @param earliestDate Start of submission period
     * @param latestDate End of submission period
     * @param initialScore Original applicant score
     * @param expectedScore Expected score after adjustments
     */
    @ParameterizedTest(name = "Delivery {0} in period {1}-{2}: {3} → {4}")
    @CsvSource({
            "2024-01-01T09:00:00, 2024-01-01, 2024-01-03, 6.0, 7.0",    // First day (bonus)
            "2024-01-03T13:00:00, 2024-01-01, 2024-01-03, 6.0, 5.0",    // Last day afternoon (malus)
            "2024-01-02T11:00:00, 2024-01-01, 2024-01-03, 6.0, 6.0",    // Normal day (no change)
            "2024-01-03T11:00:00, 2024-01-01, 2024-01-03, 6.0, 6.0"      // Last day before midday (no change)
    })
    void calculateAdjustedScore_AppliesCorrectRules(
            String deliveryTime,
            String earliestDate,
            String latestDate,
            double initialScore,
            double expectedScore) {

        // Given
        ApplicantDeliveryDateTime delivery = parse(deliveryTime);
        Applicant applicant = new Applicant(TEST_NAME, TEST_EMAIL, delivery, initialScore);

        // When
        double result = applicant.calculateAdjustedScore(
                parse(earliestDate + "T00:00:00"),
                parse(latestDate + "T23:59:59")
        );

        // Then
        assertEquals(expectedScore, result, 0.001,
                "Score adjustment should match expected value");
    }

    // =================================================
    // TEST CASES: PRIVATE METHOD VALIDATION
    // =================================================

    /**
     * Verifies edge case handling of private score clamping method.
     */
    @Test
    void clampScore_EdgeCases() throws Exception {
        // Given
        Applicant applicant = new Applicant(TEST_NAME, TEST_EMAIL, BASE_DATE, 5.0);
        Method clampScoreMethod = Applicant.class.getDeclaredMethod("clampScore", double.class);
        clampScoreMethod.setAccessible(true);

        // When & Then
        assertAll(
                () -> assertEquals(0.0, clampScoreMethod.invoke(applicant, -1.0),
                        "Negative scores should clamp to 0"),
                () -> assertEquals(10.0, clampScoreMethod.invoke(applicant, 11.0),
                        "Scores >10 should clamp to 10"),
                () -> assertEquals(5.5, clampScoreMethod.invoke(applicant, 5.5),
                        "In-range scores should remain unchanged")
        );
    }

    // =================================================
    // HELPER METHODS
    // =================================================

    /**
     * Test helper for concise datetime parsing.
     * @param datetime Valid ISO-8601 datetime string
     * @return Parsed ApplicantDeliveryDateTime object
     */
    private ApplicantDeliveryDateTime parse(String datetime) {
        return ApplicantDeliveryDateTime.parsePreValidated(datetime);
    }
}