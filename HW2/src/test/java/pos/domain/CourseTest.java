package pos.domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CourseTest {
    private final Course course = new Course();
    private final WeightedResultService results = new WeightedResultService();

    @BeforeEach
    void setUp() {
        course.addStudent(new Student("s1", "Анна"));
        course.addCriterion(new Criterion("exam", "Экзамен", number("10"), number("3")));
        course.addCriterion(new Criterion("lab", "Практика", number("20"), number("1")));
    }

    @Test
    void rejectsInvalidEvaluationWithoutReplacingPreviousScore() {
        course.grade("s1", "exam", score("7"));

        assertThrows(IllegalArgumentException.class, () -> course.grade("s1", "exam", score("10.01")));
        assertThrows(IllegalArgumentException.class, () -> course.grade("s1", "exam", score("-1")));
        assertThrows(IllegalArgumentException.class, () -> course.grade("s1", "unknown", score("1")));
        assertThrows(IllegalArgumentException.class, () -> course.grade("unknown", "exam", score("1")));
        assertEquals(1, course.evaluations("s1").size());
        assertEquals(score("7"), course.evaluations("s1").get(0).score());
    }

    @Test
    void rejectsInvalidCriteriaAndDuplicateIdentities() {
        assertThrows(IllegalArgumentException.class,
                () -> new Criterion("x", "X", BigDecimal.ZERO, BigDecimal.ONE));
        assertThrows(IllegalArgumentException.class,
                () -> new Criterion("x", "X", BigDecimal.ONE, BigDecimal.ZERO));
        assertThrows(IllegalArgumentException.class, () -> course.addStudent(new Student("s1", "Другое имя")));
        assertThrows(IllegalArgumentException.class, () -> course.addCriterion(course.criteria().get(0)));
        assertEquals("Анна", course.student("s1").name());
    }

    @Test
    void calculatesUsingCurrentEvaluationsAndRubric() {
        assertTrue(results.calculate(course, "s1", ScorePolicy.GRADED_ONLY).isEmpty());
        course.grade("s1", "exam", score("8"));
        assertEquals(number("60.00"), result(ScorePolicy.FINAL));
        assertEquals(number("80.00"), result(ScorePolicy.GRADED_ONLY));

        course.grade("s1", "lab", score("10"));
        assertEquals(number("72.50"), result(ScorePolicy.FINAL));
        course.grade("s1", "exam", score("10"));
        assertEquals(number("87.50"), result(ScorePolicy.FINAL));

        course.cancelEvaluation("s1", "lab");
        assertEquals(number("75.00"), result(ScorePolicy.FINAL));
        course.addCriterion(new Criterion("quiz", "Тест", number("5"), number("2")));
        assertEquals(number("50.00"), result(ScorePolicy.FINAL));
    }

    @Test
    void distinguishesEmptyRubricFromZeroScore() {
        Course emptyRubric = new Course();
        emptyRubric.addStudent(new Student("s1", "Анна"));
        assertTrue(results.calculate(emptyRubric, "s1", ScorePolicy.FINAL).isEmpty());

        course.grade("s1", "exam", score("0"));
        assertEquals(number("0.00"), result(ScorePolicy.GRADED_ONLY));
    }

    @Test
    void roundsTheExactWeightedResultOnce() {
        Course singleCriterion = new Course();
        singleCriterion.addStudent(new Student("s1", "Анна"));
        singleCriterion.addCriterion(new Criterion("exam", "Экзамен", number("3"), number("1")));
        singleCriterion.grade("s1", "exam", score("0.00015"));

        assertEquals(number("0.01"), results.calculate(singleCriterion, "s1", ScorePolicy.FINAL).orElseThrow());
    }

    private BigDecimal result(ScorePolicy policy) {
        return results.calculate(course, "s1", policy).orElseThrow();
    }

    private static Score score(String value) {
        return new Score(number(value));
    }

    private static BigDecimal number(String value) {
        return new BigDecimal(value);
    }
}
