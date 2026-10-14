package vyshkat.model;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

/**
 * Общие данные транспорта: номер, модель и удобство для новичков.
 */
@Getter
@SuperBuilder
public abstract class AbstractTransport implements Transport {
    /** Уникальный инвентарный номер транспорта. */
    private final String inventoryNumber;

    /** Наименование модели транспорта. */
    @Getter(AccessLevel.NONE)
    private final String modelName;

    /** Удобство для новичков по шкале от 1 до 10. */
    private final int noviceFriendliness;

    /**
     * Возвращает название модели транспорта.
     * Подклассы могут переопределять метод, сохраняя
     * человекочитаемое название.
     *
     * @return название модели
     */
    @Override
    public String getDisplayName() {
        return modelName;
    }

    /**
     * Представляет транспорт с номером и оценкой удобства.
     * При переопределении в подклассе рекомендуется
     * сохранять общие сведения о транспорте.
     *
     * @return строковое представление транспорта
     */
    @Override
    public String toString() {
        return "%s [%s], простота для новичка: %d"
                .formatted(modelName, inventoryNumber, noviceFriendliness);
    }
}
