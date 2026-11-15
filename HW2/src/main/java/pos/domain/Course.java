package pos.domain;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public final class Course {
    private final Map<String, Student> students = new LinkedHashMap<>();
    private final Map<String, Criterion> rubric = new LinkedHashMap<>();
    private final Map<String, Map<String, Evaluation>> evaluations = new LinkedHashMap<>();

    public void addStudent(Student student) {
        if (students.putIfAbsent(student.id(), student) != null) {
            throw new IllegalArgumentException("Студент с таким ID уже существует");
        }
        evaluations.put(student.id(), new LinkedHashMap<>());
    }

    public void addCriterion(Criterion criterion) {
        if (rubric.putIfAbsent(criterion.id(), criterion) != null) {
            throw new IllegalArgumentException("Критерий с таким ID уже существует");
        }
    }

    public List<Student> students() {
        return List.copyOf(students.values());
    }

    public List<Criterion> criteria() {
        return List.copyOf(rubric.values());
    }

    public Student student(String id) {
        Student student = students.get(id);
        if (student == null) {
            throw new IllegalArgumentException("Студент не найден: " + id);
        }
        return student;
    }

    public List<Evaluation> evaluations(String studentId) {
        return List.copyOf(studentEvaluations(studentId).values());
    }

    public void grade(String studentId, String criterionId, Score score) {
        Map<String, Evaluation> grades = studentEvaluations(studentId);
        Criterion criterion = rubric.get(criterionId);
        if (criterion == null) {
            throw new IllegalArgumentException("Критерий не найден в рубрикаторе: " + criterionId);
        }
        grades.put(criterionId, Evaluation.create(criterion, score));
    }

    public void cancelEvaluation(String studentId, String criterionId) {
        if (studentEvaluations(studentId).remove(criterionId) == null) {
            throw new IllegalArgumentException("Оценка по критерию не найдена: " + criterionId);
        }
    }

    private Map<String, Evaluation> studentEvaluations(String studentId) {
        return evaluations.get(student(studentId).id());
    }
}
