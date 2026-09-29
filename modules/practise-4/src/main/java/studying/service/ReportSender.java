package studying.service;

import studying.model.Report;

@FunctionalInterface
public interface ReportSender {
    void send(Report report, String email);
}
