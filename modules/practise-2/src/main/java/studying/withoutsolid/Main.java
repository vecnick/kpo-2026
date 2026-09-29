package studying.withoutsolid;

public class Main {
    static void main() {
        var reportService = new ReportService();
        var report = reportService.generateReport();

        reportService.saveReport(report, "without-solid-report.txt");
        reportService.sendReport(report, "example@example.com");
    }
}
