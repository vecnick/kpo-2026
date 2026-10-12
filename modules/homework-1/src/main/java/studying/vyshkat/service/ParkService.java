package studying.vyshkat.service;

import org.springframework.stereotype.Service;
import studying.vyshkat.inspection.InspectionResult;
import studying.vyshkat.inspection.ServiceCenter;
import studying.vyshkat.model.Equipment;
import studying.vyshkat.model.RentalTransport;
import studying.vyshkat.repository.ParkRepository;

/** Coordinates transport admission and inventory registration. */
@Service
public final class ParkService {
    private final ParkRepository repository;
    private final ServiceCenter serviceCenter;

    /**
     * Creates a park service with supplied dependencies.
     *
     * @param parkRepository fleet storage
     * @param inspectionService service center
     */
    public ParkService(final ParkRepository parkRepository,
                       final ServiceCenter inspectionService) {
        this.repository = parkRepository;
        this.serviceCenter = inspectionService;
    }

    /**
     * Inspects and, when approved, registers transport in the fleet.
     *
     * @param transport transport to admit
     * @return inspection decision
     */
    public InspectionResult acceptTransport(
            final RentalTransport transport) {
        final InspectionResult result = serviceCenter.inspect(transport);
        if (result.accepted()) {
            repository.save(transport);
        }
        return result;
    }

    /**
     * Registers non-transport equipment on the company balance.
     *
     * @param equipment equipment to register
     */
    public void registerEquipment(final Equipment equipment) {
        repository.save(equipment);
    }
}
