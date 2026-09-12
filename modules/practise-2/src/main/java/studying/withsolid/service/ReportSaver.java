package studying.withsolid.service;

import studying.withsolid.model.Report;

/** Saves reports to a destination. */
@FunctionalInterface
public interface ReportSaver {
    /**
     * Saves a report.
     *
     * @param report report to save
     */
    void save(Report report);
}
