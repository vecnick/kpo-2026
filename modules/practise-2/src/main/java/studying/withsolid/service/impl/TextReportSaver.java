package studying.withsolid.service.impl;

import studying.withsolid.exception.ApplicationErrorCode;
import studying.withsolid.exception.ApplicationException;
import studying.withsolid.model.Report;
import studying.withsolid.service.ReportSaver;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.format.DateTimeFormatter;

/**
 * Saves a report as a text file whose name contains the report date and time.
 */
public class TextReportSaver implements ReportSaver {
    private static final DateTimeFormatter TIME_FILE_FORMAT = DateTimeFormatter.ofPattern("HH-mm-ss");

    private final Path outputDirectory;

    /**
     * Creates a saver that writes into the {@code reports} directory.
     */
    public TextReportSaver() {
        this(Path.of("reports"));
    }

    /**
     * Creates a saver that writes into the given directory.
     *
     * @param outputDirectory directory for generated report files
     */
    public TextReportSaver(Path outputDirectory) {
        this.outputDirectory = outputDirectory;
    }

    /**
     * Writes the report text to {@code report-<date>-<time>.txt}.
     *
     * @param report report to save
     */
    @Override
    public void save(Report report) {
        if (report == null) {
            throw new ApplicationException(ApplicationErrorCode.VALIDATION_ERROR, "Отчёт не задан");
        }
        if (report.carsSold() < 0 || report.motorcyclesSold() < 0) {
            throw new ApplicationException(
                    ApplicationErrorCode.VALIDATION_ERROR,
                    "Показатели продаж не могут быть отрицательными"
            );
        }

        var fileName = "report-%s-%s.txt".formatted(
                report.date(),
                report.time().format(TIME_FILE_FORMAT)
        );
        var file = outputDirectory.resolve(fileName);

        try {
            Files.createDirectories(outputDirectory);
            Files.writeString(file, report.toString());
        } catch (IOException exception) {
            throw new ApplicationException(
                    ApplicationErrorCode.FILE_WRITE_ERROR,
                    "Не удалось сохранить отчёт в " + file,
                    exception
            );
        }
    }
}
