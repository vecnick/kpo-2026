package studying.vyshkat.model;

import lombok.Builder;
import studying.vyshkat.validation.DomainValidation;

/**
 * Electric bicycle registered in the rental fleet.
 *
 * @param name human-readable bicycle name
 * @param inventoryNumber unique inventory number
 * @param dailyEnergyConsumption daily consumption in kilowatt-hours
 * @param beginnerEase beginner-friendliness score
 * @param technicalCondition technical condition score
 */
@Builder
public record ElectricBicycle(
        String name,
        String inventoryNumber,
        double dailyEnergyConsumption,
        int beginnerEase,
        int technicalCondition
) implements RentalTransport, EnergyConsumer {
    /** Validates electric bicycle data. */
    public ElectricBicycle {
        DomainValidation.requireText(name, "Наименование");
        DomainValidation.requireText(inventoryNumber, "Инвентарный номер");
        DomainValidation.requireNonNegative(
                dailyEnergyConsumption, "Суточное энергопотребление");
        DomainValidation.requireScore(
                beginnerEase, "Простота для новичка");
        DomainValidation.requireScore(
                technicalCondition, "Техническое состояние");
    }
}
