package pos.application;

import pos.domain.Student;

import java.math.BigDecimal;
import java.util.Optional;

public record ResultRow(Student student, int graded, int criteria, Optional<BigDecimal> percent) {}
