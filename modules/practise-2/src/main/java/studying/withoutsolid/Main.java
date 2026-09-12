package studying.withoutsolid;

/** Demonstrates the monolithic implementation without SOLID. */
public class Main {
    /**
     * Generates, saves, and sends one report.
     *
     * @param args command-line arguments
     */
    public static void main(String[] args) {
        var reportService = new ReportService();
        var report = reportService.generateReport();

        reportService.saveReport(report, "without-solid-report.txt");
        reportService.sendReport(report, "example@example.com");
    }
}
