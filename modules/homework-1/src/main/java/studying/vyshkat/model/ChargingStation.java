package studying.vyshkat.model;

import lombok.Builder;
import studying.vyshkat.validation.DomainValidation;

/**
 * Charging station registered as energy-consuming inventory.
 *
 * @param name human-readable station name
 * @param inventoryNumber unique inventory number
 * @param dailyEnergyConsumption daily consumption in kilowatt-hours
 */
@Builder
public record ChargingStation(
        String name,
        String inventoryNumber,
        double dailyEnergyConsumption
) implements Equipment, EnergyConsumer {
    /** Validates charging station data. */
    public ChargingStation {
        DomainValidation.requireText(name, "Наименование");
        DomainValidation.requireText(inventoryNumber, "Инвентарный номер");
        DomainValidation.requireNonNegative(
                dailyEnergyConsumption, "Суточное энергопотребление");
    }
}
