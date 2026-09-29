package studying.service;


import studying.model.Report;

@FunctionalInterface
public interface ReportSaver {
    void save(Report report);
}
