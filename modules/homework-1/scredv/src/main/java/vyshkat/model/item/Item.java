package vyshkat.model.item;

import lombok.Getter;
import lombok.experimental.SuperBuilder;
import vyshkat.model.InventoryItem;

/**
 * Базовый класс инвентаризируемых вещей, не являющихся транспортом.
 */
@Getter
@SuperBuilder
public abstract class Item implements InventoryItem {
    /** Уникальный инвентарный номер вещи. */
    private final String inventoryNumber;
    /** Наименование вещи. */
    private final String name;

    /**
     * Возвращает название вещи.
     * При переопределении в подклассе следует сохранять
     * человекочитаемый формат названия.
     *
     * @return название вещи
     */
    @Override
    public String getDisplayName() {
        return name;
    }

    /**
     * Представляет вещь в виде названия и номера.
     * При переопределении в подклассе рекомендуется
     * сохранять инвентарный номер в строке.
     *
     * @return строковое представление вещи
     */
    @Override
    public String toString() {
        return "%s [%s]".formatted(name, inventoryNumber);
    }
}
