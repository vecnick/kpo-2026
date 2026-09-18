package studying.service.impl;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import studying.withsolid.exception.ApplicationErrorCode;
import studying.withsolid.exception.ApplicationException;
import studying.withsolid.model.Report;
import studying.withsolid.service.impl.JsonReportSaver;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.LocalTime;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class JsonReportSaverTest {

    @Test
    @DisplayName("Сохраняет отчёт в JSON-файл с ожидаемым содержимым")
    void saveWritesReportToJsonFile(@TempDir Path tempDir) throws IOException {
        var report = createReport();

        new JsonReportSaver(tempDir).save(report);

        var file = tempDir.resolve("report-2026-09-11-12-00-00.json");
        var content = Files.readString(file);

        assertTrue(content.contains("\"title\": \"Продажи\""));
        assertTrue(content.contains("\"carsSold\": 100"));
        assertTrue(content.contains("\"motorcyclesSold\": 50"));
    }

    @Test
    @DisplayName("Выбрасывает прикладную ошибку, если отчёт не задан")
    void saveRejectsMissingReport(@TempDir Path tempDir) {
        var exception = assertThrows(ApplicationException.class,
                () -> new JsonReportSaver(tempDir).save(null));

        assertEquals(ApplicationErrorCode.VALIDATION_ERROR, exception.getCode());
    }

    private Report createReport() {
        return Report.builder()
                .title("Продажи")
                .date(LocalDate.of(2026, 9, 11))
                .time(LocalTime.NOON)
                .carsSold(100)
                .motorcyclesSold(50)
                .build();
    }
}