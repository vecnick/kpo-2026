package studying;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.test.context.support.TestPropertySourceUtils;
import studying.configuration.ApplicationConfiguration;
import studying.model.Report;
import studying.service.ReportSender;
import studying.service.ReportService;
import studying.service.impl.ReportSenderImpl;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class ReportIntegrationTest {
    @TempDir
    Path tempDir;

    @Test
    void processesReportThroughSpringContext() throws Exception {
        try (var context = new AnnotationConfigApplicationContext()) {
            TestPropertySourceUtils.addInlinedPropertiesToEnvironment(context,
                    "report.storage.directory=" + tempDir.toAbsolutePath());
            context.register(ApplicationConfiguration.class);
            context.refresh();

            var service = context.getBean(ReportService.class);
            var sender = (ReportSenderImpl) context.getBean(ReportSender.class);
            var report = new Report("Продажи", LocalDate.of(2026, 9, 29),
                    LocalTime.of(12, 30, 15), 10, 5);

            service.process(report, "student@hse.ru");

            var file = tempDir.resolve("report-2026-09-29-12-30-15.txt");
            assertEquals(report.toString(), Files.readString(file));
            assertEquals(report, sender.getLastDelivery().report());
            assertEquals("student@hse.ru", sender.getLastDelivery().email());
            assertNotNull(service);
        }
    }
}
