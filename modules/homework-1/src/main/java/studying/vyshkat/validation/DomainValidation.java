package studying.vyshkat.validation;

import studying.vyshkat.exception.DomainValidationException;

/** Common validation rules for the domain model. */
public final class DomainValidation {
    /** Lowest allowed score. */
    public static final int MINIMUM_SCORE = 1;
    /** Highest allowed score. */
    public static final int MAXIMUM_SCORE = 10;

    private DomainValidation() {
    }

    /**
     * Checks that a required text value is present.
     *
     * @param value value to check
     * @param field field name for an error message
     */
    public static void requireText(final String value, final String field) {
        if (value == null || value.isBlank()) {
            throw new DomainValidationException(field + " не задано");
        }
    }

    /**
     * Checks that a score belongs to the supported range.
     *
     * @param value score to check
     * @param field field name for an error message
     */
    public static void requireScore(final int value, final String field) {
        if (value < MINIMUM_SCORE || value > MAXIMUM_SCORE) {
            throw new DomainValidationException(
                    field + " должно быть от " + MINIMUM_SCORE
                            + " до " + MAXIMUM_SCORE);
        }
    }

    /**
     * Checks that a numeric value is not negative.
     *
     * @param value value to check
     * @param field field name for an error message
     */
    public static void requireNonNegative(final double value,
                                          final String field) {
        if (value < 0) {
            throw new DomainValidationException(
                    field + " не может быть отрицательным");
        }
    }
}
