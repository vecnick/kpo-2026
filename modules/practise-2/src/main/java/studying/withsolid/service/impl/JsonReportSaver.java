package studying.withsolid.service.impl;

import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import studying.withsolid.model.Report;

import java.io.IOException;
import java.nio.file.Path;

/** Saves reports as JSON files. */
public final class JsonReportSaver extends FileReportSaver {
    private static final ObjectWriter WRITER = JsonMapper.builder()
            .addModule(new JavaTimeModule())
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
            .build()
            .writerWithDefaultPrettyPrinter();

    /** Creates a saver that writes files to the {@code reports} directory. */
    public JsonReportSaver() {
        this(Path.of("reports"));
    }

    /**
     * Creates a saver that writes files to the specified directory.
     *
     * @param reportsDirectory destination directory
     */
    public JsonReportSaver(Path reportsDirectory) {
        super(reportsDirectory, "json");
    }

    @Override
    protected String serialize(Report report) throws IOException {
        return WRITER.writeValueAsString(report);
    }
}
