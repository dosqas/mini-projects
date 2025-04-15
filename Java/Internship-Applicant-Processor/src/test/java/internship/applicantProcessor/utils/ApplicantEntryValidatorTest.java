package internship.applicantProcessor.utils;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Comprehensive validation tests for {@link ApplicantEntryValidator}.
 * Verifies strict business rules for applicant data fields according to specifications.
 */
class ApplicantEntryValidatorTest {
    // =================================================
    // TEST CASES: NAME VALIDATION
    // =================================================

    /**
     * Tests valid name patterns according to specifications:
     * - FirstName + LastName (minimum)
     * - Multiple middle names
     * - Various name part lengths
     * @param validName Test case name string
     */
    @ParameterizedTest(name = "Valid name: {0}")
    @CsvSource({
            "John Doe",                     // Minimum required (first + last)
            "Anna Maria Louisa Smith",       // Multiple middle names
            "X Æ A-12 Musk",                // Special characters allowed
            "O'Conner-Del Toro"             // Hyphens and apostrophes
    })
    void isValidName_WithValidPatterns_ReturnsTrue(String validName) {
        assertTrue(ApplicantEntryValidator.isValidName(validName),
                "Should accept valid name: " + validName);
    }

    /**
     * Tests invalid name cases including:
     * - Null/empty values
     * - Single name part
     * - Empty name parts
     * - Leading/trailing spaces
     * @param invalidName Test case name string
     */
    @ParameterizedTest(name = "Invalid name: {0}")
    @NullAndEmptySource
    @ValueSource(strings = {
            "Doe",                          // Single name part
            "John@Doe"                      // Invalid characters
    })
    void isValidName_WithInvalidPatterns_ReturnsFalse(String invalidName) {
        assertFalse(ApplicantEntryValidator.isValidName(invalidName),
                "Should reject invalid name: " + invalidName);
    }

    // =================================================
    // TEST CASES: EMAIL VALIDATION
    // =================================================

    /**
     * Tests valid email patterns according to strict specifications:
     * - Starts with letter
     * - Exactly one @
     * - Contains . after @
     * - Ends with letter
     * @param validEmail Test case email string
     */
    @ParameterizedTest(name = "Valid email: {0}")
    @CsvSource({
            "a@b.co",                       // Minimal valid
            "user.name@sub.domain.com",      // Multiple dots
            "user_name@domain.org",          // Underscore allowed
            "u@d.c"                         // Short but valid
    })
    void isValidEmail_WithValidPatterns_ReturnsTrue(String validEmail) {
        assertTrue(ApplicantEntryValidator.isValidEmail(validEmail),
                "Should accept valid email: " + validEmail);
    }

    /**
     * Tests invalid email cases including:
     * - Null/empty values
     * - Non-ASCII characters
     * - Missing/multiple @
     * - Invalid start/end characters
     * - Dot immediately after @
     * @param invalidEmail Test case email string
     */
    @ParameterizedTest(name = "Invalid email: {0}")
    @NullAndEmptySource
    @ValueSource(strings = {
            "user@.com",                    // Dot immediately after @
            "user@domain",                   // Missing . after @
            "@domain.com",                   // Missing local part
            "user@domain.",                  // Ends with dot
            "1user@domain.com",              // Starts with digit
            "user@domain.com-",              // Ends with hyphen
            "user@domain_com",               // Underscore in domain
            "user@domain.cöm",              // Non-ASCII character
            "user@@domain.com"              // Double @
    })
    void isValidEmail_WithInvalidPatterns_ReturnsFalse(String invalidEmail) {
        assertFalse(ApplicantEntryValidator.isValidEmail(invalidEmail),
                "Should reject invalid email: " + invalidEmail);
    }

    // =================================================
    // TEST CASES: DATETIME VALIDATION
    // =================================================

    /**
     * Tests valid ISO-8601 datetime patterns:
     * - Correct yyyy-MM-ddTHH:mm:ss format
     * - Various valid dates/times
     * @param validDateTime Test case datetime string
     */
    @ParameterizedTest(name = "Valid datetime: {0}")
    @CsvSource({
            "2024-01-01T00:00:00",          // Start of day
            "2024-12-31T23:59:59",          // End of day
            "2020-02-29T12:30:00",          // Leap day
            "9999-12-31T23:59:59"           // Far future
    })
    void isValidDeliveryDateTime_WithValidPatterns_ReturnsTrue(String validDateTime) {
        assertTrue(ApplicantEntryValidator.isValidDeliveryDateTime(validDateTime),
                "Should accept valid datetime: " + validDateTime);
    }

