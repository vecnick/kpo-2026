package studying.withoutsolid;

/**
 * Демонстрирует монолитную реализацию сервиса без применения принципов SOLID.
 */
public final class Main {
    private Main() {
    }

    /**
     * Формирует, сохраняет и отправляет один отчёт через монолитный сервис.
     *
     * @param args аргументы командной строки; в демонстрации не используются
     */
    public static void main(String[] args) {
        var reportService = new ReportService();
        var report = reportService.generateReport();

        reportService.saveReport(report, "without-solid-report.txt");
        reportService.sendReport(report, "example@example.com");
    }
}
