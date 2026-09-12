package studying.service.impl;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import studying.withsolid.model.Report;
import studying.withsolid.service.impl.SoapReportSaver;

import javax.xml.parsers.DocumentBuilderFactory;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SoapReportSaverTest {
    @TempDir
    Path directory;

    @Test
    void savesWellFormedSoapEnvelope() throws Exception {
        var file = directory.resolve("report-2026-09-11-12-00-00.soap.xml");
        new SoapReportSaver(directory).save(report());

        var factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(true);
        var document = factory.newDocumentBuilder().parse(file.toFile());

        assertEquals("Envelope", document.getDocumentElement().getLocalName());
        assertEquals("Продажи & сервис", document
                .getElementsByTagNameNS("urn:hse:reports", "title").item(0).getTextContent());
    }

    private Report report() {
        return Report.builder().title("Продажи & сервис")
                .date(LocalDate.of(2026, 9, 11)).time(LocalTime.NOON)
                .carsSold(100).motorcyclesSold(50).build();
    }
}
