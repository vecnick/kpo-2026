package studying.service.impl;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import studying.withsolid.model.Report;
import studying.withsolid.service.impl.JsonReportSaver;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.assertTrue;

class JsonReportSaverTest {
    @TempDir
    Path directory;

    @Test
    void savesJsonRepresentation() throws Exception {
        new JsonReportSaver(directory).save(report());

        var content = Files.readString(directory.resolve("report-2026-09-11-12-00-00.json"));
        assertTrue(content.contains("\"title\": \"Продажи & сервис\""));
        assertTrue(content.contains("\"carsSold\": 100"));
    }

    private Report report() {
        return Report.builder().title("Продажи & сервис")
                .date(LocalDate.of(2026, 9, 11)).time(LocalTime.NOON)
                .carsSold(100).motorcyclesSold(50).build();
    }
}
