package service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import vyshkat.exception.ApplicationErrorCode;
import vyshkat.exception.ApplicationException;
import vyshkat.model.Bicycle;
import vyshkat.model.ElectricScooter;
import vyshkat.model.Scooter;
import vyshkat.model.Transport;
import vyshkat.model.item.ChargingStation;
import vyshkat.model.item.Helmet;
import vyshkat.model.item.Item;
import vyshkat.service.ServiceCenter;
import vyshkat.service.impl.FleetServiceImpl;
import vyshkat.service.impl.ServiceCenterImpl;

/**
 * Проверяет работу парка и взаимодействие с подменённым техосмотром.
 */
@Tag("mock")
@DisplayName("Mock-тесты FleetService без Spring")
class FleetServiceInteractionTest {
    /** Тестовый инвентарный номер. */
    private static final String NUMBER = "INV-001";
    /** Тестовое название модели. */
    private static final String MODEL = "Яндекс Самокат";

    /**
     * Минимальная оценка удобства транспорта для новичков.
     */
    private static final int NOVICE_SIMPLICITY_THRESHOLD = 6;
    /**
     * Минимальная допустимая оценка удобства.
     */
    private static final int MIN_SIMPLICITY = 1;
    /**
     * Максимальная допустимая оценка удобства.
     */
    private static final int MAX_SIMPLICITY = 10;
    /**
     * Допустимая погрешность сравнения дробных чисел.
     */
    private static final double DELTA = 0.001;

    /** Числовое значение тестового сценария. */
    private static final double SAMPLE_NEG_1_0 = -1.0;
    /** Числовое значение тестового сценария. */
    private static final double SAMPLE_NEG_5_0 = -5.0;
    /** Числовое значение тестового сценария. */
    private static final double SAMPLE_0_0 = 0.0;
    /** Числовое значение тестового сценария. */
    private static final double SAMPLE_10_0 = 10.0;
    /** Числовое значение тестового сценария. */
    private static final double SAMPLE_2_5 = 2.5;
    /** Числовое значение тестового сценария. */
    private static final int SAMPLE_3 = 3;
    /** Числовое значение тестового сценария. */
    private static final double SAMPLE_3_0 = 3.0;
    /** Числовое значение тестового сценария. */
    private static final double SAMPLE_4_5 = 4.5;
    /** Числовое значение тестового сценария. */
    private static final int SAMPLE_5 = 5;
    /** Числовое значение тестового сценария. */
    private static final double SAMPLE_5_0 = 5.0;
    /** Числовое значение тестового сценария. */
    private static final int SAMPLE_6 = 6;
    /** Числовое значение тестового сценария. */
    private static final int SAMPLE_7 = 7;
    /** Числовое значение тестового сценария. */
    private static final int SAMPLE_8 = 8;
    /** Числовое значение тестового сценария. */
    private static final int SAMPLE_9 = 9;

    // ---------------------------------------------------------------------
    // Подготовка тестовых объектов
    // ---------------------------------------------------------------------

    private static Scooter scooter(final String number, final int simplicity) {
        return Scooter.builder()
                .inventoryNumber(number)
                .modelName(MODEL)
                .noviceFriendliness(simplicity)
                .build();
    }

    private static ElectricScooter electricScooter(final String number,
                                                   final int simplicity,
                                                   final double consumption) {
        return ElectricScooter.builder()
                .inventoryNumber(number)
                .modelName(MODEL)
                .noviceFriendliness(simplicity)
                .dailyEnergyConsumption(consumption)
                .build();
    }

    private static Helmet helmet(final String number) {
        return Helmet.builder()
                .inventoryNumber(number)
                .name("Шлем")
                .build();
    }

    private static ChargingStation station(final String number,
                                           final double consumption) {
        return ChargingStation.builder()
                .inventoryNumber(number)
                .name("Зарядка")
                .dailyEnergyConsumption(consumption)
                .build();
    }

    // ---------------------------------------------------------------------
    // Взаимодействие с техосмотром
    // ---------------------------------------------------------------------

