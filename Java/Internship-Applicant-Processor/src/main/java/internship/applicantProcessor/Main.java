package internship.applicantProcessor;

/**
 * The bootstrap class for the Internship Applicant Processing System.
 * <p>
 * This class serves as the application entry point and is responsible for:
 * <ul>
 *   <li>Launching the application</li>
 *   <li>Forwarding command-line arguments</li>
 *   <li>Providing a clean separation between bootstrap and application logic</li>
 * </ul>
 *
 * <h2>Usage</h2>
 * <pre>
 * java -jar application.jar [input_file.csv]
 * </pre>
 * If no input file is specified, defaults to "input.csv" in the classpath.
 *
 * @see ApplicantApp The main application class containing business logic
 */
public class Main {
    /** The core application instance responsible for processing applicants. */
    private final ApplicantApp app;

    public Main() {
        this(new ApplicantApp());
    }

    /**
     * Parameterized constructor.
     * Allows injection of a custom {@link ApplicantApp} instance for testing or customization.
     *
     * @param app The {@link ApplicantApp} instance to use.
     */
    public Main(ApplicantApp app) {
        this.app = app;
    }

    /**
     * Runs the application with the provided command-line arguments.
     *
     * @param args Command-line arguments specifying the input file.
     */
    public void run(String[] args) {
        app.run(args);
    }

    /**
     * The main entry point of the application.
     * <p>
     * Initializes the application and forwards command-line arguments.
     *
     * @param args Command-line arguments specifying the input file.
     */
    public static void main(String[] args) {
        new Main().run(args);
    }
}