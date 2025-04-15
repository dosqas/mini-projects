package internship.applicantProcessor.model;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Comprehensive tests for {@link ApplicantDeliveryDateTime} class.
 * Verifies ISO-8601 parsing, date comparison, and time-based operations.
 */
class ApplicantDeliveryDateTimeTest {

    // =================================================
    // TEST CONSTANTS
    // =================================================
    private static final String VALID_ISO_DATE = "2024-01-15T14:30:00";

    // =================================================
    // TEST CASES: PARSING VALIDATION
    // =================================================

    /**
     * Verifies successful parsing of valid ISO-8601 strings.
     */
    @Test
    void parsePreValidated_WithValidIso8601String_ReturnsCorrectDateTime() {
        // When
        ApplicantDeliveryDateTime result =
                ApplicantDeliveryDateTime.parsePreValidated(VALID_ISO_DATE);

        // Then
        assertNotNull(result, "Should return non-null object");
        assertEquals(
                LocalDateTime.parse(VALID_ISO_DATE),
                result.dateTime(),
                "Parsed datetime should match input string"
        );
    }

    /**
     * Verifies rejection of invalid datetime formats.
     * @param invalidInput Test cases including null, empty, and malformed strings
     */
    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {
            "invalid",          // Completely malformed
            "2024-01-15",       // Missing time component
            "14:30:00",         // Missing date component
            "2024-01-15T25:00:00" // Invalid hour
    })
    void parsePreValidated_WithInvalidInput_ThrowsException(String invalidInput) {
        assertThrows(
                Exception.class,
                () -> ApplicantDeliveryDateTime.parsePreValidated(invalidInput),
                "Should reject invalid datetime: " + invalidInput
        );
    }

    // =================================================
    // TEST CASES: DATE COMPARISON
    // =================================================

    /**
     * Tests same-day detection across various scenarios.
     * @param firstDateTime First datetime string to compare
     * @param secondDateTime Second datetime string to compare
     * @param expectedResult Whether dates should be considered same day
     */
    @ParameterizedTest(name = "{0} vs {1} → same day? {2}")
    @CsvSource({
            "2024-01-15T14:30:00, 2024-01-15T10:00:00, true",   // Different times, same day
            "2024-01-15T14:30:00, 2024-01-16T10:00:00, false",   // Different days
            "2024-01-15T00:00:00, 2024-01-15T23:59:59, true"     // Day boundary edges
    })
    void isOnSameDate_WithVariousDatePairs_ReturnsCorrectResult(
            String firstDateTime,
            String secondDateTime,
            boolean expectedResult) {

        // Given
        ApplicantDeliveryDateTime first = parse(firstDateTime);
        ApplicantDeliveryDateTime second = parse(secondDateTime);

        // When & Then
        assertEquals(
                expectedResult,
                first.isOnSameDate(second),
                String.format(
                        "Expected %s and %s %s to be same day",
                        firstDateTime,
                        secondDateTime,
                        expectedResult ? "" : "not"
                )
        );
    }

    // =================================================
    // TEST CASES: TIME OPERATIONS
    // =================================================

    /**
     * Verifies midday detection logic.
     * @param testDateTime Datetime string to evaluate
     * @param isAfterMidday Expected result of isAfterMidday()
     */
    @ParameterizedTest(name = "{0} is after midday? {1}")
    @CsvSource({
            "2024-01-15T12:00:00, true",   // Exactly midday
            "2024-01-15T14:30:00, true",   // Afternoon
            "2024-01-15T11:59:59, false",  // Late morning
            "2024-01-15T00:00:00, false"   // Midnight
    })
    void isAfterMidday_AtVariousTimes_ReturnsCorrectResult(
            String testDateTime,
            boolean isAfterMidday) {

        // Given
        ApplicantDeliveryDateTime datetime = parse(testDateTime);

        // When & Then
        assertEquals(
                isAfterMidday,
                datetime.isAfterMidday(),
                String.format(
                        "Expected %s to %s be after midday",
                        testDateTime,
                        isAfterMidday ? "" : "not"
                )
        );
    }

    // =================================================
    // TEST CASES: COMPARISON LOGIC
    // =================================================

    /**
     * Tests chronological ordering of date-times.
     * @param earlierDateTime Earlier datetime string
     * @param laterDateTime Later datetime string
     * @param expectedComparison Expected compareTo() result
     */
    @ParameterizedTest(name = "Compare {0} to {1} → {2}")
    @CsvSource({
            "2024-01-15T10:00:00, 2024-01-15T14:30:00, -1",  // Morning vs afternoon
            "2024-01-16T10:00:00, 2024-01-15T14:30:00, 1",   // Next day
            "2024-01-15T14:30:00, 2024-01-15T14:30:00, 0"    // Equal times
    })
    void compareTo_WithVariousDatePairs_ReturnsCorrectOrdering(
            String earlierDateTime,
            String laterDateTime,
            int expectedComparison) {

        // Given
        ApplicantDeliveryDateTime first = parse(earlierDateTime);
        ApplicantDeliveryDateTime second = parse(laterDateTime);

        // When & Then
        assertEquals(
                expectedComparison,
                first.compareTo(second),
                String.format(
                        "Expected %s to be %s %s",
                        earlierDateTime,
                        expectedComparison < 0 ? "before" :
                                expectedComparison > 0 ? "after" : "equal to",
                        laterDateTime
                )
        );
    }

    // =================================================
    // HELPER METHODS
    // =================================================

    /**
     * Test helper for concise datetime parsing.
     * @param isoDateTime Valid ISO-8601 datetime string
     * @return Parsed ApplicantDeliveryDateTime
     */
    private ApplicantDeliveryDateTime parse(String isoDateTime) {
        return ApplicantDeliveryDateTime.parsePreValidated(isoDateTime);
    }
}