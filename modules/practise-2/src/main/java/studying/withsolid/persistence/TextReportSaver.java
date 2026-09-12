package studying.withsolid.persistence;

import studying.withsolid.error.ApplicationException;
import studying.withsolid.report.Report;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

import static studying.withsolid.error.ApplicationErrorCode.FILE_WRITE_ERROR;
import static studying.withsolid.error.ApplicationErrorCode.VALIDATION_ERROR;

/**
 * Сохраняет текстовое представление отчёта в файл с датой и временем в имени.
 */
public final class TextReportSaver implements ReportSaver {
    private static final DateTimeFormatter FILE_TIME_FORMATTER =
            DateTimeFormatter.ofPattern("HH-mm-ss");

    private final Path reportDirectory;

    /**
     * Создаёт сохранитель, записывающий отчёты в каталог {@code reports}.
     */
    public TextReportSaver() {
        this(Path.of("reports"));
    }

    /**
     * Создаёт сохранитель с настраиваемым каталогом назначения.
     *
     * @param reportDirectory каталог для файлов отчётов
     */
    public TextReportSaver(Path reportDirectory) {
        this.reportDirectory = Objects.requireNonNull(reportDirectory,
                "Каталог отчётов не должен отсутствовать");
    }

    /**
     * Создаёт каталог при необходимости и записывает текст отчёта в UTF-8.
     * Ошибка файловой системы оборачивается в прикладное исключение с сохранением причины.
     *
     * @param report сохраняемый отчёт
     * @throws ApplicationException если отчёт отсутствует или файл нельзя записать
     */
    @Override
    public void save(Report report) {
        if (report == null) {
            throw new ApplicationException(VALIDATION_ERROR,
                    "Отчёт не должен отсутствовать");
        }

        var file = reportDirectory.resolve(createFileName(report));

        try {
            Files.createDirectories(reportDirectory);
            Files.writeString(file, report.toString());
        } catch (IOException exception) {
            throw new ApplicationException(
                    FILE_WRITE_ERROR,
                    "Не удалось сохранить отчёт в файл: " + file,
                    exception
            );
        }
    }

    private String createFileName(Report report) {
        return "report-%s-%s.txt".formatted(
                report.date(),
                report.time().format(FILE_TIME_FORMATTER)
        );
    }
}
