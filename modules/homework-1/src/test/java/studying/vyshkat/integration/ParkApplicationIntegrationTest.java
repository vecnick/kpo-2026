package studying.vyshkat.integration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import studying.Main;
import studying.vyshkat.inspection.InspectionResult;
import studying.vyshkat.inspection.ServiceCenter;
import studying.vyshkat.model.ElectricScooter;
import studying.vyshkat.service.ParkReportService;
import studying.vyshkat.service.ParkService;

/** Integration tests for Spring dependency injection and fleet admission. */
@SpringBootTest(classes = Main.class)
@ActiveProfiles("test")
@DisplayName("Интеграционные тесты парка")
class ParkApplicationIntegrationTest {
    @Autowired
    private ParkService parkService;

    @Autowired
    private ParkReportService reportService;

    @MockitoBean
    private ServiceCenter serviceCenter;

    @Test
    @DisplayName("Подменённый техосмотр управляет приёмкой через DI")
    void mockedServiceCenterControlsAdmission() {
        final ElectricScooter scooter = ElectricScooter.builder()
                .name("Электросамокат")
                .inventoryNumber("TR-INTEGRATION-001")
                .dailyEnergyConsumption(1.5)
                .beginnerEase(7)
                .technicalCondition(1)
                .build();
        when(serviceCenter.inspect(scooter))
                .thenReturn(InspectionResult.approved());

        final InspectionResult result =
                parkService.acceptTransport(scooter);

        assertTrue(result.accepted());
        assertEquals(1, reportService.transportCount());
        assertEquals(1.5, reportService.totalDailyEnergyConsumption(), 0.001);
    }
}
