package studying.withsolid;

import studying.withsolid.model.Report;

import java.time.LocalDateTime;
import studying.withsolid.service.ReportServiceFacade;
import studying.withsolid.service.impl.ReportSaverImpl;
import studying.withsolid.service.impl.ReportSenderImpl;

public class Main {
    static void main() {
        var now = LocalDateTime.now();
        var report = new Report("Отчёт", now.toLocalDate(),
                now.toLocalTime(), 100, 50);

        var reportService = new ReportServiceFacade(
                new ReportSenderImpl(),
                new ReportSaverImpl()
        );

        reportService.process(report, "example@example.com");
    }
}
