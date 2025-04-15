package internship.applicantProcessor.service;

import internship.applicantProcessor.model.Applicant;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Comprehensive tests for {@link ApplicantsProcessor}.
 * Verifies CSV processing, applicant validation, and error handling.
 */
class ApplicantsProcessorTest {

    private ApplicantService applicantService;
    private ApplicantsProcessor processor;

    @BeforeEach
    void setUp() {
        applicantService = mock(ApplicantService.class);
        processor = new ApplicantsProcessor(applicantService);
    }

    /**
     * Tests processing of valid CSV input with complete applicant data.
     * Verifies that:
     * - Applicant is added to repository
     * - Correct JSON output is returned
     */
    @Test
    void testProcessApplicants_validCSV_shouldCallServiceAndReturnJson() {
        String csv = "John Doe,john@example.com,2023-05-01T10:00:00,9.5\n";
        InputStream inputStream = new ByteArrayInputStream(csv.getBytes(StandardCharsets.UTF_8));

        when(applicantService.getJsonFormatOutput()).thenReturn("{\"result\": \"ok\"}");

        String result = processor.processApplicants(inputStream);

        verify(applicantService, times(1)).addApplicantToRepository(any(Applicant.class));
        assertEquals("{\"result\": \"ok\"}", result);
    }

    /**
     * Tests handling of CSV with invalid email format.
     * Verifies that:
     * - No applicant is added to repository
     * - Empty JSON is returned
     */
    @Test
    void testProcessApplicants_withInvalidEmail_shouldSkipLine() {
        String csv = "John Doe,invalid_email,2023-05-01T10:00:00,9.5\n";
        InputStream inputStream = new ByteArrayInputStream(csv.getBytes(StandardCharsets.UTF_8));

        when(applicantService.getJsonFormatOutput()).thenReturn("{}");

        String result = processor.processApplicants(inputStream);

        verify(applicantService, never()).addApplicantToRepository(any());
        assertEquals("{}", result);
    }

    /**
     * Tests handling of CSV with missing required fields.
     * Verifies that:
     * - Line with missing fields is skipped
     * - No applicant is added
     * - Empty JSON is returned
     */
    @Test
    void testProcessApplicants_withMissingFields_shouldSkipLine() {
        String csv = "John Doe,john@example.com,2023-05-01T10:00:00\n";
        InputStream inputStream = new ByteArrayInputStream(csv.getBytes(StandardCharsets.UTF_8));

        when(applicantService.getJsonFormatOutput()).thenReturn("{}");

        String result = processor.processApplicants(inputStream);

        verify(applicantService, never()).addApplicantToRepository(any());
        assertEquals("{}", result);
    }

    /**
     * Tests creation of valid applicant from properly formatted data.
     * Verifies that:
     * - Applicant object is created
     * - All fields are correctly parsed and assigned
     */
    @Test
    void testCreateValidApplicant_validData_shouldReturnApplicant() {
        String[] parts = {"John Doe", "john@example.com", "2023-05-01T10:00:00", "9.5"};

        Applicant applicant = processor.createValidApplicant(parts);

        assertNotNull(applicant);
        assertEquals("john@example.com", applicant.email());
        assertEquals(9.5, applicant.score(), 0.001);
    }

    /**
     * Tests handling of invalid score values in applicant data.
     * Verifies that:
     * - IllegalArgumentException is thrown
     */
    @Test
    void testCreateValidApplicant_invalidScore_shouldThrowException() {
        String[] parts = {"John Doe", "john@example.com", "2023-05-01T10:00:00", "invalid"};

        assertThrows(IllegalArgumentException.class, () -> processor.createValidApplicant(parts));
    }

    /**
     * Tests handling of invalid date formats in applicant data.
     * Verifies that:
     * - IllegalArgumentException is thrown
     */
    @Test
    void testCreateValidApplicant_invalidDate_shouldThrowException() {
        String[] parts = {"John Doe", "john@example.com", "bad-date", "95.5"};

        assertThrows(IllegalArgumentException.class, () -> processor.createValidApplicant(parts));
    }

