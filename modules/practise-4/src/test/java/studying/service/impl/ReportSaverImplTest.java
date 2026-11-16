package studying.service.impl;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import studying.model.Report;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ReportSaverImplTest {
    /** Fixed report date for predictable file names. */
    private static final LocalDate DATE = LocalDate.parse("2026-09-11");
    /** Fixed report time for predictable file names. */
    private static final LocalTime TIME = LocalTime.parse("12:30:05");
    /** Number of cars in the report. */
    private static final int CARS_SOLD = 100;
    /** Number of motorcycles in the report. */
    private static final int MOTORCYCLES_SOLD = 50;

    /** Temporary storage isolated from the project directory. */
    @TempDir
    private Path temporaryDirectory;

    @Test
    @DisplayName("Сохранитель создаёт файл с ожидаемым именем и содержимым")
    void savesReportWithExpectedFileNameAndContent() throws IOException {
        Report report = new Report("Продажи", DATE, TIME,
                CARS_SOLD, MOTORCYCLES_SOLD);
        Path expectedFile = temporaryDirectory.resolve(
                "report-2026-09-11-12-30-05.txt");
        String expectedContent = String.join(System.lineSeparator(),
                "Продажи",
                "Дата: 2026-09-11",
                "Время: 12:30:05",
                "--------------------------------",
                "Продано автомобилей: 100 шт.",
                "Продано мотоциклов: 50 шт.",
                "--------------------------------",
                "");

        new ReportSaverImpl(temporaryDirectory).save(report);

        try (var files = Files.list(temporaryDirectory)) {
            assertEquals(List.of(expectedFile), files.toList());
        }
        assertEquals(expectedContent, Files.readString(expectedFile));
    }
}
