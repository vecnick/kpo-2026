package studying.service.impl;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import studying.exception.ApplicationErrorCode;
import studying.exception.ApplicationException;
import studying.model.Report;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ReportSaverImplTest {
    @TempDir
    Path tempDir;

    @Test
    void savesReportWithExpectedNameAndContent() throws Exception {
        var report = report();
        var expectedFile = tempDir.resolve("report-2026-09-29-12-30-15.txt");

        new ReportSaverImpl(tempDir).save(report);

        assertEquals(report.toString(), Files.readString(expectedFile));
    }

    @Test
    void keepsOriginalWriteFailure() throws Exception {
        var fileInsteadOfDirectory = tempDir.resolve("file");
        Files.writeString(fileInsteadOfDirectory, "data");

        var exception = assertThrows(ApplicationException.class,
                () -> new ReportSaverImpl(fileInsteadOfDirectory).save(report()));

        assertEquals(ApplicationErrorCode.FILE_WRITE_ERROR, exception.getCode());
        assertTrue(exception.getCause() instanceof IOException);
    }

    private Report report() {
        return new Report("Продажи", LocalDate.of(2026, 9, 29),
                LocalTime.of(12, 30, 15), 10, 5);
    }
}
