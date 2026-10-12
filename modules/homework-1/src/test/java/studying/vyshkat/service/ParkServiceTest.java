package studying.vyshkat.service;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import studying.vyshkat.inspection.InspectionResult;
import studying.vyshkat.inspection.ServiceCenter;
import studying.vyshkat.model.ElectricScooter;
import studying.vyshkat.model.Helmet;
import studying.vyshkat.repository.ParkRepository;

/** Unit tests for transport admission and equipment registration. */
@ExtendWith(MockitoExtension.class)
@DisplayName("Тесты приёмки техники")
class ParkServiceTest {
    @Mock
    private ParkRepository repository;

    @Mock
    private ServiceCenter serviceCenter;

    private ParkService parkService;

    @BeforeEach
    void setUp() {
        parkService = new ParkService(repository, serviceCenter);
    }

    @Test
    @DisplayName("Техника после успешного техосмотра попадает в парк")
    void savesTransportAfterSuccessfulInspection() {
        final ElectricScooter scooter = scooter();
        when(serviceCenter.inspect(scooter))
                .thenReturn(InspectionResult.approved());

        final InspectionResult result = parkService.acceptTransport(scooter);

        assertTrue(result.accepted());
        verify(repository).save(scooter);
    }

    @Test
    @DisplayName("Отклонённая техосмотром техника не попадает в парк")
    void doesNotSaveRejectedTransport() {
        final ElectricScooter scooter = scooter();
        when(serviceCenter.inspect(scooter)).thenReturn(
                InspectionResult.rejected("Нужен ремонт"));

        final InspectionResult result = parkService.acceptTransport(scooter);

        assertFalse(result.accepted());
        verify(repository, never()).save(scooter);
    }

    @Test
    @DisplayName("Вещь ставится на баланс без техосмотра транспорта")
    void registersEquipmentWithoutTransportInspection() {
        final Helmet helmet = Helmet.builder()
                .name("Защитный шлем")
                .inventoryNumber("EQ-001")
                .build();

        parkService.registerEquipment(helmet);

        verify(repository).save(helmet);
        verifyNoInteractions(serviceCenter);
    }

    private ElectricScooter scooter() {
        return ElectricScooter.builder()
                .name("Электросамокат")
                .inventoryNumber("TR-001")
                .dailyEnergyConsumption(1.4)
                .beginnerEase(8)
                .technicalCondition(9)
                .build();
    }
}
