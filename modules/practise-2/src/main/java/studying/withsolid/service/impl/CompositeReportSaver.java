package studying.withsolid.service.impl;

import studying.withsolid.model.Report;
import studying.withsolid.service.ReportSaver;

import java.util.List;

/** Saves each report through several persistence strategies. */
public final class CompositeReportSaver implements ReportSaver {
    private final List<ReportSaver> reportSavers;

    /**
     * Creates a saver that delegates to every supplied saver in order.
     *
     * @param reportSavers persistence strategies to invoke
     */
    public CompositeReportSaver(ReportSaver... reportSavers) {
        this.reportSavers = List.of(reportSavers);
        if (this.reportSavers.isEmpty()) {
            throw new IllegalArgumentException("Не задано ни одного способа сохранения отчёта");
        }
    }

    @Override
    public void save(Report report) {
        reportSavers.forEach(reportSaver -> reportSaver.save(report));
    }
}
