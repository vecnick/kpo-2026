package studying.withsolid.exception;

import lombok.Getter;

/**
 * Базовое прикладное исключение сервиса отчётов.
 * <p>
 * Наследуется от {@link RuntimeException}, поэтому не требует
 * объявления {@code throws} в сигнатурах методов контрактов
 * {@code ReportSaver} и {@code ReportSender}. Хранит код ошибки
 * ({@link ApplicationErrorCode}) для программной обработки и, при
 * наличии, исходную причину сбоя — например, {@code IOException},
 * возникшее при записи файла.
 */
@Getter
public class ApplicationException extends RuntimeException {

    /**
     * Код прикладной ошибки, позволяющий отличать сценарии сбоя
     * друг от друга без анализа текста сообщения.
     */
    private final ApplicationErrorCode code;

    /**
     * Создаёт исключение без указания исходной причины — используется,
     * когда ошибка возникла на уровне валидации, а не как следствие
     * другого исключения (например, отчёт или email не переданы).
     *
     * @param code    код прикладной ошибки
     * @param message сообщение об ошибке, понятное человеку
     */
    public ApplicationException(ApplicationErrorCode code, String message) {
        super(message);
        this.code = code;
    }

    /**
     * Создаёт исключение с указанием исходной причины сбоя.
     * <p>
     * Используется, когда ошибка возникла как следствие другого
     * исключения (например, {@code IOException} при записи файла) —
     * причина сохраняется, чтобы не терять полную информацию для отладки.
     *
     * @param code    код прикладной ошибки
     * @param message сообщение об ошибке, понятное человеку
     * @param cause   исходное исключение, ставшее причиной сбоя
     */
    public ApplicationException(ApplicationErrorCode code, String message, Throwable cause) {
        super(message, cause);
        this.code = code;
    }
}