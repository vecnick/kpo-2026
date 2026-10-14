package vyshkat.model;

/**
 * Общий контракт объектов с инвентарным номером.
 */
public interface InventoryItem {
    /**
     * Уникальный инвентарный номер объекта.
     *
     * @return уникальный инвентарный номер
     */
    String getInventoryNumber();

    /**
     * Название объекта для отображения в консоли.
     *
     * @return название для вывода пользователю
     */
    String getDisplayName();
}
