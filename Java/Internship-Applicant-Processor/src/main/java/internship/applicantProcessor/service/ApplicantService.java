package internship.applicantProcessor.service;

import internship.applicantProcessor.model.Applicant;
import internship.applicantProcessor.model.ApplicantDeliveryDateTime;
import internship.applicantProcessor.repository.ApplicantRepository;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

/**
 * Service layer for processing applicant data and generating statistics.
 * Handles business logic including score adjustments and ranking calculations.
 */
public class ApplicantService {
    private static final int TOP_APPLICANT_COUNT = 3;
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    private final ApplicantRepository applicantRepository;

    /**
     * Creates a new ApplicantService with the specified repository.
     * @param applicantRepository The repository for applicant data access
     */
    public ApplicantService(@NotNull ApplicantRepository applicantRepository) {
        Objects.requireNonNull(applicantRepository, "applicantRepository must not be null");
        this.applicantRepository = applicantRepository;
    }

    /**
     * Adds an applicant to the repository.
     * @param applicant The applicant to add (must not be null)
     */
    public void addApplicantToRepository(@NotNull Applicant applicant) {
        Objects.requireNonNull(applicant, "Applicant cannot be null");
        this.applicantRepository.addApplicant(applicant);
    }

    /**
     * Finds the earliest delivery date among all applicants.
     * @return The earliest delivery date, or null if no applicants exist
     */
    public @Nullable ApplicantDeliveryDateTime findEarliestDeliveryDate() {
        return applicantRepository.getApplicants().stream()
                .map(Applicant::deliveryDateTime)
                .min(ApplicantDeliveryDateTime::compareTo)
                .orElse(null);
    }

    /**
     * Finds the latest delivery date among all applicants.
     * @return The latest delivery date, or null if no applicants exist
     */
    public @Nullable ApplicantDeliveryDateTime findLatestDeliveryDate() {
        return applicantRepository.getApplicants().stream()
                .map(Applicant::deliveryDateTime)
                .max(ApplicantDeliveryDateTime::compareTo)
                .orElse(null);
    }

    /**
     * Gets top applicants sorted by:
     * 1. Adjusted score (descending) - includes bonus/malus adjustments
     * 2. Original score (descending) - tiebreaker for same adjusted score
     * 3. Delivery time (ascending) - earlier deliveries rank higher
     * 4. Email (ascending) - alphabetical order as final tiebreaker
     * Score adjustments:
     * - +1.0 bonus if delivered on first day (earliest date in dataset)
     * - -1.0 penalty if delivered in second half (≥12:00:00) of last day (latest date)
     * - No adjustments if all applicants delivered on same day
     */
    private @NotNull List<Applicant> getTopApplicants() {
        ApplicantDeliveryDateTime earliest = findEarliestDeliveryDate();
        ApplicantDeliveryDateTime latest = findLatestDeliveryDate();

        if (earliest == null || latest == null || earliest.isOnSameDate(latest)) {
            // No adjustments if all delivered same day or no applicants
            return applicantRepository.getApplicants().stream()
                    .sorted(Comparator.comparingDouble(Applicant::score).reversed())
                    .limit(TOP_APPLICANT_COUNT)
                    .toList();
        }

        return applicantRepository.getApplicants().stream()
                .sorted(createApplicantComparator(earliest, latest))
                .limit(TOP_APPLICANT_COUNT)
                .toList();
    }

    /**
     * Creates comparator for sorting applicants by:
     * 1. Adjusted score (descending)
     * 2. Original score (descending)
     * 3. Delivery time (ascending)
     * 4. Email (ascending)
     */
    private @NotNull Comparator<Applicant> createApplicantComparator(
            @NotNull ApplicantDeliveryDateTime earliest,
            @NotNull ApplicantDeliveryDateTime latest) {
        return Comparator.comparingDouble((Applicant a) ->
                        a.calculateAdjustedScore(earliest, latest)).reversed()
                .thenComparing(Comparator.comparingDouble(Applicant::score).reversed())
                .thenComparing(Applicant::deliveryDateTime)
                .thenComparing(Applicant::email);
    }

    /**
     * Gets last names of top applicants.
     * @return List of last names ordered by ranking
     */
    public @NotNull List<String> getTopApplicantsLastNames() {
        return getTopApplicants().stream()
                .map(applicant -> applicant.name().lastName())
                .toList();
    }

    /**
     * Calculates average score of top half applicants (before score adjustments).
     * For odd numbers of applicants, the top half includes the middle applicant.
     * Uses half-up rounding to 2 decimal places.
     * Example:
     * - 5 applicants → top 3 scores averaged
     * - 6 applicants → top 3 scores averaged
     */
    public double getAverageScoreOfTopHalf() {
        List<Applicant> sortedApplicants = applicantRepository.getApplicants().stream()
                .sorted(Comparator.comparingDouble(Applicant::score).reversed())
                .toList();

        if (sortedApplicants.isEmpty()) {
            return 0.0;
        }

        // For odd counts, (n + 1)/2 gives the larger half (e.g., 5 → 3)
        int topHalfSize = (sortedApplicants.size() + 1) / 2;
        return sortedApplicants.stream()
                .limit(topHalfSize)
                .mapToDouble(Applicant::score)
                .average()
                .orElse(0.0);
    }

    /**
     * Generates JSON output containing statistics.
     * @return Formatted JSON string with:
     *         - uniqueApplicants: count
     *         - topApplicants: array of last names
     *         - averageScore: rounded to 2 decimals
     */
    public @NotNull String getJsonFormatOutput() {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("uniqueApplicants", applicantRepository.getUniqueApplicantCount());
        jsonObject.add("topApplicants", createTopApplicantsArray());
        jsonObject.addProperty("averageScore", getAverageScoreOfTopHalf());
        return GSON.toJson(jsonObject);
    }

    private @NotNull JsonArray createTopApplicantsArray() {
        JsonArray array = new JsonArray();
        getTopApplicantsLastNames().forEach(array::add);
        return array;
    }
}