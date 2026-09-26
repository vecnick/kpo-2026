package studying.ioc.locator;

import studying.model.Report;
import studying.service.ReportSaver;
import studying.service.ReportSender;

/** Processes reports by resolving collaborators from a Service Locator. */
public final class ReportService {
    /** Registry used to resolve report services. */
    private final ServiceLocator locator;

    /**
     * Creates a service that resolves collaborators from the locator.
     *
     * @param serviceLocator service registry
     */
    public ReportService(final ServiceLocator serviceLocator) {
        this.locator = serviceLocator;
    }

    /**
     * Saves a report and sends it to the recipient.
     *
     * @param report report to process
     * @param email recipient email address
     */
    public void process(final Report report, final String email) {
        locator.getRequired(ReportSaver.class).save(report);
        locator.getRequired(ReportSender.class).send(report, email);
    }
}
