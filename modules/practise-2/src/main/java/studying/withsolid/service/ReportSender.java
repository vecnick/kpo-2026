package studying.withsolid.service;

import studying.withsolid.model.Report;

/**
 * Контракт доставки отчёта получателю.
 */
@FunctionalInterface
public interface ReportSender {
    /**
     * Отправляет отчёт заданному получателю.
     *
     * @param report отправляемый отчёт
     * @param email адрес получателя
     * @throws studying.withsolid.exception.ApplicationException если отчёт или email не заданы
     */
    void send(Report report, String email);
}
