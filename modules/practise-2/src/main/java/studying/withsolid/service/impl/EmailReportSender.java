package studying.withsolid.service.impl;

import studying.withsolid.exception.ApplicationErrorCode;
import studying.withsolid.exception.ApplicationException;
import studying.withsolid.model.Report;
import studying.withsolid.service.ReportSender;

/** Imitates report delivery by email. */
public final class EmailReportSender implements ReportSender {
    /**
     * Prints a message confirming report delivery to the recipient.
     *
     * @param report report to send
     * @param email recipient email address
     */
    @Override
    public void send(Report report, String email) {
        if (report == null) {
            throw validationError("Отчёт не задан");
        }
        if (email == null || email.isBlank()) {
            throw validationError("Email получателя не задан");
        }

        System.out.printf("Отчёт «%s» отправлен на email: %s%n", report.title(), email);
    }

    private ApplicationException validationError(String message) {
        return new ApplicationException(ApplicationErrorCode.VALIDATION_ERROR, message);
    }
}
