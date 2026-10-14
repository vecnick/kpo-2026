package vyshkat.model;

/**
 * Контракт объекта, потребляющего электроэнергию.
 */
public interface EnergyConsumer {
    /**
     * Суточное потребление энергии в кВт·ч.
     *
     * @return суточное потребление энергии в кВт·ч
     */
    double getDailyEnergyConsumption();
}
