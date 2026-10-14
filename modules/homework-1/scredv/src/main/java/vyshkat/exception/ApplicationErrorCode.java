package vyshkat.exception;

/**
 * Коды ошибок, возникающих при выполнении операций парка.
 */
public enum ApplicationErrorCode {
    /**
     * Некорректные входные данные.
     */
    VALIDATION_ERROR,
    /**
     * Транспорт не прошёл техосмотр.
     */
    TRANSPORT_REJECTED,
    /**
     * Инвентарный номер уже используется.
     */
    DUPLICATE_INVENTORY_NUMBER,
}
