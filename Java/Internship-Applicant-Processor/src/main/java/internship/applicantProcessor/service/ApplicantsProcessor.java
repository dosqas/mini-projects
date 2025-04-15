package internship.applicantProcessor.service;

import internship.applicantProcessor.model.Applicant;
import internship.applicantProcessor.model.ApplicantDeliveryDateTime;
import internship.applicantProcessor.model.ApplicantName;
import internship.applicantProcessor.utils.ApplicantEntryValidator;
import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvException;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Objects;

/**
 * Processes CSV input containing applicant data and delegates to ApplicantService.
 * Handles CSV parsing, validation, and conversion to domain objects.
 */
public class ApplicantsProcessor {
    private static final Logger logger = LoggerFactory.getLogger(ApplicantsProcessor.class);
    private final ApplicantService applicantService;

    /**
     * Creates a new ApplicantsProcessor with the specified service.
     * @param applicantService The service to handle processed applicants
     */
    public ApplicantsProcessor(@NotNull ApplicantService applicantService) {
        Objects.requireNonNull(applicantService, "ApplicantService cannot be null");
        this.applicantService = applicantService;
    }

    /**
     * Processes CSV input stream containing applicant data.
     * @param csvStream The input stream containing CSV data
     * @return JSON formatted output of processed applicants
     */
    public String processApplicants(InputStream csvStream) {
        Objects.requireNonNull(csvStream, "CSV stream cannot be null");
        try (CSVReader csvReader = new CSVReader(new InputStreamReader(csvStream))) {
            String[] nextLine;
            while ((nextLine = csvReader.readNext()) != null) {
                try {
                    processApplicantLine(nextLine);
                } catch (IllegalArgumentException e) {
                     // logger.warn("Skipping invalid line: {}", e.getMessage());
                }
            }
        } catch (CsvException | IOException e) {
            logger.error("Error processing CSV: {}", e.getMessage(), e);
            return "{}"; // Return an empty JSON-formatted string
        }

        return applicantService.getJsonFormatOutput();
    }

    /**
     * Processes a single line of CSV data representing an applicant.
     * Validates the line and, if valid, converts it into an Applicant object
     * and adds it to the repository via the ApplicantService.
     *
     * @param parts The parts of the CSV line, expected to contain:
     *              - parts[0]: Applicant's full name
     *              - parts[1]: Applicant's email
     *              - parts[2]: Delivery date and time in ISO-8601 format
     *              - parts[3]: Applicant's score
     * @throws IllegalArgumentException if the line contains invalid data
     */
    private void processApplicantLine(String[] parts) {
        if (shouldSkipLine(parts)) {
            return;
        }

        Applicant applicant = createValidApplicant(parts);
        applicantService.addApplicantToRepository(applicant);
    }

    /**
     * Determines whether a CSV line should be skipped during processing.
     * A line is skipped if the first part (name) is blank.
     *
     * @param parts The parts of the CSV line to check
     * @return true if the line should be skipped, false otherwise
     */
    private boolean shouldSkipLine(String[] parts) {
        return parts[0].trim().isEmpty();
    }

    /**
     * Creates an Applicant from CSV parts if valid.
     * @param parts CSV line parts (name, email, datetime, score)
     * @return Valid Applicant or null if invalid
     * @throws IllegalArgumentException if data is malformed
     */
    public Applicant createValidApplicant(@NotNull String[] parts) {
        if (parts.length != 4) {
            throw new IllegalArgumentException("CSV line must have exactly 4 fields");
        }

        String name = parts[0].trim();
        String email = parts[1].trim();
        String deliveryDatetime = parts[2].trim();
        String score = parts[3].trim();

        if (!ApplicantEntryValidator.isValidName(name)) {
            throw new IllegalArgumentException("Invalid applicant name: " + name);
        }
        if (!ApplicantEntryValidator.isValidEmail(email)) {
            throw new IllegalArgumentException("Invalid applicant email: " + email);
        }
        if (!ApplicantEntryValidator.isValidDeliveryDateTime(deliveryDatetime)) {
            throw new IllegalArgumentException("Invalid delivery date and time: " + deliveryDatetime);
        }
        if (!ApplicantEntryValidator.isValidScore(score)) {
            throw new IllegalArgumentException("Invalid applicant score: " + score);
        }

        return new Applicant(
                ApplicantName.parsePreValidated(name),
                email,
                ApplicantDeliveryDateTime.parsePreValidated(deliveryDatetime),
                Double.parseDouble(score)
        );
    }
}