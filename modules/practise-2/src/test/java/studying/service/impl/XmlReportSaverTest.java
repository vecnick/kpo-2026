package studying.service.impl;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import studying.withsolid.exception.ApplicationErrorCode;
import studying.withsolid.exception.ApplicationException;
import studying.withsolid.model.Report;
import studying.withsolid.service.impl.XmlReportSaver;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class XmlReportSaverTest {

    @Test
    @DisplayName("Сохраняет отчёт в XML-файл с ожидаемым содержимым")
    void saveWritesReportToXmlFile(@TempDir Path tempDir) throws IOException {
        var report = createReport();

        new XmlReportSaver(tempDir).save(report);

        var file = tempDir.resolve("report-2026-09-11-12-00-00.xml");
        var content = Files.readString(file);

        assertTrue(content.contains("<title>Продажи</title>"));
        assertTrue(content.contains("<carsSold>100</carsSold>"));
        assertTrue(content.contains("<motorcyclesSold>50</motorcyclesSold>"));
    }

    @Test
    @DisplayName("Выбрасывает прикладную ошибку, если отчёт не задан")
    void saveRejectsMissingReport(@TempDir Path tempDir) {
        var exception = assertThrows(ApplicationException.class,
                () -> new XmlReportSaver(tempDir).save(null));

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