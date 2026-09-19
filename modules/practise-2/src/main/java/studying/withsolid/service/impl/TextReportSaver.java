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

public class TextReportSaver implements ReportSaver {
    private static final DateTimeFormatter FILE_NAME_FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd-HH-mm-ss");
    private final Path reportsDirectory;

    /**
     * Creates a saver that writes reports to the default {@code reports} directory.
     */
    public TextReportSaver() {
        this(Path.of("reports"));
    }

    /**
     * Creates a saver that writes reports to the specified directory.
     *
     * @param reportsDirectory directory for generated reports
     */
    public TextReportSaver(Path reportsDirectory) {
        this.reportsDirectory = reportsDirectory;
    }

    /**
     * Saves the report as a text file whose name contains its date and time.
     *
     * @param report report to save
     * @throws ApplicationException if the report is missing or the file cannot be written
     */
    @Override
    public void save(Report report) {
        if (report == null) {
            throw new ApplicationException(
                    ApplicationErrorCode.VALIDATION_ERROR,
                    "Report must not be null",
                    null
            );
        }

        LocalDateTime reportDateTime = LocalDateTime.of(report.getDate(), report.getTime());
        Path reportPath = reportsDirectory.resolve(
                "report-" + reportDateTime.format(FILE_NAME_FORMATTER) + ".txt"
        );

        try {
            Files.createDirectories(reportsDirectory);
            Files.writeString(reportPath, report.toString());
        } catch (IOException exception) {
            throw new ApplicationException(
                    ApplicationErrorCode.FILE_WRITE_ERROR,
                    "Could not save report to " + reportPath,
                    exception
            );
        }
    }

}
