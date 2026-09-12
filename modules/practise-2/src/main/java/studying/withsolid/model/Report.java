package studying.withsolid.model;

import lombok.Builder;
import studying.withsolid.exception.ApplicationErrorCode;
import studying.withsolid.exception.ApplicationException;

import java.time.LocalDate;
import java.time.LocalTime;

/**
 * Неизменяемые данные отчёта о продажах.
 * Модель не зависит от способа сохранения и доставки отчёта.
 *
 * @param title заголовок отчёта
 * @param date дата формирования отчёта
 * @param time время формирования отчёта
 * @param carsSold количество проданных автомобилей
 * @param motorcyclesSold количество проданных мотоциклов
 */
@Builder
public record Report(
        String title,
        LocalDate date,
        LocalTime time,
        int carsSold,
        int motorcyclesSold
) {
    public Report {
        if (title == null || title.isBlank()) {
            throw new ApplicationException(ApplicationErrorCode.VALIDATION_ERROR,
                    "Заголовок отчёта не задан");
        }
        if (date == null || time == null) {
            throw new ApplicationException(ApplicationErrorCode.VALIDATION_ERROR,
                    "Дата и время отчёта не заданы");
        }
        if (carsSold < 0 || motorcyclesSold < 0) {
            throw new ApplicationException(ApplicationErrorCode.VALIDATION_ERROR,
                    "Показатели продаж не могут быть отрицательными");
        }
    }

    /**
     * Формирует человекочитаемое текстовое представление отчёта.
     *
     * @return отформатированный отчёт
     */
    @Override
    public String toString() {
        return "%s%nДата: %s%nВремя: %s%nПродано автомобилей: %d%nПродано мотоциклов: %d%n"
                .formatted(title, date, time, carsSold, motorcyclesSold);
    }
}
