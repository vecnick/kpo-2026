package pos.adapter;

import pos.domain.Course;
import pos.domain.CourseRepository;

public final class InMemoryCourseRepository implements CourseRepository {
    private Course course = new Course();

    @Override
    public Course load() {
        return course;
    }

    @Override
    public void save(Course course) {
        this.course = course;
    }
}
