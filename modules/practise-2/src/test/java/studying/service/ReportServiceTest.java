package studying.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import studying.withsolid.model.Report;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import studying.withsolid.service.ReportSaver;
import studying.withsolid.service.ReportSender;
import studying.withsolid.service.ReportService;

import studying.withsolid.exception.ApplicationErrorCode;
import studying.withsolid.exception.ApplicationException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ReportServiceTest {
    @Test
    @DisplayName("Обрабатывает отчёт: сначала сохраняет, затем отправляет")
    void processSavesAndThenSendsTheSameReport() {
        var calls = new ArrayList<String>();
        ReportSaver saver = report -> calls.add("save:" + report.title());
        ReportSender sender = (report, email) -> calls.add("send:" + report.title() + ":" + email);
        var report = createReport();

        new ReportService(saver, sender).process(report, "student@hse.ru");

        assertEquals(List.of("save:Продажи", "send:Продажи:student@hse.ru"), calls);
    }

    @Test
    @DisplayName("Выбрасывает прикладную ошибку, если отчёт не задан")
    void processRejectsMissingReport() {
        ReportSaver saver = report -> { };
        ReportSender sender = (report, email) -> { };

        var exception = assertThrows(ApplicationException.class,
                () -> new ReportService(saver, sender).process(null, "student@hse.ru"));

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
