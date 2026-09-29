package studying.withsolid.service.impl;

import studying.withsolid.exception.ApplicationErrorCode;
import studying.withsolid.exception.ApplicationException;
import studying.withsolid.model.Report;
import studying.withsolid.service.ReportSender;

/**
 * Imitates email delivery by printing the report title and recipient address.
 */
public class EmailReportSender implements ReportSender {
    /**
     * Prints a delivery message instead of talking to SMTP.
     *
     * @param report report to send
     * @param email  recipient address
     */
    @Override
    public void send(Report report, String email) {
        if (report == null) {
            throw new ApplicationException(ApplicationErrorCode.VALIDATION_ERROR, "Отчёт не задан");
        }
        if (email == null || email.isBlank()) {
            throw new ApplicationException(ApplicationErrorCode.VALIDATION_ERROR, "Email получателя не задан");
        }
        if (report.carsSold() < 0 || report.motorcyclesSold() < 0) {
            throw new ApplicationException(
                    ApplicationErrorCode.VALIDATION_ERROR,
                    "Показатели продаж не могут быть отрицательными"
            );
        }

        System.out.printf("Отправка отчёта %s на email: %s%n", report.title(), email);
    }
}
