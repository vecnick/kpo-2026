package studying.service.impl;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.format.DateTimeFormatter;
import studying.exception.ApplicationErrorCode;
import studying.exception.ApplicationException;
import studying.model.Report;
import studying.service.ReportSaver;

public final class ReportSaverImpl implements ReportSaver {
    private static final DateTimeFormatter DATE_TIME_FORMATTER =
            DateTimeFormatter.ofPattern("HH-mm-ss");
    private final Path reportsDirectory;

    public ReportSaverImpl() {
        this(Path.of("reports"));
    }

    public ReportSaverImpl(final Path reportsDirectory) {
        this.reportsDirectory = reportsDirectory;
    }

    @Override
    public void save(final Report report) {
        if (report == null) {
            throw new ApplicationException(
                    ApplicationErrorCode.VALIDATION_ERROR,
                    "Отчет не может быть null"
            );
        }

        var reportForSave = reportsDirectory.resolve(
                "report-%s-%s.txt".formatted(
                report.date(),
                report.time().format(DATE_TIME_FORMATTER)
        ));

        try {
            Files.createDirectories(reportForSave.getParent());
            Files.writeString(reportForSave, report.toString());
        } catch (IOException exception) {
            throw new ApplicationException(
                    ApplicationErrorCode.FILE_WRITE_ERROR,
                    "Не удалось сохранить отчёт в " + reportForSave, exception);
        }

    }
}
