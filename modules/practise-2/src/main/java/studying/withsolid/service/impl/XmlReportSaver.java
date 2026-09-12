package studying.withsolid.service.impl;

import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import studying.withsolid.model.Report;

import java.io.IOException;
import java.nio.file.Path;

/** Saves reports as XML files. */
public final class XmlReportSaver extends FileReportSaver {
    private static final ObjectWriter WRITER = XmlMapper.builder()
            .addModule(new JavaTimeModule())
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
            .build()
            .writer()
            .withRootName("report")
            .withDefaultPrettyPrinter();

    /** Creates a saver that writes files to the {@code reports} directory. */
    public XmlReportSaver() {
        this(Path.of("reports"));
    }

    /**
     * Creates a saver that writes files to the specified directory.
     *
     * @param reportsDirectory destination directory
     */
    public XmlReportSaver(Path reportsDirectory) {
        super(reportsDirectory, "xml");
    }

    @Override
    protected String serialize(Report report) throws IOException {
        return WRITER.writeValueAsString(report);
    }
}
