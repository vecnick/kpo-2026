package studying.service.impl;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import studying.withsolid.exception.ApplicationErrorCode;
import studying.withsolid.exception.ApplicationException;
import studying.withsolid.model.Report;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.LocalTime;
import studying.withsolid.service.impl.ReportSaverImpl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TextReportSaverTest {
    @TempDir
    Path tempDir;

    @Test
    @DisplayName("Сохраняет текстовое представление отчёта в динамически сформированный файл")
    void saveWritesReportToFile() throws Exception {
        var report = createReport();
        var file = tempDir.resolve("report-2026-09-11-12-00-00.txt");

        new ReportSaverImpl(tempDir).save(report);

        assertEquals(report.toString(), Files.readString(file));
    }

    @Test
    @DisplayName("Выбрасывает прикладную ошибку, если отчёт не задан")
    void saveRejectsMissingReport() {
        var exception = assertThrows(ApplicationException.class,
                () -> new ReportSaverImpl().save(null));

        assertEquals(ApplicationErrorCode.VALIDATION_ERROR, exception.getCode());
    }

    @Test
    @DisplayName("Сохраняет исходную причину ошибки записи")
    void saveKeepsWriteFailureCause() throws Exception {
        var fileInsteadOfDirectory = tempDir.resolve("file");
        Files.writeString(fileInsteadOfDirectory, "data");

        var exception = assertThrows(ApplicationException.class,
                () -> new ReportSaverImpl(fileInsteadOfDirectory).save(createReport()));

        assertEquals(ApplicationErrorCode.FILE_WRITE_ERROR, exception.getCode());
        assertTrue(exception.getCause() instanceof java.io.IOException);
    }

    private Report createReport() {
        return new Report("Продажи", LocalDate.of(2026, 9, 11),
                LocalTime.NOON, 100, 50);
    }
}
