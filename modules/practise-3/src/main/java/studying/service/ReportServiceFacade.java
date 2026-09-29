package studying.service;

import studying.model.Report;

public final class ReportServiceFacade {
    private final ReportSender reportSender;
    private final ReportSaver reportSaver;

    public ReportServiceFacade(final ReportSender reportSender,
                               final ReportSaver reportSaver) {
        this.reportSender = reportSender;
        this.reportSaver = reportSaver;
    }

    public void process(final Report report, final String email) {
        reportSaver.save(report);

        reportSender.send(report, email);
    }
}
