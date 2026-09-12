package studying.withsolid;

import studying.withsolid.model.Report;
import studying.withsolid.service.ReportService;
import studying.withsolid.service.impl.CompositeReportSaver;
import studying.withsolid.service.impl.EmailReportSender;
import studying.withsolid.service.impl.JsonReportSaver;
import studying.withsolid.service.impl.SoapReportSaver;

import java.time.LocalDateTime;

/**
 * Демонстрирует сохранение одного отчёта в JSON и SOAP и условную email-отправку.
 */
public final class Main {
    private Main() {
    }

    /**
     * Запускает демонстрационный сценарий приложения.
     *
     * @param args аргументы командной строки; не используются
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
                        new JsonReportSaver(),
                        new SoapReportSaver()
                ),
                new EmailReportSender()
        );

        reportService.process(report, "example@example.com");
    }
}
