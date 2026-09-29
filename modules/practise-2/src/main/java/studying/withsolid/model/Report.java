package studying.withsolid.model;

import studying.withsolid.exception.ApplicationErrorCode;
import studying.withsolid.exception.ApplicationException;

import java.time.LocalDate;
import java.time.LocalTime;

public record Report(
        String title,
        LocalDate date,
        LocalTime time,
        int carsSold,
        int motorcyclesSold
) {
    public Report {
        if (title == null || title.isBlank() || date == null || time == null) {
            throw new ApplicationException(
                    ApplicationErrorCode.VALIDATION_ERROR,
                    "Название, дата и время отчёта обязательны");
        }
        if (carsSold < 0 || motorcyclesSold < 0) {
            throw new ApplicationException(
                    ApplicationErrorCode.VALIDATION_ERROR,
                    "Количество продаж не может быть отрицательным");
        }
    }

    @Override
    public String toString() {
        return "%s%nДата: %s%nВремя: %s%n--------------------------------%n"
                .formatted(title, date, time.withNano(0))
                + "Продано автомобилей: %d шт.%nПродано мотоциклов: %d шт.%n--------------------------------%n"
                .formatted(carsSold, motorcyclesSold);
    }
}
