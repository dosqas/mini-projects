package internship.applicantProcessor.utils;

/**
 * Provides strict validation methods for applicant data fields according to task specifications.
 * All validation rules follow exact business requirements for the internship application process.
 */
public class ApplicantEntryValidator {
    // Regex for ASCII-only emails with exact specification requirements
    private static final String EMAIL_REGEX =
            "^[a-zA-Z]" +                   // Must start with letter
                    "[a-zA-Z0-9._-]*" +             // May contain letters, digits, ._-
                    "@" +                           // Exactly one @
                    "[a-zA-Z0-9_-]+" +              // Domain part after @ (no . immediately after)
                    "\\." +                         // Must contain . after @
                    "[a-zA-Z0-9._-]*" +             // Optional subdomains
                    "[a-zA-Z]$";                    // Must end with letter

    // Strict ISO-8601 local datetime format
    private static final String DATETIME_REGEX =
            "^\\d{4}" +                     // Year: exactly 4 digits
                    "-" +                           // Separator: dash (-)
                    "\\d{2}" +                      // Month: exactly 2 digits
                    "-" +                           // Separator: dash (-)
                    "\\d{2}" +                      // Day: exactly 2 digits
                    "T" +                           // Separator: capital T
                    "\\d{2}" +                      // Hour: exactly 2 digits (24-hour format)
                    ":" +                           // Separator: colon (:)
                    "\\d{2}" +                      // Minutes: exactly 2 digits
                    ":" +                           // Separator: colon (:)
                    "\\d{2}$";                      // Seconds: exactly 2 digits

    // Score validation (0-10 with max 2 decimals)
    private static final String SCORE_REGEX =
            "^(" +                          // Start of the regex
                    "10" +                          // Integer 10
                    "(\\.0{1,2})?" +                // Optional decimals for 10 (e.g., 10.0 or 10.00)
                    "|" +                           // OR
                    "[0-9]" +                       // Single digit (0-9)
                    "(\\.[0-9]{1,2})?" +            // Optional decimals for 0-9 (e.g., 0.1, 0.12)
                    ")$";

    /**
     * Validates a full applicant name according to specification:
     * - Pattern: FirstName MiddleName1 MiddleName2... LastName
     * - Middle names are optional
     * - FirstName and LastName are mandatory
     * - Minimum 2 name parts (first + last)
     * - No empty name parts allowed
     *
     * @param fullName The name to validate
     * @return true if valid according to specification
     */
    public static boolean isValidName(String fullName) {
        if (fullName == null || fullName.trim().isEmpty()) {
            return false;
        }

        String[] parts = fullName.trim().split("\\s+");

        return parts.length >= 2; // Need at least first and last name
    }

    /**
     * Validates an email address with strict requirements:
     * - ASCII characters only
     * - Only letters, digits, and @._- special characters
     * - Starts with a letter
     * - Exactly one @ symbol
     * - Contains . after @, but not immediately after
     * - Ends with a letter
     *
     * @param email The email to validate
     * @return true if meets all email requirements
     */
    public static boolean isValidEmail(String email) {
        if (email == null || email.isEmpty()) {
            return false;
        }

        // Check ASCII-only
        if (!email.matches("^\\p{ASCII}+$")) {
            return false;
        }

        return email.matches(EMAIL_REGEX);
    }

    /**
     * Validates delivery datetime in strict ISO-8601 format:
     * - yyyy-MM-ddTHH:mm:ss
     * - 4-digit year, 2-digit month/day
     * - 2-digit hour (24h), minute, second
     * - Separated by - : T exactly as specified
     *
     * @param deliveryDateTime The datetime string to validate
     * @return true if format matches exactly
     */
    public static boolean isValidDeliveryDateTime(String deliveryDateTime) {
        return deliveryDateTime != null &&
                deliveryDateTime.matches(DATETIME_REGEX);
    }

    /**
     * Validates score with strict requirements:
     * - Decimal number with up to 2 decimals
     * - Non-negative
     * - Maximum value of 10
     * - 10 must be integer (10.0 invalid)
     * - . as decimal separator
     *
     * @param score The score string to validate
     * @return true if meets all score requirements
     */
    public static boolean isValidScore(String score) {
        if (score == null || score.isEmpty()) {
            return false;
        }

        return score.matches(SCORE_REGEX);

        // No additional checks needed since regex
        // takes care of strings or other non-number
        // values.
    }
}