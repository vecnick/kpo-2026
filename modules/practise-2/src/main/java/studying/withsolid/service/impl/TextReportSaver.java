package studying.withsolid.service.impl;

import studying.withsolid.model.Report;

import java.nio.file.Path;

/** Saves reports as UTF-8 text files. */
public final class TextReportSaver extends FileReportSaver {
    /** Creates a saver that writes files to the {@code reports} directory. */
    public TextReportSaver() {
        this(Path.of("reports"));
    }

    /**
     * Creates a saver that writes files to the specified directory.
     *
     * @param reportsDirectory destination directory
     */
    public TextReportSaver(Path reportsDirectory) {
        super(reportsDirectory, "txt");
    }

    @Override
    protected String serialize(Report report) {
        return report.toString();
    }
}
