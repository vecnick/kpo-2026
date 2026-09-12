package studying.withsolid.delivery;

import studying.withsolid.error.ApplicationException;
import studying.withsolid.report.Report;

import static studying.withsolid.error.ApplicationErrorCode.VALIDATION_ERROR;

/**
 * Имитирует отправку отчёта по email, выводя результат доставки в консоль.
 */
public final class EmailReportSender implements ReportSender {
    /**
     * Создаёт отправителя, имитирующего доставку отчёта по email.
     */
    public EmailReportSender() {
    }

    /**
     * Проверяет отчёт и получателя, затем выводит сообщение об отправке.
     *
     * @param report отправляемый отчёт
     * @param email email получателя
     * @throws ApplicationException если отчёт или email не заданы
     */
    @Override
    public void send(Report report, String email) {
        if (report == null) {
            throw new ApplicationException(VALIDATION_ERROR,
                    "Отчёт не должен отсутствовать");
        }
        if (email == null || email.isBlank()) {
            throw new ApplicationException(VALIDATION_ERROR,
                    "Email получателя не должен быть пустым");
        }

        System.out.printf(
                "Отчёт \"%s\" отправлен на email: %s%n",
                report.title(),
                email
        );
    }
}
