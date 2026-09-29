package studying.ioc.locator;

import java.time.LocalDate;
import java.time.LocalTime;
import studying.model.Report;
import studying.service.ReportSaver;
import studying.service.ReportSender;
import studying.service.impl.ReportSaverImpl;
import studying.service.impl.ReportSenderImpl;

public final class Main {
    private static final int DEMO_CARS_SOLD = 100;
    private static final int DEMO_MOTORCYCLES_SOLD = 50;

    private Main() { }

    public static void main(final String[] args) {
        var locator = new ServiceLocator();
        locator.register(ReportSaver.class, new ReportSaverImpl());
        locator.register(ReportSender.class, new ReportSenderImpl());

        var report = new Report("Продажи", LocalDate.now(), LocalTime.now(),
                DEMO_CARS_SOLD, DEMO_MOTORCYCLES_SOLD);
        new ReportService(locator).process(report, "student@hse.ru");
    }
}
