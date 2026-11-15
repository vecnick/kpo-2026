package pos.application;

import pos.domain.Course;
import pos.domain.CourseRepository;
import pos.domain.Criterion;
import pos.domain.Evaluation;
import pos.domain.Score;
import pos.domain.ScorePolicy;
import pos.domain.Student;
import pos.domain.WeightedResultService;

import java.io.IOException;
import java.util.List;

public final class Gradebook {
    private final CourseRepository repository;
    private final WeightedResultService results;
    private final ResultExporter exporter;

    public Gradebook(CourseRepository repository, WeightedResultService results, ResultExporter exporter) {
        this.repository = repository;
        this.results = results;
        this.exporter = exporter;
    }

    public void addStudent(Student student) {
        Course course = repository.load();
        course.addStudent(student);
        repository.save(course);
    }

    public void addCriterion(Criterion criterion) {
        Course course = repository.load();
        course.addCriterion(criterion);
        repository.save(course);
    }

    public List<Student> students() {
        return repository.load().students();
    }

    public List<Criterion> criteria() {
        return repository.load().criteria();
    }

    public void grade(String studentId, String criterionId, Score score) {
        Course course = repository.load();
        course.grade(studentId, criterionId, score);
        repository.save(course);
    }

    public void cancel(String studentId, String criterionId) {
        Course course = repository.load();
        course.cancelEvaluation(studentId, criterionId);
        repository.save(course);
    }

    public List<Evaluation> evaluations(String studentId) {
        return repository.load().evaluations(studentId);
    }

    public ResultRow result(String studentId, ScorePolicy policy) {
        Course course = repository.load();
        return result(course, course.student(studentId), policy);
    }

    public List<ResultRow> results() {
        Course course = repository.load();
        return course.students().stream()
                .map(student -> result(course, student, ScorePolicy.FINAL))
                .toList();
    }

    public void export(String destination) throws IOException {
        exporter.export(destination, results());
    }

    private ResultRow result(Course course, Student student, ScorePolicy policy) {
        return new ResultRow(student, course.evaluations(student.id()).size(), course.criteria().size(),
                results.calculate(course, student.id(), policy));
    }
}
