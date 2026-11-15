package pos.domain;

public interface CourseRepository {
    Course load();
    void save(Course course);
}
