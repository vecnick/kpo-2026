package studying.withsolid.service;

import studying.withsolid.model.Report;

public class ReportServiceFacade {
    private final ReportSender reportSender;
    private final ReportSaver reportSaver;

    public ReportServiceFacade(ReportSender reportSender, ReportSaver reportSaver) {
        this.reportSender = reportSender;
        this.reportSaver = reportSaver;
    }

    public void process(Report report, String email) {
        reportSaver.save(report);

        reportSender.send(report, email);
    }
}
