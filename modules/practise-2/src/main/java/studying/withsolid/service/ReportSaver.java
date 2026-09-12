package studying.withsolid.service;

import studying.withsolid.model.Report;

/**
 * Контракт сохранения отчёта во внешнее хранилище.
 */
@FunctionalInterface
public interface ReportSaver {
    /**
     * Сохраняет переданный отчёт.
     *
     * @param report сохраняемый отчёт
     * @throws studying.withsolid.exception.ApplicationException если отчёт некорректен
     *         или сохранить его не удалось
     */
    void save(Report report);
}