    @Test
    @DisplayName("Сервис вызывает техосмотр при добавлении транспорта")
    void inspectsTransportOnAdd() {
        final ServiceCenter serviceCenter = mock(ServiceCenter.class);
        final FleetServiceImpl service = new FleetServiceImpl(serviceCenter);
        when(serviceCenter.inspect(any())).thenReturn(true);
        final var scooter = scooter(NUMBER, SAMPLE_7);

        service.addTransport(scooter);

        verify(serviceCenter, times(1)).inspect(scooter);
    }

    @Test
    @DisplayName("При отказе техосмотра транспорт не добавляется в парк")
    void doesNotAddWhenInspectionRejects() {
        final ServiceCenter serviceCenter = mock(ServiceCenter.class);
        final FleetServiceImpl service = new FleetServiceImpl(serviceCenter);
        when(serviceCenter.inspect(any())).thenReturn(false);

        final var exception = assertThrows(ApplicationException.class,
                () -> service.addTransport(scooter(NUMBER, SAMPLE_7)));

        assertEquals(ApplicationErrorCode.TRANSPORT_REJECTED,
                exception.getCode());
        assertEquals(0, service.getAllInventoryItems().size());
    }

    // ---------------------------------------------------------------------
    // Уникальность инвентарных номеров
    // ---------------------------------------------------------------------

    @Test
    @DisplayName("Повторный инвентарный номер транспорта вызывает ошибку")
    void rejectsDuplicateTransportNumber() {
        final ServiceCenter serviceCenter = mock(ServiceCenter.class);
        final FleetServiceImpl service = new FleetServiceImpl(serviceCenter);
        when(serviceCenter.inspect(any())).thenReturn(true);

        service.addTransport(scooter(NUMBER, SAMPLE_7));

        final var exception = assertThrows(ApplicationException.class,
                () -> service.addTransport(scooter(NUMBER, SAMPLE_8)));

        assertEquals(ApplicationErrorCode.DUPLICATE_INVENTORY_NUMBER,
                exception.getCode());
        assertEquals(1, service.getAllInventoryItems().size());
    }

    @Test
    @DisplayName("Номер, занятый вещью, отклоняется при добавлении транспорта")
    void rejectsTransportNumberAlreadyTakenByItem() {
        final ServiceCenter serviceCenter = mock(ServiceCenter.class);
        final FleetServiceImpl service = new FleetServiceImpl(serviceCenter);
        when(serviceCenter.inspect(any())).thenReturn(true);

        service.addItem(helmet(NUMBER));

        final var exception = assertThrows(ApplicationException.class,
                () -> service.addTransport(scooter(NUMBER, SAMPLE_7)));

        assertEquals(ApplicationErrorCode.DUPLICATE_INVENTORY_NUMBER,
                exception.getCode());
    }

    @Test
    @DisplayName("Номер, занятый транспортом, отклоняется при добавлении вещи")
    void rejectsItemNumberAlreadyTakenByTransport() {
        final ServiceCenter serviceCenter = mock(ServiceCenter.class);
        final FleetServiceImpl service = new FleetServiceImpl(serviceCenter);
        when(serviceCenter.inspect(any())).thenReturn(true);

        service.addTransport(scooter(NUMBER, SAMPLE_7));

        final var exception = assertThrows(ApplicationException.class,
                () -> service.addItem(helmet(NUMBER)));

        assertEquals(ApplicationErrorCode.DUPLICATE_INVENTORY_NUMBER,
                exception.getCode());
    }

    // ---------------------------------------------------------------------
    // Проверка входных данных
    // ---------------------------------------------------------------------

    @Test
    @DisplayName(
            "Пустой инвентарный номер транспорта отклоняется до техосмотра")
    void rejectsBlankTransportNumberBeforeInspection() {
        final ServiceCenter serviceCenter = mock(ServiceCenter.class);
        final FleetServiceImpl service = new FleetServiceImpl(serviceCenter);

        final var exception = assertThrows(ApplicationException.class,
                () -> service.addTransport(scooter("   ", SAMPLE_7)));

        assertEquals(ApplicationErrorCode.VALIDATION_ERROR,
                exception.getCode());
        verify(serviceCenter, never()).inspect(any());
    }

