package studying.service;

import studying.model.Report;

public final class ReportService {
    private final ReportSaver saver;
    private final ReportSender sender;

    public ReportService(final ReportSaver saver, final ReportSender sender) {
        this.saver = saver;
        this.sender = sender;
    }

    public void process(final Report report, final String email) {
        saver.save(report);
        sender.send(report, email);
    }
}
