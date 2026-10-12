package studying;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/** Starts the VyshKat fleet accounting application. */
@SpringBootApplication
@NoArgsConstructor(access = AccessLevel.PACKAGE)
public class Main {
    /**
     * Runs the Spring application context.
     *
     * @param args command line arguments
     */
    public static void main(final String[] args) {
        SpringApplication.run(Main.class, args);
    }
}
