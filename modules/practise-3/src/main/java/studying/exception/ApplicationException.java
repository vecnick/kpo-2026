package studying.exception;

public class ApplicationException extends RuntimeException {
    private final ApplicationErrorCode code;

    public ApplicationException(final ApplicationErrorCode errorCode,
                                final String message,
                                final Throwable cause) {
        super(message, cause);
        this.code = errorCode;
    }

    public ApplicationException(final ApplicationErrorCode errorCode,
                                final String message) {
        super(message);
        this.code = errorCode;
    }

    public ApplicationErrorCode getCode() {
        return code;
    }
}
