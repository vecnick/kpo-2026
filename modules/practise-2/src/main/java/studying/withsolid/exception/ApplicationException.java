package studying.withsolid.exception;

public class ApplicationException extends RuntimeException {
    private final ApplicationErrorCode code;

    public ApplicationException(ApplicationErrorCode code, String message, Throwable cause) {
        super(message, cause);
        this.code = code;
    }

    public ApplicationException(ApplicationErrorCode code, String message) {
        super(message);
        this.code = code;
    }

    public ApplicationErrorCode getCode() {
        return code;
    }
}