    @Test
    @DisplayName("Пустой инвентарный номер вещи отклоняется")
    void rejectsBlankItemNumber() {
        final ServiceCenter serviceCenter = mock(ServiceCenter.class);
        final FleetServiceImpl service = new FleetServiceImpl(serviceCenter);

        final var exception = assertThrows(ApplicationException.class,
                () -> service.addItem(helmet("")));

        assertEquals(ApplicationErrorCode.VALIDATION_ERROR,
                exception.getCode());
    }

    @Test
    @DisplayName("Простота ниже допустимого минимума отклоняется")
    void rejectsSimplicityBelowMinimum() {
        final ServiceCenter serviceCenter = mock(ServiceCenter.class);
        final FleetServiceImpl service = new FleetServiceImpl(serviceCenter);

        final var exception = assertThrows(ApplicationException.class,
                () -> service
                        .addTransport(scooter(NUMBER, MIN_SIMPLICITY - 1)));

        assertEquals(ApplicationErrorCode.VALIDATION_ERROR,
                exception.getCode());
        verify(serviceCenter, never()).inspect(any());
    }

    @Test
    @DisplayName("Простота выше допустимого максимума отклоняется")
    void rejectsSimplicityAboveMaximum() {
        final ServiceCenter serviceCenter = mock(ServiceCenter.class);
        final FleetServiceImpl service = new FleetServiceImpl(serviceCenter);

        final var exception = assertThrows(ApplicationException.class,
                () -> service
                        .addTransport(scooter(NUMBER, MAX_SIMPLICITY + 1)));

        assertEquals(ApplicationErrorCode.VALIDATION_ERROR,
                exception.getCode());
        verify(serviceCenter, never()).inspect(any());
    }

    @Test
    @DisplayName("Отрицательное потребление транспорта отклоняется")
    void rejectsNegativeTransportConsumption() {
        final ServiceCenter serviceCenter = mock(ServiceCenter.class);
        final FleetServiceImpl service = new FleetServiceImpl(serviceCenter);

        final var exception = assertThrows(ApplicationException.class,
                () -> service
                        .addTransport(electricScooter(
                                NUMBER, SAMPLE_7, SAMPLE_NEG_1_0)));

        assertEquals(ApplicationErrorCode.VALIDATION_ERROR,
                exception.getCode());
        verify(serviceCenter, never()).inspect(any());
    }

    @Test
    @DisplayName("Отрицательное потребление вещи отклоняется")
    void rejectsNegativeItemConsumption() {
        final ServiceCenter serviceCenter = mock(ServiceCenter.class);
        final FleetServiceImpl service = new FleetServiceImpl(serviceCenter);

        final var exception = assertThrows(ApplicationException.class,
                () -> service.addItem(station(NUMBER, SAMPLE_NEG_5_0)));

        assertEquals(ApplicationErrorCode.VALIDATION_ERROR,
                exception.getCode());
    }

    // ---------------------------------------------------------------------
    // Расчёт суммарного энергопотребления
    // ---------------------------------------------------------------------

    @Test
    @DisplayName("Пустой парк потребляет ноль энергии")
    void emptyFleetConsumesZero() {
        final ServiceCenter serviceCenter = mock(ServiceCenter.class);
        final FleetServiceImpl service = new FleetServiceImpl(serviceCenter);

        assertEquals(SAMPLE_0_0, service
                .getTotalDailyEnergyConsumption(), DELTA);
    }

