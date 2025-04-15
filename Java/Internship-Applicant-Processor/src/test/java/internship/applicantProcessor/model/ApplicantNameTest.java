package internship.applicantProcessor.model;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Comprehensive tests for {@link ApplicantName} class.
 * Verifies name parsing logic and component validation.
 */
class ApplicantNameTest {

    // =================================================
    // TEST CASES: PARSING VALIDATION
    // =================================================

    /**
     * Verifies successful parsing of valid name strings.
     * @param fullName Input name string to test
     * @param expectedFirst Expected first name component
     * @param expectedMiddle Expected middle names (null if none)
     * @param expectedLast Expected last name component
     */
    @ParameterizedTest(name = "Parse \"{0}\" → [{1}, {2}, {3}]")
    @CsvSource({
            "'John Doe', John, , Doe",                     // Simple case
            "'John Michael Doe', John, Michael, Doe",      // Single middle name
            "'John Michael Smith Doe', John, 'Michael Smith', Doe" // Multiple middle names
    })
    void parsePreValidated_WithValidNames_ReturnsCorrectComponents(
            String fullName,
            String expectedFirst,
            String expectedMiddle,
            String expectedLast) {

        // When
        ApplicantName result = ApplicantName.parsePreValidated(fullName);

        // Then
        assertAll(
                () -> assertEquals(expectedFirst, result.firstName(),
                        "First name should match"),
                () -> assertEquals(expectedMiddle != null ?
                                List.of(expectedMiddle.split(" ")) : null,
                        result.middleNames(),
                        "Middle names should match"),
                () -> assertEquals(expectedLast, result.lastName(),
                        "Last name should match")
        );
    }

    // =================================================
    // TEST CASES: EDGE CASES
    // =================================================

    /**
     * Verifies handling of names with excessive whitespace.
     */
    @Test
    void parsePreValidated_WithExtraWhitespace_TrimsCorrectly() {
        // Given
        String spacedName = "  John   Michael   Doe  ";

        // When
        ApplicantName result = ApplicantName.parsePreValidated(spacedName);

        // Then
        assertAll(
                () -> assertEquals("John", result.firstName(),
                        "Should trim first name"),
                () -> assertEquals(List.of("Michael"), result.middleNames(),
                        "Should handle spaced middle names"),
                () -> assertEquals("Doe", result.lastName(),
                        "Should trim last name")
        );
    }

    /**
     * Verifies proper handling of single middle name.
     */
    @Test
    void parsePreValidated_WithSingleMiddleName_CreatesSingletonList() {
        // When
        ApplicantName result = ApplicantName.parsePreValidated("John Michael Doe");

        // Then
        assertEquals(List.of("Michael"), result.middleNames(),
                "Single middle name should be wrapped in list");
    }

    /**
     * Verifies proper handling of multiple middle names.
     */
    @Test
    void parsePreValidated_WithMultipleMiddleNames_CreatesCorrectList() {
        // When
        ApplicantName result = ApplicantName.parsePreValidated("John Michael James Doe");

        // Then
        assertEquals(List.of("Michael", "James"), result.middleNames(),
                "Should preserve middle name order and quantity");
    }
}