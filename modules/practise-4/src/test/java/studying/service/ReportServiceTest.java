package studying.service;

import org.junit.jupiter.api.Test;
import studying.model.Report;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ReportServiceTest {
    @Test
    void savesAndThenSendsSameReport() {
        var calls = new ArrayList<String>();
        var savedReport = new AtomicReference<Report>();
        var sentReport = new AtomicReference<Report>();
        var sentEmail = new AtomicReference<String>();
        var saveCount = new AtomicInteger();
        var sendCount = new AtomicInteger();
        var report = report();
        ReportSaver saver = value -> {
            saveCount.incrementAndGet();
            savedReport.set(value);
            calls.add("save");
        };
        ReportSender sender = (value, email) -> {
            sendCount.incrementAndGet();
            sentReport.set(value);
            sentEmail.set(email);
            calls.add("send");
        };

        new ReportService(saver, sender).process(report, "student@hse.ru");

        assertEquals(1, saveCount.get());
        assertEquals(1, sendCount.get());
        assertSame(report, savedReport.get());
        assertSame(report, sentReport.get());
        assertEquals("student@hse.ru", sentEmail.get());
        assertEquals(List.of("save", "send"), calls);
    }

    @Test
    void doesNotSendWhenSavingFails() {
        ReportSaver saver = value -> {
            throw new IllegalStateException("Ошибка записи");
        };
        var sendCount = new AtomicInteger();
        ReportSender sender = (value, email) -> sendCount.incrementAndGet();
        var report = report();

        assertThrows(IllegalStateException.class,
                () -> new ReportService(saver, sender)
                        .process(report, "student@hse.ru"));

        assertEquals(0, sendCount.get());
    }

    private Report report() {
        return new Report("Продажи", LocalDate.of(2026, 9, 29),
                LocalTime.NOON, 10, 5);
    }
}
