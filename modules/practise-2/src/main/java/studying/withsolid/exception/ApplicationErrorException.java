package studying.withsolid.exception;

/**
 * Устаревшее исключение, оставленное для обратной совместимости.
 *
 * @deprecated используйте {@link ApplicationException}
 */
@Deprecated
public class ApplicationErrorException extends ApplicationException {
    /**
     * Создаёт ошибку валидации с заданным сообщением.
     *
     * @param message описание ошибки
     * @deprecated используйте {@link ApplicationException}
     */
    @Deprecated
    public ApplicationErrorException(String message) {
        super(ApplicationErrorCode.VALIDATION_ERROR, message);
    }
}
