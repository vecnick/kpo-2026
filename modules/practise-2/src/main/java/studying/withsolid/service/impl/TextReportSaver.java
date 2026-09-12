package studying.withsolid.service.impl;

import studying.withsolid.model.Report;

import java.nio.file.Path;

/**
 * Сохраняет человекочитаемое представление отчёта в текстовый файл.
 */
public final class TextReportSaver extends AbstractFileReportSaver {
    /**
     * Создаёт сохранитель, записывающий файлы в каталог {@code reports}.
     */
    public TextReportSaver() {
        this(Path.of("reports"));
    }

    /**
     * Создаёт сохранитель с заданным каталогом назначения.
     *
     * @param reportsDirectory каталог для файлов отчётов
     */
    public TextReportSaver(Path reportsDirectory) {
        super(reportsDirectory, ".txt");
    }

    @Override
    protected String serialize(Report report) {
        return report.toString();
    }
}
