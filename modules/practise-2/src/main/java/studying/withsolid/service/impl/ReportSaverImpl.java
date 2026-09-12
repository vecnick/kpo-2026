package studying.withsolid.service.impl;

import studying.withsolid.model.Report;
import studying.withsolid.service.ReportSaver;

/**
 * Учебная заглушка сохранителя, фиксирующая факт сохранения в консоли.
 * Для файлового экспорта следует использовать форматные реализации
 * {@link TextReportSaver}, {@link JsonReportSaver} или {@link SoapReportSaver}.
 */
public class ReportSaverImpl implements ReportSaver {
    /**
     * Выводит сообщение об условном сохранении отчёта.
     *
     * @param report сохраняемый отчёт
     */
    @Override
    public void save(Report report) {
        System.out.println("Report has been saved");
    }
}
