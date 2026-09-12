package studying.service.impl;

import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import studying.withsolid.model.Report;
import studying.withsolid.service.impl.XmlReportSaver;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

class XmlReportSaverTest {
    @TempDir
    Path temporaryDirectory;

    @Test
    @DisplayName("Сохраняет все поля отчёта в XML-файл")
    void saveWritesReportToXmlFile() throws Exception {
        var file = temporaryDirectory.resolve("report-2026-09-11-12-00-00.xml");

        new XmlReportSaver(temporaryDirectory).save(createReport());

        var xml = new XmlMapper().readTree(Files.readString(file));
        assertEquals("Продажи", xml.get("title").asText());
        assertEquals("2026-09-11", xml.get("date").asText());
        assertEquals("12:00:00", xml.get("time").asText());
        assertEquals(100, xml.get("carsSold").asInt());
        assertEquals(50, xml.get("motorcyclesSold").asInt());
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
