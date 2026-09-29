package studying.service.impl;

import studying.exception.ApplicationErrorCode;
import studying.exception.ApplicationException;
import studying.model.Report;
import studying.service.ReportSender;

public final class ReportSenderImpl implements ReportSender {
    private Delivery lastDelivery;

    @Override
    public void send(final Report report, final String email) {
        if (report == null || email == null || email.isBlank()) {
            throw new ApplicationException(
                    ApplicationErrorCode.VALIDATION_ERROR,
                    String.format("Отчет или email: \"%s\" не может быть null.",
                            email)
            );
        }

        lastDelivery = new Delivery(report, email);
    }

    public Delivery getLastDelivery() {
        return lastDelivery;
    }

    public record Delivery(Report report, String email) { }
}
