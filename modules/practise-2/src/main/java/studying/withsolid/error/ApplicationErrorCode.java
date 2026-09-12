package studying.withsolid.error;

/**
 * Коды прикладных ошибок сервиса отчётов.
 */
public enum ApplicationErrorCode {
    /** Входные данные отсутствуют или имеют недопустимое значение. */
    VALIDATION_ERROR,

    /** Не удалось записать отчёт в файловую систему. */
    FILE_WRITE_ERROR
}
