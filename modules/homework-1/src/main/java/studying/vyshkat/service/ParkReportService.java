package studying.vyshkat.service;

import java.util.List;
import org.springframework.stereotype.Service;
import studying.vyshkat.model.EnergyConsumer;
import studying.vyshkat.model.InventoryItem;
import studying.vyshkat.model.RentalTransport;
import studying.vyshkat.repository.ParkRepository;

/** Builds reports for the active fleet and registered equipment. */
@Service
public final class ParkReportService {
    private final ParkRepository repository;

    /**
     * Creates a report service with fleet storage.
     *
     * @param parkRepository fleet storage
     */
    public ParkReportService(final ParkRepository parkRepository) {
        this.repository = parkRepository;
    }

    /** @return number of accepted transport units */
    public long transportCount() {
        return repository.findAll().stream()
                .filter(RentalTransport.class::isInstance)
                .count();
    }

    /** @return total daily consumption of all energy-consuming inventory */
    public double totalDailyEnergyConsumption() {
        return repository.findAll().stream()
                .filter(EnergyConsumer.class::isInstance)
                .map(EnergyConsumer.class::cast)
                .mapToDouble(EnergyConsumer::dailyEnergyConsumption)
                .sum();
    }

    /** @return accepted transport suitable for beginners */
    public List<RentalTransport> beginnerFriendlyTransport() {
        return repository.findAll().stream()
                .filter(RentalTransport.class::isInstance)
                .map(RentalTransport.class::cast)
                .filter(RentalTransport::isSuitableForBeginner)
                .toList();
    }

    /** @return immutable snapshot of transport and equipment */
    public List<InventoryItem> inventory() {
        return repository.findAll();
    }
}
