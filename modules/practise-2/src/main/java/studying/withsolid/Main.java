package studying.withsolid;

import studying.withsolid.model.Report;
import studying.withsolid.service.ReportService;
import studying.withsolid.service.impl.CompositeReportSaver;
import studying.withsolid.service.impl.EmailReportSender;
import studying.withsolid.service.impl.JsonReportSaver;
import studying.withsolid.service.impl.TextReportSaver;
import studying.withsolid.service.impl.XmlReportSaver;

import java.time.LocalDateTime;

public class Main {
    /**
     * Runs the report creation, persistence, and delivery demonstration.
     *
     * @param args command-line arguments
     */
    public static void main(String[] args) {
        var now = LocalDateTime.now();
        var report = Report.builder()
                .title("Отчёт")
                .date(now.toLocalDate())
                .time(now.toLocalTime())
                .carsSold(100)
                .motorcyclesSold(50)
                .build();

        var reportService = new ReportService(
                new CompositeReportSaver(
                        new TextReportSaver(),
                        new JsonReportSaver(),
                        new XmlReportSaver()
                ),
                new EmailReportSender()
        );

        reportService.process(report, "example@example.com");
    }
}
