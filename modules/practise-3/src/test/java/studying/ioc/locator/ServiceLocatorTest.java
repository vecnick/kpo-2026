package studying.ioc.locator;

import org.junit.jupiter.api.Test;
import studying.exception.ApplicationErrorCode;
import studying.exception.ApplicationException;
import studying.service.ReportSaver;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ServiceLocatorTest {
    @Test
    void returnsRegisteredService() {
        var locator = new ServiceLocator();
        ReportSaver saver = report -> { };

        locator.register(ReportSaver.class, saver);

        assertSame(saver, locator.getRequired(ReportSaver.class));
    }

    @Test
    void throwsWhenServiceIsMissing() {
        var exception = assertThrows(ApplicationException.class,
                () -> new ServiceLocator().getRequired(ReportSaver.class));

        assertEquals(ApplicationErrorCode.SERVICE_NOT_FOUND, exception.getCode());
    }
}
