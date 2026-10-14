package vyshkat.service.impl;

import vyshkat.model.Transport;
import vyshkat.model.EnergyConsumer;
import vyshkat.service.ServiceCenter;

/**
 * Техосмотр: простота не ниже 3, потребление не выше 150 кВт·ч.
 */
public final class ServiceCenterImpl implements ServiceCenter {
    /** Минимальная оценка удобства для прохождения техосмотра. */
    private static final int MIN_NOVICE_FRIENDLINESS = 3;
    /** Максимальное допустимое суточное потребление энергии. */
    private static final double MAX_DAILY_CONSUMPTION = 150.0;

    @Override
    public boolean inspect(final Transport transport) {
        if (transport.getNoviceFriendliness() < MIN_NOVICE_FRIENDLINESS) {
            return false;
        }
        return !(transport instanceof EnergyConsumer consumer)
                || consumer.getDailyEnergyConsumption()
                        <= MAX_DAILY_CONSUMPTION;
    }
}
