package studying.withsolid.service.impl;

import studying.withsolid.model.Report;

import java.nio.file.Path;

/**
 * Сохраняет отчёт внутри XML-конверта SOAP 1.2 в кодировке UTF-8.
 */
public final class SoapReportSaver extends AbstractFileReportSaver {
    /**
     * Создаёт сохранитель, записывающий файлы в каталог {@code reports}.
     */
    public SoapReportSaver() {
        this(Path.of("reports"));
    }

    /**
     * Создаёт SOAP-сохранитель с заданным каталогом назначения.
     *
     * @param reportsDirectory каталог для файлов отчётов
     */
    public SoapReportSaver(Path reportsDirectory) {
        super(reportsDirectory, ".soap.xml");
    }

    @Override
    protected String serialize(Report report) {
        return """
                <?xml version="1.0" encoding="UTF-8"?>
                <soap:Envelope xmlns:soap="http://www.w3.org/2003/05/soap-envelope"
                               xmlns:report="urn:hse:reports">
                  <soap:Header/>
                  <soap:Body>
                    <report:SalesReport>
                      <report:title>%s</report:title>
                      <report:date>%s</report:date>
                      <report:time>%s</report:time>
                      <report:carsSold>%d</report:carsSold>
                      <report:motorcyclesSold>%d</report:motorcyclesSold>
                    </report:SalesReport>
                  </soap:Body>
                </soap:Envelope>
                """.formatted(
                escapeXml(report.title()),
                report.date(),
                report.time(),
                report.carsSold(),
                report.motorcyclesSold());
    }

    private String escapeXml(String value) {
        return value
                .replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&apos;");
    }
}
