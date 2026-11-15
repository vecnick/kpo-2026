package pos;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import pos.adapter.ConsoleApp;
import pos.application.Gradebook;

import java.io.BufferedReader;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ConsoleAppTest {
    @TempDir
    Path directory;

    @Test
    void createsAReportThroughTheConsoleAndKeepsExistingFile() throws Exception {
        Path csv = directory.resolve("results.csv");
        String commands = """
                student | s1 | Анна, "Аня"
                criterion | exam | Экзамен | 10 | 3
                criterion | lab | Практика | 20 | 1
                grade | s1 | exam | 8
                grade | s1 | exam | 11
                progress | s1
                grade | s1 | lab | 10
                result | s1
                export | %s
                cancel | s1 | lab
                result | s1
                export | %s
                exit
                """.formatted(csv, csv);

        StringWriter output = new StringWriter();
        try (var context = new AnnotationConfigApplicationContext(AppConfig.class)) {
            var console = new ConsoleApp(context.getBean(Gradebook.class),
                    new BufferedReader(new StringReader(commands)), new PrintWriter(output));
            console.run();
        }

        assertTrue(output.toString().contains("Балл превышает максимум 10"));
        assertTrue(output.toString().contains("80.00% | оценено 1 из 2"));
        assertTrue(output.toString().contains("72.50% | оценено 2 из 2"));
        assertTrue(output.toString().contains("60.00% | оценено 1 из 2"));
        assertTrue(output.toString().contains("файл уже существует"));
        assertEquals("student_id,student_name,graded,criteria,percent\r\n"
                + "\"s1\",\"Анна, \"\"Аня\"\"\",2,2,72.50\r\n", Files.readString(csv));
    }
}
