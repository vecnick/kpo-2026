package studying.vyshkat.console;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import studying.vyshkat.inspection.InspectionResult;
import studying.vyshkat.model.Bicycle;
import studying.vyshkat.model.ChargingStation;
import studying.vyshkat.model.ElectricBicycle;
import studying.vyshkat.model.ElectricScooter;
import studying.vyshkat.model.Helmet;
import studying.vyshkat.model.InventoryItem;
import studying.vyshkat.model.RentalTransport;
import studying.vyshkat.service.ParkReportService;
import studying.vyshkat.service.ParkService;

/** Demonstrates all required application scenarios in the console. */
@Component
@Profile("!test")
public final class ConsoleScenario implements CommandLineRunner {
    private static final double SCOOTER_ENERGY = 1.4;
    private static final double ELECTRIC_BICYCLE_ENERGY = 0.8;
    private static final double CHARGING_STATION_ENERGY = 2.2;
    private static final int GOOD_CONDITION = 9;
    private static final int ACCEPTABLE_CONDITION = 8;
    private static final int POOR_CONDITION = 4;
    private static final int EASY_FOR_BEGINNER = 8;
    private static final int EXPERIENCED_USER = 5;

    private final ParkService parkService;
    private final ParkReportService reportService;

    /**
     * Creates a console scenario with application services.
     *
     * @param fleetService fleet admission service
     * @param fleetReportService fleet reporting service
     */
    public ConsoleScenario(final ParkService fleetService,
                           final ParkReportService fleetReportService) {
        this.parkService = fleetService;
        this.reportService = fleetReportService;
    }

    @Override
    public void run(final String... args) {
        final ElectricScooter scooter = ElectricScooter.builder()
                .name("Электросамокат HSE S1")
                .inventoryNumber("TR-001")
                .dailyEnergyConsumption(SCOOTER_ENERGY)
                .beginnerEase(EASY_FOR_BEGINNER)
                .technicalCondition(GOOD_CONDITION)
                .build();
        final Bicycle bicycle = Bicycle.builder()
                .name("Городской велосипед")
                .inventoryNumber("TR-002")
                .beginnerEase(EASY_FOR_BEGINNER)
                .technicalCondition(POOR_CONDITION)
                .build();
        final ElectricBicycle electricBicycle = ElectricBicycle.builder()
                .name("Электровелосипед HSE E1")
                .inventoryNumber("TR-003")
                .dailyEnergyConsumption(ELECTRIC_BICYCLE_ENERGY)
                .beginnerEase(EXPERIENCED_USER)
                .technicalCondition(ACCEPTABLE_CONDITION)
                .build();

        printInspection(scooter,
                parkService.acceptTransport(scooter));
        printInspection(bicycle,
                parkService.acceptTransport(bicycle));
        printInspection(electricBicycle,
                parkService.acceptTransport(electricBicycle));

        parkService.registerEquipment(Helmet.builder()
                .name("Защитный шлем")
                .inventoryNumber("EQ-001")
                .build());
        parkService.registerEquipment(ChargingStation.builder()
                .name("Зарядная станция")
                .inventoryNumber("EQ-002")
                .dailyEnergyConsumption(CHARGING_STATION_ENERGY)
                .build());

        printReports();
    }

    private void printInspection(final RentalTransport transport,
                                 final InspectionResult result) {
        System.out.printf("Техосмотр: %s [%s] - %s%n",
                transport.name(), transport.inventoryNumber(), result.reason());
    }

    private void printReports() {
        System.out.printf("%nТранспорт в активном парке: %d%n",
                reportService.transportCount());
        System.out.printf("Суточное энергопотребление: %.1f кВт·ч%n",
                reportService.totalDailyEnergyConsumption());

        System.out.println("Транспорт для новичков:");
        reportService.beginnerFriendlyTransport().forEach(transport ->
                System.out.printf("- %s [%s]%n", transport.name(),
                        transport.inventoryNumber()));

        System.out.println("Вся техника и вещи на балансе:");
        reportService.inventory().forEach(this::printInventoryItem);
    }

    private void printInventoryItem(final InventoryItem item) {
        System.out.printf("- %s [%s]%n",
                item.name(), item.inventoryNumber());
    }
}
