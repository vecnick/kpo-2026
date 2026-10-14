package vyshkat;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import vyshkat.console.ConsoleApp;
import vyshkat.service.FleetService;
import vyshkat.service.ServiceCenter;

/**
 * Проверяет создание основных компонентов в контексте Spring.
 */
@Tag("integration")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@DisplayName("Интеграционный тест поднятия Spring-контекста")
class VyshKatApplicationTest {

    /** Сервис парка, созданный контекстом Spring. */
    @Autowired
    private FleetService fleetService;

    /** Сервис техосмотра, созданный контекстом Spring. */
    @Autowired
    private ServiceCenter serviceCenter;

    /** Подменённая реализация консольного приложения. */
    @MockitoBean
    private ConsoleApp consoleApp;

    @Test
    @DisplayName("Spring-контекст содержит бин FleetService")
    void fleetServiceBeanIsPresent() {
        assertNotNull(fleetService);
    }

    @Test
    @DisplayName("Spring-контекст содержит бин ServiceCenter")
    void serviceCenterBeanIsPresent() {
        assertNotNull(serviceCenter);
    }

    @Test
    @DisplayName("Spring-контекст содержит бин ConsoleApp")
    void consoleAppBeanIsPresent() {
        assertNotNull(consoleApp);
    }
}
