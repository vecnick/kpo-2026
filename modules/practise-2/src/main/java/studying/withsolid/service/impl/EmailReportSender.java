package studying.withsolid.service.impl;

import studying.withsolid.exception.ApplicationErrorCode;
import studying.withsolid.exception.ApplicationException;
import studying.withsolid.model.Report;
import studying.withsolid.service.ReportSender;

public class EmailReportSender implements ReportSender {
    /**
     * Imitates sending a report by printing delivery information to the console.
     *
     * @param report report to send
     * @param email recipient email address
     * @throws ApplicationException if the report or email is missing
     */
    @Override
    public void send(Report report, String email) {
        if (report == null) {
            throw new ApplicationException(
                    ApplicationErrorCode.VALIDATION_ERROR,
                    "Report must not be null",
                    null
            );
        }

        if (email == null || email.isBlank()) {
            throw new ApplicationException(
                    ApplicationErrorCode.VALIDATION_ERROR,
                    "Email must not be blank",
                    null
            );
        }

        System.out.printf("Report '%s' sent to %s%n", report.getTitle(), email);
    }
}
