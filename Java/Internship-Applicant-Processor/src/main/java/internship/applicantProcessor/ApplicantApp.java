package internship.applicantProcessor;

import internship.applicantProcessor.repository.ApplicantRepository;
import internship.applicantProcessor.service.ApplicantService;
import internship.applicantProcessor.service.ApplicantsProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;

/**
 * Main application class for processing internship applicant data.
 * <p>
 * This class serves as the entry point for the application and coordinates:
 * <ul>
 *   <li>Input file handling</li>
 *   <li>Dependency initialization</li>
 *   <li>Processing pipeline execution</li>
 *   <li>Error handling and logging</li>
 * </ul>
 *
 * Expected usage: {@code java ApplicantApp [inputFileName]}
 */
public class ApplicantApp {
    private static final Logger logger = LoggerFactory.getLogger(ApplicantApp.class);
    private static final String DEFAULT_FILE_NAME = "input.csv";

    /**
     * Main application execution method.
     * <p>
     * Processes command line arguments and initiates the applicant processing pipeline.
     *
     * @param args Command line arguments (optional input file name)
     */
    public void run(String[] args) {
        // Determine input source with fallback to default
        String fileName = determineInputFile(args);
        // logger.debug("Using input file: {}", fileName);

        try (InputStream csvStream = loadInputFile(fileName)) {
            if (csvStream == null) {
                handleFileNotFound(fileName);
                return;
            }

            processApplicants(csvStream);
        } catch (Exception e) {
            logger.error("Application processing failed for file: {}", fileName, e);
        }
    }

    /**
     * Determines the input file name from arguments or uses default.
     * @param args Command line arguments
     * @return Valid file name to process
     */
    private String determineInputFile(String[] args) {
        return args.length > 0 && args[0] != null && !args[0].isBlank() ? args[0] : DEFAULT_FILE_NAME;
    }

    /**
     * Attempts to load the input file from classpath resources.
     * @param fileName Name of the file to load
     * @return Input stream for the file, or null if not found
     */
    public InputStream loadInputFile(String fileName) {
        return getClass().getClassLoader().getResourceAsStream(fileName);
    }

    /**
     * Handles file not found scenario with appropriate logging.
     * @param fileName Name of the missing file
     */
    private void handleFileNotFound(String fileName) {
        logger.error("Input file not found in classpath: {}", fileName);
        logger.info("Please ensure the file exists in one of these locations:");
        logger.info("- src/main/resources/{}", fileName);
        logger.info("- The root of your JAR file");
    }

    /**
     * Initializes and executes the applicant processing pipeline.
     * @param csvStream Input stream containing CSV data
     */
    public void processApplicants(InputStream csvStream) {
        ApplicantRepository repository = new ApplicantRepository();
        ApplicantService service = new ApplicantService(repository);
        ApplicantsProcessor processor = new ApplicantsProcessor(service);

        String result = processor.processApplicants(csvStream);
        // logger.info("Processing completed successfully. Results:\n{}", result);
        System.out.println(result);
    }
}