package studying.vyshkat.console;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import studying.vyshkat.inspection.StandardServiceCenter;
import studying.vyshkat.repository.InMemoryParkRepository;
import studying.vyshkat.service.ParkReportService;
import studying.vyshkat.service.ParkService;

/** Tests the complete console demonstration scenario. */
@DisplayName("Тесты консольного сценария")
class ConsoleScenarioTest {
    private final ByteArrayOutputStream output =
            new ByteArrayOutputStream();
    private PrintStream originalOutput;

    @BeforeEach
    void redirectOutput() {
        originalOutput = System.out;
        System.setOut(new PrintStream(
                output, true, StandardCharsets.UTF_8));
    }

    @AfterEach
    void restoreOutput() {
        System.setOut(originalOutput);
    }

    @Test
    @DisplayName("Консоль показывает все требуемые сценарии")
    void demonstratesAllRequiredScenarios() {
        final InMemoryParkRepository repository =
                new InMemoryParkRepository();
        final ParkService parkService = new ParkService(
                repository, new StandardServiceCenter());
        final ParkReportService reportService =
                new ParkReportService(repository);
        final ConsoleScenario scenario =
                new ConsoleScenario(parkService, reportService);

        scenario.run();

        final String text = output.toString(StandardCharsets.UTF_8);
        assertEquals(2, reportService.transportCount());
        assertEquals(4, reportService.inventory().size());
        assertEquals(4.4,
                reportService.totalDailyEnergyConsumption(), 0.001);
        assertTrue(text.contains("Техника прошла техосмотр"));
        assertTrue(text.contains(
                "Техническое состояние ниже допустимого уровня"));
        assertTrue(text.contains("Транспорт для новичков"));
        assertTrue(text.contains("Вся техника и вещи на балансе"));
    }
}
