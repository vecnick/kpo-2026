package studying.withsolid.service;

import studying.withsolid.model.Report;

/**
 * Contract for persisting a report.
 */
@FunctionalInterface
public interface ReportSaver {
    /**
     * Saves the given report.
     *
     * @param report report to persist
     */
    void save(Report report);
}
