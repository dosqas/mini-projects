package internship.applicantProcessor.model;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import java.util.List;
import java.util.Objects;

/**
 * Immutable record representing a pre-validated applicant's name.
 * Assumes input follows strict format: "FirstName [MiddleNames...] LastName"
 */
public record ApplicantName(
        @NotNull String firstName,
        @Nullable List<String> middleNames,  // null when no middle names exist
        @NotNull String lastName
) {
    /**
     * Efficiently parses pre-validated full name string.
     * @param fullName Guaranteed to be non-null, non-empty, and contain ≥2 names
     * @return Parsed name components
     */
    public static ApplicantName parsePreValidated(@NotNull String fullName) {
        Objects.requireNonNull(fullName, "Full name cannot be null");
        String[] parts = fullName.trim().split("\\s+");  // Splits by one or more spaces

        return new ApplicantName(
                parts[0],
                parts.length > 2 ? List.of(parts).subList(1, parts.length - 1) : null,
                parts[parts.length - 1]
        );
    }
}