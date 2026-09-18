package studying.withsolid.service;

import studying.withsolid.model.Report;

/**
 * Контракт доставки отчёта получателю.
 * Реализация решает, каким каналом отправить отчёт
 * (email, Telegram, SMS и т.д.).
 */
@FunctionalInterface
public interface ReportSender {

    /**
     * Отправляет отчёт указанному получателю.
     *
     * @param report отчёт для отправки; не должен быть {@code null}
     * @param email  адрес получателя; не должен быть {@code null} или пустым
     * @throws studying.withsolid.exception.ApplicationException если данные некорректны
     *         или отправка завершилась ошибкой
     */
    void send(Report report, String email);
}