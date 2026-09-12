package studying.withsolid.report;

import lombok.Builder;
import studying.withsolid.error.ApplicationException;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

import static studying.withsolid.error.ApplicationErrorCode.VALIDATION_ERROR;

/**
 * Неизменяемые данные отчёта о продажах автомобилей и мотоциклов.
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
    private static final DateTimeFormatter DISPLAY_TIME_FORMATTER =
            DateTimeFormatter.ofPattern("HH:mm:ss");

    /**
     * Проверяет обязательные поля и числовые показатели при создании отчёта.
     */
    public Report {
        if (title == null || title.isBlank()) {
            throw new ApplicationException(VALIDATION_ERROR,
                    "Заголовок отчёта не должен быть пустым");
        }
        if (date == null) {
            throw new ApplicationException(VALIDATION_ERROR,
                    "Дата отчёта не должна отсутствовать");
        }
        if (time == null) {
            throw new ApplicationException(VALIDATION_ERROR,
                    "Время отчёта не должно отсутствовать");
        }
        if (carsSold < 0) {
            throw new ApplicationException(VALIDATION_ERROR,
                    "Количество проданных автомобилей не может быть отрицательным");
        }
        if (motorcyclesSold < 0) {
            throw new ApplicationException(VALIDATION_ERROR,
                    "Количество проданных мотоциклов не может быть отрицательным");
        }
    }

    /**
     * Формирует текстовое представление отчёта без зависимости от способа его сохранения.
     *
     * @return текст отчёта
     */
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
                """.formatted(
                title,
                date,
                time.format(DISPLAY_TIME_FORMATTER),
                carsSold,
                motorcyclesSold
        );
    }
}
