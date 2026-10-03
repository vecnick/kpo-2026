package studying.integration;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Map;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.core.env.MapPropertySource;
import studying.configuration.ApplicationConfiguration;
import studying.model.Report;
import studying.service.ReportSaver;
import studying.service.ReportSender;
import studying.service.ReportService;
import studying.service.impl.ReportSaverImpl;
import studying.service.impl.ReportSenderImpl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ReportIntegrationTest {
    /** Fixed report date for predictable file names. */
    private static final LocalDate DATE = LocalDate.parse("2026-09-11");
    /** Fixed report time for predictable file names. */
    private static final LocalTime TIME = LocalTime.parse("12:30:05");
    /** Number of cars in the demonstration report. */
    private static final int CARS_SOLD = 100;
    /** Number of motorcycles in the demonstration report. */
    private static final int MOTORCYCLES_SOLD = 50;

    /** Temporary storage isolated from the project directory. */
    @TempDir
    private Path temporaryDirectory;

    @Test
    @DisplayName("Настоящие сервисы сохраняют отчёт и запоминают отправку")
    void processesReportWithRealComponents() throws IOException {
        ReportSaverImpl saver = new ReportSaverImpl(temporaryDirectory);
        ReportSenderImpl sender = new ReportSenderImpl();
        ReportService service = new ReportService(saver, sender);
        Report report = new Report("Продажи", DATE, TIME,
                CARS_SOLD, MOTORCYCLES_SOLD);
        String email = "student@hse.ru";

        service.process(report, email);

        Path expectedFile = temporaryDirectory.resolve(
                "report-2026-09-11-12-30-05.txt");
        assertTrue(Files.exists(expectedFile));
        String content = Files.readString(expectedFile);
        assertTrue(content.contains("Продажи"));
        assertTrue(content.contains("Дата: 2026-09-11"));
        assertTrue(content.contains("Время: 12:30:05"));
        assertTrue(content.contains("Продано автомобилей: 100 шт."));
        assertTrue(content.contains("Продано мотоциклов: 50 шт."));

        ReportSenderImpl.Delivery delivery = sender.getLastDelivery();
        assertNotNull(delivery);
        assertSame(report, delivery.report());
        assertEquals(email, delivery.email());
    }

    @Test
    @DisplayName("Spring создаёт ReportService и разрешает обе зависимости")
    void springContextCreatesReportService() {
        try (var context = new AnnotationConfigApplicationContext()) {
            context.getEnvironment().getPropertySources().addFirst(
                    new MapPropertySource("test", Map.of(
                            "report.storage.directory",
                            temporaryDirectory.toString())));
            context.register(ApplicationConfiguration.class);
            context.refresh();

            assertNotNull(context.getBean(ReportService.class));
            assertInstanceOf(ReportSaverImpl.class,
                    context.getBean(ReportSaver.class));
            assertInstanceOf(ReportSenderImpl.class,
                    context.getBean(ReportSender.class));
        }
    }
}
