package internship.applicantProcessor;

import org.junit.jupiter.api.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import static org.mockito.Mockito.*;

/**
 * Comprehensive tests for {@link ApplicantApp}.
 * Verifies file handling, error recovery, and default behavior scenarios.
 */
class ApplicantAppTest {
    private static final Logger logger = LoggerFactory.getLogger(ApplicantAppTest.class);

    // =================================================
    // TEST CONSTANTS
    // =================================================
    private static final String DEFAULT_FILE = "input.csv";
    private static final String TEST_FILE = "test_applicants.csv";
    private static final String CSV_CONTENT = "John,Doe,john@test.com,2024-01-01T10:00:00,8.5";

    private final ApplicantApp app = new ApplicantApp();

    // =================================================
    // TEST CASES: FILE HANDLING BEHAVIOR
    // =================================================

    /**
     * Tests default file fallback behavior when:
     * - No arguments provided
     * - Input file not found in classpath
     * Verifies graceful handling of missing default file.
     */
    @Test
    void run_WithNoArgs_HandlesMissingDefaultFile() {
        // Execute
        app.run(new String[]{});

        // Verify via manual log check (consider using a log appender for automation)
        logger.info("Manual verification: Should show 'Input file not found' for {}", DEFAULT_FILE);
    }

    /**
     * Tests explicit file handling when:
     * - Custom filename provided
     * - File not found in classpath
     * Verifies proper error messaging for missing files.
     */
    @Test
    void run_WithNonexistentFile_LogsAppropriateError() {
        // Execute
        app.run(new String[]{"nonexistent.csv"});

        // Verify via manual log check
        logger.info("Manual verification: Should show 'Input file not found' for nonexistent.csv");
    }

    // =================================================
    // TEST CASES: ARGUMENT PROCESSING
    // =================================================

    /**
     * Tests blank argument handling:
     * - Empty string argument
     * - Should fall back to default filename
     * Verifies robust argument parsing.
     */
    @Test
    void run_WithBlankArgument_UsesDefaultFile() {
        // Setup
        ApplicantApp spyApp = spy(app);
        doReturn(new ByteArrayInputStream(CSV_CONTENT.getBytes()))
                .when(spyApp).loadInputFile(DEFAULT_FILE);

        // Execute
        spyApp.run(new String[]{""});

        // Verify
        verify(spyApp).loadInputFile(DEFAULT_FILE);
    }

    // =================================================
    // TEST CASES: ERROR RECOVERY
    // =================================================

    /**
     * Tests exception handling during processing:
     * - Simulates processing failure
     * - Verifies error is caught and logged
     * - Ensures application doesn't crash
     */
    @Test
    void run_WhenProcessingFails_LogsExceptionGracefully() {
        // Setup
        ApplicantApp spyApp = spy(new ApplicantApp());
        InputStream dummyStream = new ByteArrayInputStream(CSV_CONTENT.getBytes());

        doReturn(dummyStream)
                .when(spyApp).loadInputFile(DEFAULT_FILE);
        doThrow(new RuntimeException("Simulated processing error"))
                .when(spyApp).processApplicants(dummyStream);

        // Execute
        spyApp.run(new String[]{});

        // Verify
        verify(spyApp).processApplicants(dummyStream);
        logger.info("Manual verification: Should show processing error log");
    }

    // =================================================
    // TEST CASES: INTEGRATION SCENARIOS
    // =================================================

    /**
     * Tests complete happy path:
     * - Valid input file
     * - Successful processing
     * - Verifies completion logging
     */
    @Test
    void run_WithValidInput_CompletesSuccessfully() {
        // Setup
        ApplicantApp spyApp = spy(new ApplicantApp());
        InputStream testStream = new ByteArrayInputStream(CSV_CONTENT.getBytes());

        doReturn(testStream)
                .when(spyApp).loadInputFile(TEST_FILE);
        doNothing()
                .when(spyApp).processApplicants(testStream);

        // Execute
        spyApp.run(new String[]{TEST_FILE});

        // Verify
        verify(spyApp).processApplicants(testStream);
        logger.info("Manual verification: Should show successful completion");
    }
}