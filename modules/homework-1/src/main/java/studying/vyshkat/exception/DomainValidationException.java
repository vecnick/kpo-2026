package studying.vyshkat.exception;

/** Raised when a domain object contains invalid data. */
public final class DomainValidationException extends IllegalArgumentException {
    /**
     * Creates an exception with a validation error description.
     *
     * @param message validation error description
     */
    public DomainValidationException(final String message) {
        super(message);
    }
}
