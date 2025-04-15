package internship.applicantProcessor.model;

import org.jetbrains.annotations.NotNull;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Objects;

/**
 * Immutable record representing a pre-validated delivery datetime in ISO-8601 format.
 * Wraps java.time.LocalDateTime for better date/time operations.
 */
public record ApplicantDeliveryDateTime(@NotNull LocalDateTime dateTime)
        implements Comparable<ApplicantDeliveryDateTime> {

    private static final DateTimeFormatter ISO_FORMATTER =
            DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    /**
     * Parses an ISO-8601 datetime string (yyyy-MM-dd'T'HH:mm:ss).
     * @param deliveryDateTime Pre-validated datetime string
     * @return Parsed datetime object
     * @throws DateTimeParseException if format is invalid
     * @throws NullPointerException if deliveryDateTime is null
     */
    public static @NotNull ApplicantDeliveryDateTime parsePreValidated(@NotNull String deliveryDateTime) {
        Objects.requireNonNull(deliveryDateTime, "Delivery date cannot be null");
        return new ApplicantDeliveryDateTime(
                LocalDateTime.parse(deliveryDateTime, ISO_FORMATTER)
        );
    }

    /**
     * Checks if this date is on the same calendar day as another date.
     * @throws NullPointerException if other is null
     */
    public boolean isOnSameDate(@NotNull ApplicantDeliveryDateTime other) {
        Objects.requireNonNull(other, "Other delivery date cannot be null");
        return this.dateTime.toLocalDate().equals(other.dateTime.toLocalDate());
    }

    /**
     * Checks if time is at or after midday (12:00:00).
     */
    public boolean isAfterMidday() {
        return this.dateTime.getHour() >= 12;
    }

    @Override
    public int compareTo(@NotNull ApplicantDeliveryDateTime other) {
        Objects.requireNonNull(other, "Other delivery date cannot be null");
        return this.dateTime.compareTo(other.dateTime);
    }
}