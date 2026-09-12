package studying.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import studying.withsolid.model.Report;
import studying.withsolid.service.impl.JsonReportSaver;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

class JsonReportSaverTest {
    @TempDir
    Path temporaryDirectory;

    @Test
    @DisplayName("Сохраняет все поля отчёта в JSON-файл")
    void saveWritesReportToJsonFile() throws Exception {
        var file = temporaryDirectory.resolve("report-2026-09-11-12-00-00.json");

        new JsonReportSaver(temporaryDirectory).save(createReport());

        var json = new ObjectMapper().readTree(Files.readString(file));
        assertEquals("Продажи", json.get("title").asText());
        assertEquals("2026-09-11", json.get("date").asText());
        assertEquals("12:00:00", json.get("time").asText());
        assertEquals(100, json.get("carsSold").asInt());
        assertEquals(50, json.get("motorcyclesSold").asInt());
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
