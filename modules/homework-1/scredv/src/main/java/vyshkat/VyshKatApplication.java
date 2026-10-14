package vyshkat;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Точка входа в консольное приложение «ВышКат».
 */
@SpringBootApplication
public final class VyshKatApplication {
    private VyshKatApplication() { }

    /**
     * Запускает приложение через Spring Boot.
     *
     * @param args аргументы командной строки
     */
    public static void main(final String[] args) {
        SpringApplication.run(VyshKatApplication.class, args);
    }
}
