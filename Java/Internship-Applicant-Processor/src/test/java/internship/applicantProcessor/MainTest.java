package internship.applicantProcessor;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

/**
 * Comprehensive tests for {@link Main} - the application bootstrap class.
 * Verifies proper initialization, argument forwarding, and edge case handling.
 *
 * <p>Tests focus on verifying the interaction between the Main class
 * and its dependencies while maintaining clean separation of concerns.</p>
 */
class MainTest {

    // =================================================
    // TEST CONSTANTS
    // =================================================
    private static final String[] TEST_ARGS = {"input.csv"};
    private static final String[] EMPTY_ARGS = new String[0];

    // =================================================
    // TEST CASES: ARGUMENT FORWARDING
    // =================================================

    /**
     * Tests argument forwarding with provided input file:
     * - Verifies ApplicantApp receives correct arguments
     * - Confirms proper interaction between components
     * - Ensures command-line arguments are processed
     */
    @Test
    void main_WithArguments_ForwardsToApplicantApp() {
        // Setup
        ApplicantApp mockApp = mock(ApplicantApp.class);
        Main main = new Main(mockApp);

        // Execute
        main.run(TEST_ARGS);

        // Verify
        verify(mockApp).run(TEST_ARGS);
    }

    /**
     * Tests empty argument handling:
     * - Verifies empty array is properly forwarded
     * - Confirms default behavior is maintained
     * - Ensures no argument corruption occurs
     */
    @Test
    void main_WithoutArguments_ForwardsEmptyArray() {
        // Setup
        ApplicantApp mockApp = mock(ApplicantApp.class);
        Main main = new Main(mockApp);

        // Execute
        main.run(EMPTY_ARGS);

        // Verify
        verify(mockApp).run(EMPTY_ARGS);
    }

    // =================================================
    // TEST CASES: EDGE CASE HANDLING
    // =================================================

    /**
     * Tests null argument safety:
     * - Verifies null arguments are handled gracefully
     * - Confirms no NullPointerException occurs
     * - Ensures robust error handling
     */
    @Test
    void main_WithNullArguments_HandlesGracefully() {
        // Setup
        ApplicantApp mockApp = mock(ApplicantApp.class);
        Main main = new Main(mockApp);

        // Execute
        main.run(null);

        // Verify
        verify(mockApp).run(null);
    }

    // =================================================
    // TEST CASES: INITIALIZATION
    // =================================================

    /**
     * Tests default constructor behavior:
     * - Verifies instance creation succeeds
     * - Confirms proper dependency initialization
     * - Provides basic smoke test for construction
     */
    @Test
    void defaultConstructor_CreatesInstanceSuccessfully() {
        // Execute
        Main main = new Main(); // uses real ApplicantApp

        // Verify
        assertNotNull(main, "Main instance should be created successfully");
    }

    // =================================================
    // TEST CASES: MAIN METHOD EXECUTION
    // =================================================

    /**
     * Tests main method execution:
     * - Verifies static entry point works
     * - Confirms no exceptions during execution
     * - Provides basic smoke test for full launch
     */
    @Test
    void mainMethod_WorksCorrectly() {
        // Execute & Verify (no exception expected)
        assertDoesNotThrow(() -> Main.main(new String[]{"test", "args"}),
                "Main method should execute without exceptions");
    }
}