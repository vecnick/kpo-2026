package pos;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import pos.adapter.ConsoleApp;

import java.io.IOException;

public final class Main {
    private Main() {}

    public static void main(String[] args) throws IOException {
        try (var context = new AnnotationConfigApplicationContext(AppConfig.class)) {
            context.getBean(ConsoleApp.class).run();
        }
    }
}
