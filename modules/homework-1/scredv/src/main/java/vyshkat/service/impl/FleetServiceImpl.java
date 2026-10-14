package vyshkat.service.impl;

import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import vyshkat.exception.ApplicationErrorCode;
import vyshkat.exception.ApplicationException;
import vyshkat.model.EnergyConsumer;
import vyshkat.model.InventoryItem;
import vyshkat.model.Transport;
import vyshkat.model.item.Item;
import vyshkat.service.FleetService;
import vyshkat.service.ServiceCenter;

/**
 * Управляет парком и вещами в памяти приложения.
 */
@RequiredArgsConstructor
public final class FleetServiceImpl implements FleetService {
    /**
     * Порог удобства транспорта для новичков.
     */
    private static final int NOVICE_SIMPLICITY_THRESHOLD = 6;
    /**
     * Нижняя включённая граница оценки удобства.
     */
    private static final int MIN_SIMPLICITY = 1;
    /**
     * Верхняя включённая граница оценки удобства.
     */
    private static final int MAX_SIMPLICITY = 10;

    /** Сервис проверки транспорта перед приёмкой. */
    private final ServiceCenter serviceCenter;
    /** Принятый транспорт парка. */
    private final List<Transport> fleet = new ArrayList<>();
    /** Вещи, состоящие на балансе. */
    private final List<Item> items = new ArrayList<>();

    @Override
    public void addTransport(final Transport transport) {
        validateInventoryNumber(transport.getInventoryNumber());
        validateSimplicity(transport.getNoviceFriendliness());
        validateConsumption(transport);

        if (inventoryNumberExists(transport.getInventoryNumber())) {
            throw new ApplicationException(
                    ApplicationErrorCode.DUPLICATE_INVENTORY_NUMBER,
                    "Инвентарный номер %s уже занят"
                            .formatted(transport.getInventoryNumber()));
        }

        if (!serviceCenter.inspect(transport)) {
            throw new ApplicationException(
                    ApplicationErrorCode.TRANSPORT_REJECTED,
                    "Транспорт %s не прошёл техосмотр"
                            .formatted(transport.getDisplayName()));
        }
        fleet.add(transport);
    }

    @Override
    public void addItem(final Item item) {
        validateInventoryNumber(item.getInventoryNumber());
        validateConsumption(item);

        if (inventoryNumberExists(item.getInventoryNumber())) {
            throw new ApplicationException(
                    ApplicationErrorCode.DUPLICATE_INVENTORY_NUMBER,
                    "Инвентарный номер %s уже занят"
                            .formatted(item.getInventoryNumber()));
        }
        items.add(item);
    }

    @Override
    public double getTotalDailyEnergyConsumption() {
        double total = 0.0;
        for (final Transport transport : fleet) {
            if (transport instanceof EnergyConsumer consumer) {
                total += consumer.getDailyEnergyConsumption();
            }
        }
        for (final Item item : items) {
            if (item instanceof EnergyConsumer consumer) {
                total += consumer.getDailyEnergyConsumption();
            }
        }
        return total;
    }

    @Override
    public List<Transport> getNoviceFriendlyTransport() {
        return fleet.stream()
                .filter(t -> t.getNoviceFriendliness()
                        >= NOVICE_SIMPLICITY_THRESHOLD)
                .toList();
    }

    @Override
    public List<InventoryItem> getAllInventoryItems() {
        final List<InventoryItem> all = new ArrayList<>(fleet);
        all.addAll(items);
        return all;
    }

    private void validateInventoryNumber(final String inventoryNumber) {
        if (inventoryNumber == null || inventoryNumber.isBlank()) {
            throw new ApplicationException(
                    ApplicationErrorCode.VALIDATION_ERROR,
                    "Инвентарный номер не может быть пустым");
        }
    }

    private void validateSimplicity(final int simplicity) {
        if (simplicity < MIN_SIMPLICITY || simplicity > MAX_SIMPLICITY) {
            throw new ApplicationException(
                    ApplicationErrorCode.VALIDATION_ERROR,
                    "Простота для новичка должна быть "
                            + "в диапазоне %d-%d, получено: %d"
                            .formatted(MIN_SIMPLICITY, MAX_SIMPLICITY,
                                    simplicity));
        }
    }

    private void validateConsumption(final InventoryItem item) {
        if (item instanceof EnergyConsumer consumer) {
            double consumption = consumer.getDailyEnergyConsumption();

            if (!Double.isFinite(consumption) || consumption < 0) {
                throw new ApplicationException(
                        ApplicationErrorCode.VALIDATION_ERROR,
                        "Суточное потребление должно быть "
                                + "конечным неотрицательным числом"
                );
            }
        }
    }

    private boolean inventoryNumberExists(final String inventoryNumber) {
        return fleet.stream()
                .anyMatch(t -> t.getInventoryNumber().equals(inventoryNumber))
                || items.stream()
                .anyMatch(i -> i.getInventoryNumber().equals(inventoryNumber));
    }
}