    @Test
    @DisplayName("Энергия суммируется только по потребителям, включая вещи")
    void sumsEnergyOnlyForConsumers() {
        final ServiceCenter serviceCenter = mock(ServiceCenter.class);
        final FleetServiceImpl service = new FleetServiceImpl(serviceCenter);
        when(serviceCenter.inspect(any())).thenReturn(true);

        // Не потребляет энергию.
        service.addTransport(scooter("INV-001", SAMPLE_8));
        service.addTransport(Bicycle.builder()
                .inventoryNumber("INV-002").modelName("Stels")
                // Не потребляет энергию.
                .noviceFriendliness(SAMPLE_7).build());
        // Потребляет энергию.
        service.addTransport(electricScooter("INV-003", SAMPLE_7, SAMPLE_2_5));
        // Потребляет энергию.
        service.addTransport(electricScooter("INV-004", SAMPLE_6, SAMPLE_3_0));
        // Не потребляет энергию.
        service.addItem(helmet("INV-005"));
        // Потребляет энергию.
        service.addItem(station("INV-006", SAMPLE_4_5));

        assertEquals(SAMPLE_10_0, service
                .getTotalDailyEnergyConsumption(), DELTA);
    }

    // ---------------------------------------------------------------------
    // Выбор транспорта для новичков
    // ---------------------------------------------------------------------

    @Test
    @DisplayName("Фильтр новичков включает простое ровно на пороге")
    void includesTransportAtThreshold() {
        final ServiceCenter serviceCenter = mock(ServiceCenter.class);
        final FleetServiceImpl service = new FleetServiceImpl(serviceCenter);
        when(serviceCenter.inspect(any())).thenReturn(true);

        service.addTransport(scooter("INV-001", NOVICE_SIMPLICITY_THRESHOLD));

        final var result = service.getNoviceFriendlyTransport();

        assertEquals(1, result.size());
        assertEquals("INV-001", result.get(0).getInventoryNumber());
    }

    @Test
    @DisplayName("Фильтр новичков отсекает простое ниже порога")
    void excludesTransportBelowThreshold() {
        final ServiceCenter serviceCenter = mock(ServiceCenter.class);
        final FleetServiceImpl service = new FleetServiceImpl(serviceCenter);
        when(serviceCenter.inspect(any())).thenReturn(true);

        service.addTransport(
                scooter("INV-001", NOVICE_SIMPLICITY_THRESHOLD - 1));

        final var result = service.getNoviceFriendlyTransport();

        assertEquals(0, result.size());
    }

    @Test
    @DisplayName("Фильтр новичков возвращает только подходящий транспорт")
    void filtersMixedFleet() {
        final ServiceCenter serviceCenter = mock(ServiceCenter.class);
        final FleetServiceImpl service = new FleetServiceImpl(serviceCenter);
        when(serviceCenter.inspect(any())).thenReturn(true);

        service.addTransport(scooter("INV-001", SAMPLE_9));
        service.addTransport(scooter("INV-002", SAMPLE_3));
        service.addTransport(electricScooter("INV-003", SAMPLE_7, SAMPLE_5_0));
        service.addTransport(scooter("INV-004", SAMPLE_5));

        final var result = service.getNoviceFriendlyTransport();

        assertEquals(2, result.size());
        assertEquals("INV-001", result.get(0).getInventoryNumber());
        assertEquals("INV-003", result.get(1).getInventoryNumber());
    }

    // ---------------------------------------------------------------------
    // Полный список инвентаря
    // ---------------------------------------------------------------------

    @Test
    @DisplayName("Инвентарь содержит и транспорт, и вещи")
    void inventoryContainsBothTransportsAndItems() {
        final ServiceCenter serviceCenter = mock(ServiceCenter.class);
        final FleetServiceImpl service = new FleetServiceImpl(serviceCenter);
        when(serviceCenter.inspect(any())).thenReturn(true);

        final Transport scooter = scooter("INV-001", SAMPLE_7);
        final Item helmet = helmet("INV-002");
        service.addTransport(scooter);
        service.addItem(helmet);

        final var all = service.getAllInventoryItems();

        assertEquals(2, all.size());
        assertEquals(scooter, all.get(0));
        assertEquals(helmet, all.get(1));
    }

    @Test
    @DisplayName("Пустой инвентарь возвращает пустой список")
    void emptyInventoryIsEmpty() {
        final ServiceCenter serviceCenter = mock(ServiceCenter.class);
        final FleetServiceImpl service = new FleetServiceImpl(serviceCenter);

        assertEquals(0, service.getAllInventoryItems().size());
    }

