package vyshkat.model;

import lombok.Getter;
import lombok.experimental.SuperBuilder;

/**
 * Электросамокат с учётом суточного энергопотребления.
 */
@Getter
@SuperBuilder
public final class ElectricScooter extends AbstractTransport
        implements EnergyConsumer {
    /** Суточное потребление энергии в кВт·ч. */
    private final double dailyEnergyConsumption;

    @Override
    public String toString() {
        return super.toString()
                + ", потребление: %.1f кВт·ч".formatted(dailyEnergyConsumption);
    }
}
