package studying.withsolid.exception;

public class ApplicationException extends RuntimeException {
    private final ApplicationErrorCode errorCode;

    /**
     * Creates an application exception with an error code and an optional root cause.
     *
     * @param errorCode machine-readable application error code
     * @param message human-readable description of the error
     * @param cause original exception that caused the error, or {@code null}
     */
    public ApplicationException(ApplicationErrorCode errorCode, String message, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
    }

    /**
     * Returns the application-specific error code.
     *
     * @return error code associated with this exception
     */
    public ApplicationErrorCode getCode() {
        return errorCode;
    }
}
