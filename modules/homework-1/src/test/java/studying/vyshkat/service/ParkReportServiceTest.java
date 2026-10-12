package studying.vyshkat.service;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import studying.vyshkat.model.Bicycle;
import studying.vyshkat.model.ChargingStation;
import studying.vyshkat.model.ElectricScooter;
import studying.vyshkat.model.Helmet;
import studying.vyshkat.model.RentalTransport;
import studying.vyshkat.repository.InMemoryParkRepository;

/** Unit tests for fleet reports. */
@DisplayName("Тесты отчётов парка")
class ParkReportServiceTest {
    private InMemoryParkRepository repository;
    private ParkReportService reportService;

    @BeforeEach
    void setUp() {
        repository = new InMemoryParkRepository();
        reportService = new ParkReportService(repository);
    }

    @Test
    @DisplayName("Отчёт считает транспорт и всё энергопотребление")
    void countsTransportAndEnergyConsumers() {
        repository.save(scooter("TR-001", 8));
        repository.save(bicycle("TR-002", 5));
        repository.save(ChargingStation.builder()
                .name("Зарядная станция")
                .inventoryNumber("EQ-001")
                .dailyEnergyConsumption(2.2)
                .build());
        repository.save(Helmet.builder()
                .name("Шлем")
                .inventoryNumber("EQ-002")
                .build());

        assertEquals(2, reportService.transportCount());
        assertEquals(3.6, reportService.totalDailyEnergyConsumption(), 0.001);
        assertEquals(4, reportService.inventory().size());
    }

    @Test
    @DisplayName("Для новичков выбирается транспорт с простотой не ниже шести")
    void selectsBeginnerFriendlyTransport() {
        final ElectricScooter suitable = scooter("TR-001", 6);
        final Bicycle advanced = bicycle("TR-002", 5);
        repository.save(suitable);
        repository.save(advanced);

        final List<RentalTransport> result =
                reportService.beginnerFriendlyTransport();

        assertEquals(List.of(suitable), result);
    }

    private ElectricScooter scooter(final String inventoryNumber,
                                    final int beginnerEase) {
        return ElectricScooter.builder()
                .name("Электросамокат")
                .inventoryNumber(inventoryNumber)
                .dailyEnergyConsumption(1.4)
                .beginnerEase(beginnerEase)
                .technicalCondition(9)
                .build();
    }

    private Bicycle bicycle(final String inventoryNumber,
                            final int beginnerEase) {
        return Bicycle.builder()
                .name("Велосипед")
                .inventoryNumber(inventoryNumber)
                .beginnerEase(beginnerEase)
                .technicalCondition(8)
                .build();
    }
}
