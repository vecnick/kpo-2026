package studying.withsolid.service.impl;

import studying.withsolid.model.Report;

import java.nio.file.Path;

/**
 * Сохраняет отчёт как JSON-документ в кодировке UTF-8.
 */
public final class JsonReportSaver extends AbstractFileReportSaver {
    /**
     * Создаёт сохранитель, записывающий файлы в каталог {@code reports}.
     */
    public JsonReportSaver() {
        this(Path.of("reports"));
    }

    /**
     * Создаёт JSON-сохранитель с заданным каталогом назначения.
     *
     * @param reportsDirectory каталог для файлов отчётов
     */
    public JsonReportSaver(Path reportsDirectory) {
        super(reportsDirectory, ".json");
    }

    @Override
    protected String serialize(Report report) {
        return """
                {
                  "title": "%s",
                  "date": "%s",
                  "time": "%s",
                  "carsSold": %d,
                  "motorcyclesSold": %d
                }
                """.formatted(
                escapeJson(report.title()),
                report.date(),
                report.time(),
                report.carsSold(),
                report.motorcyclesSold());
    }

    private String escapeJson(String value) {
        var result = new StringBuilder(value.length());
        for (int index = 0; index < value.length(); index++) {
            char character = value.charAt(index);
            switch (character) {
                case '"' -> result.append("\\\"");
                case '\\' -> result.append("\\\\");
                case '\b' -> result.append("\\b");
                case '\f' -> result.append("\\f");
                case '\n' -> result.append("\\n");
                case '\r' -> result.append("\\r");
                case '\t' -> result.append("\\t");
                default -> {
                    if (character < 0x20) {
                        result.append("\\u%04x".formatted((int) character));
                    } else {
                        result.append(character);
                    }
                }
            }
        }
        return result.toString();
    }
}
