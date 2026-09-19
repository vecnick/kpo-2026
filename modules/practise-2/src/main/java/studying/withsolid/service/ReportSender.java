package studying.withsolid.service;

import studying.withsolid.model.Report;

@FunctionalInterface
public interface ReportSender {
    /**
     * Sends the supplied report to an email recipient.
     *
     * @param report report to send
     * @param email recipient email address
     */
    void send(Report report, String email);
}