    // ---------------------------------------------------------------------
    // Дополнительные граничные сценарии
    // ---------------------------------------------------------------------

    @Test
    @DisplayName("NaN и бесконечность запрещены для транспорта")
    void rejectsNonFiniteTransportConsumption() {
        final ServiceCenter center = mock(ServiceCenter.class);
        final FleetServiceImpl service = new FleetServiceImpl(center);
        final double[] invalidValues = {
                Double.NaN, Double.POSITIVE_INFINITY, Double.NEGATIVE_INFINITY
        };

        for (final double consumption : invalidValues) {
            final var exception = assertThrows(ApplicationException.class,
                    () -> service.addTransport(
                            electricScooter(NUMBER, SAMPLE_7, consumption)));
            assertEquals(ApplicationErrorCode.VALIDATION_ERROR,
                    exception.getCode());
        }
        assertEquals(0, service.getAllInventoryItems().size());
        verify(center, never()).inspect(any());
    }

    @Test
    @DisplayName("NaN и бесконечность запрещены для вещей")
    void rejectsNonFiniteItemConsumption() {
        final FleetServiceImpl service = new FleetServiceImpl(
                mock(ServiceCenter.class));
        final double[] invalidValues = {
                Double.NaN, Double.POSITIVE_INFINITY, Double.NEGATIVE_INFINITY
        };

        for (final double consumption : invalidValues) {
            final var exception = assertThrows(ApplicationException.class,
                    () -> service.addItem(station(NUMBER, consumption)));
            assertEquals(ApplicationErrorCode.VALIDATION_ERROR,
                    exception.getCode());
        }
        assertEquals(0, service.getAllInventoryItems().size());
    }

    @Test
    @DisplayName("Null-номер транспорта отклоняется до техосмотра")
    void rejectsNullTransportNumber() {
        final ServiceCenter center = mock(ServiceCenter.class);
        final FleetServiceImpl service = new FleetServiceImpl(center);

        final var exception = assertThrows(ApplicationException.class,
                () -> service.addTransport(scooter(null, SAMPLE_7)));

        assertEquals(ApplicationErrorCode.VALIDATION_ERROR,
                exception.getCode());
        verify(center, never()).inspect(any());
    }

    @Test
    @DisplayName("Null-номер вещи отклоняется")
    void rejectsNullItemNumber() {
        final FleetServiceImpl service = new FleetServiceImpl(
                mock(ServiceCenter.class));

        final var exception = assertThrows(ApplicationException.class,
                () -> service.addItem(helmet(null)));

        assertEquals(ApplicationErrorCode.VALIDATION_ERROR,
                exception.getCode());
        assertEquals(0, service.getAllInventoryItems().size());
    }

    @Test
    @DisplayName("Две вещи с одинаковым номером не могут быть добавлены")
    void rejectsDuplicateItemNumber() {
        final FleetServiceImpl service = new FleetServiceImpl(
                mock(ServiceCenter.class));
        final Item first = helmet(NUMBER);
        service.addItem(first);

        final var exception = assertThrows(ApplicationException.class,
                () -> service.addItem(station(NUMBER, SAMPLE_2_5)));

        assertEquals(ApplicationErrorCode.DUPLICATE_INVENTORY_NUMBER,
                exception.getCode());
        assertEquals(1, service.getAllInventoryItems().size());
        assertSame(first, service.getAllInventoryItems().get(0));
    }

    @Test
    @DisplayName("Оценки простоты 1 и 10 проходят проверку диапазона")
    void acceptsSimplicityAtRangeBoundaries() {
        final ServiceCenter center = mock(ServiceCenter.class);
        final FleetServiceImpl service = new FleetServiceImpl(center);
        when(center.inspect(any())).thenReturn(true);
        final Transport minimum = scooter("INV-MIN", MIN_SIMPLICITY);
        final Transport maximum = scooter("INV-MAX", MAX_SIMPLICITY);

        service.addTransport(minimum);
        service.addTransport(maximum);

        assertEquals(2, service.getAllInventoryItems().size());
        verify(center).inspect(minimum);
        verify(center).inspect(maximum);
    }

