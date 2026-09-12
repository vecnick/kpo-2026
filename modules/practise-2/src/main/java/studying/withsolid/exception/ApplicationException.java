package studying.withsolid.exception;

/**
 * Исключение прикладного уровня с машиночитаемым кодом ошибки.
 */
public class ApplicationException extends RuntimeException {
    private final ApplicationErrorCode code;

    /**
     * Создаёт прикладное исключение без исходной причины.
     *
     * @param code код ошибки
     * @param message понятное пользователю описание ошибки
     */
    public ApplicationException(ApplicationErrorCode code, String message) {
        super(message);
        this.code = code;
    }

    /**
     * Создаёт прикладное исключение, сохраняя исходную причину сбоя.
     *
     * @param code код ошибки
     * @param message понятное пользователю описание ошибки
     * @param cause исходное исключение
     */
    public ApplicationException(ApplicationErrorCode code, String message, Throwable cause) {
        super(message, cause);
        this.code = code;
    }

    /**
     * Возвращает код прикладной ошибки.
     *
     * @return код ошибки
     */
    public ApplicationErrorCode getCode() {
        return code;
    }
}
