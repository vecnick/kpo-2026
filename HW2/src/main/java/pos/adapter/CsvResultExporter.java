package pos.adapter;

import pos.application.ResultExporter;
import pos.application.ResultRow;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static java.nio.file.StandardOpenOption.CREATE_NEW;

public final class CsvResultExporter implements ResultExporter {
    @Override
    public void export(String destination, List<ResultRow> rows) throws IOException {
        try (var writer = Files.newBufferedWriter(Path.of(destination), StandardCharsets.UTF_8, CREATE_NEW)) {
            writer.write("student_id,student_name,graded,criteria,percent\r\n");
            for (ResultRow row : rows) {
                String percent = row.percent().map(BigDecimal::toPlainString).orElse("");
                writer.write("%s,%s,%d,%d,%s\r\n".formatted(
                        escape(row.student().id()), escape(row.student().name()), row.graded(), row.criteria(),
                        percent));
            }
        }
    }

    private static String escape(String value) {
        return "\"" + value.replace("\"", "\"\"") + "\"";
    }
}
