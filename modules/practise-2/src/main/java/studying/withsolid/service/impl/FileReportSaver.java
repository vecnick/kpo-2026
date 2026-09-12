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

/** Common file creation logic for report serialization formats. */
abstract class FileReportSaver implements ReportSaver {
    private static final DateTimeFormatter FILE_TIME_FORMATTER = DateTimeFormatter.ofPattern("HH-mm-ss");

    private final Path reportsDirectory;
    private final String fileExtension;

    protected FileReportSaver(Path reportsDirectory, String fileExtension) {
        if (reportsDirectory == null) {
            throw validationError("Каталог для отчётов не задан");
        }
        this.reportsDirectory = reportsDirectory;
        this.fileExtension = fileExtension;
    }

    @Override
    public final void save(Report report) {
        if (report == null) {
            throw validationError("Отчёт не задан");
        }

        var reportPath = reportsDirectory.resolve(fileName(report));
        try {
            Files.createDirectories(reportsDirectory);
            Files.writeString(reportPath, serialize(report), StandardCharsets.UTF_8);
        } catch (IOException exception) {
            throw new ApplicationException(
                    ApplicationErrorCode.FILE_WRITE_ERROR,
                    "Не удалось сохранить отчёт в " + reportPath,
                    exception
            );
        }
    }

    protected abstract String serialize(Report report) throws IOException;

    private String fileName(Report report) {
        return "report-%s-%s.%s".formatted(
                report.date(),
                report.time().format(FILE_TIME_FORMATTER),
                fileExtension
        );
    }

    private static ApplicationException validationError(String message) {
        return new ApplicationException(ApplicationErrorCode.VALIDATION_ERROR, message);
    }
}
