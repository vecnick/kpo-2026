package studying.withsolid;

import studying.withsolid.delivery.EmailReportSender;
import studying.withsolid.persistence.TextReportSaver;
import studying.withsolid.report.Report;
import studying.withsolid.report.ReportService;

import java.time.LocalDateTime;

/**
 * Демонстрирует сборку и запуск приложения, построенного по принципам SOLID.
 */
public final class Main {
    private Main() {
    }

    /**
     * Создаёт отчёт и передаёт конкретные реализации сохранения и отправки
     * основному сервису через конструктор.
     *
     * @param args аргументы командной строки; в демонстрации не используются
     */
    public static void main(String[] args) {
        var now = LocalDateTime.now();
        var report = Report.builder()
                .title("Отчёт о продажах транспорта")
                .date(now.toLocalDate())
                .time(now.toLocalTime())
                .carsSold(100)
                .motorcyclesSold(50)
                .build();

        var reportService = new ReportService(
                new TextReportSaver(),
                new EmailReportSender()
        );

        reportService.process(report, "example@example.com");
    }
}