    /**
     * Tests invalid datetime cases including:
     * - Null/empty values
     * - Incorrect separators
     * - Invalid date components
     * - Missing time components
     * @param invalidDateTime Test case datetime string
     */
    @ParameterizedTest(name = "Invalid datetime: {0}")
    @NullAndEmptySource
    @ValueSource(strings = {
            "2024-01-15",                   // Missing time
            "14:30:00",                     // Missing date
            "2024/01/15T14:30:00",          // Wrong date separator
            "2024-01-15 14:30:00",          // Space instead of T
            "2024-01-15T14:30",             // Missing seconds
            "2024-01-15T14.30.00"           // Wrong time separator
    })
    void isValidDeliveryDateTime_WithInvalidPatterns_ReturnsFalse(String invalidDateTime) {
        assertFalse(ApplicantEntryValidator.isValidDeliveryDateTime(invalidDateTime),
                "Should reject invalid datetime: " + invalidDateTime);
    }

    // =================================================
    // TEST CASES: SCORE VALIDATION
    // =================================================

    /**
     * Tests valid score patterns:
     * - Integer and decimal values
     * - Edge cases (0 and 10)
     * - Various decimal precisions
     * @param validScore Test case score string
     */
    @ParameterizedTest(name = "Valid score: {0}")
    @CsvSource({
            "0",                            // Minimum value
            "10",                           // Maximum value
            "5.5",                          // Single decimal
            "7.25",                         // Two decimals
            "0.01",                         // Small positive
            "9.99",                         // Near maximum
    })
    void isValidScore_WithValidPatterns_ReturnsTrue(String validScore) {
        assertTrue(ApplicantEntryValidator.isValidScore(validScore),
                "Should accept valid score: " + validScore);
    }

    /**
     * Tests invalid score cases including:
     * - Null/empty values
     * - Out of range values
     * - Invalid formats
     * - More than 2 decimals
     * @param invalidScore Test case score string
     */
    @ParameterizedTest(name = "Invalid score: {0}")
    @NullAndEmptySource
    @ValueSource(strings = {
            "-1",                           // Negative
            "10.1",                         // Exceeds maximum
            "5.555",                        // Too many decimals
            "5,5",                          // Wrong decimal separator
            " 5.5 ",                        // Whitespace
            "5.5a",                         // Non-digit characters
            "5.",                           // Incomplete decimal
            ".5",                            // Missing integer part
            "11",
            "-43",
            "String"
    })
    void isValidScore_WithInvalidPatterns_ReturnsFalse(String invalidScore) {
        assertFalse(ApplicantEntryValidator.isValidScore(invalidScore),
                "Should reject invalid score: " + invalidScore);
    }

    // =================================================
    // TEST CASES: EDGE CASE VALIDATION
    // =================================================

    /**
     * Verifies exact pattern matching for email validation.
     * Tests specific edge cases mentioned in requirements.
     */
    @Test
    void isValidEmail_WithExactPatternRequirements_StrictlyEnforced() {
        assertAll("Email pattern strict validation",
                () -> assertFalse(ApplicantEntryValidator.isValidEmail("1user@domain.com"),
                        "Should reject emails starting with digit"),
                () -> assertFalse(ApplicantEntryValidator.isValidEmail("user@domain.com-"),
                        "Should reject emails ending with hyphen")
        );
    }

    /**
     * Verifies exact datetime format requirements.
     * Tests strict ISO-8601 compliance.
     */
    @Test
    void isValidDeliveryDateTime_WithExactFormatRequirements_StrictlyEnforced() {
        assertAll("Datetime format strict validation",
                () -> assertFalse(ApplicantEntryValidator.isValidDeliveryDateTime("2024-01-15T9:30:00"),
                        "Should require two-digit hours"),
                () -> assertFalse(ApplicantEntryValidator.isValidDeliveryDateTime("2024-1-15T14:30:00"),
                        "Should require two-digit months"),
                () -> assertFalse(ApplicantEntryValidator.isValidDeliveryDateTime("24-01-15T14:30:00"),
                        "Should require four-digit year"),
                () -> assertFalse(ApplicantEntryValidator.isValidDeliveryDateTime("2024-01-15t14:30:00"),
                        "Should require capital T separator")
        );
    }
}