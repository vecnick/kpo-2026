package studying.vyshkat.model;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import studying.vyshkat.exception.DomainValidationException;

/** Unit tests for domain model invariants. */
@DisplayName("Тесты доменной модели")
class DomainModelTest {
    @Test
    @DisplayName("Граница шесть подходит новичку")
    void acceptsBeginnerThreshold() {
        assertTrue(bicycle(6).isSuitableForBeginner());
        assertFalse(bicycle(5).isSuitableForBeginner());
    }

    @Test
    @DisplayName("Модель отклоняет пустой инвентарный номер")
    void rejectsBlankInventoryNumber() {
        assertThrows(DomainValidationException.class,
                () -> Helmet.builder()
                        .name("Шлем")
                        .inventoryNumber(" ")
                        .build());
    }

    @Test
    @DisplayName("Модель отклоняет отрицательное энергопотребление")
    void rejectsNegativeEnergyConsumption() {
        assertThrows(DomainValidationException.class,
                () -> ChargingStation.builder()
                        .name("Зарядная станция")
                        .inventoryNumber("EQ-001")
                        .dailyEnergyConsumption(-0.1)
                        .build());
    }

    private Bicycle bicycle(final int beginnerEase) {
        return Bicycle.builder()
                .name("Велосипед")
                .inventoryNumber("TR-001")
                .beginnerEase(beginnerEase)
                .technicalCondition(8)
                .build();
    }
}
