package studying.withoutsolid;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;

/**
 * Намеренно монолитный сервис, который формирует, сохраняет и отправляет
 * отчёт самостоятельно и тем самым нарушает принцип единственной ответственности.
 */
public final class ReportService {
    /**
     * Создаёт монолитный сервис работы с отчётами.
     */
    public ReportService() {
    }

    /**
     * Формирует текстовый отчёт о продажах на текущие дату и время.
     *
     * @return сформированный текст отчёта
     */
    public String generateReport() {
        var now = LocalDateTime.now();

        return """
                Отчёт
                Дата: %tF
                Время: %tT
                --------------------------------
                Продано автомобилей: 100 шт.
                Продано мотоциклов: 50 шт.
                --------------------------------
                """.formatted(now, now);
    }

    /**
     * Записывает текст отчёта в указанный файл.
     *
     * @param report текст сохраняемого отчёта
     * @param fileName имя файла назначения
     * @throws UncheckedIOException если файл не удалось записать
     */
    public void saveReport(String report, String fileName) {
        try {
            Files.writeString(Path.of(fileName), report);
        } catch (IOException exception) {
            throw new UncheckedIOException("Не удалось сохранить отчёт в " + fileName, exception);
        }
    }

    /**
     * Имитирует отправку отчёта получателю по электронной почте.
     *
     * @param report текст отправляемого отчёта
     * @param email адрес получателя
     */
    public void sendReport(String report, String email) {
        System.out.printf("Отправка отчёта %s на email: %s%n", report, email);
    }
}
