package studying.withsolid.exception;

import lombok.Getter;

/**
 * Runtime exception that carries an application error code and optional cause.
 */
@Getter
public class ApplicationException extends RuntimeException {
    private final ApplicationErrorCode code;

    /**
     * Creates an exception without a nested cause.
     *
     * @param code    application error code
     * @param message human-readable description
     */
    public ApplicationException(ApplicationErrorCode code, String message) {
        super(message);
        this.code = code;
    }

    /**
     * Creates an exception that preserves the original failure.
     *
     * @param code    application error code
     * @param message human-readable description
     * @param cause   original exception
     */
    public ApplicationException(ApplicationErrorCode code, String message, Throwable cause) {
        super(message, cause);
        this.code = code;
    }
}
