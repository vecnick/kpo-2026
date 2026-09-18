package studying.withsolid.service;

import lombok.RequiredArgsConstructor;
import studying.withsolid.model.Report;

/**
 * Основной сценарий работы с отчётом: сначала сохранить отчёт,
 * затем отправить его указанному получателю.
 * <p>
 * Класс не создаёт конкретные реализации {@link ReportSaver} и
 * {@link ReportSender} самостоятельно — они передаются извне через
 * конструктор (внедрение зависимостей). Это позволяет подключать
 * новые способы сохранения (JSON, PDF) или отправки (Telegram)
 * без изменения данного класса — в соответствии с Open/Closed Principle.
 */
@RequiredArgsConstructor
public final class ReportService {

    // final-поля гарантируют, что зависимости заданы один раз при
    // создании сервиса и не могут быть подменены впоследствии.
    // Lombok @RequiredArgsConstructor сгенерирует конструктор,
    // принимающий оба поля в порядке их объявления.
    private final ReportSaver reportSaver;
    private final ReportSender reportSender;

    /**
     * Выполняет основной сценарий: сохраняет отчёт, затем отправляет его
     * указанному получателю.
     * <p>
     * Порядок операций фиксирован и важен: отчёт должен быть сохранён
     * до того, как о нём сообщат получателю.
     *
     * @param report отчёт для обработки; проверка на {@code null}
     *               выполняется внутри конкретных реализаций
     *               {@link ReportSaver} и {@link ReportSender}
     * @param email  адрес получателя отчёта; проверка на пустоту/{@code null}
     *               выполняется внутри конкретной реализации {@link ReportSender}
     * @throws studying.withsolid.exception.ApplicationException если сохранение
     *         или отправка завершились ошибкой — см. документацию
     *         конкретных реализаций {@link ReportSaver} и {@link ReportSender}
     */
    public void process(Report report, String email) {
        // Сначала сохраняем — если сохранение упадёт с исключением,
        // отправка не выполнится вовсе, и получатель не узнает об отчёте,
        // которого физически не существует на диске.
        reportSaver.save(report);

        // Отправляем только после успешного сохранения.
        reportSender.send(report, email);
    }
}