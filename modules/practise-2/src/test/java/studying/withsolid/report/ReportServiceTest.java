package studying.withsolid.report;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import studying.withsolid.delivery.ReportSender;
import studying.withsolid.error.ApplicationErrorCode;
import studying.withsolid.error.ApplicationException;
import studying.withsolid.persistence.ReportSaver;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Проверяет основной сценарий обработки отчёта и его входные данные.
 */
class ReportServiceTest {
    @Test
    @DisplayName("Сначала сохраняет отчёт, затем отправляет тот же отчёт")
    void processSavesAndThenSendsTheSameReport() {
        var calls = new ArrayList<String>();
        ReportSaver saver = report -> calls.add("save:" + report.title());
        ReportSender sender = (report, email) ->
                calls.add("send:" + report.title() + ":" + email);
        var report = createReport();

        new ReportService(saver, sender).process(report, "student@hse.ru");

        assertEquals(
                List.of("save:Продажи", "send:Продажи:student@hse.ru"),
                calls
        );
    }

    @Test
    @DisplayName("Отклоняет отсутствующий отчёт до сохранения и отправки")
    void processRejectsMissingReport() {
        var calls = new ArrayList<String>();
        ReportSaver saver = report -> calls.add("save");
        ReportSender sender = (report, email) -> calls.add("send");
        var service = new ReportService(saver, sender);

        var exception = assertThrows(ApplicationException.class,
                () -> service.process(null, "student@hse.ru"));

        assertEquals(ApplicationErrorCode.VALIDATION_ERROR, exception.getCode());
        assertEquals(List.of(), calls);
    }

    @Test
    @DisplayName("Отклоняет пустой email до сохранения и отправки")
    void processRejectsBlankEmail() {
        var calls = new ArrayList<String>();
        ReportSaver saver = report -> calls.add("save");
        ReportSender sender = (report, email) -> calls.add("send");
        var service = new ReportService(saver, sender);

        var exception = assertThrows(ApplicationException.class,
                () -> service.process(createReport(), " "));

        assertEquals(ApplicationErrorCode.VALIDATION_ERROR, exception.getCode());
        assertEquals(List.of(), calls);
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
