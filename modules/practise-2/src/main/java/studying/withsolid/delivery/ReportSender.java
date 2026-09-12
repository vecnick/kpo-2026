package studying.withsolid.delivery;

import studying.withsolid.report.Report;

/**
 * Контракт доставки отчёта, не зависящий от конкретного канала связи.
 */
@FunctionalInterface
public interface ReportSender {
    /**
     * Отправляет отчёт указанному получателю.
     *
     * @param report отправляемый отчёт
     * @param email email получателя
     */
    void send(Report report, String email);
}
