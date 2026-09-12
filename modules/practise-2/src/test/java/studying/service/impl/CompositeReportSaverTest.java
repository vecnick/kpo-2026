package studying.service.impl;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import studying.withsolid.model.Report;
import studying.withsolid.service.impl.CompositeReportSaver;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CompositeReportSaverTest {
    @Test
    @DisplayName("Передаёт отчёт всем сохранителям по порядку")
    void saveDelegatesToEverySaver() {
        var calls = new ArrayList<String>();
        var report = Report.builder()
                .title("Продажи")
                .date(LocalDate.of(2026, 9, 11))
                .time(LocalTime.NOON)
                .carsSold(100)
                .motorcyclesSold(50)
                .build();
        var saver = new CompositeReportSaver(
                savedReport -> calls.add("txt:" + savedReport.title()),
                savedReport -> calls.add("json:" + savedReport.title()),
                savedReport -> calls.add("xml:" + savedReport.title())
        );

        saver.save(report);

        assertEquals(List.of("txt:Продажи", "json:Продажи", "xml:Продажи"), calls);
    }
}
