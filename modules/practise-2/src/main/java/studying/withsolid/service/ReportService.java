package studying.withsolid.service;

import studying.withsolid.exception.ApplicationErrorCode;
import studying.withsolid.exception.ApplicationException;
import studying.withsolid.model.Report;

import java.util.Objects;

/** Coordinates report persistence and delivery through injected contracts. */
public final class ReportService {
    private final ReportSaver reportSaver;
    private final ReportSender reportSender;

    /**
     * Creates a report service with the selected persistence and delivery strategies.
     *
     * @param reportSaver report persistence strategy
     * @param reportSender report delivery strategy
     */
    public ReportService(ReportSaver reportSaver, ReportSender reportSender) {
        this.reportSaver = Objects.requireNonNull(reportSaver, "reportSaver");
        this.reportSender = Objects.requireNonNull(reportSender, "reportSender");
    }

    /**
     * Saves a report and sends it only after successful persistence.
     *
     * @param report report to process
     * @param email recipient email address
     */
    public void process(Report report, String email) {
        if (report == null) {
            throw validationError("Отчёт не задан");
        }
        if (email == null || email.isBlank()) {
            throw validationError("Email получателя не задан");
        }

        reportSaver.save(report);
        reportSender.send(report, email);
    }

    private ApplicationException validationError(String message) {
        return new ApplicationException(ApplicationErrorCode.VALIDATION_ERROR, message);
    }
}
