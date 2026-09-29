package studying.ioc.locator;

import studying.model.Report;
import studying.service.ReportSaver;
import studying.service.ReportSender;

public final class ReportService {
    private final ServiceLocator locator;

    public ReportService(final ServiceLocator serviceLocator) {
        this.locator = serviceLocator;
    }

    public void process(final Report report, final String email) {
        locator.getRequired(ReportSaver.class).save(report);
        locator.getRequired(ReportSender.class).send(report, email);
    }
}
