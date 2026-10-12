package studying.vyshkat.model;

/** Contract for devices that consume energy. */
public interface EnergyConsumer {
    /** @return daily energy consumption in kilowatt-hours */
    double dailyEnergyConsumption();
}
