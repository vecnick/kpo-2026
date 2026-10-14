package vyshkat.model.item;

import lombok.Getter;
import lombok.experimental.SuperBuilder;
import vyshkat.model.EnergyConsumer;

/**
 * Зарядная станция, учитываемая как энергопотребляющая вещь.
 */
@Getter
@SuperBuilder
public final class ChargingStation extends Item implements EnergyConsumer {
    /** Суточное потребление энергии в кВт·ч. */
    private final double dailyEnergyConsumption;

    @Override
    public String toString() {
        return super.toString()
                + ", потребление: %.1f кВт·ч".formatted(dailyEnergyConsumption);
    }
}
