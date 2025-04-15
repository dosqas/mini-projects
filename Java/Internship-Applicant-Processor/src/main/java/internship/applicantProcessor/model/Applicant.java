package internship.applicantProcessor.model;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 * Immutable record representing a pre-validated internship applicant.
 */
public record Applicant(
        @NotNull ApplicantName name,
        @NotNull String email,
        @NotNull ApplicantDeliveryDateTime deliveryDateTime,
        double score
) {
    /**
     * Calculates adjusted score with bonus/malus rules:
     * - +1.0 if delivered on first day
     * - -1.0 if delivered in second half of last day
     * @return Score clamped between 0.0 and 10.0
     */
    public double calculateAdjustedScore(
            @NotNull ApplicantDeliveryDateTime earliestDelivery,
            @NotNull ApplicantDeliveryDateTime latestDelivery) {

        Objects.requireNonNull(earliestDelivery, "Earliest delivery cannot be null");
        Objects.requireNonNull(latestDelivery, "Latest delivery cannot be null");
        double adjusted = score;

        if (deliveryDateTime.isOnSameDate(earliestDelivery)) {
            adjusted += 1.0;
        }
        else if (deliveryDateTime.isOnSameDate(latestDelivery)
                && deliveryDateTime.isAfterMidday()) {
            adjusted -= 1.0;
        }

        return clampScore(adjusted);
    }

    /**
     * Clamps the score to be between 0 and 10
     * @param value the score to be clamped
     * @return The clamped score
     */
    private double clampScore(double value) {
        return Math.max(0.0, Math.min(10.0, value));
    }
}