package studying.withsolid.service;

import studying.withsolid.model.Report;

@FunctionalInterface
public interface ReportSender {
    void send(Report report, String email);
}
