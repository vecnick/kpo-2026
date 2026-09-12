package studying.withsolid.report;

import studying.withsolid.delivery.ReportSender;
import studying.withsolid.error.ApplicationException;
import studying.withsolid.persistence.ReportSaver;

import java.util.Objects;

import static studying.withsolid.error.ApplicationErrorCode.VALIDATION_ERROR;

/**
 * Выполняет основной сценарий обработки отчёта: сначала сохраняет его,
 * затем отправляет получателю через переданные извне контракты.
 */
public final class ReportService {
    private final ReportSaver reportSaver;
    private final ReportSender reportSender;

    /**
     * Создаёт сервис с заменяемыми способами сохранения и отправки.
     *
     * @param reportSaver способ сохранения отчёта
     * @param reportSender способ доставки отчёта
     */
    public ReportService(ReportSaver reportSaver, ReportSender reportSender) {
        this.reportSaver = Objects.requireNonNull(reportSaver,
                "Сохранитель отчёта не должен отсутствовать");
        this.reportSender = Objects.requireNonNull(reportSender,
                "Отправитель отчёта не должен отсутствовать");
    }

    /**
     * Проверяет входные данные, сохраняет отчёт и только после этого отправляет его.
     *
     * @param report обрабатываемый отчёт
     * @param email адрес получателя
     * @throws ApplicationException если отчёт или email не заданы
     */
    public void process(Report report, String email) {
        if (report == null) {
            throw new ApplicationException(VALIDATION_ERROR,
                    "Отчёт не должен отсутствовать");
        }
        if (email == null || email.isBlank()) {
            throw new ApplicationException(VALIDATION_ERROR,
                    "Email получателя не должен быть пустым");
        }

        reportSaver.save(report);
        reportSender.send(report, email);
    }
}
