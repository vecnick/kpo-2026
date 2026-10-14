package vyshkat.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import vyshkat.console.ConsoleApp;
import vyshkat.service.FleetService;
import vyshkat.service.ServiceCenter;
import vyshkat.service.impl.FleetServiceImpl;
import vyshkat.service.impl.ServiceCenterImpl;

/**
 * Создаёт и связывает компоненты приложения через Spring.
 */
@Configuration(proxyBeanMethods = false)
public final class ApplicationConfiguration {
    @Bean
    ServiceCenter serviceCenter() {
        return new ServiceCenterImpl();
    }

    @Bean
    FleetService fleetService(final ServiceCenter serviceCenter) {
        return new FleetServiceImpl(serviceCenter);
    }

    @Bean
    ConsoleApp consoleApp(final FleetService fleetService) {
        return new ConsoleApp(fleetService);
    }
}
