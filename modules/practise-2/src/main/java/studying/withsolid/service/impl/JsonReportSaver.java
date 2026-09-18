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
 * Реализация {@link ReportSaver}, сохраняющая отчёт в формате JSON.
 * <p>
 * JSON собирается вручную, без сторонних библиотек сериализации:
 */
public final class JsonReportSaver implements ReportSaver {

    // Тот же формат имени файла, что и у остальных сохранителей —
    // единообразие упрощает поиск и сортировку файлов на диске.
    private static final DateTimeFormatter FILE_NAME_FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd-HH-mm-ss");

    private static final Path DEFAULT_REPORTS_DIRECTORY = Path.of("reports");

    private final Path reportsDirectory;

    /**
     * Создаёт сохранитель отчётов в JSON со стандартным каталогом
     * {@code reports} относительно рабочей директории приложения.
     */
    public JsonReportSaver() {
        this(DEFAULT_REPORTS_DIRECTORY);
    }

    /**
     * Создаёт сохранитель отчётов в JSON, работающий с указанным каталогом.
     *
     * @param reportsDirectory каталог для сохранения JSON-отчётов;
     *                         будет создан автоматически, если ещё не существует
     */
    public JsonReportSaver(Path reportsDirectory) {
        this.reportsDirectory = reportsDirectory;
    }

    /**
     * Сохраняет отчёт в JSON-файл внутри каталога отчётов.
     *
     * @param report отчёт для сохранения; не должен быть {@code null}
     * @throws ApplicationException с кодом {@link ApplicationErrorCode#VALIDATION_ERROR},
     *         если отчёт не передан
     * @throws ApplicationException с кодом {@link ApplicationErrorCode#FILE_WRITE_ERROR},
     *         если запись файла завершилась ошибкой ввода-вывода
     */
    @Override
    public void save(Report report) {
        if (report == null) {
            throw new ApplicationException(
                    ApplicationErrorCode.VALIDATION_ERROR,
                    "Отчёт не должен быть null"
            );
        }

        Path filePath = buildFilePath(report);

        try {
            Files.createDirectories(reportsDirectory);
            Files.writeString(filePath, toJson(report));
        } catch (IOException e) {
            throw new ApplicationException(
                    ApplicationErrorCode.FILE_WRITE_ERROR,
                    "Не удалось сохранить отчёт в JSON-файл: " + filePath,
                    e
            );
        }
    }

    /**
     * Формирует путь к JSON-файлу отчёта на основе каталога отчётов
     * и даты/времени, указанных в самом отчёте.
     *
     * @param report отчёт, для которого строится путь
     * @return путь вида {@code <reportsDirectory>/report-<дата>-<время>.json}
     */
    private Path buildFilePath(Report report) {
        String timestamp = LocalDateTime.of(report.date(), report.time())
                .format(FILE_NAME_FORMATTER);
        String fileName = "report-" + timestamp + ".json";
        return reportsDirectory.resolve(fileName);
    }

    /**
     * Вручную формирует JSON-представление отчёта.
     * <p>
     * Строковые поля экранируются через {@link #escapeJson(String)},
     * числовые и структурированные типы (дата, время) выводятся как есть
     * или в виде строки в кавычках — JSON не имеет собственного типа "дата".
     *
     * @param report отчёт для сериализации
     * @return строка с JSON-представлением отчёта
     */
    private String toJson(Report report) {
        return "{\n" +
                "  \"title\": \"" + escapeJson(report.title()) + "\",\n" +
                "  \"date\": \"" + report.date() + "\",\n" +
                "  \"time\": \"" + report.time() + "\",\n" +
                "  \"carsSold\": " + report.carsSold() + ",\n" +
                "  \"motorcyclesSold\": " + report.motorcyclesSold() + "\n" +
                "}";
    }

    /**
     * Экранирует специальные символы, недопустимые внутри JSON-строки
     * в необработанном виде (кавычки и обратный слэш).
     *
     * @param value исходная строка
     * @return строка, безопасная для вставки внутрь JSON-значения
     */
    private String escapeJson(String value) {
        return value
                .replace("\\", "\\\\")
                .replace("\"", "\\\"");
    }
}