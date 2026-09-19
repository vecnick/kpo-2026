package studying.withsolid.service;

import lombok.RequiredArgsConstructor;
import studying.withsolid.model.Report;

@RequiredArgsConstructor
public final class ReportService {
    private final ReportSaver reportSaver;
    private final ReportSender reportSender;

    /**
     * Saves the report and then sends it to the recipient.
     *
     * @param report report to process
     * @param email recipient email address
     */
    public void process(Report report, String email) {
        reportSaver.save(report);
        reportSender.send(report, email);
    }
}
