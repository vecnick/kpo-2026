package studying.withsolid.service;

import lombok.RequiredArgsConstructor;
import studying.withsolid.exception.ApplicationErrorCode;
import studying.withsolid.exception.ApplicationException;
import studying.withsolid.model.Report;

/**
 * Orchestrates report processing: persist first, then deliver.
 */
@RequiredArgsConstructor
public final class ReportService {
    private final ReportSaver reportSaver;
    private final ReportSender reportSender;

    /**
     * Saves the report and then sends it to the recipient.
     *
     * @param report report to process
     * @param email  recipient email
     */
    public void process(Report report, String email) {
        validate(report, email);
        reportSaver.save(report);
        reportSender.send(report, email);
    }

    private static void validate(Report report, String email) {
        if (report == null) {
            throw new ApplicationException(ApplicationErrorCode.VALIDATION_ERROR, "Отчёт не задан");
        }
        if (email == null || email.isBlank()) {
            throw new ApplicationException(ApplicationErrorCode.VALIDATION_ERROR, "Email получателя не задан");
        }
        if (report.title() == null || report.title().isBlank()) {
            throw new ApplicationException(ApplicationErrorCode.VALIDATION_ERROR, "Заголовок отчёта не задан");
        }
        if (report.date() == null || report.time() == null) {
            throw new ApplicationException(ApplicationErrorCode.VALIDATION_ERROR, "Дата или время отчёта не заданы");
        }
        if (report.carsSold() < 0 || report.motorcyclesSold() < 0) {
            throw new ApplicationException(
                    ApplicationErrorCode.VALIDATION_ERROR,
                    "Показатели продаж не могут быть отрицательными"
            );
        }
    }
}
