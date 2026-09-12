package studying.withsolid.service.impl;

import studying.withsolid.model.Report;
import studying.withsolid.service.ReportSaver;

import java.util.Arrays;
import java.util.List;

/**
 * Последовательно применяет несколько способов сохранения к одному отчёту.
 */
public final class CompositeReportSaver implements ReportSaver {
    private final List<ReportSaver> savers;

    /**
     * Создаёт составной сохранитель.
     *
     * @param savers один или несколько способов сохранения
     * @throws IllegalArgumentException если не передан ни один сохранитель
     * @throws NullPointerException если массив или один из его элементов равен {@code null}
     */
    public CompositeReportSaver(ReportSaver... savers) {
        this.savers = List.copyOf(Arrays.asList(savers));
        if (this.savers.isEmpty()) {
            throw new IllegalArgumentException("Не задан ни один способ сохранения");
        }
    }

    /**
     * Передаёт отчёт всем сохранителям в порядке их объявления.
     * Выполнение прекращается при первой ошибке.
     *
     * @param report сохраняемый отчёт
     */
    @Override
    public void save(Report report) {
        savers.forEach(saver -> saver.save(report));
    }
}
