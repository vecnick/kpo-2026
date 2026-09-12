package studying.withsolid.delivery;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import studying.withsolid.error.ApplicationErrorCode;
import studying.withsolid.error.ApplicationException;
import studying.withsolid.report.Report;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Проверяет имитацию email-доставки и валидацию её входных данных.
 */
class EmailReportSenderTest {
    @Test
    @DisplayName("Выводит адрес получателя и заголовок отчёта")
    void sendPrintsDeliveryMessage() {
        var originalOutput = System.out;
        var output = new ByteArrayOutputStream();
        var report = createReport();

        try (var interceptedOutput = new PrintStream(output, true, StandardCharsets.UTF_8)) {
            System.setOut(interceptedOutput);
            new EmailReportSender().send(report, "student@hse.ru");
        } finally {
            System.setOut(originalOutput);
        }

        var message = output.toString(StandardCharsets.UTF_8);
        assertTrue(message.contains("Продажи"));
        assertTrue(message.contains("student@hse.ru"));
    }

    @Test
    @DisplayName("Отклоняет отсутствующий email получателя")
    void sendRejectsMissingEmail() {
        var exception = assertThrows(ApplicationException.class,
                () -> new EmailReportSender().send(createReport(), null));

        assertEquals(ApplicationErrorCode.VALIDATION_ERROR, exception.getCode());
    }

    @Test
    @DisplayName("Отклоняет отсутствующий отчёт")
    void sendRejectsMissingReport() {
        var exception = assertThrows(ApplicationException.class,
                () -> new EmailReportSender().send(null, "student@hse.ru"));

        assertEquals(ApplicationErrorCode.VALIDATION_ERROR, exception.getCode());
    }

    private Report createReport() {
        return Report.builder()
                .title("Продажи")
                .date(LocalDate.of(2026, 9, 11))
                .time(LocalTime.NOON)
                .carsSold(100)
                .motorcyclesSold(50)
                .build();
    }
}
