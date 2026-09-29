package studying.model;

import org.junit.jupiter.api.Test;
import studying.withsolid.exception.ApplicationErrorCode;
import studying.withsolid.exception.ApplicationException;
import studying.withsolid.model.Report;

import java.time.LocalDate;
import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ReportTest {
    @Test
    void rejectsNegativeSales() {
        var exception = assertThrows(ApplicationException.class,
                () -> new Report("Продажи", LocalDate.now(), LocalTime.NOON, -1, 0));

        assertEquals(ApplicationErrorCode.VALIDATION_ERROR, exception.getCode());
    }

    @Test
    void rejectsBlankTitle() {
        var exception = assertThrows(ApplicationException.class,
                () -> new Report(" ", LocalDate.now(), LocalTime.NOON, 1, 1));

        assertEquals(ApplicationErrorCode.VALIDATION_ERROR, exception.getCode());
    }
}
