package studying.vyshkat.inspection;

import studying.vyshkat.model.RentalTransport;

/** Inspects transport before it enters the active fleet. */
public interface ServiceCenter {
    /**
     * Inspects a transport unit.
     *
     * @param transport transport to inspect
     * @return inspection result
     */
    InspectionResult inspect(RentalTransport transport);
}
