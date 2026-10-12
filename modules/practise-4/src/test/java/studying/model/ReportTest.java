package studying.model;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.stream.IntStream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import studying.exception.ApplicationErrorCode;
import studying.exception.ApplicationException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ReportTest {
    /** Fixed report date for repeatable tests. */
    private static final LocalDate DATE = LocalDate.parse("2026-09-11");
    /** Fixed report time for repeatable tests. */
    private static final LocalTime TIME = LocalTime.parse("12:30");
    /** Number of cars in a valid report. */
    private static final int CARS_SOLD = 100;
    /** Number of motorcycles in a valid report. */
    private static final int MOTORCYCLES_SOLD = 50;
    /** Second valid count used by parameterized tests. */
    private static final int MAX_VALID_COUNT = 3;
    /** Lowest invalid count used by parameterized tests. */
    private static final int MIN_INVALID_COUNT = -3;

    @Test
    @DisplayName("Корректный отчёт сохраняет все переданные поля")
    void validReportKeepsAllFields() {
        Report report = new Report("Продажи", DATE, TIME,
                CARS_SOLD, MOTORCYCLES_SOLD);

        assertEquals("Продажи", report.title());
        assertEquals(DATE, report.date());
        assertEquals(TIME, report.time());
        assertEquals(CARS_SOLD, report.carsSold());
        assertEquals(MOTORCYCLES_SOLD, report.motorcyclesSold());
    }

    @Test
    @DisplayName("Заголовок из пробелов вызывает ошибку валидации")
    void blankTitleIsRejected() {
        ApplicationException exception = assertThrows(
                ApplicationException.class,
                () -> new Report("   ", DATE, TIME,
                        CARS_SOLD, MOTORCYCLES_SOLD));

        assertEquals(ApplicationErrorCode.VALIDATION_ERROR,
                exception.getCode());
    }

    @ParameterizedTest
    @MethodSource("validSalesCounts")
    @DisplayName("Допустимое число автомобилей сохраняется")
    void validCarsSoldIsAccepted(final int carsSold) {
        Report report = new Report("Продажи", DATE, TIME,
                carsSold, MOTORCYCLES_SOLD);

        assertEquals(carsSold, report.carsSold());
    }

    @ParameterizedTest
    @MethodSource("validSalesCounts")
    @DisplayName("Допустимое число мотоциклов сохраняется")
    void validMotorcyclesSoldIsAccepted(final int motorcyclesSold) {
        Report report = new Report("Продажи", DATE, TIME,
                CARS_SOLD, motorcyclesSold);

        assertEquals(motorcyclesSold, report.motorcyclesSold());
    }

    @ParameterizedTest
    @MethodSource("negativeSalesCounts")
    @DisplayName("Отрицательное число автомобилей отклоняется")
    void negativeCarsSoldIsRejected(final int carsSold) {
        ApplicationException exception = assertThrows(
                ApplicationException.class,
                () -> new Report("Продажи", DATE, TIME,
                        carsSold, MOTORCYCLES_SOLD));

        assertEquals(ApplicationErrorCode.VALIDATION_ERROR,
                exception.getCode());
    }

    @ParameterizedTest
    @MethodSource("negativeSalesCounts")
    @DisplayName("Отрицательное число мотоциклов отклоняется")
    void negativeMotorcyclesSoldIsRejected(final int motorcyclesSold) {
        ApplicationException exception = assertThrows(
                ApplicationException.class,
                () -> new Report("Продажи", DATE, TIME,
                        CARS_SOLD, motorcyclesSold));

        assertEquals(ApplicationErrorCode.VALIDATION_ERROR,
                exception.getCode());
    }

    private static IntStream validSalesCounts() {
        return IntStream.of(0, MAX_VALID_COUNT);
    }

    private static IntStream negativeSalesCounts() {
        return IntStream.rangeClosed(MIN_INVALID_COUNT, -1);
    }
}
