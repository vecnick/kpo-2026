package pos;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import pos.adapter.ConsoleApp;
import pos.adapter.CsvResultExporter;
import pos.adapter.InMemoryCourseRepository;
import pos.application.Gradebook;
import pos.application.ResultExporter;
import pos.domain.CourseRepository;
import pos.domain.WeightedResultService;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;

@Configuration(proxyBeanMethods = false)
public class AppConfig {
    @Bean
    CourseRepository courseRepository() {
        return new InMemoryCourseRepository();
    }

    @Bean
    WeightedResultService weightedResultService() {
        return new WeightedResultService();
    }

    @Bean
    ResultExporter resultExporter() {
        return new CsvResultExporter();
    }

    @Bean
    Gradebook gradebook(CourseRepository repository, WeightedResultService results, ResultExporter exporter) {
        return new Gradebook(repository, results, exporter);
    }

    @Bean
    ConsoleApp consoleApp(Gradebook gradebook) {
        return new ConsoleApp(gradebook,
                new BufferedReader(new InputStreamReader(System.in, StandardCharsets.UTF_8)),
                new PrintWriter(System.out, true, StandardCharsets.UTF_8));
    }
}