    /**
     * Tests handling of corrupted CSV input streams.
     * Verifies that:
     * - IOException is handled gracefully
     * - Empty JSON is returned
     * - No applicant is added
     */
    @Test
    void testProcessApplicants_invalidCsvFormat_shouldReturnEmptyJson() {
        InputStream corruptedStream = new InputStream() {
            @Override
            public int read() throws IOException {
                throw new IOException("Corrupted Stream");
            }
        };

        when(applicantService.getJsonFormatOutput()).thenReturn("{}");

        String result = processor.processApplicants(corruptedStream);

        assertEquals("{}", result);
        verify(applicantService, never()).addApplicantToRepository(any());
    }

    /**
     * Tests processing of empty CSV content.
     * Verifies that:
     * - Empty input is handled gracefully
     * - Empty JSON is returned
     * - No applicant is added
     */
    @Test
    void testProcessApplicants_emptyCsv_shouldReturnEmptyJson() {
        String csv = "";
        InputStream inputStream = new ByteArrayInputStream(csv.getBytes(StandardCharsets.UTF_8));

        when(applicantService.getJsonFormatOutput()).thenReturn("{}");

        String result = processor.processApplicants(inputStream);

        assertEquals("{}", result);
        verify(applicantService, never()).addApplicantToRepository(any());
    }

    /**
     * Tests handling of null-like input in CSV processing.
     * Verifies that:
     * - Empty lines are skipped
     * - No applicant is added
     * - Empty JSON is returned
     */
    @Test
    void testProcessApplicants_nullParts_shouldSkipLine() {
        String csv = "";
        InputStream inputStream = new ByteArrayInputStream(csv.getBytes(StandardCharsets.UTF_8));

        when(applicantService.getJsonFormatOutput()).thenReturn("{}");

        String result = processor.processApplicants(inputStream);

        verify(applicantService, never()).addApplicantToRepository(any());
        assertEquals("{}", result);
    }

    /**
     * Tests handling of completely empty CSV lines.
     * Verifies that:
     * - Lines with only commas are skipped
     * - No applicant is added
     * - Empty JSON is returned
     */
    @Test
    void testProcessApplicants_emptyArray_shouldSkipLine() {
        String csv = ",,,\n";
        InputStream inputStream = new ByteArrayInputStream(csv.getBytes(StandardCharsets.UTF_8));

        when(applicantService.getJsonFormatOutput()).thenReturn("{}");

        String result = processor.processApplicants(inputStream);

        verify(applicantService, never()).addApplicantToRepository(any());
        assertEquals("{}", result);
    }

    /**
     * Tests handling of empty name fields in CSV data.
     * Verifies that:
     * - Records with empty names are skipped
     * - No applicant is added
     * - Empty JSON is returned
     */
    @Test
    void testProcessApplicants_emptyName_shouldSkipLine() {
        String csv = ",john@example.com,2023-05-01T10:00:00,9.5\n";
        InputStream inputStream = new ByteArrayInputStream(csv.getBytes(StandardCharsets.UTF_8));

        when(applicantService.getJsonFormatOutput()).thenReturn("{}");

        String result = processor.processApplicants(inputStream);

        verify(applicantService, never()).addApplicantToRepository(any());
        assertEquals("{}", result);
    }

    /**
     * Tests processing of valid name formats.
     * Verifies that:
     * - Properly formatted names are accepted
     * - Applicant is added to repository
     * - Success JSON is returned
     */
    @Test
    void testProcessApplicants_validName_shouldNotSkipLine() {
        String csv = "John Doe,john@example.com,2023-05-01T10:00:00,9.5\n";
        InputStream inputStream = new ByteArrayInputStream(csv.getBytes(StandardCharsets.UTF_8));

        when(applicantService.getJsonFormatOutput()).thenReturn("{\"result\": \"ok\"}");

        String result = processor.processApplicants(inputStream);

        verify(applicantService, times(1)).addApplicantToRepository(any(Applicant.class));
        assertEquals("{\"result\": \"ok\"}", result);
    }

    /**
     * Tests handling of invalid name formats.
     * Verifies that:
     * - Overly short names are rejected
     * - No applicant is added
     * - Empty JSON is returned
     */
    @Test
    void testProcessApplicants_invalidName_shouldSkipLine() {
        String csv = "J,john@example.com,2023-05-01T10:00:00,9\n";
        InputStream inputStream = new ByteArrayInputStream(csv.getBytes(StandardCharsets.UTF_8));

        when(applicantService.getJsonFormatOutput()).thenReturn("{}");

        String result = processor.processApplicants(inputStream);

        verify(applicantService, never()).addApplicantToRepository(any());
        assertEquals("{}", result);
    }
}