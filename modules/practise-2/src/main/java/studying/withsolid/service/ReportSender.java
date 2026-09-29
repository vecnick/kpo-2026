package studying.withsolid.service;

import studying.withsolid.model.Report;

/**
 * Contract for delivering a report to a recipient.
 */
@FunctionalInterface
public interface ReportSender {
    /**
     * Sends the report to the specified email address.
     *
     * @param report report to deliver
     * @param email  recipient address
     */
    void send(Report report, String email);
}
