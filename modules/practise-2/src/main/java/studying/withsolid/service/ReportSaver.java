package studying.withsolid.service;

import studying.withsolid.model.Report;

@FunctionalInterface
public interface ReportSaver {
    void save(Report report);
}
