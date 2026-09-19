package studying.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import studying.withsolid.exception.ApplicationErrorCode;
import studying.withsolid.exception.ApplicationException;
import studying.withsolid.model.Report;

import java.time.LocalDate;
import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ReportTest {
    @Test
    @DisplayName("Выбрасывает прикладную ошибку при отрицательном числе проданных автомобилей")
    void buildRejectsNegativeCarsSold() {
        var exception = assertThrows(ApplicationException.class,
                () -> createReport().carsSold(-1).build());

        assertEquals(ApplicationErrorCode.VALIDATION_ERROR, exception.getCode());
    }

    @Test
    @DisplayName("Выбрасывает прикладную ошибку при отрицательном числе проданных мотоциклов")
    void buildRejectsNegativeMotorcyclesSold() {
        var exception = assertThrows(ApplicationException.class,
                () -> createReport().motorcyclesSold(-1).build());

        assertEquals(ApplicationErrorCode.VALIDATION_ERROR, exception.getCode());
    }

    private Report.ReportBuilder createReport() {
        return Report.builder()
                .title("Продажи")
                .date(LocalDate.of(2026, 9, 11))
                .time(LocalTime.NOON)
                .carsSold(100)
                .motorcyclesSold(50);
    }
}
