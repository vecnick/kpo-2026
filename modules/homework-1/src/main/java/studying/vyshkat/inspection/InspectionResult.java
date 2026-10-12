package studying.vyshkat.inspection;

import studying.vyshkat.validation.DomainValidation;

/**
 * Result returned by the service center after an inspection.
 *
 * @param accepted whether the transport passed inspection
 * @param reason human-readable inspection decision
 */
public record InspectionResult(boolean accepted, String reason) {
    /** Validates the inspection result. */
    public InspectionResult {
        DomainValidation.requireText(reason, "Причина решения техосмотра");
    }

    /**
     * Creates a successful inspection result.
     *
     * @return successful result
     */
    public static InspectionResult approved() {
        return new InspectionResult(true, "Техника прошла техосмотр");
    }

    /**
     * Creates a rejected inspection result.
     *
     * @param reason rejection reason
     * @return rejected result
     */
    public static InspectionResult rejected(final String reason) {
        return new InspectionResult(false, reason);
    }
}
