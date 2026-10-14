package vyshkat.exception;

import lombok.Getter;

/**
 * Ошибка приложения с кодом, сообщением и возможной причиной.
 */
@Getter
public class ApplicationException extends RuntimeException {
    /**
     * Код ошибки приложения.
     */
    private final ApplicationErrorCode code;

    /**
     * Создаёт ошибку приложения.
     *
     * @param errorCode код ошибки
     * @param message описание ошибки
     */
    public ApplicationException(final ApplicationErrorCode errorCode,
                                final String message) {
        super(message);
        this.code = errorCode;
    }

    /**
     * Создаёт ошибку приложения с исходной причиной.
     *
     * @param errorCode код ошибки
     * @param message описание ошибки
     * @param cause исходная причина ошибки
     */
    public ApplicationException(final ApplicationErrorCode errorCode,
                                final String message,
                                final Throwable cause) {
        super(message, cause);
        this.code = errorCode;
    }
}
