package studying.withsolid.service;

import studying.withsolid.model.Report;

/** Delivers reports to recipients. */
@FunctionalInterface
public interface ReportSender {
    /**
     * Sends a report to an email recipient.
     *
     * @param report report to send
     * @param email recipient email address
     */
    void send(Report report, String email);
}
