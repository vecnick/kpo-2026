package studying.service;

import lombok.RequiredArgsConstructor;
import studying.model.Report;

@RequiredArgsConstructor
public final class ReportServiceFacade {
    private final ReportSender reportSender;
    private final ReportSaver reportSaver;

    public void process(Report report, String email) {
        reportSaver.save(report);

        reportSender.send(report, email);
    }
}
