package internship.applicantProcessor.repository;

import internship.applicantProcessor.model.Applicant;
import internship.applicantProcessor.model.ApplicantDeliveryDateTime;
import internship.applicantProcessor.model.ApplicantName;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;


import static org.junit.jupiter.api.Assertions.*;

/**
 * Comprehensive tests for {@link ApplicantRepository} class.
 * Verifies repository behavior including CRUD operations and edge cases.
 */
class ApplicantRepositoryTest {

    // =================================================
    // TEST CONSTANTS
    // =================================================
    private static final ApplicantName TEST_NAME =
            new ApplicantName("John", null, "Doe");
    private static final ApplicantDeliveryDateTime TEST_DATE =
            ApplicantDeliveryDateTime.parsePreValidated("2024-01-01T10:00:00");
    private static final double TEST_SCORE = 7.5;

    private ApplicantRepository repository;

    // =================================================
    // TEST SETUP
    // =================================================

    @BeforeEach
    void setUp() {
        repository = new ApplicantRepository();
    }

    // =================================================
    // TEST CASES: ADD OPERATIONS
    // =================================================

    /**
     * Verifies successful addition of valid applicants.
     *
     * @param email Test email address to use
     */
    @ParameterizedTest(name = "Add applicant with email {0}")
    @ValueSource(strings = {
            "john.doe@test.com",
            "jane.smith@example.org",
            "user+filter@domain.co.uk"
    })
    void addApplicant_WithValidInput_StoresApplicant(String email) {
        // Given
        Applicant applicant = new Applicant(TEST_NAME, email, TEST_DATE, TEST_SCORE);

        // When
        repository.addApplicant(applicant);

        // Then
        assertEquals(1, repository.getUniqueApplicantCount(),
                "Repository should contain exactly one applicant");
        assertTrue(repository.getApplicants().contains(applicant),
                "Repository should contain the added applicant");
    }
}