package studying.withsolid.service;

import studying.withsolid.exception.ApplicationErrorCode;
import studying.withsolid.exception.ApplicationException;
import studying.withsolid.model.Report;

import java.util.Objects;

/**
 * Координирует сохранение и отправку отчёта, завися только от контрактов.
 */
public final class ReportService {
    private final ReportSaver reportSaver;
    private final ReportSender reportSender;

    /**
     * Создаёт сервис с выбранными стратегиями сохранения и отправки.
     *
     * @param reportSaver стратегия сохранения
     * @param reportSender стратегия отправки
     * @throws NullPointerException если одна из стратегий не задана
     */
    public ReportService(ReportSaver reportSaver, ReportSender reportSender) {
        this.reportSaver = Objects.requireNonNull(reportSaver, "ReportSaver не задан");
        this.reportSender = Objects.requireNonNull(reportSender, "ReportSender не задан");
    }

    /**
     * Сначала сохраняет отчёт и только после успешного сохранения отправляет его.
     *
     * @param report обрабатываемый отчёт
     * @param email адрес получателя
     * @throws ApplicationException если данные некорректны, сохранение или отправка не удались
     */
    public void process(Report report, String email) {
        if (report == null) {
            throw new ApplicationException(ApplicationErrorCode.VALIDATION_ERROR,
                    "Отчёт не задан");
        }
        reportSaver.save(report);
        reportSender.send(report, email);
    }
}