    @Test
    @DisplayName("Нулевое энергопотребление допустимо")
    void acceptsZeroEnergyConsumption() {
        final ServiceCenter center = mock(ServiceCenter.class);
        final FleetServiceImpl service = new FleetServiceImpl(center);
        when(center.inspect(any())).thenReturn(true);

        service.addTransport(electricScooter("INV-ES", SAMPLE_7, 0.0));
        service.addItem(station("INV-ST", 0.0));

        assertEquals(2, service.getAllInventoryItems().size());
        assertEquals(0.0, service.getTotalDailyEnergyConsumption(), DELTA);
    }

    @Test
    @DisplayName("Дублирующийся номер не вызывает повторный техосмотр")
    void duplicateNumberDoesNotTriggerSecondInspection() {
        final ServiceCenter center = mock(ServiceCenter.class);
        final FleetServiceImpl service = new FleetServiceImpl(center);
        when(center.inspect(any())).thenReturn(true);
        final Transport first = scooter(NUMBER, SAMPLE_7);
        service.addTransport(first);

        final var exception = assertThrows(ApplicationException.class,
                () -> service.addTransport(scooter(NUMBER, SAMPLE_8)));

        assertEquals(ApplicationErrorCode.DUPLICATE_INVENTORY_NUMBER,
                exception.getCode());
        verify(center, times(1)).inspect(first);
        assertSame(first, service.getAllInventoryItems().get(0));
    }

    @Test
    @DisplayName("Отказ техосмотра не изменяет уже принятый инвентарь")
    void inspectionFailurePreservesExistingInventory() {
        final ServiceCenter center = mock(ServiceCenter.class);
        final FleetServiceImpl service = new FleetServiceImpl(center);
        final Item existing = helmet("INV-HELMET");
        service.addItem(existing);
        when(center.inspect(any())).thenReturn(false);

        final var exception = assertThrows(ApplicationException.class,
                () -> service.addTransport(scooter(NUMBER, SAMPLE_7)));

        assertEquals(ApplicationErrorCode.TRANSPORT_REJECTED,
                exception.getCode());
        assertEquals(1, service.getAllInventoryItems().size());
        assertSame(existing, service.getAllInventoryItems().get(0));
    }

    @Test
    @DisplayName("Потребление правильно считается по отдельным категориям")
    void calculatesEnergyForTransportAndItemsSeparately() {
        final ServiceCenter center = mock(ServiceCenter.class);
        final FleetServiceImpl transportOnly = new FleetServiceImpl(center);
        when(center.inspect(any())).thenReturn(true);

        transportOnly.addTransport(
                electricScooter(NUMBER, SAMPLE_7, SAMPLE_2_5));
        transportOnly.addTransport(
                scooter("INV-002", SAMPLE_7));

        final FleetServiceImpl itemsOnly = new FleetServiceImpl(center);
        itemsOnly.addItem(station("INV-003", SAMPLE_4_5));
        itemsOnly.addItem(helmet("INV-004"));

        assertEquals(
                SAMPLE_2_5,
                transportOnly.getTotalDailyEnergyConsumption(),
                DELTA);

        assertEquals(
                SAMPLE_4_5,
                itemsOnly.getTotalDailyEnergyConsumption(),
                DELTA);
    }

    @Test
    @DisplayName("Проверка приёмки с реальным техосмотром")
    void integratesFleetWithRealServiceCenter() {
        final FleetServiceImpl service = new FleetServiceImpl(
                new ServiceCenterImpl());
        final Transport accepted = scooter("INV-GOOD", SAMPLE_7);
        service.addTransport(accepted);

        final var exception = assertThrows(ApplicationException.class,
                () -> service.addTransport(scooter("INV-BAD", 2)));

        assertEquals(ApplicationErrorCode.TRANSPORT_REJECTED,
                exception.getCode());
        assertEquals(1, service.getAllInventoryItems().size());
        assertSame(accepted, service.getAllInventoryItems().get(0));
    }
}
