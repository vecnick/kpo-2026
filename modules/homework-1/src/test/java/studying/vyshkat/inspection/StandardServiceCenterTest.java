package studying.vyshkat.inspection;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import studying.vyshkat.model.Bicycle;

/** Unit tests for the standard technical inspection rule. */
@DisplayName("Тесты стандартного техосмотра")
class StandardServiceCenterTest {
    private final StandardServiceCenter serviceCenter =
            new StandardServiceCenter();

    @Test
    @DisplayName("Исправная техника проходит техосмотр")
    void acceptsTransportInGoodCondition() {
        assertTrue(serviceCenter.inspect(bicycle(7)).accepted());
    }

    @Test
    @DisplayName("Неисправная техника отклоняется")
    void rejectsTransportInPoorCondition() {
        assertFalse(serviceCenter.inspect(bicycle(6)).accepted());
    }

    private Bicycle bicycle(final int technicalCondition) {
        return Bicycle.builder()
                .name("Велосипед")
                .inventoryNumber("TR-001")
                .beginnerEase(8)
                .technicalCondition(technicalCondition)
                .build();
    }
}
