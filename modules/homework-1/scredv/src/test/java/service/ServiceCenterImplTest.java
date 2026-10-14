package service;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import vyshkat.model.Bicycle;
import vyshkat.model.ElectricScooter;
import vyshkat.model.Scooter;
import vyshkat.service.impl.ServiceCenterImpl;

/**
 * Проверяет правила допуска транспорта по результатам техосмотра.
 */
@Tag("unit")
@DisplayName("Юнит-тесты ServiceCenterImpl")
class ServiceCenterImplTest {
    /**
     * Минимальная оценка удобства для прохождения техосмотра.
     */
    private static final int MIN_SAFE_SIMPLICITY = 3;
    /**
     * Максимальное допустимое суточное потребление в кВт·ч.
     */
    private static final double MAX_DAILY_CONSUMPTION = 150.0;

    /** Числовое значение тестового сценария. */
    private static final double SAMPLE_0_1 = 0.1;
    /** Числовое значение тестового сценария. */
    private static final double SAMPLE_10_0 = 10.0;
    /** Числовое значение тестового сценария. */
    private static final int SAMPLE_5 = 5;
    /** Числовое значение тестового сценария. */
    private static final int SAMPLE_7 = 7;

    /** Проверяемая реализация техосмотра. */
    private final ServiceCenterImpl serviceCenter = new ServiceCenterImpl();

    private static Scooter scooter(final int simplicity) {
        return Scooter.builder()
                .inventoryNumber("INV-001")
                .modelName("Kick")
                .noviceFriendliness(simplicity)
                .build();
    }

    private static ElectricScooter electricScooter(final int simplicity,
                                                   final double consumption) {
        return ElectricScooter.builder()
                .inventoryNumber("INV-002")
                .modelName("E-Scoot")
                .noviceFriendliness(simplicity)
                .dailyEnergyConsumption(consumption)
                .build();
    }

    @Test
    @DisplayName("Устройство с простотой на нижней границе проходит техосмотр")
    void acceptsAtMinimumSimplicity() {
        assertTrue(serviceCenter.inspect(scooter(MIN_SAFE_SIMPLICITY)));
    }

    @Test
    @DisplayName("Устройство с простотой ниже минимума отклоняется")
    void rejectsBelowMinimumSimplicity() {
        assertFalse(serviceCenter.inspect(scooter(MIN_SAFE_SIMPLICITY - 1)));
    }

    @Test
    @DisplayName("Велосипед проходит техосмотр независимо от потребления")
    void acceptsBicycleRegardlessOfConsumption() {
        final Bicycle bicycle = Bicycle.builder()
                .inventoryNumber("INV-003")
                .modelName("Stels")
                .noviceFriendliness(SAMPLE_5)
                .build();

        assertTrue(serviceCenter.inspect(bicycle));
    }

    @Test
    @DisplayName("Потребитель на верхней границе потребления принимается")
    void acceptsConsumerAtMaximumConsumption() {
        assertTrue(serviceCenter.inspect(
                electricScooter(SAMPLE_7, MAX_DAILY_CONSUMPTION)));
    }

    @Test
    @DisplayName("Потребитель выше границы потребления отклоняется")
    void rejectsConsumerAboveMaximumConsumption() {
        assertFalse(serviceCenter.inspect(
                electricScooter(SAMPLE_7, MAX_DAILY_CONSUMPTION + SAMPLE_0_1)));
    }

    @Test
    @DisplayName(
            "Потребитель с низкой простотой отклоняется до проверки "
            + "потребления")
    void rejectsLowSimplicityEvenWithAcceptableConsumption() {
        assertFalse(serviceCenter.inspect(electricScooter(2, SAMPLE_10_0)));
    }
}
