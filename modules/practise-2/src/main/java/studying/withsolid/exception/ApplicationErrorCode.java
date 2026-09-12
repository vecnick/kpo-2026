package studying.withsolid.exception;

/**
 * Коды прикладных ошибок сервиса отчётов.
 */
public enum ApplicationErrorCode {
    /** Переданы отсутствующие или недопустимые данные. */
    VALIDATION_ERROR,

    /** Не удалось записать отчёт в файловую систему. */
    FILE_WRITE_ERROR
}
