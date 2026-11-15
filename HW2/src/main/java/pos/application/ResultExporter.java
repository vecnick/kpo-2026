package pos.application;

import java.io.IOException;
import java.util.List;

public interface ResultExporter {
    void export(String destination, List<ResultRow> rows) throws IOException;
}
