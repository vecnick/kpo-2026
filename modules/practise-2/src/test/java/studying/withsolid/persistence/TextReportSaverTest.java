package studying.withsolid.persistence;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import studying.withsolid.error.ApplicationErrorCode;
import studying.withsolid.error.ApplicationException;
import studying.withsolid.report.Report;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Проверяет текстовое сохранение отчёта и преобразование ошибок файловой системы.
 */
class TextReportSaverTest {
    @TempDir
    Path temporaryDirectory;

    @Test
    @DisplayName("Сохраняет текст отчёта в файл с датой и временем в имени")
    void saveWritesReportToGeneratedFile() throws IOException {
        var report = createReport();
        var file = temporaryDirectory.resolve("report-2026-09-11-12-00-00.txt");

        new TextReportSaver(temporaryDirectory).save(report);

        assertEquals(report.toString(), Files.readString(file));
    }

    @Test
    @DisplayName("Отклоняет отсутствующий отчёт")
    void saveRejectsMissingReport() {
        var exception = assertThrows(ApplicationException.class,
                () -> new TextReportSaver(temporaryDirectory).save(null));

        assertEquals(ApplicationErrorCode.VALIDATION_ERROR, exception.getCode());
    }

    @Test
    @DisplayName("Сохраняет код и исходную причину ошибки записи файла")
    void savePreservesFileWriteFailureCause() throws IOException {
        var fileInsteadOfDirectory = Files.createFile(
                temporaryDirectory.resolve("not-a-directory")
        );

        var exception = assertThrows(ApplicationException.class,
                () -> new TextReportSaver(fileInsteadOfDirectory).save(createReport()));

        assertEquals(ApplicationErrorCode.FILE_WRITE_ERROR, exception.getCode());
        assertInstanceOf(IOException.class, exception.getCause());
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
