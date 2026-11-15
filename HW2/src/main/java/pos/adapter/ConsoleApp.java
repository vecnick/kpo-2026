package pos.adapter;

import pos.application.Gradebook;
import pos.application.ResultRow;
import pos.domain.Criterion;
import pos.domain.Score;
import pos.domain.ScorePolicy;
import pos.domain.Student;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.nio.file.FileAlreadyExistsException;
import java.util.Arrays;

public final class ConsoleApp {
    private static final String HELP = """
            Команды ПОС (аргументы разделяются символом |)
              student | ID | имя
              students
              criterion | ID | название | максимум | вес
              criteria
              grade | ID студента | ID критерия | балл
              grades | ID студента
              cancel | ID студента | ID критерия
              result | ID студента
              progress | ID студента
              results
              export | путь.csv
              help
              exit
            """;

    private final Gradebook gradebook;
    private final BufferedReader input;
    private final PrintWriter output;

    public ConsoleApp(Gradebook gradebook, BufferedReader input, PrintWriter output) {
        this.gradebook = gradebook;
        this.input = input;
        this.output = output;
    }

    public void run() throws IOException {
        output.print(HELP);
        while (true) {
            output.print("ПОС> ");
            output.flush();
            String line = input.readLine();
            if (line == null) {
                return;
            }
            if (line.isBlank()) {
                continue;
            }
            String[] args = Arrays.stream(line.split("\\|", -1)).map(String::strip).toArray(String[]::new);
            try {
                if (args[0].equals("exit")) {
                    requireArguments(args, 0);
                    return;
                }
                execute(args);
            } catch (FileAlreadyExistsException exception) {
                output.println("Ошибка: файл уже существует. Укажите другой путь.");
            } catch (IOException | IllegalArgumentException exception) {
                output.println("Ошибка: " + exception.getMessage());
            }
        }
    }

    private void execute(String[] args) throws IOException {
        switch (args[0]) {
            case "student" -> {
                requireArguments(args, 2);
                gradebook.addStudent(new Student(args[1], args[2]));
                output.println("Студент добавлен.");
            }
            case "students" -> {
                requireArguments(args, 0);
                var students = gradebook.students();
                if (students.isEmpty()) {
                    output.println("Студентов пока нет.");
                }
                students.forEach(student -> output.printf("%s | %s%n", student.id(), student.name()));
            }
            case "criterion" -> {
                requireArguments(args, 4);
                gradebook.addCriterion(new Criterion(args[1], args[2], decimal(args[3]), decimal(args[4])));
                output.println("Критерий добавлен.");
            }
            case "criteria" -> {
                requireArguments(args, 0);
                var criteria = gradebook.criteria();
                if (criteria.isEmpty()) {
                    output.println("Рубрикатор пуст.");
                }
                criteria.forEach(criterion -> output.printf("%s | %s | максимум %s | вес %s%n",
                        criterion.id(), criterion.name(), criterion.maximum().toPlainString(),
                        criterion.weight().toPlainString()));
            }
            case "grade" -> {
                requireArguments(args, 3);
                gradebook.grade(args[1], args[2], new Score(decimal(args[3])));
                output.println("Оценка сохранена.");
            }
            case "grades" -> {
                requireArguments(args, 1);
                var evaluations = gradebook.evaluations(args[1]);
                if (evaluations.isEmpty()) {
                    output.println("Оценок пока нет.");
                }
                evaluations.forEach(evaluation -> output.printf("%s | %s%n", evaluation.criterionId(),
                        evaluation.score().value().toPlainString()));
            }
            case "cancel" -> {
                requireArguments(args, 2);
                gradebook.cancel(args[1], args[2]);
                output.println("Оценка отменена.");
            }
            case "result", "progress" -> {
                requireArguments(args, 1);
                ScorePolicy policy = args[0].equals("result") ? ScorePolicy.FINAL : ScorePolicy.GRADED_ONLY;
                printResult(gradebook.result(args[1], policy));
            }
            case "results" -> {
                requireArguments(args, 0);
                var rows = gradebook.results();
                if (rows.isEmpty()) {
                    output.println("В ведомости пока нет студентов.");
                }
                rows.forEach(this::printResult);
            }
            case "export" -> {
                requireArguments(args, 1);
                gradebook.export(args[1]);
                output.println("Ведомость сохранена: " + args[1]);
            }
            case "help" -> {
                requireArguments(args, 0);
                output.print(HELP);
            }
            default -> output.println("Неизвестная команда. Список команд можно открыть через help.");
        }
    }

    private void printResult(ResultRow row) {
        String percent = row.percent().map(value -> value.toPlainString() + "%").orElse("нет данных");
        output.printf("%s | %s | %s | оценено %d из %d%n",
                row.student().id(), row.student().name(), percent, row.graded(), row.criteria());
    }

    private static void requireArguments(String[] args, int count) {
        if (args.length != count + 1 || Arrays.stream(args, 1, args.length).anyMatch(String::isBlank)) {
            throw new IllegalArgumentException("Проверьте аргументы команды " + args[0] + " (см. help)");
        }
    }

    private static BigDecimal decimal(String value) {
        try {
            // Обычная десятичная запись. Запятая удобна при вводе с русской раскладки.
            if (!value.matches("[+-]?[0-9]+([.,][0-9]+)?")) {
                throw new NumberFormatException();
            }
            return new BigDecimal(value.replace(',', '.'));
        } catch (NumberFormatException exception) {
            throw new IllegalArgumentException("Ожидалось число: " + value, exception);
        }
    }
}
