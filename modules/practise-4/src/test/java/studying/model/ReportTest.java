package studying.model;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import studying.exception.ApplicationErrorCode;
import studying.exception.ApplicationException;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ReportTest {
    private static final LocalDate DATE = LocalDate.of(2026, 9, 29);
    private static final LocalTime TIME = LocalTime.of(12, 30);

    @Test
    void createsValidReport() {
        var report = new Report("Продажи", DATE, TIME, 10, 5);

        assertEquals("Продажи", report.title());
        assertEquals(DATE, report.date());
        assertEquals(TIME, report.time());
        assertEquals(10, report.carsSold());
        assertEquals(5, report.motorcyclesSold());
    }

    @ParameterizedTest
    @MethodSource("blankTitles")
    void rejectsBlankTitle(String title) {
        var exception = assertThrows(ApplicationException.class,
                () -> new Report(title, DATE, TIME, 0, 0));

        assertEquals(ApplicationErrorCode.VALIDATION_ERROR, exception.getCode());
    }

    @ParameterizedTest
    @MethodSource("negativeValues")
    void rejectsNegativeCars(int value) {
        assertThrows(ApplicationException.class,
                () -> new Report("Продажи", DATE, TIME, value, 0));
    }

    @ParameterizedTest
    @MethodSource("negativeValues")
    void rejectsNegativeMotorcycles(int value) {
        assertThrows(ApplicationException.class,
                () -> new Report("Продажи", DATE, TIME, 0, value));
    }

    @ParameterizedTest
    @MethodSource("validValues")
    void acceptsNonNegativeSales(int value) {
        var report = new Report("Продажи", DATE, TIME, value, value);

        assertEquals(value, report.carsSold());
        assertEquals(value, report.motorcyclesSold());
    }

    static java.util.stream.Stream<String> blankTitles() {
        return java.util.stream.Stream.of(null, "", "   ");
    }

    static IntStream negativeValues() {
        return IntStream.rangeClosed(-3, -1);
    }

    static IntStream validValues() {
        return IntStream.rangeClosed(0, 3);
    }
}
