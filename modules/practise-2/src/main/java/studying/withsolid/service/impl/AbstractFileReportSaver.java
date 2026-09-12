package studying.withsolid.service.impl;

import studying.withsolid.exception.ApplicationErrorCode;
import studying.withsolid.exception.ApplicationException;
import studying.withsolid.model.Report;
import studying.withsolid.service.ReportSaver;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

/** Shared file-writing mechanics for report serialization formats. */
abstract class AbstractFileReportSaver implements ReportSaver {
    private static final DateTimeFormatter FILE_TIME_FORMAT =
            DateTimeFormatter.ofPattern("HH-mm-ss");

    private final Path reportsDirectory;
    private final String fileSuffix;

    protected AbstractFileReportSaver(Path reportsDirectory, String fileSuffix) {
        this.reportsDirectory = Objects.requireNonNull(reportsDirectory,
                "Каталог отчётов не задан");
        this.fileSuffix = Objects.requireNonNull(fileSuffix, "Расширение файла не задано");
    }

    @Override
    public final void save(Report report) {
        if (report == null) {
            throw new ApplicationException(ApplicationErrorCode.VALIDATION_ERROR,
                    "Отчёт не задан");
        }

        var file = reportsDirectory.resolve("report-%s-%s%s".formatted(
                report.date(), report.time().format(FILE_TIME_FORMAT), fileSuffix));
        try {
            Files.createDirectories(reportsDirectory);
            Files.writeString(file, serialize(report), StandardCharsets.UTF_8);
            System.out.printf("[SAVE] Отчёт сохранён: %s%n", file.toAbsolutePath());
        } catch (IOException exception) {
            throw new ApplicationException(ApplicationErrorCode.FILE_WRITE_ERROR,
                    "Не удалось сохранить отчёт в " + file, exception);
        }
    }

    protected abstract String serialize(Report report);
}
