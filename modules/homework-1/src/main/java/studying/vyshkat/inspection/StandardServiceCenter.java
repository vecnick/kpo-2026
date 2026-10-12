package studying.vyshkat.inspection;

import org.springframework.stereotype.Service;
import studying.vyshkat.model.RentalTransport;

/** Service center that accepts transport in sufficient technical condition. */
@Service
public final class StandardServiceCenter implements ServiceCenter {
    private static final int MINIMUM_TECHNICAL_CONDITION = 7;

    @Override
    public InspectionResult inspect(final RentalTransport transport) {
        if (transport.technicalCondition()
                < MINIMUM_TECHNICAL_CONDITION) {
            return InspectionResult.rejected(
                    "Техническое состояние ниже допустимого уровня");
        }
        return InspectionResult.approved();
    }
}
