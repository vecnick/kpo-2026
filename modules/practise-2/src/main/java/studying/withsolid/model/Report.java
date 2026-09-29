package studying.withsolid.model;

import lombok.Builder;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

/**
 * Immutable sales report data. It does not know about files, email or consoles.
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
     * Formats the report as a human-readable text block.
     *
     * @return text representation of the report
     */
    @Override
    public String toString() {
        var timeText = time == null ? "" : time.truncatedTo(ChronoUnit.SECONDS)
                .format(DateTimeFormatter.ISO_LOCAL_TIME);
        return """
                %s
                Дата: %s
                Время: %s
                --------------------------------
                Продано автомобилей: %d шт.
                Продано мотоциклов: %d шт.
                --------------------------------
                """.formatted(title, date, timeText, carsSold, motorcyclesSold);
    }
}
