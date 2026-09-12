package studying.withsolid.report;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import studying.withsolid.error.ApplicationErrorCode;
import studying.withsolid.error.ApplicationException;

import java.time.LocalDate;
import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Проверяет инварианты неизменяемой модели отчёта.
 */
class ReportTest {
    @Test
    @DisplayName("Отклоняет отрицательное количество проданных автомобилей")
    void rejectsNegativeCarsSold() {
        var exception = assertThrows(ApplicationException.class,
                () -> Report.builder()
                        .title("Продажи")
                        .date(LocalDate.of(2026, 9, 11))
                        .time(LocalTime.NOON)
                        .carsSold(-1)
                        .motorcyclesSold(50)
                        .build());

        assertEquals(ApplicationErrorCode.VALIDATION_ERROR, exception.getCode());
    }

    @Test
    @DisplayName("Отклоняет пустой заголовок")
    void rejectsBlankTitle() {
        var exception = assertThrows(ApplicationException.class,
                () -> Report.builder()
                        .title(" ")
                        .date(LocalDate.of(2026, 9, 11))
                        .time(LocalTime.NOON)
                        .carsSold(100)
                        .motorcyclesSold(50)
                        .build());

        assertEquals(ApplicationErrorCode.VALIDATION_ERROR, exception.getCode());
    }
}
