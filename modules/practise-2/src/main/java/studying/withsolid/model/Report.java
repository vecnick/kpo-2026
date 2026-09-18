package studying.withsolid.model;

import lombok.Builder;

import java.time.LocalDate;
import java.time.LocalTime;

/**
 * Неизменяемые данные отчёта о продажах автомобилей и мотоциклов.
 * <p>
 * Модель не зависит от способа сохранения или отправки отчёта —
 * она не знает ни о файловой системе, ни о {@code Path}, ни об email.
 * Это позволяет свободно добавлять новые {@code ReportSaver} и
 * {@code ReportSender}, не затрагивая саму модель.
 *
 * @param title             заголовок отчёта
 * @param date              дата, к которой относится отчёт
 * @param time              время формирования отчёта
 * @param carsSold          количество проданных автомобилей; не должно быть отрицательным
 * @param motorcyclesSold   количество проданных мотоциклов; не должно быть отрицательным
 */
@Builder
public record Report(
        String title,
        LocalDate date,
        LocalTime time,
        int carsSold,
        int motorcyclesSold
) {

    /**
     * Формирует человекочитаемое текстовое представление отчёта.
     * <p>
     * Используется, например, {@code TextReportSaver} для записи
     * отчёта в текстовый файл, а также может применяться для вывода
     * содержимого отчёта в консоль или лог.
     *
     * @return многострочный текст отчёта с заголовком, датой, временем
     *         и показателями продаж
     */
    public String toReportText() {
        return """
                Отчёт: %s
                Дата: %s
                Время: %s
                Продано автомобилей: %d
                Продано мотоциклов: %d
                """.formatted(title, date, time, carsSold, motorcyclesSold);
    }
}