package studying.withsolid.service;

import studying.withsolid.model.Report;

@FunctionalInterface
public interface ReportSaver {
    /**
     * Persists the supplied report.
     *
     * @param report report to save
     */
    void save(Report report);
}
