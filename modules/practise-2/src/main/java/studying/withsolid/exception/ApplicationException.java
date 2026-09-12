package studying.withsolid.exception;

/** An application-level error with a stable machine-readable code. */
public class ApplicationException extends RuntimeException {
    private final ApplicationErrorCode code;

    /**
     * Creates an application error without an underlying cause.
     *
     * @param code error code
     * @param message human-readable error description
     */
    public ApplicationException(ApplicationErrorCode code, String message) {
        super(message);
        this.code = code;
    }

    /**
     * Creates an application error and preserves its underlying cause.
     *
     * @param code error code
     * @param message human-readable error description
     * @param cause underlying cause
     */
    public ApplicationException(ApplicationErrorCode code, String message, Throwable cause) {
        super(message, cause);
        this.code = code;
    }

    /**
     * Returns the machine-readable error code.
     *
     * @return error code
     */
    public ApplicationErrorCode getCode() {
        return code;
    }
}
