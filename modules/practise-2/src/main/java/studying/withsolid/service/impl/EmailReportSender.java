package studying.withsolid.service.impl;

import studying.withsolid.exception.ApplicationErrorCode;
import studying.withsolid.exception.ApplicationException;
import studying.withsolid.model.Report;
import studying.withsolid.service.ReportSender;

/**
 * Реализация {@link ReportSender}, имитирующая отправку отчёта по email.
 * <p>
 * Реальная интеграция с почтовым сервером (SMTP) не выполняется:
 * вместо этого факт отправки и адрес получателя выводятся в консоль,
 * что достаточно для учебной демонстрации сценария.
 */
public final class EmailReportSender implements ReportSender {

    /**
     * Имитирует отправку отчёта на указанный email.
     *
     * @param report отчёт для отправки; не должен быть {@code null}
     * @param email  адрес получателя; не должен быть {@code null} или пустым
     * @throws ApplicationException с кодом {@link ApplicationErrorCode#VALIDATION_ERROR},
     *         если отчёт не передан или email пуст/отсутствует
     */
    @Override
    public void send(Report report, String email) {
        // Этому классу нужны оба параметра, поэтому проверяет оба.
        if (report == null) {
            throw new ApplicationException(
                    ApplicationErrorCode.VALIDATION_ERROR,
                    "Отчёт не должен быть null"
            );
        }
        if (email == null || email.isBlank()) {
            throw new ApplicationException(
                    ApplicationErrorCode.VALIDATION_ERROR,
                    "Email получателя не должен быть пустым"
            );
        }

        // Имитация отправки: в реальной системе здесь был бы вызов
        // SMTP-клиента или сторонней библиотеки для отправки почты.
        System.out.printf(
                "Отчёт \"%s\" отправлен на адрес %s%n",
                report.title(),
                email
        );
    }
}