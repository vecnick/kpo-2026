package studying.withoutsolid;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;

public class ReportService {
    public String generateReport() {
        var now = LocalDateTime.now();

        return "Отчёт%nДата: %tF%nВремя: %tT%n--------------------------------%n"
                .formatted(now, now)
                + "Продано автомобилей: 100 шт.%nПродано мотоциклов: 50 шт.%n--------------------------------%n";
    }

    public void saveReport(String report, String fileName) {
        try {
            Files.writeString(Path.of(fileName), report);
        } catch (IOException exception) {
            throw new UncheckedIOException("Не удалось сохранить отчёт в " + fileName, exception);
        }
    }

    public void sendReport(String report, String email) {
        System.out.printf("Отправка отчёта %s на email: %s%n", report, email);
    }
}
