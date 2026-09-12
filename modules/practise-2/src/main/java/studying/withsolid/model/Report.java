package studying.withsolid.model;

import lombok.Builder;
import studying.withsolid.exception.ApplicationErrorCode;
import studying.withsolid.exception.ApplicationException;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

/**
 * Immutable sales report data.
 *
 * @param title report title
 * @param date report date
 * @param time report time
 * @param carsSold number of cars sold
 * @param motorcyclesSold number of motorcycles sold
 */
@Builder
public record Report(
        String title,
        LocalDate date,
        LocalTime time,
        int carsSold,
        int motorcyclesSold
) {
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm:ss");

    /** Validates report data when a report is created. */
    public Report {
        if (title == null || title.isBlank()) {
            throw validationError("Заголовок отчёта не задан");
        }
        if (date == null) {
            throw validationError("Дата отчёта не задана");
        }
        if (time == null) {
            throw validationError("Время отчёта не задано");
        }
        if (carsSold < 0 || motorcyclesSold < 0) {
            throw validationError("Показатели продаж не могут быть отрицательными");
        }
    }

    @Override
    public String toString() {
        return """
                %s
                Дата: %s
                Время: %s
                --------------------------------
                Продано автомобилей: %d шт.
                Продано мотоциклов: %d шт.
                --------------------------------
                """.formatted(title, date, time.format(TIME_FORMATTER), carsSold, motorcyclesSold);
    }

    private static ApplicationException validationError(String message) {
        return new ApplicationException(ApplicationErrorCode.VALIDATION_ERROR, message);
    }
}
