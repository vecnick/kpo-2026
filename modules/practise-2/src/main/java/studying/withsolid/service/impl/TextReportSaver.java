package studying.withsolid.service.impl;

import studying.withsolid.exception.ApplicationErrorCode;
import studying.withsolid.exception.ApplicationException;
import studying.withsolid.model.Report;
import studying.withsolid.service.ReportSaver;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Реализация {@link ReportSaver}, сохраняющая текстовое представление
 * отчёта в файл на локальной файловой системе.
 * <p>
 * Имя файла формируется автоматически из даты и времени отчёта,
 * что гарантирует уникальность файлов при сохранении нескольких отчётов.
 */
public final class TextReportSaver implements ReportSaver {

    // Формат имени файла: без символа ':' (недопустим в именах файлов
    // на Windows) и с сортировкой по возрастанию времени "как есть".
    private static final DateTimeFormatter FILE_NAME_FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd-HH-mm-ss");

    // Каталог по умолчанию, используемый при создании сохранителя
    // без явного указания пути. Относительный путь — каталог будет
    // создан рядом с рабочей директорией запуска приложения.
    private static final Path DEFAULT_REPORTS_DIRECTORY = Path.of("reports");

    // Каталог, куда сохраняются отчёты. Передаётся извне,
    // чтобы не привязывать класс к конкретному расположению на диске.
    private final Path reportsDirectory;

    /**
     * Создаёт сохранитель отчётов со стандартным каталогом {@code reports}
     * относительно рабочей директории приложения.
     * <p>
     * Удобно для быстрого запуска и демонстрации; для тестов или
     * нестандартного расположения файлов используйте
     * {@link #TextReportSaver(Path)}.
     */
    public TextReportSaver() {
        // Делегируем основному конструктору, чтобы логика хранения
        // каталога оставалась в одном месте (без дублирования).
        this(DEFAULT_REPORTS_DIRECTORY);
    }

    /**
     * Создаёт сохранитель отчётов, работающий с указанным каталогом.
     *
     * @param reportsDirectory каталог для сохранения текстовых отчётов;
     *                         будет создан автоматически, если ещё не существует
     */
    public TextReportSaver(Path reportsDirectory) {
        this.reportsDirectory = reportsDirectory;
    }

    /**
     * Сохраняет отчёт в текстовый файл внутри каталога отчётов.
     *
     * @param report отчёт для сохранения; не должен быть {@code null}
     * @throws ApplicationException с кодом {@link ApplicationErrorCode#VALIDATION_ERROR},
     *         если отчёт не передан
     * @throws ApplicationException с кодом {@link ApplicationErrorCode#FILE_WRITE_ERROR},
     *         если запись файла завершилась ошибкой ввода-вывода
     */
    @Override
    public void save(Report report) {
        // Проверка выполняется в точке использования: именно этому классу
        // нужен report, поэтому именно он и отвечает за его валидность.
        if (report == null) {
            throw new ApplicationException(
                    ApplicationErrorCode.VALIDATION_ERROR,
                    "Отчёт не должен быть null"
            );
        }

        Path filePath = buildFilePath(report);

        try {
            // Каталог может отсутствовать при первом запуске приложения —
            // createDirectories безопасно ничего не делает, если он уже есть.
            Files.createDirectories(reportsDirectory);
            Files.writeString(filePath, report.toString());
        } catch (IOException e) {
            // Низкоуровневая IOException — деталь реализации файловой системы.
            // Оборачиваем её в прикладное исключение, сохраняя исходную
            // причину через cause, чтобы не потерять стектрейс при отладке.
            throw new ApplicationException(
                    ApplicationErrorCode.FILE_WRITE_ERROR,
                    "Не удалось сохранить отчёт в файл: " + filePath,
                    e
            );
        }
    }

    /**
     * Формирует путь к файлу отчёта на основе каталога отчётов
     * и даты/времени, указанных в самом отчёте.
     *
     * @param report отчёт, для которого строится путь
     * @return путь вида {@code <reportsDirectory>/report-<дата>-<время>.txt}
     */
    private Path buildFilePath(Report report) {
        String timestamp = LocalDateTime.of(report.date(), report.time())
                .format(FILE_NAME_FORMATTER);
        String fileName = "report-" + timestamp + ".txt";
        return reportsDirectory.resolve(fileName);
    }
}