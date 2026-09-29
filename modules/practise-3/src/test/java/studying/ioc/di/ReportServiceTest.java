package studying.ioc.di;

import org.junit.jupiter.api.Test;
import studying.model.Report;
import studying.service.ReportSaver;
import studying.service.ReportSender;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ReportServiceTest {
    @Test
    void usesInjectedDependencies() {
        var calls = new ArrayList<String>();
        ReportSaver saver = report -> calls.add("save");
        ReportSender sender = (report, email) -> calls.add("send:" + email);
        var report = new Report("Продажи", LocalDate.of(2026, 9, 29),
                LocalTime.NOON, 2, 1);

        new ReportService(saver, sender).process(report, "student@hse.ru");

        assertEquals(java.util.List.of("save", "send:student@hse.ru"), calls);
    }
}
