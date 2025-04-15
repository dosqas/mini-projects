package internship.applicantProcessor.repository;

import internship.applicantProcessor.model.Applicant;
import org.jetbrains.annotations.NotNull;
import java.util.*;

/**
 * Repository for storing and managing {@link Applicant} records using email as the unique key.
 * <p>
 * Key behavior:
 * <ul>
 *   <li><b>Last-seen-wins</b>: When multiple entries with the same email are processed,
 *       the last <em>valid</em> entry in the input file is retained</li>
 *   <li><b>Validation first</b>: Only valid entries can replace existing records</li>
 *   <li><b>Order-sensitive</b>: Duplicate resolution depends on processing order,
 *       not delivery timestamps</li>
 * </ul>
 *
 * <p><b>Not thread-safe</b>: Concurrent modifications may require external synchronization.
 */
public class ApplicantRepository {
    private final Map<String, Applicant> applicants;

    /**
     * Constructs an empty repository.
     */
    public ApplicantRepository() {
        this.applicants = new HashMap<>();
    }

    /**
     * Adds or updates an applicant in the repository.
     * <p>
     * If an applicant with the same email already exists, it will be replaced.
     *
     * @param applicant the applicant to add (must not be {@code null})
     * @throws NullPointerException if the applicant or their email is {@code null}
     */
    public void addApplicant(@NotNull Applicant applicant) {
        Objects.requireNonNull(applicant, "Applicant cannot be null");
        Objects.requireNonNull(applicant.email(), "Applicant email cannot be null");
        applicants.put(applicant.email(), applicant);
    }

    /**
     * Returns all applicants in the repository.
     * <p>
     * The returned collection is unmodifiable to maintain repository integrity.
     *
     * @return an unmodifiable view of all applicants
     */
    public @NotNull Collection<Applicant> getApplicants() {
        return Collections.unmodifiableCollection(applicants.values());
    }

    /**
     * Returns the count of unique applicants in the repository.
     * <p>
     * This count is based on unique email addresses.
     *
     * @return the number of unique applicants
     */
    public int getUniqueApplicantCount() {
        return applicants.size();
    }
}