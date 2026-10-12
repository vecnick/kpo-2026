package studying.service;

import java.time.LocalDate;
import java.time.LocalTime;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InOrder;
import studying.exception.ApplicationErrorCode;
import studying.exception.ApplicationException;
import studying.model.Report;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.same;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;

class ReportServiceTest {
    /** Fixed report date for repeatable tests. */
    private static final LocalDate DATE = LocalDate.parse("2026-09-11");
    /** Fixed report time for repeatable tests. */
    private static final LocalTime TIME = LocalTime.parse("12:30");
    /** Number of cars in a valid report. */
    private static final int CARS_SOLD = 100;
    /** Number of motorcycles in a valid report. */
    private static final int MOTORCYCLES_SOLD = 50;

    @Test
    @DisplayName("Сервис сохраняет отчёт перед отправкой")
    void savesBeforeSendingWithExpectedArguments() {
        ReportSaver saver = mock(ReportSaver.class);
        ReportSender sender = mock(ReportSender.class);
        Report report = createReport();
        String email = "student@hse.ru";

        new ReportService(saver, sender).process(report, email);

        verify(saver, times(1)).save(same(report));
        verify(sender, times(1)).send(same(report), eq(email));

        InOrder order = inOrder(saver, sender);
        order.verify(saver).save(report);
        order.verify(sender).send(report, email);
        verifyNoMoreInteractions(saver, sender);
    }

    @Test
    @DisplayName("При ошибке сохранения отправка не происходит")
    void doesNotSendWhenSavingFails() {
        ReportSaver saver = mock(ReportSaver.class);
        ReportSender sender = mock(ReportSender.class);
        Report report = createReport();
        ApplicationException failure = new ApplicationException(
                ApplicationErrorCode.FILE_WRITE_ERROR,
                "Не удалось сохранить отчёт");
        doThrow(failure).when(saver).save(same(report));

        ApplicationException thrown = assertThrows(ApplicationException.class,
                () -> new ReportService(saver, sender)
                        .process(report, "student@hse.ru"));

        assertSame(failure, thrown);
        verify(saver, times(1)).save(same(report));
        verifyNoInteractions(sender);
    }

    private Report createReport() {
        return new Report("Продажи", DATE, TIME,
                CARS_SOLD, MOTORCYCLES_SOLD);
    }
}
