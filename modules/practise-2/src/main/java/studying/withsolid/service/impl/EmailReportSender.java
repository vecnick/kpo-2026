package studying.withsolid.service.impl;

import studying.withsolid.exception.ApplicationErrorCode;
import studying.withsolid.exception.ApplicationException;
import studying.withsolid.model.Report;
import studying.withsolid.service.ReportSender;

/**
 * Имитирует отправку по электронной почте записью сообщения в консольный лог.
 */
public final class EmailReportSender implements ReportSender {
    /**
     * Проверяет данные и выводит в лог заголовок отчёта и адрес получателя.
     *
     * @param report отправляемый отчёт
     * @param email адрес получателя
     * @throws ApplicationException если отчёт или email не заданы
     */
    @Override
    public void send(Report report, String email) {
        if (report == null) {
            throw new ApplicationException(ApplicationErrorCode.VALIDATION_ERROR,
                    "Отчёт не задан");
        }
        if (email == null || email.isBlank()) {
            throw new ApplicationException(ApplicationErrorCode.VALIDATION_ERROR,
                    "Email получателя не задан");
        }

        System.out.printf("[EMAIL] Отчёт \"%s\" отправлен получателю %s%n",
                report.title(), email);
    }
}
