package studying.withsolid.service;

import studying.withsolid.model.Report;

/**
 * Контракт сохранения отчёта.
 * Реализация решает, куда и в каком виде сохранить отчёт
 * (текстовый файл, JSON, PDF и т.д.).
 */
@FunctionalInterface
public interface ReportSaver {

    /**
     * Сохраняет переданный отчёт.
     *
     * @param report отчёт для сохранения; не должен быть {@code null}
     * @throws studying.withsolid.exception.ApplicationException если отчёт некорректен
     *         или сохранение завершилось ошибкой
     */
    void save(Report report);
}