package studying.withsolid.error;

import lombok.Getter;

import java.io.Serial;
import java.util.Objects;

/**
 * Прикладное исключение с машиночитаемым кодом, понятным сообщением
 * и необязательной исходной причиной.
 */
public final class ApplicationException extends RuntimeException {
    /** Версия сериализованного представления исключения. */
    @Serial
    private static final long serialVersionUID = 1L;

    /** Машиночитаемый код прикладной ошибки. */
    @Getter
    private final ApplicationErrorCode code;

    /**
     * Создаёт прикладное исключение без исходной причины.
     *
     * @param code код ошибки
     * @param message понятное описание ошибки
     */
    public ApplicationException(ApplicationErrorCode code, String message) {
        super(message);
        this.code = Objects.requireNonNull(code, "Код ошибки не должен отсутствовать");
    }

    /**
     * Создаёт прикладное исключение, сохраняя исходную причину.
     *
     * @param code код ошибки
     * @param message понятное описание ошибки
     * @param cause исходная причина ошибки
     */
    public ApplicationException(ApplicationErrorCode code, String message, Throwable cause) {
        super(message, cause);
        this.code = Objects.requireNonNull(code, "Код ошибки не должен отсутствовать");
    }

}
