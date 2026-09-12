package studying.withsolid.persistence;

import studying.withsolid.report.Report;

/**
 * Контракт сохранения отчёта, не привязанный к конкретному формату или хранилищу.
 */
@FunctionalInterface
public interface ReportSaver {
    /**
     * Сохраняет переданный отчёт.
     *
     * @param report сохраняемый отчёт
     */
    void save(Report report);
}
