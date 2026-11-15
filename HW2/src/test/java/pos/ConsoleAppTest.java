package pos;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import pos.adapter.ConsoleApp;
import pos.application.Gradebook;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ConsoleAppTest {
    private static final int EXAM_MAXIMUM = 10;
    private static final String SETUP = """
            student | s1 | Анна, "Аня"
            criterion | exam | Экзамен | %d | 3
            criterion | lab | Практика | 20 | 1
            """.formatted(EXAM_MAXIMUM);

    @TempDir
    Path directory;

    @Test
    void exportsWeightedResultToCsv() throws IOException {
        Path csv = directory.resolve("results.csv");
        String output = run("""
                grade | s1 | exam | 8
                grade | s1 | lab | 10
                result | s1
                export | %s
                """.formatted(csv));

        assertTrue(output.contains("72.50% | оценено 2 из 2"));
        assertEquals("student_id,student_name,graded,criteria,percent\r\n"
                + "\"s1\",\"Анна, \"\"Аня\"\"\",2,2,72.50\r\n", Files.readString(csv));
    }

    @Test
    void invalidScoreLeavesPreviousScoreInProgressReport() throws IOException {
        String output = run("""
                grade | s1 | exam | 8
                grade | s1 | exam | %d
                progress | s1
                """.formatted(EXAM_MAXIMUM + 1));

        assertTrue(output.contains("Балл превышает максимум " + EXAM_MAXIMUM));
        assertTrue(output.contains("80.00% | оценено 1 из 2"));
    }

    @Test
    void canceledEvaluationChangesTheFinalResult() throws IOException {
        String output = run("""
                grade | s1 | exam | 8
                grade | s1 | lab | 10
                cancel | s1 | lab
                result | s1
                """);

        assertTrue(output.contains("60.00% | оценено 1 из 2"));
    }

    @Test
    void exportKeepsExistingFile() throws IOException {
        Path csv = directory.resolve("results.csv");
        String original = "Ранее сохранённая ведомость";
        Files.writeString(csv, original);

        String output = run("export | " + csv + "\n");

        assertTrue(output.contains("файл уже существует"));
        assertEquals(original, Files.readString(csv));
    }

    private String run(String commands) throws IOException {
        StringWriter output = new StringWriter();
        try (var context = new AnnotationConfigApplicationContext(AppConfig.class)) {
            var console = new ConsoleApp(context.getBean(Gradebook.class),
                    new BufferedReader(new StringReader(SETUP + commands + "exit\n")), new PrintWriter(output));
            console.run();
        }
        return output.toString();
    }
}
