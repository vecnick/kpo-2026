package studying.withsolid.service.impl;

import studying.withsolid.exception.ApplicationErrorCode;
import studying.withsolid.exception.ApplicationException;
import studying.withsolid.model.Report;
import studying.withsolid.service.ReportSaver;

import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Реализация {@link ReportSaver}, сохраняющая отчёт в формате XML.
 * <p>
 * XML формируется через стандартный StAX-writer ({@link XMLStreamWriter}),
 * входящий в JDK, без сторонних библиотек сериализации.
 */
public final class XmlReportSaver implements ReportSaver {

    private static final DateTimeFormatter FILE_NAME_FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd-HH-mm-ss");

    private static final Path DEFAULT_REPORTS_DIRECTORY = Path.of("reports");

    private final Path reportsDirectory;

    /**
     * Создаёт сохранитель отчётов в XML со стандартным каталогом
     * {@code reports} относительно рабочей директории приложения.
     */
    public XmlReportSaver() {
        this(DEFAULT_REPORTS_DIRECTORY);
    }

    /**
     * Создаёт сохранитель отчётов в XML, работающий с указанным каталогом.
     *
     * @param reportsDirectory каталог для сохранения XML-отчётов;
     *                         будет создан автоматически, если ещё не существует
     */
    public XmlReportSaver(Path reportsDirectory) {
        this.reportsDirectory = reportsDirectory;
    }

    /**
     * Сохраняет отчёт в XML-файл внутри каталога отчётов.
     *
     * @param report отчёт для сохранения; не должен быть {@code null}
     * @throws ApplicationException с кодом {@link ApplicationErrorCode#VALIDATION_ERROR},
     *         если отчёт не передан
     * @throws ApplicationException с кодом {@link ApplicationErrorCode#FILE_WRITE_ERROR},
     *         если запись файла или построение XML завершились ошибкой
     */
    @Override
    public void save(Report report) {
        if (report == null) {
            throw new ApplicationException(
                    ApplicationErrorCode.VALIDATION_ERROR,
                    "Отчёт не должен быть null"
            );
        }

        Path filePath = buildFilePath(report);

        try {
            Files.createDirectories(reportsDirectory);
            Files.writeString(filePath, toXml(report));
        } catch (IOException e) {
            throw new ApplicationException(
                    ApplicationErrorCode.FILE_WRITE_ERROR,
                    "Не удалось сохранить отчёт в XML-файл: " + filePath,
                    e
            );
        } catch (XMLStreamException e) {
            // Ошибка построения самого XML-документа (а не записи на диск) —
            // тоже относим к FILE_WRITE_ERROR, так как для клиента это
            // одна и та же категория сбоя: "не удалось сохранить отчёт".
            throw new ApplicationException(
                    ApplicationErrorCode.FILE_WRITE_ERROR,
                    "Не удалось сформировать XML-представление отчёта",
                    e
            );
        }
    }

    /**
     * Формирует путь к XML-файлу отчёта на основе каталога отчётов
     * и даты/времени, указанных в самом отчёте.
     *
     * @param report отчёт, для которого строится путь
     * @return путь вида {@code <reportsDirectory>/report-<дата>-<время>.xml}
     */
    private Path buildFilePath(Report report) {
        String timestamp = LocalDateTime.of(report.date(), report.time())
                .format(FILE_NAME_FORMATTER);
        String fileName = "report-" + timestamp + ".xml";
        return reportsDirectory.resolve(fileName);
    }

    /**
     * Формирует XML-представление отчёта с помощью StAX {@link XMLStreamWriter}.
     * <p>
     * StAX сам экранирует спецсимволы в текстовых узлах (например, {@code &},
     * {@code <}), поэтому дополнительная ручная обработка строк не нужна.
     *
     * @param report отчёт для сериализации
     * @return строка с XML-документом
     * @throws XMLStreamException если построение XML-потока завершилось ошибкой
     */
    private String toXml(Report report) throws XMLStreamException {
        StringWriter stringWriter = new StringWriter();
        XMLStreamWriter writer = XMLOutputFactory.newInstance()
                .createXMLStreamWriter(stringWriter);

        writer.writeStartDocument("UTF-8", "1.0");
        writer.writeCharacters("\n");
        writer.writeStartElement("report");
        writer.writeCharacters("\n");

        writeElement(writer, "title", report.title());
        writeElement(writer, "date", report.date().toString());
        writeElement(writer, "time", report.time().toString());
        writeElement(writer, "carsSold", String.valueOf(report.carsSold()));
        writeElement(writer, "motorcyclesSold", String.valueOf(report.motorcyclesSold()));

        writer.writeEndElement(); // </report>
        writer.writeEndDocument();
        writer.flush();
        writer.close();

        return stringWriter.toString();
    }

    /**
     * Записывает один простой XML-элемент вида {@code <name>value</name>}
     * с переносом строки и базовым отступом после него — только
     * для читаемости получившегося файла человеком.
     *
     * @param writer поток, в который выполняется запись
     * @param name   имя элемента
     * @param value  текстовое содержимое элемента
     * @throws XMLStreamException если запись элемента завершилась ошибкой
     */
    private void writeElement(XMLStreamWriter writer, String name, String value)
            throws XMLStreamException {
        writer.writeCharacters("  ");
        writer.writeStartElement(name);
        writer.writeCharacters(value);
        writer.writeEndElement();
        writer.writeCharacters("\n");
    }
}