package vyshkat.service;

import java.util.List;
import vyshkat.model.InventoryItem;
import vyshkat.model.Transport;
import vyshkat.model.item.Item;

/**
 * Операции по приёмке транспорта, учёту вещей и получению отчётов.
 */
public interface FleetService {
    /**
     * Добавляет транспорт после проверки данных и техосмотра.
     *
     * @param transport принимаемый транспорт
     * @throws vyshkat.exception.ApplicationException при ошибке данных,
     *         повторном номере или отказе техосмотра
     */
    void addTransport(Transport transport);

    /**
     * Добавляет вещь в инвентарь.
     *
     * @param item учитываемая вещь
     * @throws vyshkat.exception.ApplicationException при ошибке данных
     *         или повторном инвентарном номере
     */
    void addItem(Item item);

    /**
     * Суммарное суточное потребление энергии всеми объектами в кВт·ч.
     *
     * @return суммарное потребление энергии в кВт·ч
     */
    double getTotalDailyEnergyConsumption();

    /**
     * Возвращает транспорт с оценкой удобства для новичков от 6.
     *
     * @return транспорт, подходящий новичкам
     */
    List<Transport> getNoviceFriendlyTransport();

    /**
     * Возвращает список транспорта и вещей, состоящих на балансе.
     *
     * @return все транспортные средства и вещи
     */
    List<InventoryItem> getAllInventoryItems();
}
