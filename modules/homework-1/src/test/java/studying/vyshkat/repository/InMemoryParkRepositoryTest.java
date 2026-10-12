package studying.vyshkat.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import studying.vyshkat.exception.DuplicateInventoryNumberException;
import studying.vyshkat.model.Helmet;

/** Unit tests for in-memory inventory storage. */
@DisplayName("Тесты хранилища парка")
class InMemoryParkRepositoryTest {
    @Test
    @DisplayName("Хранилище запрещает одинаковые инвентарные номера")
    void rejectsDuplicateInventoryNumber() {
        final InMemoryParkRepository repository =
                new InMemoryParkRepository();
        final Helmet first = helmet("Первый шлем");
        final Helmet second = helmet("Второй шлем");
        repository.save(first);

        assertThrows(DuplicateInventoryNumberException.class,
                () -> repository.save(second));
        assertEquals(1, repository.findAll().size());
    }

    private Helmet helmet(final String name) {
        return Helmet.builder()
                .name(name)
                .inventoryNumber("EQ-001")
                .build();
    }
}
